/**
 * 
 */
package minhwan.researchCF.sampler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import minhwan.util.IO.FileIOWriter;
import minhwan.util.IO.FileSystem;
import minhwan.util.common.logger.LogType;
import minhwan.util.common.logger.Logger;
import minhwan.util.database.mongodb.MongoDB;
import minhwan.util.database.mongodb.MongoDBType;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 7.
 * @fileName Sampler.java
 */
public class DatabaseSampler {
	public static String ROOT_DIR = null;
	
	MongoDB restDB;
	MongoDB reviewDB;
	
	HashSet<String> objectKeys;
	HashMap<String, Integer> reviewCnts;
	
	public DatabaseSampler(){
		restDB = new MongoDB(MongoDBType.Yelp_Restaurant);
		reviewDB = new MongoDB(MongoDBType.Yelp_Review);
	}
	
	/*
	 * 
	 */
	public void sampling(HashMap<String, Object> queryMap) throws IOException{
		Logger.log(LogType.START, "Sampling Object : " + queryMap);
		objectKeys = new HashSet<String>();
		reviewCnts = new HashMap<String, Integer>();

		ArrayList<Document> restaurants = restaurantSampling(queryMap);
		
		// find reviews for each restaurant
		reviewDB.connect();
		
		for(final Document restaurant : restaurants)
			reviewFetch(restaurant);
		
		reviewDB.close();
	}
	
	private ArrayList<Document> restaurantSampling(HashMap<String, Object> samplingQuery){
		// find restaurants by address
		final ArrayList<Document> restaurants = new ArrayList<Document>();
		
		restDB.connect();
		
		Document query = MongoDB.hashMap2andQuery(samplingQuery);
		FindIterable<Document> restIter = restDB.findAll(query);
		
		restIter.forEach(new Block<Document>() {
			public void apply(final Document d) {
				restaurants.add(d);
			}
		});
		restDB.close();
		
		return restaurants;
	}
	
	/*
	 * 
	 */
	public void sampling(HashMap<String, Object> queryMap, int topN) throws IOException{
		Logger.log(LogType.START, "Sampling Top" + topN + " Restaurant : " + queryMap);
		objectKeys = new HashSet<String>();
		reviewCnts = new HashMap<String, Integer>();

		ArrayList<Document> restaurants = restaurantSortedSampling(queryMap);
		
		// find reviews for each restaurant
		reviewDB.connect();
		
		for(int i = 0;  i < topN; i++){
			reviewFetch(restaurants.get(i));
		}
		
		reviewDB.close();
	}
	
	private ArrayList<Document> restaurantSortedSampling(HashMap<String, Object> samplingQuery){
		// find restaurants by address
		final ArrayList<Document> restaurants = new ArrayList<Document>();
		
		restDB.connect();
		
		Document query = MongoDB.hashMap2andQuery(samplingQuery);
		FindIterable<Document> restIter = restDB.findAll(query, new Document("reviewCnt", -1));
		
		restIter.forEach(new Block<Document>() {
			public void apply(final Document d) {
				restaurants.add(d);
			}
		});
		restDB.close();
		
		return restaurants;
	} 
	
	/*
	 * 
	 */
	private void reviewFetch(Document restaurant) throws IOException{
		final String sourceKey = restaurant.getString("sourceKey");
		Logger.log(LogType.INFO, "reviewFetch: " + sourceKey);
		
		final StringBuffer sb = new StringBuffer();
		sb.append(restaurant.toJson()).append("\n");
		
		FindIterable<Document> reviewIter = reviewDB.findAll(new Document("sourceObjectKey", sourceKey));
		
		reviewIter.forEach(new Block<Document>(){
			public void apply(Document t) {
				sb.append(t.toJson()).append("\n");
			}
		});
		
		// write data
		String filePath = ROOT_DIR + sourceKey + ".dat";
		FileSystem.mkdir_path(filePath);
		FileIOWriter fiw = new FileIOWriter(filePath, false);
		fiw.write(sb.toString());
		fiw.close();
	}
}
