/**
 * 
 */
package minhwan.researchCF.sampler;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import minhwan.researchCF.sampler.filters.restaurant.ObjectReviewCountFilter;
import minhwan.researchCF.sampler.model.LocalDataModel;
import minhwan.util.IO.FileSystem;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName LocalSamplerTest.java
 */
public class LocalSamplerTest {
	String filePath = "D:/Research/FM/data/all/sanf/2nd-sunday-san-francisco.dat";
	String rootDir = "D:/Research/FM/data/sanf/";
	
	LocalSampler ls;
	
	@Before
	public void setup(){
		ls = new LocalSampler(false);
	}
	
//	@Test
	public void loadTest() throws IOException{
		ArrayList<String> filePaths = 
				FileSystem.readFileList("D:/Research/FM/data/sanf");
		
		for(int i = 0; i < filePaths.size(); i++){
			LocalDataModel sm = LocalSampler.data2model(filePaths.get(i));
			System.out.println(sm);
			
			if(i == 3)
				break;
		}
	}
	
	@Test
	public void samplingTest() throws IOException{
		ls.addObjectFilter(new ObjectReviewCountFilter(5, Integer.MAX_VALUE));
		ls.sampling(rootDir);
	}
}
