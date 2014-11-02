package com.alibaba.gongxiang.maxiao.datadiscretization.engineImp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.gongxiang.maxiao.datadiscretization.engine.SupervisedTool;
import com.alibaba.gongxiang.maxiao.datadiscretization.model.EntropyCutPointPair;
import com.alibaba.gongxiang.maxiao.datadiscretization.model.Feature2Category;
import com.alibaba.gongxiang.maxiao.datadiscretization.model.Interval;
import com.alibaba.gongxiang.maxiao.datadiscretization.tool.XiaoMaLogger;

public class InformationEntropyTool extends SupervisedTool {
	
	/**
	 * split configuration
	 */
	//entropy threshold , in some situations use threshold to manage the stop criterion
	static float infoEntropyThreshold = 0.2f;
	
	//proportion of the whole samples of single interval(if the instances of interval > this_value means that this interval can be divided)
	static float minimumInstancesPerInterval = 0.1f;
	
	//split number also interval number upper bound
	static int splitNumberLimit = 10;
	
	//split the start interval into small units,the minimum compute unit 
	static int unitNumber = 1000;

	//computed by the unit number = whole size/unitNumber
	static float minimumUnitSize = 0.0f;
	
	
	
	/**
	 * merge interval configuration
	 */
	//when the interval's sample size < this bound,then merge it to left or right interval
	static float singleMergedIntervalLowBound = (float) (1.0/(splitNumberLimit));
	
	//merge to left or  right,that is a question.
	static boolean invalidIntervalMergeToRightInterval = true;//or left:false
	
	
	
	//the info entropy that computing entropy can't reach the value
	private static float EntropyUpperBound = 1000;
	//private static XiaoMaLogger logger = new XiaoMaLogger("data/output.txt",false);
	
	
	public InformationEntropyTool(List<Feature2Category> data, Set<Integer> categoryTypeSet) {
		super(data,categoryTypeSet);

	}

	@Override
	public void computeCutIntervals() {
		

		ArrayList<Float> cutList = new ArrayList<Float>();
		
		//sort 
		Collections.sort(this.featureCategoryList);
		
		int startIndex = 0,endIndex = this.featureCategoryList.size()-1;
		
		
		this.minimumUnitSize = (this.featureCategoryList.get(endIndex).getFeature()-this.featureCategoryList.get(startIndex).getFeature())/unitNumber;
		
		
		System.out.println("minimum Unit Size:"+this.minimumUnitSize);
		
		
		//recursiveFindCutPoint(cutList,startIndex,endIndex);
		
		iterateFindCutPoint(cutList,startIndex,endIndex);
		mergeCutPointIntoIntervalList(cutList);
		
	//	logger.close();
		
		
	}
	/**
	 *	merge init split result into large valid intervals,
	 *	include (1)init previous result
	 *	and 	(2)merge these intervals based on init proportion result,when the proportion of an interval is too low,then merge it to adj large interval 
	 * @param cutList
	 */
	private void mergeCutPointIntoIntervalList(List<Float> cutList) {
		Collections.sort(cutList);
		
		//--------------------------below code is to init the previous cut point list,proportionList and intervalList-----------------
		//add last cutpoint to make sure that end of last point contains all value
		cutList.add(this.featureCategoryList.get(this.featureCategoryList.size()-1).getFeature()+1);
		List<Interval> initIntervalList = new ArrayList<Interval>();
		List<Float> initProportionList = new ArrayList<Float>();
		
		int newIntervalEnd = -1;
		int newIntervalStart = 0;
		for(int i = 0;i < cutList.size();i ++){
			float currentPoint = cutList.get(i);
			newIntervalEnd=newIntervalStart = newIntervalEnd+1;
			while(newIntervalEnd < this.featureCategoryList.size() && this.featureCategoryList.get(newIntervalEnd).getFeature() < currentPoint){
				newIntervalEnd++;
			}
			newIntervalEnd--;
			Interval newInterval = new Interval(newIntervalStart,newIntervalEnd);
			float proportion  = (float)(newInterval.getEnd()-newInterval.getStart())/this.featureCategoryList.size();
			initIntervalList.add(newInterval);
			initProportionList.add(proportion);
		}
		System.out.println(cutList.toString());
		System.out.println(initIntervalList.toString());
		System.out.println(initProportionList.toString());
		
		//-----------------------------------------------//below are merge process-----------------------------------------------
		//find the first valid block
		int firstindex  = 0;
		float sumProportion = 0.0f;
		while(initProportionList.get(firstindex) < singleMergedIntervalLowBound){
			sumProportion += initProportionList.get(firstindex);
			firstindex++;
		}
		sumProportion += initProportionList.get(firstindex);
		Interval prevInterval = new Interval(initIntervalList.get(0).getStart(),initIntervalList.get(firstindex).getEnd());
		
		this.mergedIntervalList.add(prevInterval);
		this.mergedProportionList.add(sumProportion);
		this.mergedCutPointList.add(cutList.get(firstindex));
	//	System.out.println(this.mergedProportionList.toString());
		for(int i = firstindex+1;i < initProportionList.size();i++){
			
			if(invalidIntervalMergeToRightInterval){
				int currentStartIndex = i;
				//below are the code that : the invalid interval merge to RIGHT big interval;
				float singleSum = 0.0f;
				while(i < initProportionList.size() && initProportionList.get(i) < singleMergedIntervalLowBound){
					singleSum += initProportionList.get(i);
					i++;
				}
				if(i != initProportionList.size()){
					singleSum += initProportionList.get(i);
					Interval newInterval = new Interval(initIntervalList.get(currentStartIndex).getStart(),initIntervalList.get(i).getEnd());
					
					this.mergedIntervalList.add(newInterval);
					this.mergedProportionList.add(singleSum);
					this.mergedCutPointList.add(cutList.get(i));
				}else{
					//when the i reach the end and still some little intervals have not be merged,just add it to previous valid interval
					int currentSize = this.mergedIntervalList.size();
					this.mergedIntervalList.get(currentSize-1).setEnd(initIntervalList.get(i).getEnd());
					this.mergedProportionList.set(currentSize-1, this.mergedProportionList.get(currentSize-1)+initProportionList.get(i));
					this.mergedCutPointList.set(currentSize-1,cutList.get(i));
				}
			}
			else{
				//below are the code that : the invalid interval merge to LEFT big interval;
				if (initProportionList.get(i) < singleMergedIntervalLowBound) {
					prevInterval.setEnd(initIntervalList.get(i).getEnd());

					this.mergedCutPointList.set(
							this.mergedCutPointList.size() - 1, cutList.get(i));
					int currentProportionSize = this.mergedProportionList
							.size();
					this.mergedProportionList.set(
							currentProportionSize - 1,
							this.mergedProportionList
									.get(currentProportionSize - 1)
									+ initProportionList.get(i));

				} else {
					prevInterval = new Interval(initIntervalList.get(i)
							.getStart(), initIntervalList.get(i).getEnd());
					this.mergedIntervalList.add(prevInterval);
					this.mergedCutPointList.add(cutList.get(i));
					this.mergedProportionList.add(initProportionList.get(i));
				}
				 
			}
		}
		System.out.println("after merge:");
		System.out.println(this.mergedCutPointList.toString());
		System.out.println(this.mergedIntervalList.toString());
		System.out.println(this.mergedProportionList.toString());
		
	}

