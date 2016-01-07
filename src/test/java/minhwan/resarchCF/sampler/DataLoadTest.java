/**
 * 
 */
package minhwan.resarchCF.sampler;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 7.
 * @fileName DataLoadTest.java
 */
public class DataLoadTest {

	String filePath = "D:/Research/FM/data/sanf-sampling/data/2nd-sunday-san-francisco.dat";
	DataLoader dl;
	
	@Before
	public void setup(){
		dl = new DataLoader();
	}
	
	@Test
	public void loadTest() throws IOException{
		SamplingModel sd = dl.load(filePath);
		System.out.println(sd);
	}
}
