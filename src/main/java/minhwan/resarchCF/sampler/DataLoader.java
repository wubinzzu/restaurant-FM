/**
 * 
 */
package minhwan.resarchCF.sampler;

import java.io.IOException;
import java.util.ArrayList;

import org.bson.BsonDocument;
import org.bson.Document;

import minhwan.util.IO.FileIO;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 7.
 * @fileName DataLoader.java
 */
public class DataLoader {
	
	public SamplingModel load(String filePath) throws IOException{
		String[] lines = FileIO.readEachLine(filePath);
		
		Document objectData = null;
		ArrayList<Document> reviewData = new ArrayList<Document>();
		
		for(int i = 0; i < lines.length;  i++){
			if(i == 0)
				objectData = Document.parse(lines[i]);
			else
				reviewData.add(Document.parse(lines[i]));
		}
		
		return new SamplingModel(objectData, reviewData);
	}
}
