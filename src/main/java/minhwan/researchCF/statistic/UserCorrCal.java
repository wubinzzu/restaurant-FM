/**
 * 
 */
package minhwan.researchCF.statistic;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import minhwan.researchCF.sampler.model.UserRating;
import minhwan.util.IO.FileIOWriter;
import minhwan.util.IO.FileSystem;
import minhwan.util.common.logger.LogType;
import minhwan.util.common.logger.Logger;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 13.
 * @fileName UserCorrelationCalulation.java
 */
public class UserCorrCal {
	UserRating userRating;
	PearsonsCorrelation corr;
	String dataFilePath;
	
	public UserCorrCal(){
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
			FileSystem.mkdir_path(dataFilePath.replace("ratings.dat", "/correlation/"));
			
			for(int user1Idx = 0; user1Idx < ratingMatrix.length; user1Idx++){
				for(int user2Idx = user1Idx+1; user2Idx < ratingMatrix.length; user2Idx++){
					ArrayList<Double> user1 = new ArrayList<Double>();
					ArrayList<Double> user2 = new ArrayList<Double>();
					ArrayList<String> ratingObjects = new ArrayList<String>();
					
					for(int ratingIdx = 0; ratingIdx < ratingMatrix[0].length; ratingIdx++){
						double rating1 = ratingMatrix[user1Idx][ratingIdx];
						double rating2 = ratingMatrix[user2Idx][ratingIdx];
						
						if(rating1 != 0 && rating2 != 0){
							user1.add(ratingMatrix[user1Idx][ratingIdx]);
							user2.add(ratingMatrix[user2Idx][ratingIdx]);
							ratingObjects.add(objectIDs[ratingIdx]);
						}
					}
					
					if(user1.size() > 5){
						double correlation = calculate(user1, user2);

						FileIOWriter writer = new FileIOWriter(dataFilePath.replace(
								"ratings.dat", "/correlation/" + user1.size() + ".dat"), true);
						
						StringBuffer sb = new StringBuffer();
						
						sb
						.append(reviewerIDs[user1Idx]).append("\t")
						.append(reviewerIDs[user2Idx]).append("\t")
						.append(ratingObjects).append("\t")
						.append(correlation);
						writer.write(sb.toString());
						writer.close();
						
						Logger.log(LogType.INFO, 
								"Correlation(" + user1Idx + "/" + user2Idx + 
								") " + user1.size() + " = " + correlation);
					}
				}
			}
			
		} catch (IOException e){
			Logger.log(LogType.FATAL, "Creating *.correlation file is failure");
		}
	}
	
	public double calculate(double[] user1, double[] user2){
		return corr.correlation(user1, user2);
	}
	
	public double calculate(ArrayList<Double> user1, ArrayList<Double> user2){
		double[] v1 = new double[user1.size()];
		double[] v2 = new double[user2.size()];

		for(int i = 0; i < user1.size(); i++){
			v1[i] = user1.get(i);
			v2[i] = user2.get(i);
		}
		
		return calculate(v1, v2);
	}
	
	public static void main(String[] args){
		UserCorrCal uc;

		uc = new UserCorrCal();
		uc.dataLoad("D:/Research/FM/data/sanf/sampling/ratings.dat");
		
		uc.calculateAll();
	}
}
