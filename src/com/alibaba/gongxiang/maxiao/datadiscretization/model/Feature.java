package com.alibaba.gongxiang.maxiao.datadiscretization.model;

public class Feature implements Comparable<Feature> {
	float feature;
	
	public Feature(float feature) {
		super();
		this.feature = feature;
	}

	public float getFeature() {
		return feature;
	}

	public void setFeature(float feature) {
		this.feature = feature;
	}

	@Override
	public int compareTo(Feature o) {
		
		return this.feature-o.feature < 0 ? -1 : 1;
	}
	
}
