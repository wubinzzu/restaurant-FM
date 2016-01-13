/**
 * 
 */
package minhwan.researchCF.sampler.filters.user;

import minhwan.researchCF.sampler.SamplingFilter;
import minhwan.researchCF.sampler.model.DatabaseDataModel;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 13.
 * @fileName UserIdFilter.java
 */
public class UserIdFilter implements SamplingFilter{

	/* (non-Javadoc)
	 * @see minhwan.researchCF.sampler.SamplingFilter#filter(minhwan.researchCF.sampler.model.DatabaseDataModel)
	 */
	public boolean filter(DatabaseDataModel sm) {
		
		return false;
	}
		
}
