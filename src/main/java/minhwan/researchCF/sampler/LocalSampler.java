/**
 * 
 */
package minhwan.researchCF.sampler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import minhwan.researchCF.sampler.model.DatabaseDataModel;
import minhwan.util.IO.FileIO;
import minhwan.util.IO.FileIOWriter;
import minhwan.util.IO.FileSystem;
import minhwan.util.common.logger.LogType;
import minhwan.util.common.logger.Logger;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 7.
 * @fileName DataLoader.java
 */
public class LocalSampler{	
	private String rootDir;
	private String objectDir;
	private String userDir;
	
	private FileIOWriter userReviewListWriter;
	
	private boolean userLevelSampling;

	private ArrayList<SamplingFilter> objectFilters;
	private ArrayList<SamplingFilter> userFilters;
	
	private HashMap<String, ArrayList<Document>> reviewBuff;
	
	private int reviewBuffSize = 100000;
	private int samplingCnt = 0;
		
	public LocalSampler(boolean userLevelSampling){
		this.userLevelSampling = userLevelSampling;

		objectFilters = new ArrayList<SamplingFilter>();
		userFilters = new ArrayList<SamplingFilter>();
		
		reviewBuff = new HashMap<String, ArrayList<Document>>();
	}	
	
	/* managing filter */
	public void addObjectFilter(SamplingFilter filter){
		Logger.log(LogType.LOAD, "add object sampling-filter: " + filter.getClass().getSimpleName());
		objectFilters.add(filter);
	}
	public void addUserFilter(SamplingFilter filter){
		Logger.log(LogType.LOAD, "add object sampling-filter: " + filter.getClass().getSimpleName());
		userFilters.add(filter);
	}
	
	/* executing methods */
	public void sampling(String rootDir) throws IOException {
		Logger.log(LogType.INFO, "Sampling Start... <<<--- " + rootDir);
		
		ArrayList<String> filePaths = 
				FileSystem.readFileList(rootDir + "./all/");
		
		// create sampling result directory
		this.rootDir = rootDir;
		this.objectDir = rootDir + "/sampling/objects/";
		this.userDir = rootDir + "/sampling/users/";
		
		FileSystem.mkdir_path(rootDir + "/sampling/mapper.dat");
		this.userReviewListWriter = new FileIOWriter(rootDir + "/sampling/userList.dat", true);
		
		FileSystem.mkdir_path(objectDir);
		
		// iteratively sampling file
		for(String filePath : filePaths){
			DatabaseDataModel ldm = data2model(filePath);
			
			if(objectFiltering(ldm))	writeSamplingResult(ldm );
			
			Logger.log(LogType.INFO, samplingCnt + "/" + filePaths.size() + "\t" +  filePath);
		}
		
		writeUserBuff();
				
		if(userLevelSampling){
			samplingUser();
		}
		
		userReviewListWriter.close();
	}
	
