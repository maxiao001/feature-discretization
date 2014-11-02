package com.alibaba.gongxiang.maxiao.datadiscretization.engineImp;

import java.util.List;

import com.alibaba.gongxiang.maxiao.datadiscretization.engine.UnsupervisedTool;
import com.alibaba.gongxiang.maxiao.datadiscretization.model.Feature;

public class EquiDistanceTool extends UnsupervisedTool {

	public EquiDistanceTool(List<Feature> data, int K) {
		super(data, K);
	}

	@Override
	public void computeCutIntervals() {

	}

}
