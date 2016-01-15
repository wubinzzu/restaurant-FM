/**
 * 
 */
package minhwan.researchCF.statistic;

import org.junit.Before;
import org.junit.Test;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 13.
 * @fileName UserCorrelationCalTest.java
 */
public class UserCorrelationTest {
	
	UserCorrCal uc;
	
	@Before
	public void setup(){
		uc = new UserCorrCal();
		uc.dataLoad("D:/Research/FM/data/sanf/sampling/ratings_test.dat");
	}
	
	@Test
	public void test(){
		uc.calculateAll();
	}
}
