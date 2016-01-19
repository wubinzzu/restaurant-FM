/**
 * 
 */
package minhwan.researchCF.sampler.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import minhwan.util.IO.FileIO;
import minhwan.util.common.logger.LogType;
import minhwan.util.common.logger.Logger;
import minhwan.util.common.model.KeyValue;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 13.
 * @fileName UserRating.java
 */
public class UserRatingModel {
	HashMap<String, ArrayList<KeyValue>> ratings;
	HashMap<String, Integer> objectIdxMap;
	
	String[] reviewrIDs, objectIDs;
	
	public UserRatingModel(String filePath){
		objectIdxMap = new HashMap<String, Integer>();
		ratings   = new HashMap<String, ArrayList<KeyValue>>();
		
		try{
			load(filePath);
		} 
		catch(IOException e){
			Logger.log(LogType.ERROR, "User-Rating data file not exsit : " + filePath);
		}
	}
	
	private void load(String filePath) throws IOException{
		Logger.log(LogType.LOAD, "Load User-Rating data from " + filePath);
		
		String[] ss;
		String reviewerID, objectID, rating;
		int idx = 0;
		
		for(String line : FileIO.readEachLine(filePath)){
			ss = line.split("\t");
			if(ss.length < 3) continue;
			
			reviewerID = ss[0];
			objectID = ss[1];
			rating = ss[2];
			
			if(!ratings.containsKey(reviewerID))
				ratings.put(reviewerID, new ArrayList<KeyValue>());
			
			ratings.get(reviewerID).add(new KeyValue(objectID, rating));
			
			if(!objectIdxMap.containsKey(objectID))
				objectIdxMap.put(objectID, idx++);
		}
		
		Logger.log(LogType.END, "Finish to load User-Rating data from " + filePath);
	}
	
	public double[][] convert2matrix(){
		Logger.log(LogType.START, "Convert user-rating data to 2X2 Matrix");
		
		double[][] matrix = new double[ratings.size()][objectIdxMap.size()];
		
		reviewrIDs = new String[ratings.size()];
		
		int reviewerIdx = 0;
		for(String reviewerID : ratings.keySet()){
			reviewrIDs[reviewerIdx] = reviewerID;
			
			// key : objectID, value : rating
			for(KeyValue reviewRating : ratings.get(reviewerID)){
				int objectIdx = objectIdxMap.get((String)reviewRating.getKey());
				
				matrix[reviewerIdx][objectIdx] = Double.valueOf((String) reviewRating.getValue());
			}
			
			reviewerIdx++;
		}
		
		Logger.log(LogType.END, "Convert user-rating data to 2X2 Matrix");
		
		Logger.log(LogType.START, "Create ObjectIDs array");
		objectIDs = new String[objectIdxMap.size()];
		for(String objectID : objectIdxMap.keySet()){
			objectIDs[objectIdxMap.get(objectID)] = objectID;
		}
		Logger.log(LogType.END, "Create ObjectIDs array");
		
		ratings.clear();
		objectIdxMap.clear();
		return matrix;
	}

	/**
	 * @return the objectIdxMap
	 */
	public HashMap<String, Integer> getObjectIdxMap() {
		return objectIdxMap;
	}

	/**
	 * @return the reviewerIdxMap
	 */
	public String[] getReviewerIDs() {
		return reviewrIDs;
	}
	
	public String[] getObjectIDs(){
		return objectIDs;
	}
}
