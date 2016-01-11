/**
 * 
 */
package minhwan.researchCF.nlp;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName KeywordExtractTest.java
 */
public class KeywordExtractTest {
	
	KeywordExtractor ke;
	
	@Before
	public void setup(){
		ke = new KeywordExtractor("./");
	}
	
	@Test
	public void test() throws IOException{
		ke.handleReviewData("D:/Research/FM/data/sanf/sampling/users/__dUOXQ2Pa149eLgsL-tHA.json");
	}
}
