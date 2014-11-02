package com.alibaba.gongxiang.maxiao.datadiscretization.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.gongxiang.maxiao.datadiscretization.model.Feature2Category;

public class CorrelationCaculateMain {

	static String csvPath = "data/data.csv";
	static String featureName = "staytime_max1";

	/*
	 * Pay ATTENTION THAT !!!!ONLY ACCEPT Integer category input format !!!!!!!!!!!!!!!!
	 */
	static String categoryName = "trade_cnt";
	static boolean binaryCategory = true;
	
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvPath)));
		String line = reader.readLine();
		String featureArray[] = line.split(",");
		int featureIndex = -1;
		int categoryIndex = -1;
		for(int i = 0;i < featureArray.length;i++){
			if(featureName.equals(featureArray[i])) featureIndex = i;
			if(categoryName.equals(featureArray[i])) categoryIndex = i;
		}
		if(featureIndex == -1 || categoryIndex == -1) System.exit(0);
		
		
		trainUseSupervisedTool(reader,featureIndex,categoryIndex,binaryCategory);
		
		
	}
	private static void trainUseSupervisedTool(BufferedReader reader, int featureIndex, int categoryIndex,boolean binaryCategory) throws IOException {
		
		List<Feature2Category> list = new ArrayList<Feature2Category>();
		String line = null;
		Set<Integer> categoryTypeSet = new HashSet<Integer>(8);
		while((line=reader.readLine())!=null){
			String valueArr[] = line.split(",");
			float feature = Float.parseFloat(valueArr[featureIndex]);
			int categoryValue = Integer.parseInt(valueArr[categoryIndex]);
			int category = 0;
			if(binaryCategory){
				category = categoryValue > 0 ? 1:0;
			}else{
				category = categoryValue;
			}
			categoryTypeSet.add(category);
			Feature2Category fc = new Feature2Category(feature,category);
			list.add(fc);
		}
		reader.close();
		
		float result = caculateCorrelationValue(list);
		System.out.println("corelation:"+result);
	}
	
	/**
	 * Person correlation value(also cosine similarity measure)
	 * @param list
	 * @return correlation index
	 */
	private  static float caculateCorrelationValue(List<Feature2Category> list) {
		
		float feature_avg=0.0f;
		float y_avg = 0.0f;
		float featureSum = 0.0f;
		float ySum =0.0f;
		for(Feature2Category fc : list){
			featureSum += fc.getFeature();
			ySum += fc.getCategory();
		}
		
		feature_avg = featureSum/list.size();
		y_avg = ySum/list.size();
		
		float above = 0.0f;
		
		float belowLeft = 0.0f;
		float belowRight = 0.0f;
		
		for(Feature2Category fc : list){
			above += (fc.getFeature()-feature_avg)*(fc.getCategory()-y_avg);
			belowLeft += (fc.getFeature()-feature_avg)*(fc.getFeature()-feature_avg);
			belowRight += (fc.getCategory()-y_avg)*(fc.getCategory()-y_avg);
		}
		
		return (float) (above/(Math.sqrt(belowLeft)*Math.sqrt(belowRight)));
	}

}
