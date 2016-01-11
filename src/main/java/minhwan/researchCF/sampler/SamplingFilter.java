/**
 * 
 */
package minhwan.researchCF.sampler;

import minhwan.researchCF.sampler.model.DatabaseDataModel;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName samplingFilter.java
 */
public interface SamplingFilter {
	
	public boolean filter(DatabaseDataModel sm);
}
