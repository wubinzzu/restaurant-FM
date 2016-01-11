/**
 * 
 */
package minhwan.researchCF.sampler.filters.user;

import minhwan.researchCF.sampler.SamplingFilter;
import minhwan.researchCF.sampler.model.LocalDataModel;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName ReviewCountFilter.java
 */
public class UserReviewCountFilter implements SamplingFilter{
	private int min, max;
	
	public UserReviewCountFilter(int min, int max){
		this.min = min;
		this.max = max;
	}
	
	/* (non-Javadoc)
	 * @see minhwan.researchCF.sampler.SamplingFilter#filter(minhwan.researchCF.sampler.model.LocalDataModel)
	 */
	public boolean filter(LocalDataModel sm) {
		return (sm.getReviews().size() >= min &&
				sm.getReviews().size() <= max);
	}
}
