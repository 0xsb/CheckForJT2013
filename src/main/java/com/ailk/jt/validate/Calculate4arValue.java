package com.ailk.jt.validate;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.PropertiesUtil;

public class Calculate4arValue {
	private static final Logger log = Logger.getLogger(Calculate4arValue.class);

	private static Properties tran = PropertiesUtil.getProperties("/tran.properties");

	private static String _4Apath = PropertiesUtil.getValue("uap_file_uapload_for_4AR");

	private static String _AApath = PropertiesUtil.getValue("uap_file_uapload_for_AAR");
	private static String a4RFilePath = "";

	private static String now_Date = "";
	private static String rightTime = "";

	private static int dlvalue = 0;
	private static int czvalue = 0;

	private static String osflag = PropertiesUtil.getValue("os_flag");

	public static void main(String[] args) {
		try {
			now_Date = DateUtil.ymdToStr();
			System.out.println("now_Date====" + now_Date);// now_Date====2013-02-05
			Calendar calendar = Calendar.getInstance();
			calendar.add(5, -1);
			Date date = calendar.getTime();
			rightTime = DateUtil.formatDateyyyyMMDD(date);
			System.out.println("rightTime===" + rightTime);// rightTime===20130204

			// 抽取4AR目录下当天SM4AR文件中的登录值总和
			calculateValue(_4Apath, rightTime);
			// 抽取AAR目录当天SMAAR文件中的登录值总和
			calculateValue(_AApath, rightTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * filePath:文件路径 
	 * rightTime：文件名称中包含的时间
	 */
	private static void calculateValue(String filePath, String rightTime) throws Exception {
	    dlvalue = 0;
		czvalue = 0;
		File file4AR = new File(filePath);
		Collection fileSet = FileUtils.listFiles(file4AR, new String[] { "xml" }, false);
		Iterator iterator = fileSet.iterator();
		for (Iterator iterator2 = fileSet.iterator(); iterator2.hasNext();) {
			File file = (File) iterator2.next();
			if (file.getName().contains(rightTime)) {
				log.info("this file is: " + file.getName());
				a4RFilePath = filePath + osflag + file.getName();
				getVealue(a4RFilePath);
				log.info("dlvalue===" + dlvalue);
				log.info("czvalue===" + czvalue);
				DBUtil.notice(tran.getProperty("a4File") + file.getAbsolutePath() + " "
						+ tran.getProperty("get4ARdlValue") + dlvalue + ";" + tran.getProperty("get4ARczValue")
						+ czvalue);
			}
		}
	}

	/*
	 * 抽取登录操作值总和方法 
	 * *a4RFilePath：文件路径及名称
	 */
	public static void getVealue(String a4RFilePath) {
		Document doc = load(a4RFilePath);
		List list = doc.selectNodes("/smp/data/rcd");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element rcdElement = (Element) iter.next();
			Iterator dlIter = rcdElement.elementIterator("dlvalue");
			while (dlIter.hasNext()) {
				Element dlElement = (Element) dlIter.next();
				dlvalue = dlvalue + Integer.valueOf(dlElement.getText().trim());
			}
			Iterator czIter = rcdElement.elementIterator("czvalue");
			while (czIter.hasNext()) {
				Element czElement = (Element) czIter.next();
				czvalue = czvalue + Integer.valueOf(czElement.getText().trim());
			}
		}
	}

	public static Document load(String filename) {
		Document document = null;
		try {
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(new File(filename));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return document;
	}
}