/**
 * 
 */
package minhwan.researchCF.sampler;

import minhwan.researchCF.sampler.model.LocalDataModel;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName samplingFilter.java
 */
public interface SamplingFilter {
	
	public boolean filter(LocalDataModel sm);
}
