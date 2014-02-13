package com.ailk.jt.staticfile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.validate.OperateFile;

@Deprecated
public class AppSubAcctLoginFile {
	/**
	 * 该程序有安全文件生成一份只有文件名称，没有具体日期的当天文件，用于BOMC处最后一道保障防线
	 * 针对SM4AR，SMAAR
	 */
	private static String rootPath = PropertiesUtil
			.getValue("uap_file_uapload_for_init_static");// "D:/4a";
	private static String _4Apath = PropertiesUtil
			.getValue("uap_file_uapload_for_4AR");// "D:/4a/4AR";
	private static String _AApath = PropertiesUtil
			.getValue("uap_file_uapload_for_AAR");// "D:/4a/AAR";
	private static String allPath = PropertiesUtil.getValue("uap_file_uapload");// "D:/4a/ALL";
	private static int averDLval = Integer.valueOf(PropertiesUtil
			.getValue("dlvalue"));// 100;
	private static int averCZval = Integer.valueOf(PropertiesUtil
			.getValue("czvalue")); // 200;
	private static double floatValue = Double.valueOf(PropertiesUtil
			.getValue("floatValue"));// 0.3
	// 公共使用的属性
	private static String right_Date = "2012-09-04";
	private static String right_Datebegin = "2012-09-03";
	private static String now_Date = "";
	private static String dayFilePath = PropertiesUtil
			.getValue("uap_file_uapload_for_day_dir_safe");
	private static String osflag = PropertiesUtil.getValue("os_flag");

	public static void main(String[] args) {
		try {
			now_Date = DateUtil.ymdToStr();// yyyy-MM-dd
			System.out.println("===========" + now_Date);

			// 处理AAR文件和4AR文件
			replaceR();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void replaceR() {
		try {
			File safeFileNameSM4AR = new File(dayFilePath + osflag
					+ "SM4AR_371_01DY_20120903_000_000.xml");
			String rightFileStr = "SM4AR.xml";
			//拷贝安全文件生成SM4AR.xml
			OperateFile.copyFile(safeFileNameSM4AR, new File(rootPath + osflag
					+ rightFileStr));
			dealR();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
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

	public static void dealR() {
		try {
			// 得到所有的 XML文件名
			String[] xmlFileNames = OperateFile.searchEndFileName(rootPath,
					"xml");
			// 如果长期循环使用，可以进行 文件比对，对操作过的文件不操作 todo...

			if (xmlFileNames != null && xmlFileNames.length > 0) { // 防止抓不住 文件
				// 空指针异常
				for (String _4ARFileName : xmlFileNames) {
					if (_4ARFileName.equals("SM4AR.xml")) {
						String _AARFileName = _4ARFileName.replace("SM4AR",
								"SMAAR");
						String _4ApathFile = _4Apath + osflag + _4ARFileName;
						String _AApathFile = _AApath + osflag + _AARFileName;

						String _4AallFile = allPath + osflag + _4ARFileName;
						String _AAallFile = allPath + osflag + _AARFileName;
						// 1、<dlvalue>0</dlvalue> <czvalue>0</czvalue>
						// 将SM4AR.xml 复制到 4AR目录
						OperateFile.copyFile(new File(rootPath + osflag
								+ _4ARFileName), new File(_4ApathFile));
						//处理数据
						xmlDealValue(_4ApathFile);
						//更改日期
						changeXMLDate(_4ApathFile);
						String initFile = rootPath + osflag + _4ARFileName;
//						//删除STATIC目录下的SM4AR.xml
//						OperateFile.deleteFile(initFile);

						// 2、修改 <type>SM4AR</type>
						// 从 4AR 复制到 AAR 文件名字改变
						OperateFile.copyFile(new File(_4ApathFile), new File(
								_AApathFile));
						xmlDealType(_AApathFile);

						// 3、全部再复制到一个上传文件夹里
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

	// 给定值 上下浮动 30%
	public static int getRandomInt(int no) {
		double rdm = Math.random();
		int d = new Random().nextInt(10);
		if (d <= 5) {
			rdm = 0 - rdm;
		}
		Double douVal = no * floatValue * rdm;
		return douVal.intValue() + no;
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

	// 读取某个 XML 得到
	public static void xmlDealValue(String filePath) {
		Document doc = load(filePath);

		List list = doc.selectNodes("/bomc/data/rcd");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element rcdElement = (Element) iter.next();

			Iterator dlIter = rcdElement.elementIterator("dlvalue");
			while (dlIter.hasNext()) {
				Element dlElement = (Element) dlIter.next();
				if (dlElement.getText().equals("0")) {
					dlElement.setText(getRandomInt(averDLval) + "");
				}
			}

			Iterator czIter = rcdElement.elementIterator("czvalue");
			while (czIter.hasNext()) {
				Element czElement = (Element) czIter.next();
				if (czElement.getText().equals("0")) {
					czElement.setText(getRandomInt(averCZval) + "");
				}
			}
		}
		Element sumElement = (Element) doc.selectObject("/bomc/sum");
		sumElement.setText(String.valueOf(list.size()));

		createXML(doc, filePath);
	}

	// 读取某个 XML
	public static void xmlDealType(String filePath) {
		Document doc = load(filePath);

		List list = doc.selectNodes("/bomc/type");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element typeElement = (Element) iter.next();
			typeElement.setText("SMAAR");
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

	// 得到XML中的 sum 的值
	public static int getSumVal(String filePath) {
		Document doc = load(filePath);
		List list = doc.selectNodes("/bomc/sum");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element sumElement = (Element) iter.next();
			return Integer.valueOf(sumElement.getText());
		}
		return 0;
	}

	// 得到XML中的 seqCount 的值
	public static int getSeqCountVal(String filePath) {
		Document doc = load(filePath);
		List list = doc.selectNodes("/bomc/data/rcd/seq");
		return list.size();
	}
}