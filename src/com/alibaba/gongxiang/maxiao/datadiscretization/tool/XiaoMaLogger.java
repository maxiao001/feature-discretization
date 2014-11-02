package com.alibaba.gongxiang.maxiao.datadiscretization.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class XiaoMaLogger {

	
	BufferedWriter logger = null;
	public XiaoMaLogger(String filePath,boolean append){
		File f = new File(filePath);
		if(!f.exists()){
			System.out.println("log output file not exists");
			System.exit(0);
		}
		try {
			logger = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,append)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void println(String content){
		
		try {
			this.logger.write(content);
			this.logger.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void println(float content){
		
		try {
			this.logger.write(new Float(content).toString());
			this.logger.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void print(String content){
		try {
			this.logger.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void println(){
		try {
			this.logger.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void close(){
		
		try {
			this.logger.flush();
			this.logger.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
