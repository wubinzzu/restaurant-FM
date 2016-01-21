/**
 * 
 */
package minhwan.researchCF.machinelearning;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.bson.Document;

import minhwan.researchCF.sampler.model.UserRatingModel;
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
public class UserCorrSplitter {
	UserRatingModel userRating;
	PearsonsCorrelation corr;
	String dataFilePath;
	
	public UserCorrSplitter(){
	}
	
	public void dataLoad(String dataFilePath){
		this.dataFilePath = dataFilePath;
		userRating = new UserRatingModel(dataFilePath);
		corr = new PearsonsCorrelation();
	}
	
	public void calculateAll(){
		double[][] ratingMatrix = userRating.convert2matrix();
		String[] reviewerIDs = userRating.getReviewerIDs();
		String[] objectIDs = userRating.getObjectIDs();
		
		try {
			FileSystem.mkdir_path(dataFilePath.replace("ratings.dat", "/correlation-userPair/"));
			
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
								"ratings.dat", "/correlation-userPair/" + user1.size() + ".dat"), true);
						
						
						Document user1Rating = new Document();
						user1Rating.put("reviewerID", reviewerIDs[user1Idx]);
						user1Rating.put("ratings", user1);
						Document user2Rating = new Document();
						user2Rating.put("reviewerID", reviewerIDs[user2Idx]);
						user2Rating.put("ratings", user2);
						
						ArrayList<Document> reviewRatings = new ArrayList<Document>();
						reviewRatings.add(user1Rating);
						reviewRatings.add(user2Rating);
						
						Document f = new Document();
						f.put("correlation", correlation);
						f.put("ratings", reviewRatings);
						f.put("ratingObjects", ratingObjects);
						
						writer.write(f.toJson());
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
		Logger.logInterval = 10000;
		UserCorrSplitter uc;

		uc = new UserCorrSplitter();
		uc.dataLoad("D:/Research/FM/data/sanf-top500/sampling/ratings.dat");
		
		uc.calculateAll();
	}
}
