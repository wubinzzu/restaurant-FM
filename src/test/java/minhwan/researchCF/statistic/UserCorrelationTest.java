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
	
	UserCorrelation uc;
	
	@Before
	public void setup(){
		uc = new UserCorrelation();
		uc.dataLoad("D:/Research/FM/data/sanf/sampling/ratings.dat");
	}
	
	@Test
	public void test(){
		uc.calculateAll();
	}
}
