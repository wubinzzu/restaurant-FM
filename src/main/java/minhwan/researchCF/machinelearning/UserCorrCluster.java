/**
 * 
 */
package minhwan.researchCF.machinelearning;

import java.io.IOException;
import java.util.ArrayList;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

import minhwan.researchCF.machinelearning.model.Rating;
import minhwan.researchCF.machinelearning.model.RatingPairBean;
import minhwan.util.IO.FileIO;
import minhwan.util.common.logger.Logger;
import minhwan.util.machinelearning.clustering.ClusteringDataset;
import minhwan.util.machinelearning.clustering.algorithm.hierarchical.HierarchicalClustering;
import minhwan.util.machinelearning.clustering.algorithm.kmeans.KMeansClustering;
import minhwan.util.machinelearning.clustering.linkage.CentroidLinkage;
import minhwan.util.machinelearning.model.FeatureVector;
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
	
	public UserCorrCluster(String ratingPairFilePath) throws IOException{
		this.kmeans = new KMeansClustering(10, new EuclideDistance());
		this.hierarchical = new HierarchicalClustering(new CentroidLinkage(new EuclideDistance()));
		
		loadData(ratingPairFilePath);
	}
	
	private void loadData(String ratingPairFilePath) throws IOException{
		dataset = new ClusteringDataset();
		ratingObjectLists = new ArrayList<String>();
		
		ObjectMapper mapper = JsonFactory.create();
		for(String line : FileIO.readEachLine(ratingPairFilePath)){
			RatingPairBean bean = mapper.readValue(line, RatingPairBean.class);
			
			for(Rating r : bean.getRatings()){
				FeatureVector fv = new FeatureVector(r.getReviewerID(), r.getRatings());
				this.dataset.add(fv);
			}
		}
	}
	
	public void clustering(){
//		kmeans.clustering(dataset);
		hierarchical.clustering(dataset);
	}
	
	public static void main(String[] args) throws IOException{
		Logger.debugMode = true;
		Logger.logInterval = 1;
		
		String ratingFilePath = "D:/Research/FM/data/sanf/sampling/correlation-userPair-test/6.dat";
		UserCorrCluster ucc = new UserCorrCluster(ratingFilePath);
		
		ucc.clustering();
	}
}
