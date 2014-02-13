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
public class WebExceptionBusinessDayAddlFile_new {
	private static final Configuration configuration = ConfigurationManager
			.getConfiguration();
	private static Logger log = Logger.getLogger(WebExceptionBusinessDayAddlFile_new.class); // 获取打印日志工具类对象
	private static Properties tran = PropertiesUtil
			.getProperties("/tran.properties");
	// 公共使用的属性
	private static String right_Date = "2012-09-04";
	private static String right_Datebegin = "2012-09-03";
	private static String now_Date = "";
	private static String now_Datebegin = "";
	private static String rightdate = "";
	private static String uploadPath = PropertiesUtil
			.getValue("uap_file_uapload");
	private static String osflag = PropertiesUtil.getValue("os_flag");
	private static String dayFilePath = PropertiesUtil
			.getValue("uap_file_uapload_for_day_dir_safe");
	// 前台异常业务操作统计日增量文件
	private static String nowPathSMBHR = PropertiesUtil
			.getValue("uap_file_uapload_for_smbhr_db_now");
	private static String rightFirstStrSMBHR = "SMBHR_371_01DY_20120903_";

	public static void main(String[] args) {
		String nowDateStr = DateUtil.formatDateyyyyMMDD(new Date());// yyyyMMdd
		now_Date = DateUtil.ymdToStr();// yyyy-MM-dd

		Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendar.add(Calendar.DATE, -1); // 得到前一天
		now_Datebegin = new SimpleDateFormat("yyyy-MM-dd").format(calendar
				.getTime());
		rightdate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());

		// SMBHR的校验开始
		validateBHR();
		// SMBHR校验结束
	}

	/**
	 * @Title: validateBHR
	 * @Description: 校验前台异常业务文件
	 */
	public static void validateBHR() {
		String uploadFileName = "";
		// 文件未正常产生，直接用备份文件替换
		try {
			// 得到SMBHR的 XML文件名
			String nowFileNameSMBHR = "SMBHR_371_01DY_" + rightdate
					+ "_000_000.xml";
			uploadFileName = nowPathSMBHR + osflag + nowFileNameSMBHR;
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName
					+ tran.getProperty("notGernerated"));
			replaceSMBHR();
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName
					+ tran.getProperty("replaceSuccess"));

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * @Title: isZero
	 * @Description: 判定元素的值是否为0
	 * @return true 为0 false 不为0
	 */

	/**
	 * @Title: isZero
	 * @Description: 判定元素的值是否为0
	 * @return true 为0 false 不为0
	 */
	private static boolean isZero(String fileName, String behabiour) {
		Document doc = load(fileName);
		if ("101".equals(behabiour)) {
			List buztotalXML = doc
					.selectNodes("/bomc/data/rcd[behaviour=101 and total!=0 and buztotal!=0]");
			System.out.println(buztotalXML.size());
			if (buztotalXML.size() == 30) {
				return false;
			} else {
				return true;
			}
		}
		if ("102".equals(behabiour)) {
			List buztotalXML = doc
					.selectNodes("/bomc/data/rcd[behaviour=102 and total=0 and buztotal=0]");
			System.out.println(buztotalXML.size());
			if (buztotalXML.size() == 31) {
				return true;
			} else {
				return false;
			}
		}
		if ("103".equals(behabiour)) {
			List buztotalXML = doc
					.selectNodes("/bomc/data/rcd[behaviour=103 and total=0 and buztotal=0]");
			System.out.println(buztotalXML.size());
			if (buztotalXML.size() == 31) {
				return true;
			} else {
				return false;
			}
		}
		return false;

	}

	/**
	 * @Title: replaceSMBHR
	 * @Description: 如果文件没有正常产生，或者校验失败，直接拿备份文件修改后上传
	 */
	private static void replaceSMBHR() {
		try {
			File safeFileNameSMBHR = new File(dayFilePath + osflag
					+ "SMBHR_371_01DY_20120903_000_000.xml");
			String[] nowStrs = safeFileNameSMBHR.getName().split("_");
			// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
			Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendar.add(Calendar.DATE, -1); // 得到前一天
			String now_Datebegin = new SimpleDateFormat("yyyyMMdd")
					.format(calendar.getTime());
			String rightFileStr = "SMBHR_371_01DY_" + now_Datebegin + "_"
					+ nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
			OperateFile.copyFile(safeFileNameSMBHR, new File(uploadPath
					+ osflag + rightFileStr));

			// 将新文件 中的时间全部改成 当前日期时间
			changeXMLDate(uploadPath + osflag + rightFileStr);
		} catch (Exception e) {
			e.printStackTrace();
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