package com.alibaba.gongxiang.maxiao.datadiscretization.engineImp;

import java.util.List;

import com.alibaba.gongxiang.maxiao.datadiscretization.engine.UnsupervisedTool;
import com.alibaba.gongxiang.maxiao.datadiscretization.model.Feature;

public class KmeansTool extends UnsupervisedTool {

	public KmeansTool(List<Feature> data, int K) {
		super(data, K);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void computeCutIntervals() {

	}

}
