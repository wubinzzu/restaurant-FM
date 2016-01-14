/**
 * 
 */
package minhwan.researchCF.statistic;

import java.io.IOException;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import minhwan.researchCF.sampler.model.UserRating;
import minhwan.util.IO.FileIOWriter;
import minhwan.util.common.logger.LogType;
import minhwan.util.common.logger.Logger;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 13.
 * @fileName UserCorrelationCalulation.java
 */
public class UserCorrelation {
	UserRating userRating;
	PearsonsCorrelation corr;
	String dataFilePath;
	
	public UserCorrelation(){
		Logger.logInterval = 10000;
	}
	
	public void dataLoad(String dataFilePath){
		this.dataFilePath = dataFilePath;
		userRating = new UserRating(dataFilePath);
		corr = new PearsonsCorrelation();
	}
	
	public void calculateAll(){
		double[][] ratingMatrix = userRating.convert2matrix();
		String[] reviewerIDs = userRating.getReviewerIDs();
		String[] objectIDs = userRating.getObjectIDs();
		
		try {
			FileIOWriter writer = new FileIOWriter(dataFilePath.replace(".dat", ".userCorrelation.dat"), true);
			
			for(int user1Idx = 0; user1Idx < ratingMatrix.length; user1Idx++){
				for(int user2Idx = user1Idx+1; user2Idx < ratingMatrix.length; user2Idx++){					
					double[] user1 = ratingMatrix[user1Idx];
					double[] user2 = ratingMatrix[user2Idx];
					
					double correlation = calculate(user1, user2);
					
					writer.write(
							reviewerIDs[user1Idx] + "\t" + 
							reviewerIDs[user2Idx] + "\t" + 
							correlation);

					Logger.log(LogType.INFO, 
							"Correlation(" + user1Idx + " -- " + user2Idx + 
							") = " + correlation);
				}
			}
			writer.close();
			
		} catch (IOException e) {
			Logger.log(LogType.FATAL, "Creating *.correlation file is failure");
		}
	}
	
	public double calculate(double[] user1, double[] user2){
		return corr.correlation(user1, user2);
	}
	
	public static void main(String[] args){
		UserCorrelation uc;

		uc = new UserCorrelation();
		uc.dataLoad("D:/Research/FM/data/sanf/sampling/ratings.dat");
		
		uc.calculateAll();
	}
}
