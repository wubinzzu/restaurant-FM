/**
 * 
 */
package minhwan.researchCF.sampler.model;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName Cleaner.java
 */
public class Cleaner {
	
	public String reviewContent(String content){
		content = content.replace("\\n \\n", "\n");
		content = content.replace("\\n", "\n");
		content = content.trim();
		
		return content;
	}
}
