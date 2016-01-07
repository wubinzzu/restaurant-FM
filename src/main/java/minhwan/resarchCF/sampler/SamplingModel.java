/**
 * 
 */
package minhwan.resarchCF.sampler;

import java.util.ArrayList;

import org.bson.Document;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 7.
 * @fileName DataModel.java
 */
public class SamplingModel {
	Document object;
	ArrayList<Document> reviews;
	
	
	/**
	 * @param object
	 * @param reviews
	 */
	public SamplingModel(Document object, ArrayList<Document> reviews) {
		super();
		this.object = object;
		this.reviews = reviews;
	}
	/**
	 * @return the object
	 */
	public Document getObject() {
		return object;
	}
	/**
	 * @param object the object to set
	 */
	public void setObject(Document object) {
		this.object = object;
	}
	/**
	 * @return the reviews
	 */
	public ArrayList<Document> getReviews() {
		return reviews;
	}
	/**
	 * @param reviews the reviews to set
	 */
	public void setReviews(ArrayList<Document> reviews) {
		this.reviews = reviews;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SamplingModel [object=" + object + ", reviews=" + reviews + "]";
	}
	
	
}
