/**
 * 
 */
package minhwan.researchCF.machinelearning.model;

import java.util.ArrayList;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 19.
 * @fileName Rating.java
 */
public class Rating{
	String reviewerID;
	double[]  ratings;
	
	/**
	 * @return the reviewerID
	 */
	public String getReviewerID() {
		return reviewerID;
	}
	/**
	 * @param reviewerID the reviewerID to set
	 */
	public void setReviewerID(String reviewerID) {
		this.reviewerID = reviewerID;
	}
	/**
	 * @return the ratings
	 */
	public double[] getRatings() {
		return ratings;
	}
	/**
	 * @param ratings the ratings to set
	 */
	public void setRatings(double[] ratings) {
		this.ratings = ratings;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Rating [reviewerID=" + reviewerID + ", ratings=" + ratings + "]";
	}
}
