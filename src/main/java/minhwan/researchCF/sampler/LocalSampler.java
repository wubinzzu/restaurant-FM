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
import minhwan.researchCF.sampler.model.LocalDataModel;
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
	
	private String objectDir;
	private String userDir;
	
	private boolean userLevelSampling;
	private UserReviewDatabase userReviewDB;

	private ArrayList<SamplingFilter> objectFilters;
	private ArrayList<SamplingFilter> userFilters;
		
	public LocalSampler(boolean userLevelSampling){
		this.userLevelSampling = userLevelSampling;

		objectFilters = new ArrayList<SamplingFilter>();
		userFilters = new ArrayList<SamplingFilter>();
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
		this.objectDir = rootDir + "/sampling/objects/";
		this.userDir = rootDir + "/sampling/users/";
		
		FileSystem.mkdir_path(objectDir);
		if(userLevelSampling)
			FileSystem.mkdir_path(userDir);
		
		// iteratively sampling file
		for(String filePath : filePaths){
			LocalDataModel ldm = data2model(filePath);
			
			if(objectFiltering(ldm))	writeSamplingResult(ldm );
		}
		
		if(userLevelSampling){
			
		}
	}
	
	public static LocalDataModel data2model(String filePath){
		try {
			String[] lines = FileIO.readEachLine(filePath);
			
			Document objectData = null;
			ArrayList<Document> reviewData = new ArrayList<Document>();
			
			for(int i = 0; i < lines.length;  i++){
				if(i == 0)
					objectData = Document.parse(lines[i]);
				else
					reviewData.add(Document.parse(lines[i]));
			}
			
			return new LocalDataModel(objectData, reviewData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/* Writers */
	private void writeSamplingResult(LocalDataModel ldm) throws IOException{
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
		if(userLevelSampling){
			if(userReviewDB == null) userReviewDB = new UserReviewDatabase();
			
			for(Document review : ldm.getReviews())
				userReviewDB.addUserReview(review);
		}
	}
	
	private void writeUserSamplingResult() throws IOException{
		HashMap<String, ArrayList<Document>> userReviews =
				userReviewDB.getUserReviews();

		logger.info("Sampling Users .... <<<<---- " + userReviews.size());

		for(String reviewerID : userReviews.keySet()){
			ArrayList<Document> reviews = userReviews.get(reviewerID);
			
			LocalDataModel reviewD = new LocalDataModel(reviews.get(0), reviews);
			
			if(userFiltering(reviewD)){
				FileIOWriter writer;
				writer = new FileIOWriter(this.userDir + reviewerID + ".json", true);
				
				for(Document review: reviews)
					writer.write(review.toJson());
				
				writer.close();
			}
		}
	}
	
	/* ************************* */
	private boolean objectFiltering(LocalDataModel ldm){
		if(ldm == null) return false;
		
		for(SamplingFilter filter : objectFilters){
			if(!filter.filter(ldm)) return false;
		}
		
		return true;
	}
	
	private boolean userFiltering(LocalDataModel ldm){
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
	}
}
