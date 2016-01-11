/**
 * 
 */
package minhwan.researchCF.nlp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bson.Document;

import minhwan.researchCF.sampler.model.Cleaner;
import minhwan.util.IO.FileIO;
import minhwan.util.IO.FileSystem;
import minhwan.util.nlp.frequency.TFCalculator;
import minhwan.util.nlp.model.SentLevelModel;
import minhwan.util.nlp.preprocessing.stanford.AnnotatorType;
import minhwan.util.nlp.preprocessing.stanford.StanfordPipeline;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 7.
 * @fileName KeywordExtractor.java
 */
public class KeywordExtractor {
	public enum DATA_TYPE{ OBJECT, REVIEW };
	
	private String targetDir, inputDir;
	
	private Cleaner cleaner;
	private StanfordPipeline stanford;
	
	public KeywordExtractor(String targetDir){
		this.targetDir = targetDir;
		
		this.cleaner = new Cleaner();
		this.stanford = new StanfordPipeline(new AnnotatorType[]{
				AnnotatorType.TOKENIZER,
				AnnotatorType.SENTENCE_SPLIT,
				AnnotatorType.POS_TAGGER,
				AnnotatorType.LEMMATIZATION,
		});
	}
	
	public void execute(String inputDir, DATA_TYPE type) throws IOException{
		this.inputDir = inputDir;
		
		for(String filePath : FileSystem.readFileList(inputDir)){
			switch(type){
				case OBJECT:
					handleObjectData(filePath);
					break;
				case REVIEW:
					handleReviewData(filePath);
					break;
			}
		}
	}
	
	private void handleObjectData(String filePath){
		
	}
	
	public void handleReviewData(String filePath) throws IOException{
		String reviewKey = getKey(filePath);

		TFCalculator tfc = new TFCalculator();
		for(String line : FileIO.readEachLine(filePath)){
			Document review = Document.parse(line);
			String content;
			
			content = review.getString("content");
			content = cleaner.reviewContent(content);
			
			stanford.execute(content);
			ArrayList<SentLevelModel> result = stanford.getSentLevelResult();
			
			tfc.calculateTF(result, TFCalculator.TARGET.LEMMA, TFCalculator.GRAM.TRI);
		}
		System.out.println(reviewKey);
		System.out.println(tfc.getCounter());
	}
	
	private String getKey(String filePath){
		File f = new File(filePath);
		
		return f.getName();
	}
}