	private void iterateFindCutPoint(ArrayList<Float> cutList, int startIndex,
			int endIndex) {
		Map<Interval,EntropyCutPointPair> intervalToMinEntropyMap = new HashMap<Interval,EntropyCutPointPair>();
		intervalToMinEntropyMap.put(new Interval(startIndex,endIndex),null);
		for(int i = 0;i < splitNumberLimit-1;i++){

			Interval toCutInterval = null;
			float minEntropyOfAllIntervals = EntropyUpperBound;
			float minCutPointOfAllIntervals = 0.0f;
			Set<Entry<Interval,EntropyCutPointPair>> set = intervalToMinEntropyMap.entrySet();
			for(Entry<Interval,EntropyCutPointPair> entry : set){
				
				Interval currentInterval = entry.getKey();
//				this.logger.println(currentInterval.toString());
				//if the value is null, then compute it to avoid repeated compute
				if(entry.getValue() == null){
					
					float minEntropy = EntropyUpperBound;
					float minCutPoint = 0.0f;
					float minValue = this.featureCategoryList.get(currentInterval.getStart()).getFeature();
					float maxValue = this.featureCategoryList.get(currentInterval.getEnd()).getFeature();
					for(float cutPoint = minValue+1*this.minimumUnitSize;cutPoint < maxValue-this.minimumUnitSize;cutPoint += this.minimumUnitSize){
						float tempEntropy = caculateInfoEntropyCutInThisPoint(cutPoint,startIndex,endIndex);
						if(tempEntropy < minEntropy){
							minEntropy = tempEntropy;
							minCutPoint = cutPoint;
						}
//						this.logger.println(cutPoint+" : "+tempEntropy);
					}
					entry.setValue(new EntropyCutPointPair(minEntropy,minCutPoint));
				}
				if(entry.getValue().getEntropy() < minEntropyOfAllIntervals){
					//reset the min entropy of all intervals
					minEntropyOfAllIntervals = entry.getValue().getEntropy();
					minCutPointOfAllIntervals = entry.getValue().getCutPoint();
					toCutInterval = entry.getKey();
				}
			}
			
			//split the selected interval;
			if(toCutInterval != null){
//				this.logger.println("GET! DIVIDE : "+toCutInterval.toString());
				
				//add cut point 
				cutList.add(minCutPointOfAllIntervals);
				int cutIndex  = Collections.binarySearch(this.featureCategoryList,new Feature2Category(minCutPointOfAllIntervals,0));
				if(cutIndex < 0){
					cutIndex = -(cutIndex+1);
				}
				
				Interval newLeftInterval = new Interval(toCutInterval.getStart(),cutIndex-1);
				Interval newRightInterval = new Interval(cutIndex,toCutInterval.getEnd());
				
				//check the interval stop criterion(current situation : the interval sample size must > a setting bound)
				if(checkIntervalStopCriterion(newLeftInterval)){
					intervalToMinEntropyMap.put(new Interval(toCutInterval.getStart(),cutIndex-1), null);
				}
				
				if(checkIntervalStopCriterion(newRightInterval)){
					intervalToMinEntropyMap.put(new Interval(cutIndex,toCutInterval.getEnd()), null);
				}

				intervalToMinEntropyMap.remove(toCutInterval);
			}
			
		}
		
		
	}

