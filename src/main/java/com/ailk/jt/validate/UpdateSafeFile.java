package com.ailk.jt.validate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ailk.jt.util.PropertiesUtil;

/**
 * 该类主要作用为是更新安全文件的数据，防止多天上报相同的文件
 * 
 * @author Administrator
 */
public class UpdateSafeFile {
	private final static Logger log = Logger.getLogger(UpdateSafeFile.class);
	private static String safeFileForRPath = PropertiesUtil
//			.getValue("uap_file_safe_path");
	.getValue("uap_file_uapload_for_day_dir_safe");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UpdateSafeFile us = new UpdateSafeFile();
		log.debug("start modify R file!");
		us.modifyR();// 更新4AR文件
		log.debug("end modify R file!");
	}

	/**
	 * 更新4AR文件
	 */
	private void modifyR() {
		try {
			File safeFileForR = new File(safeFileForRPath);
			Collection<File> dirContainFiles = FileUtils.listFiles(
					safeFileForR, new String[] { "xml" }, false);
			for (File file : dirContainFiles) {
				if (file.getName().contains("4AR")) {
					log.debug(" current file path must contain '4AR',and current file path is:"
							+ file.getAbsolutePath());
					File newFile = new File(file.getAbsoluteFile() + ".bak");

					FileUtils.copyFile(file, newFile);
					file.delete();
					Document doc = modifyValue(load(newFile));
					log.debug("document has been completly modified!");
					createXML(doc, file.getAbsolutePath());
					log.debug("after replace dlvalue and czvlue!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static Document modifyValue(Document doc) {
		// 修改登陆值
		List listDL = doc.selectNodes("/bomc/data/rcd/dlvalue");
		Iterator iterDL = listDL.iterator();
		int count = (int) (Math.random()*100);
		System.out.println("count= "+count);
		int flag = -1;
		if (count%2==0) {
			flag = 1;
		}
		while (iterDL.hasNext()) {
			Element dlElement = (Element) iterDL.next();
			int dlvalue = Integer.valueOf(dlElement.getText());

			Double afterReplace = dlvalue + dlvalue*0.1 * Math.random()*flag;
			if (afterReplace.intValue()==0) {
				afterReplace=4.0;
			}
			dlElement.setText(dlElement.getText().replace(dlvalue + "",
					afterReplace.intValue() + ""));
		}

		// 修改操作值
		List listCZ = doc.selectNodes("/bomc/data/rcd/czvalue");
		Iterator iterCZ = listCZ.iterator();
		while (iterCZ.hasNext()) {
			Element czElement = (Element) iterCZ.next();
			int czvalue = Integer.valueOf(czElement.getText());

			Double afterReplace = czvalue + czvalue *0.1* Math.random()*2*flag;
			if (afterReplace.intValue()==0) {
				afterReplace=5321.0;
			}
			czElement.setText(czElement.getText().replace(czvalue + "",
					afterReplace.intValue() + ""));
		}
		return doc;
	}

	public static void createXML(Document doc, String filePath) {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setNewLineAfterDeclaration(false);
			XMLWriter writer = new XMLWriter(new FileOutputStream(new File(
					filePath)), format);

			writer.write(doc);
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 载入一个xml文档
	public static Document load(File file) {
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8")));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return document;
	}

}
