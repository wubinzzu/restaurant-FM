/**
 * 
 */
package minhwan.researchCF.sampler.model;

import java.util.ArrayList;

import org.junit.Test;

import minhwan.util.common.logger.LogType;
import minhwan.util.common.logger.Logger;

/**
 * @author yuminhwan
 *
 * @createDate 2016. 1. 13.
 * @fileName UserRating.java
 */
public class UserRatingTest {
	
	@Test
	public void test(){
		String dataPath = "D:/Research/FM/data/sanf/sampling/ratings.dat";
		UserRatingModel ur = new UserRatingModel(dataPath);
		
		double[][] matrix = ur.convert2matrix();
		String[] reviewrIDs = ur.getReviewerIDs();
		String[] objectIDs = ur.getObjectIDs();
		
		
		int userCnt = matrix.length;
		int objectCnt = matrix[0].length;
		Logger.log(LogType.DEBUG, "User Cnt: " + userCnt);
		Logger.log(LogType.DEBUG, "Object Cnt: " + objectCnt);
		
		for(int userIdx = 0; userIdx < userCnt; userIdx++){
			Logger.log(LogType.LOAD, reviewrIDs[userIdx]);
			
			for(int objectIdx = 0; objectIdx < objectCnt; objectIdx++){
				System.out.printf("%s %f\n", 
						objectIDs[objectIdx], matrix[userIdx][objectIdx]);
			}
		}
	}
}
