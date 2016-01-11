/**
 * 
 */
package minhwan.researchCF.sampler.model;

import java.io.IOException;
import java.util.ArrayList;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import minhwan.util.IO.FileIO;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 11.
 * @fileName CleanerTest.java
 */
public class CleanerTest {
	Cleaner c;
	
	@Before
	public void setup(){
		c = new Cleaner();
	}
	
	@Test
	public void reviewContentTest() throws IOException{
		String filePath = "D:/Research/FM/data/sanf/sampling/users/__05rytNjsye9MBhqB0DMA.json";
		
		ArrayList<String> reviews = FileIO.readEachLine(filePath);
		
		for(String reviewStr : reviews){
			Document review = Document.parse(reviewStr);
			String content = review.getString("content");
			System.out.println(content);
			System.out.println("-----");
			System.out.println(c.reviewContent(content));
			System.out.println("////////////////////////");
		}
	}
}
