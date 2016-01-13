/**
 * 
 */
package minhwan.researchCF.nlp;

import java.io.IOException;

import org.junit.Test;

import minhwan.researchCF.sampler.LocalSampler;
import minhwan.researchCF.sampler.filters.restaurant.ObjectReviewCountFilter;
import minhwan.researchCF.sampler.filters.user.UserReviewCountFilter;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName KeywordExtractTest.java
 */
public class KeywordExtractTest {
	
	KeywordExtractor ke;
	
//	@Before
	public void setup(){
		ke = new KeywordExtractor("D:/Research/FM/data/sanf/sampling/");
	}
	
//	@Test
	public void test() throws IOException{
		ke.createReviewDataTF("D:/Research/FM/data/sanf/sampling/users/__dUOXQ2Pa149eLgsL-tHA.json");
	}
	
	@Test
	public void samplingAndExtract() throws IOException{
		String rootDir = "D:/Research/FM/data/sanf/";

		LocalSampler ls = new LocalSampler(true);

		// add object filters
		ls.addObjectFilter(new ObjectReviewCountFilter(5, Integer.MAX_VALUE));

		// add user filters
		ls.addUserFilter(new UserReviewCountFilter(5, Integer.MAX_VALUE));
		
		ls.sampling(rootDir);
		
		KeywordExtractor ke = new KeywordExtractor("D:/Research/FM/data/sanf/sampling/");
		ke.extractTermFreq("D:/Research/FM/data/sanf/sampling/", KeywordExtractor.DATA_TYPE.REVIEW);
		ke.extractTermFreq("D:/Research/FM/data/sanf/sampling/", KeywordExtractor.DATA_TYPE.OBJECT);
	}
}
