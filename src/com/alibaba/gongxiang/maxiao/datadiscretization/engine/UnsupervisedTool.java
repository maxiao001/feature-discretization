package com.alibaba.gongxiang.maxiao.datadiscretization.engine;

import java.util.List;

import com.alibaba.gongxiang.maxiao.datadiscretization.model.Feature;

public abstract class UnsupervisedTool {
	
	
	
	List<Feature> featureList = null;

	//unsupervised trainig must have bin number !
	int binNumber = 0;
	public UnsupervisedTool(List<Feature> data,int K) {
		this.featureList = data;
		this.binNumber = K;
	}
	public abstract void computeCutIntervals();
}
