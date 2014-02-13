package com.ailk.jt.staticfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.validate.OperateFile;

@Deprecated
public class MainAcctDayAddFile {
	// 公共使用的属性
	private static String right_Date = "2013-06-02";
	private static String right_Datebegin = "2013-06-01";
	private static String now_Date = "";
	private static String uploadPath = PropertiesUtil
			.getValue("uap_file_uapload");
	private static String osflag = PropertiesUtil.getValue("os_flag");
	private static String dayFilePath = PropertiesUtil
			.getValue("uap_file_uapload_for_day_dir_safe");

	public static void main(String[] args) {
		now_Date = DateUtil.ymdToStr();// yyyy-MM-dd
		replaceMAI();
	}

	private static void replaceMAI() {
		try {
			File safeFileNameSMBHR = new File(dayFilePath + osflag
					+ "SMMAI_371_01DY_20130601_000_000.xml");
			String rightFileStr = "SMMAI.xml";
			OperateFile.copyFile(safeFileNameSMBHR, new File(uploadPath
					+ osflag + rightFileStr));

			// 将新文件 中的时间全部改成 当前日期时间
			changeXMLDateMAI(uploadPath + osflag + rightFileStr);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	private static void changeXMLDateMAI(String filePath) {

		Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendar.add(Calendar.DATE, -1); // 得到前一天
		String yestorday = new SimpleDateFormat("yyyy-MM-dd").format(calendar
				.getTime());
		Document doc = load(filePath);

		List list = doc.selectNodes("/smp/createtime");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Date,
					now_Date));
		}

		list = doc.selectNodes("/smp/begintime");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Datebegin,
					yestorday));
		}

		list = doc.selectNodes("/smp/endtime");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Date,
					now_Date));
		}

		list = doc.selectNodes("/smp/data/rcd/updatetime");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element rcdElement = (Element) iter.next();
			rcdElement.setText(rcdElement.getText().replace(right_Datebegin,
					yestorday));
		}
		list = doc.selectNodes("/smp/data/rcd");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element rcdElement = (Element) iter.next();
			Iterator ltIter = rcdElement.elementIterator("establishtime");
			while (ltIter.hasNext()) {
				Element ltElement = (Element) ltIter.next();
				ltElement.setText(ltElement.getText().replace(right_Date,
						yestorday));
			}
		}

		createXML(doc, filePath);

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

		List list = doc.selectNodes("/smp/createtime");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Date,
					now_Date));
		}
		list = doc.selectNodes("/smp/begintime");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Datebegin,
					yestorday));
		}
		list = doc.selectNodes("/smp/endtime");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Date,
					now_Date));
		}

		list = doc.selectNodes("/smp/data/rcd");
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
		Node sum = (Node) doc.selectObject("/smp/sum");
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