package com.alibaba.gongxiang.maxiao.datadiscretization.model;

public class EntropyCutPointPair {
	
	private float entropy;
	private float cutPoint;
	
	
	
	public EntropyCutPointPair(float entropy, float cutPoint) {
		super();
		this.entropy = entropy;
		this.cutPoint = cutPoint;
	}
	public float getEntropy() {
		return entropy;
	}
	public void setEntropy(float entropy) {
		this.entropy = entropy;
	}
	public float getCutPoint() {
		return cutPoint;
	}
	public void setCutPoint(float cutPoint) {
		this.cutPoint = cutPoint;
	}
	
	
	
}
