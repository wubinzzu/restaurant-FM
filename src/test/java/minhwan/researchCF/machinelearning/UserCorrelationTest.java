/**
 * 
 */
package minhwan.researchCF.machinelearning;

import org.junit.Before;
import org.junit.Test;

import minhwan.researchCF.machinelearning.UserCorrSplitter;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 13.
 * @fileName UserCorrelationCalTest.java
 */
public class UserCorrelationTest {
	
	UserCorrSplitter uc;
	
	@Before
	public void setup(){
		uc = new UserCorrSplitter();
		uc.dataLoad("D:/Research/FM/data/sanf/sampling/ratings_test.dat");
	}
	
	@Test
	public void test(){
		uc.calculateAll();
		
		
	}
}
