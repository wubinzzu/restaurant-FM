/**
 * 
 */
package minhwan.researchCF.sampler.filters;

import org.junit.Before;
import org.junit.Test;

import minhwan.researchCF.sampler.LocalSampler;
import minhwan.researchCF.sampler.filters.restaurant.ObjectReviewCountFilter;
import minhwan.researchCF.sampler.model.DatabaseDataModel;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName SamplingFilter.java
 */
public class SamplingFilterTest {

	String filePath = "D:/Research/FM/data/sanf/2nd-sunday-san-francisco.dat";
	DatabaseDataModel ldm;
	
	@Before
	public void setup(){
		ldm = LocalSampler.data2model(filePath);
	}
	
	@Test
	//reviewCountFilterTest
	public void reviewCountFilter(){
		ObjectReviewCountFilter filter = new ObjectReviewCountFilter(5, 20);
		
		boolean filterResult = filter.filter(ldm);
		
		System.out.println(ldm.getReviews().size());
		System.out.println(filterResult);
	}
}
