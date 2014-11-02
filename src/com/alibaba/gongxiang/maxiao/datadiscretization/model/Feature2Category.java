package com.alibaba.gongxiang.maxiao.datadiscretization.model;

public class Feature2Category implements Comparable<Feature2Category>{
	
	float feature;
	int category;
	
	
	public Feature2Category(float feature, int category) {
		super();
		this.feature = feature;
		this.category = category;
	}
	public float getFeature() {
		return feature;
	}
	public void setFeature(float feature) {
		this.feature = feature;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	@Override
	public int compareTo(Feature2Category o) {
		
		return (this.feature-o.feature) < 0 ? -1 : (this.feature==o.feature)?0:1;
	}
	
	
}
