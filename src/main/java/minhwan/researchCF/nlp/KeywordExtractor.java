/**
 * 
 */
package minhwan.researchCF.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bson.Document;

import minhwan.researchCF.sampler.model.Cleaner;
import minhwan.util.IO.FileIO;
import minhwan.util.IO.FileIOWriter;
import minhwan.util.IO.FileSystem;
import minhwan.util.common.logger.LogType;
import minhwan.util.common.logger.Logger;
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
	public enum DATA_TYPE{ OBJECT, REVIEW };
	
	private String targetDir, inputDir;

	private Cleaner cleaner;
	private StanfordPipeline stanford;
	
	private TFCal termFreqCounter;
	private FreqDataLoader freqDataLoader;
	
	private int logCnt;
	
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
				new String[]{"NN", "VB"});
//		this.reviewTFcounter = new TFCal(3, TFCal.TARGET.LEMMA,
//				new String[]{"NN", "VB", "CD"});
		this.freqDataLoader = new FreqDataLoader();
		
		this.logCnt = 0;
	}
	
	public void extractTermFreq(String inputDir, DATA_TYPE type) throws IOException{
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
				Logger.log(LogType.INFO, "Generate TF: " + filePath);
			
			switch(type){
				case OBJECT:
					// valid review users
					HashSet<String> validUsers = new HashSet<String>();
					for(String line : FileIO.readEachLine(this.targetDir + "/userList.dat"))
						validUsers.add(line);
					
					String objectKey = FileSystem.getFileName(filePath);
					createObjectDataTF(inputDir + "/../all/" + objectKey + ".dat", validUsers);
					break;
				case REVIEW:
					createReviewDataTF(filePath);
					break;
			}
		}
		
		createDF(tfDir, dfDir);
	}
	
	/* TF */
	private void createObjectDataTF(String filePath, HashSet<String> validUsers) throws IOException{
		String objectID = FileSystem.getFileName(filePath);
		
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
		String reviewerID = FileSystem.getFileName(filePath);
		
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
	
	/* DF */
	public void createDF(String tfDir, String targetDir) throws IOException{
		Logger.log(LogType.INFO, "Generate DF: " + tfDir);
		
		HashMap<String, Integer> tf;
		
		DFCal dfCal = new DFCal();
		for(String filePath : FileSystem.readFileList(tfDir)){			
			tf = freqDataLoader.load(filePath);
			dfCal.updateDF(tf);
		}
		
		HashMap<String, Integer> df = dfCal.getCounter();
		writeFreq(df, targetDir + "/df.dat");
	}

	private void writeFreq(HashMap<String, Integer> freq, String targetFilePath) throws IOException{
		StringBuffer sb = new StringBuffer();
		
		for(String w : freq.keySet()){
			sb.append(w + "\t" + freq.get(w)).append("\n");
		}
		

		FileIOWriter writer = new FileIOWriter(targetFilePath, true);
		writer.write(sb.toString());
		writer.close();
	}

	/* extract keyword */
	public void extractKeyword(String freqDirPath) throws NumberFormatException, IOException{
		Logger.log(LogType.INFO, "extractKeyword : " + freqDirPath);
		
		HashMap<String, Integer> df = freqDataLoader.load(freqDirPath + "/df.dat");
		ArrayList<String> tfFilePaths = FileSystem.readFileList(freqDirPath + "/tf/");
		
		FileSystem.mkdir_path(freqDirPath + "/weight/");
		
		for(String tfFilePath : tfFilePaths){
			Logger.log(LogType.DEBUG, "TF data: " + tfFilePath);
			
			String key = FileSystem.getFileName(tfFilePath);
			HashMap<String, Integer> tf = freqDataLoader.load(tfFilePath);

			StringBuffer sb = new StringBuffer();
			
			for(String term : tf.keySet()){
				sb
				.append(term).append("\t")
				.append(tf.get(term)).append("\t")
				.append(df.get(term)).append("\t")
				.append(termWeight(tf.get(term), df.get(term), tfFilePaths.size())).append("\n");
			}
			
			FileIOWriter writer = new FileIOWriter(freqDirPath + "/weight/" + key + ".dat", false);
			writer.write(sb.toString());
			writer.close();
		}
	}
	
	private double termWeight(int tf, int df, int size){
		return ( (double)tf) * Math.log( ((double)size) / df) ;
	}
}
