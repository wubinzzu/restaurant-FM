/**
 * 
 */
package minhwan.researchCF.sampler.filters.restaurant;

import java.util.ArrayList;

import org.bson.Document;

import minhwan.researchCF.sampler.SamplingFilter;
import minhwan.researchCF.sampler.model.LocalDataModel;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName reviewCnt.java
 */
public class ObjectReviewCountFilter implements SamplingFilter{
	private int min, max;
	
	public ObjectReviewCountFilter(int min, int max){
		this.min = min;
		this.max = max;
	}
	
	public boolean filter(LocalDataModel sm) {
		ArrayList<Document> reviews = sm.getReviews();

		return (reviews.size() >= min &&
				reviews.size() <= max);
	}
}
