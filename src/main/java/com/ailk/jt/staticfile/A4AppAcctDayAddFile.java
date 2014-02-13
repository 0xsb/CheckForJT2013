package com.ailk.jt.staticfile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.validate.FileValidator;
import com.ailk.jt.validate.OperateFile;

@Deprecated
public class A4AppAcctDayAddFile {
	private static String allPath = PropertiesUtil.getValue("uap_file_uapload");// "D:/4a/ALL";
	private static String right_Date = "2012-09-04";
	private static String right_Datebegin = "2012-09-03";
	private static String now_Date = "";
	private static String dayFilePath = PropertiesUtil
			.getValue("uap_file_uapload_for_day_dir_safe");
	/**
	 * 该程序有安全文件生成一份只有文件名称，没有具体日期的当天文件，用于BOMC处最后一道保障防线
	 * 针对SM4AI，SMAAI
	 */
	private static String rootPathI = PropertiesUtil
			.getValue("uap_file_uapload_for_init_static");// "D:/4a";
	private static String _4ApathI = PropertiesUtil
			.getValue("uap_file_uapload_for_4AI");// "D:/4a/4AI";
	private static String _AApathI = PropertiesUtil
			.getValue("uap_file_uapload_for_AAI");// "D:/4a/AAI";

	private static String osflag = PropertiesUtil.getValue("os_flag");

	public static void main(String[] args) {
		try {
			now_Date = DateUtil.ymdToStr();// yyyy-MM-dd
			// 处理AAI文件和4AI文件
			replaceI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void replaceI() {
		try {
			File safeFileNameSM4AI = new File(dayFilePath + osflag
					+ "SM4AI_371_01DY_20120903_000_000.xml");
			String rightFileStr = "SM4AI.xml";
			OperateFile.copyFile(safeFileNameSM4AI, new File(rootPathI + osflag
					+ rightFileStr));
			changeXMLDate(rootPathI + osflag + rightFileStr);
			dealI();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	private static void seqIToSum(String fileFuleName, int seqI) {
		Document doc = load(fileFuleName);

		List list = doc.selectNodes("/bomc/sum");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(seqI + "");
		}
		createXML(doc, fileFuleName);
	}

	public static void changeXMLDate(String filePath) {
		Document doc = load(filePath);

		List list = doc.selectNodes("/bomc/createtime");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Date,
					now_Date));
		}
		Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendar.add(Calendar.DATE, -1); // 得到前一天
		String yestorday = new SimpleDateFormat("yyyy-MM-dd").format(calendar
				.getTime());
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
			Iterator ltIter = rcdElement.elementIterator("updatetime");
			while (ltIter.hasNext()) {
				Element ltElement = (Element) ltIter.next();
				ltElement.setText(ltElement.getText().replace(right_Datebegin,
						yestorday));
			}
		}
		createXML(doc, filePath);
	}

	public static void dealI() {
		try {
			// 得到所有的 XML文件名
			String[] xmlFileNames = OperateFile.searchEndFileName(rootPathI,
					"xml");
			if (xmlFileNames != null && xmlFileNames.length > 0) { // 防止抓不住 文件
				for (String _4AIFileName : xmlFileNames) {
					if (_4AIFileName.equals("SM4AI.xml")) {
						//根据SM4AI.xml文件名负责生成SMAAI.xml
						String _AAIFileName = _4AIFileName.replace("SM4AI",
								"SMAAI");
						String _4ApathFile = _4ApathI + osflag + _4AIFileName;
						String _AApathFile = _AApathI + osflag + _AAIFileName;

						String _4AallFile = allPath + osflag + _4AIFileName;
						String _AAallFile = allPath + osflag + _AAIFileName;
						// 1、<dlvalue>0</dlvalue> <czvalue>0</czvalue>

						String srcFile = rootPathI + osflag + _4AIFileName;
						String targetFile = _4ApathFile;
						//将STATIC目录中SM4AI.xml拷贝一份放到4AR
						OperateFile.copyFile(new File(srcFile), new File(
								targetFile));
						String initFile = rootPathI + osflag + _4AIFileName;
//						//删除STATIC中的SM4AI.xml
//						OperateFile.deleteFile(initFile);

						// 2、修改 <type>SM4AR</type>
						// 从 4AI 复制到 AAI 文件名字改变
						OperateFile.copyFile(new File(_4ApathFile), new File(
								_AApathFile));
						xmlDealTypeI(_AApathFile);

						// 3、全部再复制到上传目录里
						OperateFile.copyFile(new File(_4ApathFile), new File(
								_4AallFile));
						OperateFile.copyFile(new File(_AApathFile), new File(
								_AAallFile));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 载入一个xml文档
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

	// 读取某个 XML
	public static void xmlDealTypeI(String filePath) {
		Document doc = load(filePath);

		List list = doc.selectNodes("/bomc/type");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element typeElement = (Element) iter.next();
			typeElement.setText("SMAAI");
		}

		createXML(doc, filePath);
	}

	public static void createXML(Document doc, String filePath) {
		/** 将document中的内容写入文件中 */
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