package com.ailk.jt.staticfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.springframework.context.support.StaticApplicationContext;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.validate.FileValidator;
import com.ailk.jt.validate.OperateFile;
import com.thoughtworks.xstream.io.xml.StaxWriter;

@Deprecated
public class DbOperateLogDayFile {
	// 公共使用的属性
	private static String right_Date = "2012-09-04";
	private static String right_Datebegin = "2012-09-03";
	private static String now_Date = "";
	private static String uploadPath = PropertiesUtil
			.getValue("uap_file_uapload");
	private static String osflag = PropertiesUtil.getValue("os_flag");
	private static String dayFilePath = PropertiesUtil
			.getValue("uap_file_uapload_for_day_dir_safe");
	
	public static void main(String[] args) {
		now_Date = DateUtil.ymdToStr();// yyyy-MM-dd
		replaceDAR();
	}

	private static void replaceDAR() {
		try {
			File safeFileNameSMBHR = new File(dayFilePath + osflag
					+ "SMDAR_371_01DY_20120903_000_000.xml");
			String rightFileStr = "SMDAR.xml";
			//将安全文件拷贝生成SMDAR.xml
			OperateFile.copyFile(safeFileNameSMBHR, new File(uploadPath
					+ osflag + rightFileStr));
			// 将新文件 中的时间全部改成 当前日期时间
			changeXMLDate(uploadPath + osflag + rightFileStr);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	// 载入一个xml文档
	public static Document load(String filename) {
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "UTF-8")));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return document;
	}

	public static void changeXMLDate(String filePath) {
		Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendar.add(Calendar.DATE, -1); // 得到前一天
		String yestorday = new SimpleDateFormat("yyyy-MM-dd").format(calendar
				.getTime());
		Document doc = load(filePath);

		List list = doc.selectNodes("/bomc/createtime");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Date,
					now_Date));
		}
		list = doc.selectNodes("/bomc/begintime");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Datebegin,
					yestorday));
		}
		list = doc.selectNodes("/bomc/endtime");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Date,
					now_Date));
		}

		list = doc.selectNodes("/bomc/data/rcd");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element rcdElement = (Element) iter.next();
			Iterator ltIter = rcdElement.elementIterator("logintime");
			while (ltIter.hasNext()) {
				Element ltElement = (Element) ltIter.next();
				ltElement.setText(ltElement.getText().replace(right_Date,
						now_Date));
			}
		}
		// 更改sum值为seq的总数
		Node sum = (Node) doc.selectObject("/bomc/sum");
		sum.setText(String.valueOf(list.size()));
		createXML(doc, filePath);
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
}