	/**
	 * check and judge that whether the interval continue divide into parts based on infoEntropy
	 * can use one of following reasons:
	 * (1) the samples number of current interval reach a bound;
	 * (2) the number if current cut interval number reach a bound;
	 * (3) info gain on every cut point of the interval are the same(this is really cost,if use this,
	 * 		the iterate find cut point logic should be changed to avoid repeat compute)
	 * (4) all samples of current interval belong to the same category
	 * @param interval
	 * @return
	 */
	private boolean checkIntervalStopCriterion(Interval interval) {

			int intervalSize = interval.getEnd()-interval.getStart()+1;
			if(intervalSize < this.minimumInstancesPerInterval*this.featureCategoryList.size()){
				return false;
			}
			return true;
	}

	/**
	 * Pay attention that use recursive cut point only if <strong>the correlation between the feature and y is strong</strong>
	 * if not use the iterate method to cut point or throw away the useless feature.
	 */
	private void recursiveFindCutPoint(ArrayList<Float> cutList,
			int startIndex, int endIndex) {
	
		if(startIndex >= endIndex || cutList.size() == (splitNumberLimit-1)) return;
		//choose EntropyUpperBound because the caculated entropy can't  reach the value theoratically;
		float minEntropy = EntropyUpperBound;
		float minCutPoint = 0.0f;
	
		float minValue = this.featureCategoryList.get(startIndex).getFeature();
		float maxValue = this.featureCategoryList.get(endIndex).getFeature();
		for(float cutPoint = minValue+1*this.minimumUnitSize;cutPoint < maxValue-this.minimumUnitSize;cutPoint += this.minimumUnitSize){
			float tempEntropy = caculateInfoEntropyCutInThisPoint(cutPoint,startIndex,endIndex);
			if(tempEntropy < minEntropy){
				minEntropy = tempEntropy;
				minCutPoint = cutPoint;
			}
		}
		if(minEntropy < infoEntropyThreshold || minEntropy==EntropyUpperBound) return;
		cutList.add(minCutPoint);
		int cutIndex  = Collections.binarySearch(this.featureCategoryList,new Feature2Category(minCutPoint,0));
		if(cutIndex < 0){
			cutIndex = -(cutIndex+1);
		}
		//recursive invoke cut function
	
		recursiveFindCutPoint(cutList, startIndex, cutIndex-1);
		recursiveFindCutPoint(cutList, cutIndex, endIndex);
		
	}
	
	/**
	 * 
	 * @param cutPoint
	 * @param startIndex
	 * @param endIndex
	 * @param writer 
	 * @return the current Information Entropy
	 */
	private float caculateInfoEntropyCutInThisPoint(float cutPoint,
			int startIndex, int endIndex) {
		int cutIndex = Collections.binarySearch(this.featureCategoryList, new Feature2Category(cutPoint,0));
		
		if(cutIndex < 0){
			cutIndex = -(cutIndex+1);
		}
		
		
		//left part startIndex~cutIndex-1
		//right part cutIndex~endIndex
		int wholeSize = endIndex-startIndex+1;
		
		float leftEntropy = getSinglePartEntropy(startIndex,cutIndex-1);
		float rightEntropy = getSinglePartEntropy(cutIndex,endIndex);
		
		return ((float)(cutIndex-startIndex)/(float)wholeSize)*leftEntropy
				+((float)(endIndex-cutIndex+1)/(float)wholeSize)*rightEntropy;
		
	}

	private float getSinglePartEntropy(int partStart, int partEnd) {
		
		int partSize = partEnd-partStart+1;
		float result = 0.0f;
		Map<Integer,Integer> categoryToCountMap = new HashMap<Integer,Integer>(this.categoryTypeSet.size());
		for(Integer type:this.categoryTypeSet){
			categoryToCountMap.put(type, 0);
		}
		for(int i = partStart;i <= partEnd;i++){
			int category = this.featureCategoryList.get(i).getCategory();
			int prevCount = categoryToCountMap.get(category);
			categoryToCountMap.put(category, prevCount+1);
		}
		
		Set<Entry<Integer,Integer>> set = categoryToCountMap.entrySet();
		for(Entry<Integer,Integer> entry : set){
			float proportion = (float)entry.getValue()/partSize;
			//judge proportion because of that when proportion is zero,the Math.log(proportion) returns NaN~
			result += (-1.0)*(proportion)*(proportion == 0 ? 0 : (Math.log(proportion))/Math.log(2));
		}
		
		return result;
	}
	




}