	public static DatabaseDataModel data2model(String filePath){
		try {
			ArrayList<String> lines = FileIO.readEachLine(filePath);
			
			Document objectData = null;
			ArrayList<Document> reviewData = new ArrayList<Document>();
			
			for(int i = 0; i < lines.size();  i++){
				if(i == 0)
					objectData = Document.parse(lines.get(i));
				else
					reviewData.add(Document.parse(lines.get(i)));
			}
			
			return new DatabaseDataModel(objectData, reviewData);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/* Object Writers */
	private void writeSamplingResult(DatabaseDataModel ldm) throws IOException{
		String sourceKey = ldm.getObject().getString("sourceKey");
		
		FileIOWriter writer;
		writer = new FileIOWriter(this.objectDir + sourceKey + ".dat", false);
		Document result = ldm.getObject();
		result.remove("_id");
		result.remove("host");
		result.remove("reviewIDs");
		writer.write(result.toJson());
		writer.close();
		
		// collect review for each user(reviewerID)
		
		if(userLevelSampling)
			handleUserBuff(ldm);
	}
	
	/* User/Review Buff */
	private void handleUserBuff(DatabaseDataModel ldm) throws IOException{
		if(reviewBuff.size() > reviewBuffSize)
			writeUserBuff();
		
		for(Document review : ldm.getReviews()){
			String reviewerID = review.getString("reviewerID");
			
			if(!reviewBuff.containsKey(reviewerID))
				reviewBuff.put(reviewerID, new ArrayList<Document>());
			
			review.remove("_id");
			review.remove("reviewID");
			review.remove("reviewerID");
			reviewBuff.get(reviewerID).add(review);
		}
	}
	
	/* User/Review Buff Writer */
	private void writeUserBuff() throws IOException{
		FileIOWriter writer;
		
		Logger.log(LogType.DEBUG, "reviewBuff is over " + reviewBuffSize + ", therefore write the buff data\t\t"
				+ "currSamplingCnt: " + samplingCnt);
		
		StringBuffer sb = new StringBuffer();
		
		for(String reviewerID : reviewBuff.keySet()){
			sb.append("#").append(reviewerID).append("\n");
			for(Document review : reviewBuff.get(reviewerID))
				sb.append(review.toJson()).append("\n");
		}
		
		sb.append("#");

		String targetPath = this.rootDir + "tmp/userReviewTemp.dat";
		FileSystem.mkdir_path(targetPath);
		
		writer = new FileIOWriter(targetPath, true);
		writer.write(sb.toString());
		writer.close();

		reviewBuff.clear();
		reviewBuff = new HashMap<String, ArrayList<Document>>();
		
	}
	
	/* User/Review Sampling */
	public void samplingUser(String rootDir) throws IOException{
		this.rootDir = rootDir;
		samplingUser();
	}
	
	public void samplingUser() throws IOException{
		FileSystem.mkdir_path(this.userDir);
		
		Logger.log(LogType.LOAD, "load userReviewTemp.dat for sampling user data");
		
		ArrayList<String> lines = FileIO.readEachLine(rootDir + "tmp/userReviewTemp.dat");
		
		ArrayList<Document> reviewDocs = new ArrayList<Document>();
		String reviewerID = null;
		
		Logger.log(LogType.START, "sampling user start!");
		for(int i = 0; i< lines.size(); i++){
			String line = lines.get(i);
			
			// new user or the end of file
			if(line.startsWith("#") || i == lines.size() - 1){
				if(reviewDocs.size() > 0){
					DatabaseDataModel userReviewData = new DatabaseDataModel(reviewDocs.get(0), reviewDocs);
					if(userFiltering(userReviewData)){
						writeUserSamplingResult(reviewDocs, reviewerID);
					}
					reviewDocs.clear();
				}
				
				reviewerID = line.substring(1);
			}
			// common review line
			else{
				if(line.contains("ghost user")) continue;
				reviewDocs.add(Document.parse(line));
			}	
		}
	}
	
	private void writeUserSamplingResult(ArrayList<Document> reviewDocs, String reviewerID) throws IOException{
		if(reviewDocs == null) return;
		if(reviewDocs.size() == 0) return;
		
		String targetFilePath = this.userDir + reviewerID + ".dat";
		FileIOWriter writer = new FileIOWriter(targetFilePath, true);
		
		for(Document review : reviewDocs){
			writer.write(review.toJson());
		}

		userReviewListWriter.write(reviewerID);
		
		writer.close();
	}
	
	/* ************************* */
	private boolean objectFiltering(DatabaseDataModel ldm){
		if(ldm == null) return false;
		
		for(SamplingFilter filter : objectFilters){
			if(!filter.filter(ldm)) return false;
		}
		
		return true;
	}
	
	private boolean userFiltering(DatabaseDataModel ldm){
		if(ldm == null) return false;
		
		for(SamplingFilter filter : userFilters){
			if(!filter.filter(ldm)) return false;
		}
		
		return true;		
	}
}
