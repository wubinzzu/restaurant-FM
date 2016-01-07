/**
 * 
 */
package minhwan.resarchCF.nlp;

import java.util.ArrayList;

import minhwan.resarchCF.sampler.DataLoader;
import minhwan.resarchCF.sampler.SamplingModel;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 7.
 * @fileName KeywordExtractor.java
 */
public class KeywordExtractor {
	DataLoader dataloader;
	ArrayList<SamplingModel> data;
	
	public KeywordExtractor(){
		dataloader = new DataLoader();
		data = new ArrayList<SamplingModel>();
	}
	
}
