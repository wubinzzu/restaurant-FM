import java.io.IOException;

import org.bson.Document;

import minhwan.researchCF.nlp.KeywordExtractor;
import minhwan.researchCF.sampler.DatabaseSampler;
import minhwan.researchCF.sampler.LocalSampler;
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
		DatabaseSampler.ROOT_DIR = writingDir;
		DatabaseSampler ds = new DatabaseSampler();
		ds.sampling();
	}
	
	public static void localSampling(String rootDir) throws IOException{
		/** local sampling **/
		LocalSampler ls = new LocalSampler(true);

		// add object filters
		ls.addObjectFilter(new ObjectReviewCountFilter(5, Integer.MAX_VALUE));

		// add user filters
		ls.addUserFilter(new UserReviewCountFilter(5, Integer.MAX_VALUE));
		
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
	
	public static void main(String[] args) throws IOException{
		Logger.debugMode = true;
		Logger.logInterval = 1000;
		
		databaseSampling("D:/Research/FM/data/sanf-top50/");
//		localSampling("D:/Research/FM/data/sanf/");
//		extractUserRating("D:/Research/FM/data/sanf/sampling");
//		extractKeyword("D:/Research/FM/data/sanf/sampling");
	}
}
