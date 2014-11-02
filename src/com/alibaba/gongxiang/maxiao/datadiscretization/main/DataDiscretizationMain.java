package com.alibaba.gongxiang.maxiao.datadiscretization.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.gongxiang.maxiao.datadiscretization.engine.SupervisedTool;
import com.alibaba.gongxiang.maxiao.datadiscretization.engineImp.InformationEntropyTool;
import com.alibaba.gongxiang.maxiao.datadiscretization.model.Feature2Category;

public class DataDiscretizationMain {
	
	static String csvPath = "data/iris.data";
	static String featureName = "petal_length";

	/*
	 * Pay ATTENTION THAT !!!!ONLY ACCEPT Integer category input format !!!!!!!!!!!!!!!!
	 */
	static String categoryName = "class";
	static boolean binaryCategory = false;
	
	
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
		//PS::::::::::::::only accept Integer category input!!!!!!!!!!!!!!!!
		SupervisedTool tool = new InformationEntropyTool(list,categoryTypeSet);
		tool.computeCutIntervals();


	}

}
