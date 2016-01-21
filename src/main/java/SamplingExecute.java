import java.io.IOException;
import java.util.HashMap;

import org.bson.Document;

import minhwan.researchCF.machinelearning.UserCorrSplitter;
import minhwan.researchCF.nlp.KeywordExtractor;
import minhwan.researchCF.sampler.DatabaseSampler;
import minhwan.researchCF.sampler.LocalSampler;
import minhwan.researchCF.sampler.SamplingFilter;
import minhwan.researchCF.sampler.filters.restaurant.ObjectReviewCountFilter;
import minhwan.researchCF.sampler.filters.user.UserReviewCountFilter;
import minhwan.util.IO.FileIO;
import minhwan.util.IO.FileIOWriter;
import minhwan.util.IO.FileSystem;
import minhwan.util.common.logger.LogType;
import minhwan.util.common.logger.Logger;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 13.
 * @fileName Execute.java
 */
public class SamplingExecute {
	public static void databaseSampling(String writingDir) throws IOException{				
		/** data base sampling **/
		DatabaseSampler.ROOT_DIR = writingDir + "/all/";
		DatabaseSampler ds = new DatabaseSampler();

		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("address.addressLocality", "San Francisco");
		
		ds.sampling(queryMap);
	}
	
	public static void databaseSamplingTopN(String writingDir, int N) throws IOException{				
		/** data base sampling **/
		DatabaseSampler.ROOT_DIR = writingDir + "/all/";
		DatabaseSampler ds = new DatabaseSampler();

		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("address.addressLocality", "San Francisco");
		
		ds.sampling(queryMap, N);
	}
	
	public static void localSampling(String rootDir, 
			SamplingFilter[] objectFilters, SamplingFilter[] userFilters) throws IOException{
		/** local sampling **/
		LocalSampler ls = new LocalSampler(true);

		// add object filters
		for(SamplingFilter filter : objectFilters)	ls.addObjectFilter(filter);
		// add user filters
		for(SamplingFilter filter : userFilters)	ls.addUserFilter(filter);
		
		ls.sampling(rootDir);
//		ls.samplingUser("D:/Research/FM/data/sanf/sampling/users/");
	}
	
	public static void extractUserRating(String rootDir) throws IOException{
		Logger.log(LogType.START, "Extract User Rating");
		FileIOWriter writer = new FileIOWriter(rootDir + "/ratings.dat", true);
		
		for(String filePath : FileSystem.readFileList(rootDir + "/users")){
			String reviewerID = FileSystem.getFileName(filePath);
			StringBuffer sb = new StringBuffer();
			
			Logger.log(LogType.INFO, filePath);
			for(String jsonStr : FileIO.readEachLine(filePath)){
				Document review = Document.parse(jsonStr);
				
				String sourceObjectKey = review.getString("sourceObjectKey");
				Double rating = review.getDouble("rating");
				
				sb
				.append(reviewerID).append("\t")
				.append(sourceObjectKey).append("\t")
				.append(rating).append("\n");
			}
			
			writer.write(sb.toString());
			sb.setLength(0);
		}

		Logger.log(LogType.END, "Extract User Rating");
		writer.close();
	}
	
	public static void extractKeyword(String rootDir) throws NumberFormatException, IOException{
		KeywordExtractor ke = new KeywordExtractor(rootDir);
		ke.extractTermFreq(rootDir, KeywordExtractor.DATA_TYPE.REVIEW);
		ke.extractKeyword(rootDir + "/freq/users");
		ke.extractTermFreq(rootDir, KeywordExtractor.DATA_TYPE.OBJECT);
		ke.extractKeyword(rootDir + "/freq/objects");
	}
	
	public static void splitUserCorr(String filePath){
		UserCorrSplitter uc;

		uc = new UserCorrSplitter();
		uc.dataLoad(filePath);
		
		uc.calculateAll();
	}
	
	public static void main(String[] args) throws IOException{
		Logger.debugMode = true;
		Logger.logInterval = 10;
		
		String rootDir = "D:/Research/FM/data/sanf-top500/";

		//databaseSampling(rootDir);
		
		/*database -> local dump
		 *  @Param2 : top N restaurant
		 */
		databaseSamplingTopN(rootDir, 500);
		
		/*local dump -> selected data
		 *  @Param2 : restaurant sampling filter 
		 *  @Param3 : user sampling filter
		 */
		SamplingFilter[] objectFilter = new SamplingFilter[]{
				new ObjectReviewCountFilter(50, Integer.MAX_VALUE)	
		};
		SamplingFilter[] userFilter = new SamplingFilter[]{
				new UserReviewCountFilter(10, Integer.MAX_VALUE)	
		};
		localSampling(rootDir, objectFilter, userFilter);
		
		/*selected data -> user rating data
		 */
		extractUserRating(rootDir + "/sampling");
		/*selected data -> user keyword
		 */
		extractKeyword(rootDir + "/sampling");
		
		Logger.logInterval = 100;
		splitUserCorr(rootDir + "/sampling/ratings.dat");
	}
}
