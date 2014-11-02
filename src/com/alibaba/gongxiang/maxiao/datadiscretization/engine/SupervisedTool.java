package com.alibaba.gongxiang.maxiao.datadiscretization.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.gongxiang.maxiao.datadiscretization.model.Feature2Category;
import com.alibaba.gongxiang.maxiao.datadiscretization.model.Interval;

public abstract class SupervisedTool {

	protected List<Feature2Category> featureCategoryList = null;
	protected Set<Integer> categoryTypeSet = null;
	
	
	//output 
	
	protected List<Interval> mergedIntervalList = null;
	protected List<Float> mergedProportionList = null;
	protected List<Float> mergedCutPointList = null;
	public SupervisedTool(List<Feature2Category> data, Set<Integer> categoryTypeSet) {
		this.featureCategoryList = data;
		this.categoryTypeSet = categoryTypeSet;
		
		
		this.mergedCutPointList = new ArrayList<Float>();
		this.mergedIntervalList = new ArrayList<Interval>();
		this.mergedProportionList = new ArrayList<Float>();
	}

	
	public SupervisedTool(List<Feature2Category> data){
		this.featureCategoryList = data;
	}
	
	public void setData(List<Feature2Category> data){
		this.featureCategoryList = data;
	}
	
	/**
	 * main method
	 */
	public abstract void computeCutIntervals();
	
	public List<Float> getCutPointList() {
		return mergedCutPointList;
	}

	public void setCutPointList(List<Float> cutPointList) {
		this.mergedCutPointList = cutPointList;
	}

	public List<Interval> getMergedIntervalList() {
		return mergedIntervalList;
	}

	public void setMergedIntervalList(List<Interval> mergedIntervalList) {
		this.mergedIntervalList = mergedIntervalList;
	}

	public List<Float> getMergedProportionList() {
		return mergedProportionList;
	}

	public void setMergedProportionList(List<Float> mergedProportionList) {
		this.mergedProportionList = mergedProportionList;
	}
}
