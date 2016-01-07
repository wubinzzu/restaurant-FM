/**
 * 
 */
package minhwan.resarchCF.sampler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import minhwan.util.IO.FileIOWriter;
import minhwan.util.database.mongodb.MongoDB;
import minhwan.util.database.mongodb.MongoDBType;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 7.
 * @fileName Sampler.java
 */
public class Sampler {
	static Logger logger = Logger.getLogger(Sampler.class.getName()); 

	public static String ROOT_DIR = "D:\\Research\\FM\\data\\sanf\\";
	
	MongoDB restDB;
	MongoDB reviewDB;
	
	HashSet<String> objectKeys;
	HashMap<String, Integer> reviewCnts;
	
	public Sampler(){
		restDB = new MongoDB(MongoDBType.Yelp_Restaurant);
		reviewDB = new MongoDB(MongoDBType.Yelp_Review);
	}
	
	public void sampling() throws IOException{
		objectKeys = new HashSet<String>();
		reviewCnts = new HashMap<String, Integer>();

		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		
		// CAUTION: address.addressLocality and address.addressRegion are only indexed
		queryMap.put("address.addressLocality", "San Francisco");
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
	
	private void reviewFetch(Document restaurant) throws IOException{
		final String sourceKey = restaurant.getString("sourceKey");
		System.out.println(sourceKey);
		
		final StringBuffer sb = new StringBuffer();
		sb.append(restaurant.toJson()).append("\n");
		
		FindIterable<Document> reviewIter = reviewDB.findAll(new Document("sourceObjectKey", sourceKey));
		
		reviewIter.forEach(new Block<Document>(){
			public void apply(Document t) {
				sb.append(t.toJson()).append("\n");
			}
		});
		
		// write data
		FileIOWriter fiw = new FileIOWriter(ROOT_DIR + sourceKey + ".dat", false);
		fiw.write(sb.toString());
		fiw.close();
	}
	
	public static void main(String[] args) throws IOException{
		Sampler sampler = new Sampler();
		sampler.sampling();
	}
}
