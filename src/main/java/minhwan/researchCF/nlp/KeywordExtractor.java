/**
 * 
 */
package minhwan.researchCF.nlp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import org.bson.Document;

import minhwan.researchCF.sampler.model.Cleaner;
import minhwan.util.IO.FileIO;
import minhwan.util.IO.FileIOWriter;
import minhwan.util.IO.FileSystem;
import minhwan.util.nlp.frequency.DFCal;
import minhwan.util.nlp.frequency.FreqDataLoader;
import minhwan.util.nlp.frequency.TFCal;
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
	public static Logger logger = Logger.getLogger(KeywordExtractor.class.getName());
	private int logCnt = 0;
	
	public enum DATA_TYPE{ OBJECT, REVIEW };
	
	private String targetDir, inputDir;

	private Cleaner cleaner;
	private StanfordPipeline stanford;
	
	private TFCal termFreqCounter;
	private FreqDataLoader freqDataLoader;
	
	
	public KeywordExtractor(String targetDir){
		this.targetDir = targetDir;
		
		this.cleaner = new Cleaner();
		this.stanford = new StanfordPipeline(new AnnotatorType[]{
				AnnotatorType.TOKENIZER,
				AnnotatorType.SENTENCE_SPLIT,
				AnnotatorType.POS_TAGGER,
				AnnotatorType.LEMMATIZATION,
		});
		
		this.termFreqCounter = new TFCal(3, TFCal.TARGET.LEMMA,
				new String[]{"NN", "VB", "CD"});
//		this.reviewTFcounter = new TFCal(3, TFCal.TARGET.LEMMA,
//				new String[]{"NN", "VB", "CD"});
		this.freqDataLoader = new FreqDataLoader();
	}
	
	public void execute(String inputDir, DATA_TYPE type) throws IOException{
		this.inputDir = inputDir;
		
		String tfDir = null;
		String dfDir = null;
		
		switch(type){
		case OBJECT:
			this.inputDir = inputDir + "/objects/";
			tfDir = this.targetDir + "/freq/objects/tf/";
			dfDir = this.targetDir + "/freq/objects/";
			break;
		case REVIEW:
			this.inputDir = inputDir + "/users/";
			tfDir = this.targetDir + "/freq/users/tf/";
			dfDir = this.targetDir + "/freq/users/";
			break;
		}

		FileSystem.mkdir_path(tfDir);

		
		for(String filePath : FileSystem.readFileList(this.inputDir)){
			if( logCnt++ % 100 == 0)
//				logger.info("extracting Keyword from file: " + filePath);
				System.out.println("[INFO] Generate TF: " + filePath);
			
			switch(type){
				case OBJECT:
					// valid review users
					HashSet<String> validUsers = new HashSet<String>();
					for(String line : FileIO.readEachLine(this.targetDir + "/userList.dat"))
						validUsers.add(line);
					
					String objectKey = getKey(filePath);
					createObjectDataTF(inputDir + "/../all/" + objectKey + ".dat", validUsers);
					break;
				case REVIEW:
					createReviewDataTF(filePath);
					break;
			}
		}
		
		createDF(tfDir, dfDir);
	}
	
	private void createObjectDataTF(String filePath, HashSet<String> validUsers) throws IOException{
		String objectID = getKey(filePath);
		
		ArrayList<String> lines = FileIO.readEachLine(filePath);
		
		for(int i = 1; i < lines.size(); i++){
			Document d = Document.parse(lines.get(i));
			
			if(validUsers.contains(d.get("reviewerID"))){
				String content = d.getString("content");
				content = cleaner.reviewContent(content);
				
				stanford.execute(content);
				ArrayList<SentLevelModel> result = stanford.getSentLevelResult();
				
				termFreqCounter.updateTF(result);
			}
		}
		
		HashMap<String, Integer> userTF = termFreqCounter.getCounter();
		writeFreq(userTF, targetDir + "/freq/objects/tf/" + objectID + ".dat");
		
		termFreqCounter.flush();
	}
	
	public void createReviewDataTF(String filePath) throws IOException{
		String reviewerID = getKey(filePath);
		
		for(String line : FileIO.readEachLine(filePath)){
			Document review = Document.parse(line);
			String content;
			
			content = review.getString("content");
			content = cleaner.reviewContent(content);
			
			stanford.execute(content);
			ArrayList<SentLevelModel> result = stanford.getSentLevelResult();
			
			termFreqCounter.updateTF(result);
		}
		
		// user tf
		HashMap<String, Integer> userTF = termFreqCounter.getCounter();
		writeFreq(userTF, targetDir + "/freq/users/tf/" + reviewerID + ".dat");
		
		termFreqCounter.flush();
	}
	
	public void createDF(String tfDir, String targetDir) throws IOException{
		System.out.println("[INFO] Generate DF: " + tfDir);
		
		HashMap<String, Integer> tf;
		
		DFCal dfCal = new DFCal();
		for(String filePath : FileSystem.readFileList(tfDir)){
			String key = getKey(filePath);
			
			tf = freqDataLoader.load(filePath);
			dfCal.updateDF(tf);
		}
		
		HashMap<String, Integer> df = dfCal.getCounter();
		writeFreq(df, targetDir + "/df.dat");
	}
	
	private void writeFreq(HashMap<String, Integer> freq, String targetFilePath) throws IOException{
		FileIOWriter writer =
				new FileIOWriter(targetFilePath, true);

		for(String w : freq.keySet()){
			writer.write(w + "\t" + freq.get(w));
		}
		
		writer.close();
	}
	
	private String getKey(String filePath){
		File f = new File(filePath);
		
		return f.getName().split("\\.")[0];
	}
	
	public static void main(String[] args) throws IOException{
		KeywordExtractor ke = new KeywordExtractor("D:/Research/FM/data/sanf/sampling/");
		ke.execute("D:/Research/FM/data/sanf/sampling/", KeywordExtractor.DATA_TYPE.REVIEW);
		ke.execute("D:/Research/FM/data/sanf/sampling/", KeywordExtractor.DATA_TYPE.OBJECT);
	}
}
