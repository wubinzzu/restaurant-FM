/**
 * 
 */
package minhwan.researchCF.sampler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bson.Document;

import minhwan.researchCF.sampler.filters.restaurant.ObjectReviewCountFilter;
import minhwan.researchCF.sampler.filters.user.UserReviewCountFilter;
import minhwan.researchCF.sampler.model.DatabaseDataModel;
import minhwan.util.IO.FileIO;
import minhwan.util.IO.FileIOWriter;
import minhwan.util.IO.FileSystem;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 7.
 * @fileName DataLoader.java
 */
public class LocalSampler{
	private static Logger logger = Logger.getLogger(LocalSampler.class.getName());
	
	private String rootDir;
	private String objectDir;
	private String userDir;
	
	private boolean userLevelSampling;

	private ArrayList<SamplingFilter> objectFilters;
	private ArrayList<SamplingFilter> userFilters;
	
	private HashMap<String, ArrayList<Document>> reviewBuff;
	
	private int reviewBuffSize = 100000;
	private int reivewBuffTmpNum = 1;
	private int samplingCnt = 0;
		
	public LocalSampler(boolean userLevelSampling){
		this.userLevelSampling = userLevelSampling;

		objectFilters = new ArrayList<SamplingFilter>();
		userFilters = new ArrayList<SamplingFilter>();
		
		reviewBuff = new HashMap<String, ArrayList<Document>>();
	}	
	
	/* managing filter */
	public void addObjectFilter(SamplingFilter filter){
		logger.info("add object sampling-filter: " + filter.getClass().getName());
		objectFilters.add(filter);
	}
	public void addUserFilter(SamplingFilter filter){
		logger.info("add user sampling-filter: " + filter.getClass().getName());
		userFilters.add(filter);
	}
	
	/* executing methods */
	public void sampling(String rootDir) throws IOException {
		logger.info("Sampling Start.... <<<<---- " + rootDir);
		
		ArrayList<String> filePaths = 
				FileSystem.readFileList(rootDir + "./all/");
		
		// create sampling result directory
		this.rootDir = rootDir;
		this.objectDir = rootDir + "/sampling/objects/";
		this.userDir = rootDir + "/sampling/users/";
		
		FileSystem.mkdir_path(objectDir);
		
		// iteratively sampling file
		for(String filePath : filePaths){
			DatabaseDataModel ldm = data2model(filePath);
			
			if(objectFiltering(ldm))	writeSamplingResult(ldm );
			
			if(samplingCnt++ % 100 == 0)
				System.out.printf("%d/%d %s\n", samplingCnt, filePaths.size(), filePath);
		}
		
		if(userLevelSampling){
			samplingUser();
		}
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
		writer = new FileIOWriter(this.objectDir + sourceKey + ".json", false);
		Document result = ldm.getObject();
		result.remove("_id");
		result.remove("reviewIDs");
		result.remove("host");
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
			review.remove("reviewerID");
			review.remove("reviewID");
			reviewBuff.get(reviewerID).add(review);
		}
	}
	
	/* User/Review Buff Writer */
	private void writeUserBuff() throws IOException{
		FileIOWriter writer;
		
		logger.info("reviewBuff is over " + reviewBuffSize + ", therefore writing buff data\t\t"
				+ "currSamplingCnt: " + samplingCnt);

		StringBuffer sb = new StringBuffer();
		
		for(String reviewerID : reviewBuff.keySet()){
			sb.append("#").append(reviewerID).append("\n");
			for(Document review : reviewBuff.get(reviewerID))
				sb.append(review.toJson()).append("\n");
		}
		
		sb.append("#");

		String targetPath = this.rootDir + "tmp/" + reivewBuffTmpNum + ".txt";
		FileSystem.mkdir_path(targetPath);
		
		writer = new FileIOWriter(targetPath, true);
		writer.write(sb.toString());
		writer.close();

		reivewBuffTmpNum++;
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
		
		for(String userReviewPath : FileSystem.readFileList(rootDir + "tmp")){
			logger.info("user Sampling : " + userReviewPath);
			
			ArrayList<String> lines = FileIO.readEachLine(userReviewPath);
			
			ArrayList<Document> reviewDocs = new ArrayList<Document>();
			String reviewerID = null;
			
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
					reviewDocs.add(Document.parse(line));
				}	
			}
		}
	}
	
	private void writeUserSamplingResult(ArrayList<Document> reviewDocs, String reviewerID) throws IOException{
		if(reviewDocs == null) return;
		if(reviewDocs.size() == 0) return;
		
		String targetFilePath = this.userDir + reviewerID + ".json";
		FileIOWriter writer = new FileIOWriter(targetFilePath, true);
		
		for(Document review : reviewDocs){
			writer.write(review.toJson());
		}
		
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
	
	public static void main(String[] args) throws IOException{
		String rootDir = "D:/Research/FM/data/sanf/";

		LocalSampler ls = new LocalSampler(true);

		// add object filters
		ls.addObjectFilter(new ObjectReviewCountFilter(5, Integer.MAX_VALUE));

		// add user filters
		ls.addUserFilter(new UserReviewCountFilter(5, Integer.MAX_VALUE));
		
		ls.sampling(rootDir);
//		ls.samplingUser("D:/Research/FM/data/sanf/sampling/users/");
	}
}
