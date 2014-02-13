package com.ailk.jt.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLOperatorUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	//此方法用于载入一个XML文档返回一个document
	public static Document load(String filename){
		Document document = null;
		SAXReader sReader = new SAXReader();
		try {
			document = sReader.read(new File(filename));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
		
	}
	//依据doc来创建xml
	public static void createXML(Document doc,String filename){
		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		outputFormat.setNewLineAfterDeclaration(false);
		try {
			XMLWriter xWriter = new XMLWriter(new FileOutputStream(new File(filename)),outputFormat);
			xWriter.write(doc);
			xWriter.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
