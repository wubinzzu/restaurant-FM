/**
 * 
 */
package minhwan.researchCF.sampler;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName SamplerTmpDatabase.java
 */
public class UserReviewDatabase {
	
	private HashMap<String, ArrayList<Document>> userReviews;
	
	public UserReviewDatabase(){
		userReviews = new HashMap<String, ArrayList<Document>>();
	}
	
	public void addUserReview(Document review){
		String reviewerID = review.getString("reviewerID");
		
		if(!userReviews.containsKey(reviewerID))
			userReviews.put(reviewerID, new ArrayList<Document>());
		
		userReviews.get(reviewerID).add(review);
	}

	/**
	 * @return the userReviews
	 */
	public HashMap<String, ArrayList<Document>> getUserReviews() {
		return userReviews;
	}

	/**
	 * @param userReviews the userReviews to set
	 */
	public void setUserReviews(HashMap<String, ArrayList<Document>> userReviews) {
		this.userReviews = userReviews;
	}
	
	
}
