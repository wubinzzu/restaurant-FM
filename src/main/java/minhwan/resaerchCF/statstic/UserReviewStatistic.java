/**
 * 
 */
package minhwan.resaerchCF.statstic;

import java.io.IOException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import minhwan.util.IO.FileIO;
import minhwan.util.IO.FileSystem;
import minhwan.util.common.logger.LogType;
import minhwan.util.common.logger.Logger;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 25.
 * @fileName UserReviewStatistic.java
 */
public class UserReviewStatistic {
	DescriptiveStatistics stats;
	
	public UserReviewStatistic(){
		 stats = new DescriptiveStatistics();
	}
	
	public void run(String rootDir) throws IOException{
		int userCnt = 0;
		
		for(String filePath : FileSystem.readFileList(rootDir)){
			String userID = FileSystem.getFileName(filePath);
			
			int reviewCnt = FileIO.readEachLine(filePath).size();
			
			stats.addValue(reviewCnt);
			
			userCnt++;
			
			Logger.log(LogType.INFO, userID + "\t" + reviewCnt);
		}
		
		System.out.println("User Cnt: " + userCnt);
		System.out.printf(
				"Mean: %f\n"
				+ "Std: %f\n"
				+ "Max:%f\n"
				+ "Min: %f\n",
				stats.getMean(),
				stats.getStandardDeviation(),
				stats.getMax(),
				stats.getMin());
	}
	
	public static void main(String[] args) throws IOException{
		Logger.logInterval = 100;
		UserReviewStatistic urs = new UserReviewStatistic();
		urs.run("D:/Research/FM/data/sanf/sampling/users");
	}
}
