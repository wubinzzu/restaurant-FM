/**
 * 
 */
package minhwan.researchCF.machinelearning.model;

import java.util.ArrayList;

import org.bson.Document;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 19.
 * @fileName RatingPairBean.java
 */
public class RatingPairBean {
	double correlation;
	ArrayList<String> ratingObjects;
	ArrayList<Rating> ratings;
	
	

	/**
	 * @return the correlation
	 */
	public double getCorrelation() {
		return correlation;
	}

	/**
	 * @param correlation the correlation to set
	 */
	public void setCorrelation(double correlation) {
		this.correlation = correlation;
	}

	/**
	 * @return the ratingObjects
	 */
	public ArrayList<String> getRatingObjects() {
		return ratingObjects;
	}

	/**
	 * @param ratingObjects the ratingObjects to set
	 */
	public void setRatingObjects(ArrayList<String> ratingObjects) {
		this.ratingObjects = ratingObjects;
	}

	/**
	 * @return the ratings
	 */
	public ArrayList<Rating> getRatings() {
		return ratings;
	}

	/**
	 * @param ratings the ratings to set
	 */
	public void setRatings(ArrayList<Rating> ratings) {
		this.ratings = ratings;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RatingPairBean [correlation=" + correlation + ", ratingObjects=" + ratingObjects + ", ratings="
				+ ratings + "]";
	}
	
	
}
