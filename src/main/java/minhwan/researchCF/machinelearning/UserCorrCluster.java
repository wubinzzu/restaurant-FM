/**
 * 
 */
package minhwan.researchCF.machinelearning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import org.bson.Document;

import minhwan.researchCF.machinelearning.model.Rating;
import minhwan.researchCF.machinelearning.model.RatingPairBean;
import minhwan.util.IO.FileIO;
import minhwan.util.IO.FileIOWriter;
import minhwan.util.common.logger.LogType;
import minhwan.util.common.logger.Logger;
import minhwan.util.machinelearning.clustering.ClusteringDataset;
import minhwan.util.machinelearning.clustering.algorithm.hierarchical.HierarchicalClustering;
import minhwan.util.machinelearning.clustering.algorithm.kmeans.KMeansClustering;
import minhwan.util.machinelearning.clustering.linkage.CentroidLinkage;
import minhwan.util.machinelearning.model.FeatureVector;
import minhwan.util.math.distance.CorrelationDistance;
import minhwan.util.math.distance.EuclideDistance;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 15.
 * @fileName UserCorrCluster.java
 */
public class UserCorrCluster {
	private ClusteringDataset dataset;
	private KMeansClustering kmeans;
	private HierarchicalClustering hierarchical;

	ArrayList<String> ratingObjectLists;
	
	public UserCorrCluster() throws IOException{
		this.kmeans = new KMeansClustering(10, new CorrelationDistance());
		this.hierarchical = new HierarchicalClustering(new CentroidLinkage(new EuclideDistance()));
	}
	
	// User rating piar (.json) -> data structure
	public void loadData(String ratingPairFilePath, boolean dumpMode) throws IOException{
		Logger.log(LogType.LOAD, "Load Rating Data from : " + ratingPairFilePath);
		ratingObjectLists = new ArrayList<String>();
		
		HashMap<String, Integer> objectIdxMap = new HashMap<String, Integer>();
		HashMap<String, HashMap<String, Double>> userRatings = new HashMap<String, HashMap<String, Double>>();
		
		ObjectMapper mapper = JsonFactory.create();
		
		for(String line : FileIO.readEachLine(ratingPairFilePath)){
			RatingPairBean bean = mapper.readValue(line, RatingPairBean.class);
			
			ArrayList<String> objects = bean.getRatingObjects();
			for(Rating r : bean.getRatings()){
				String reviewerID = r.getReviewerID();
				
				if(!userRatings.containsKey(reviewerID)){
					userRatings.put(reviewerID, new HashMap<String, Double>());
				}
				
				for(int i = 0; i < r.getRatings().length; i++){
					String objectID = objects.get(i);
					double rating = r.getRatings()[i];
					
					userRatings.get(reviewerID).put(objectID, rating);
					
					if(!objectIdxMap.containsKey(objectID))
						objectIdxMap.put(objectID, objectIdxMap.size());
				}
			}
		}
		
		buildData(objectIdxMap, userRatings, dumpMode, ratingPairFilePath);
	}
	
	private void buildData(HashMap<String, Integer> objectIdxMap, HashMap<String, HashMap<String, Double>> userRatings, boolean dumpMode, String ratingPairFilePath) throws IOException{
		Logger.log(LogType.START, "Build Data "
				+ "\n\t[object size : " + objectIdxMap.size() + "]"
				+ "\n\t[user   size : " + userRatings.size() + "]"
				+ "\n\t[matrix size : " + objectIdxMap.size() * userRatings.size() + "]");
		
		dataset = new ClusteringDataset();
		
		FileIOWriter writer = null;
		if(dumpMode)
			writer = new FileIOWriter(ratingPairFilePath + ".dump", true);
		
		for(String reviewerID : userRatings.keySet()){
			HashMap<String, Double> rating = userRatings.get(reviewerID);
			double[] ratingVector = new double[objectIdxMap.size()];
			
			for(String objectID : rating.keySet()){
				ratingVector[objectIdxMap.get(objectID)] = rating.get(objectID);
			}
									
			if(dumpMode){
				ArrayList<Double> l = new ArrayList<Double>();
				for(double d : ratingVector)
					l.add(d);
				Document result = new Document(reviewerID, l);
				writer.write(result.toJson());
			}
			else{
				dataset.add(new FeatureVector(reviewerID, ratingVector));
			}
		}
		
		if(dumpMode)
			writer.close();
	}
	
	public void clustering(String path) throws IOException{
//		kmeans.clustering(dataset);
		hierarchical.clustering(dataset);
		
		String history = hierarchical.getDataset().getMergeHistoryDump();
		FileIOWriter fiw = new FileIOWriter(path, false);		
		fiw.write(history);
		fiw.close();
	}
	
	public static void main(String[] args) throws IOException{
		Logger.debugMode = true;
		Logger.logInterval = 40;
		
		int fileNum = 37;
		String ratingFilePath = "D:/Research/FM/data/sanf/sampling/correlation-userPair/" + fileNum + ".dat";
		UserCorrCluster ucc = new UserCorrCluster();

//		ucc.loadData(ratingFilePath, true);
		
		ucc.loadData(ratingFilePath, false);
		ucc.clustering("C:/Users/yuminhwan/workspace/minhwan-util/python/dendrogram/" + fileNum + ".dat");
	}
}
