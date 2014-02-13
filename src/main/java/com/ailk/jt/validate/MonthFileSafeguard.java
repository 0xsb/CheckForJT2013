package com.ailk.jt.validate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;

public class MonthFileSafeguard {
	private static Logger log = Logger.getLogger(MonthFileSafeguard.class); // 获取打印日志工具类对象
	private static Properties tran = PropertiesUtil.getProperties("/tran.properties");

	// 公共使用的属性
	private static String right_Date = "2012-08-30";
	private static String right_Datebegin = "2012-08-29";
	private static String now_Date = "";
	private static String now_Date2 = "";
	private static String now_begin = "";
	private static String now_Datebegin = "";
	private static String uploadPath = PropertiesUtil.getValue("uap_file_uapload");
	private static String osflag = PropertiesUtil.getValue("os_flag");
	private static String rightMonthPath = PropertiesUtil.getValue("uap_file_uapload_for_month_dir_safe");
	// 4A侧平台资源覆盖月全量文件
	private static String nowPathSMCRF = PropertiesUtil.getValue("uap_file_uapload_for_smcrf_db_now");
	private static String rightFirstStrSMCRF = "SMCRF_371_01MO_20120801_";
	// 应用资源账号月全量文件
	private static String nowPathSMAAF = PropertiesUtil.getValue("uap_file_uapload_for_smaaf_db_now");
	private static String rightFirstStrSMAAF = "SMCRF_371_01MO_20120801_";
	// 主从账号绑定关系月全量文件
	private static String nowPathSMMSF = PropertiesUtil.getValue("uap_file_uapload_for_smmsf_db_now");
	private static String rightFirstStrSMMSF = "SMCRF_371_01MO_20120801_";
	// 主账号月全量文件
	private static String nowPathSMMAF = PropertiesUtil.getValue("uap_file_uapload_for_smmaf_db_now");
	private static String rightFirstStrSMMAF = "SMCRF_371_01MO_20120801_";
	// 系统资源账号月全量文件
	private static String nowPathSMHAF = PropertiesUtil.getValue("uap_file_uapload_for_smhaf_db_now");
	private static String rightFirstStrSMHAF = "SMCRF_371_01MO_20120801_";
	// 前台具备超级权限月全量文件
	private static String nowPathSMSMF = PropertiesUtil.getValue("uap_file_uapload_for_smsmf_db_now");
	private static String rightFirstStrSMSMF = "SMCRF_371_01MO_20120801_";

	private static Connection conn;

	public static void main(String[] args) {

		// 获得当前月的第一天和上一月的第一天
		Calendar cal = Calendar.getInstance();// 获取当前日期
		cal.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		now_Date = sdf.format(cal.getTime());
		System.out.println("==="+now_Date);
		//当前时间
		now_Date2 = new SimpleDateFormat("yyyyMMdd").format(new Date());// yyyy-MM-dd
		System.out.println("=="+now_Date2);
		cal.set(Calendar.DATE, 1);// 设置为1号,当前日期既为本月第一天
		cal.add(Calendar.MONTH, -1);// 设置为1号,当前日期既为本月第一天
		now_Datebegin = lastMonFirstDay();
		now_begin = sdf2.format(cal.getTime());

		// SMCRF的校验开始
		SafeguardSMCRF();
		// SMCRF校验结束

		// SMAAF的校验开始
		safeguardSMAAF();
		// SMAAF校验结束

		// SMMSF的校验开始
		safeguardSMMSF();
		// SMMSF校验结束

		// SMMAF的校验开始
		safeguardSMMAF();
		// SMMAF校验结束

		// SMHAF的校验开始
		safeguardSMHAF();
		// SMHAF校验结束

		// SMSMF的校验开始
		safeguardSMSMF();
		// SMSMF校验结束
	}

	private static void safeguardSMSMF() {
		String uploadFileName = "";
		try {
			// 得到SMSMF的 XML文件名
			String nowFileNameSMF = OperateFile.searchEndFile(nowPathSMSMF, "xml");
			uploadFileName = nowPathSMSMF + osflag + nowFileNameSMF;
			if (nowFileNameSMF != null) { // 防止抓不住 文件
				// 空指针异常
				String[] nowStrs = nowFileNameSMF.split("_");
				String dateStr = nowStrs[3];// 20120829
				int sum = getSumVal(nowPathSMSMF + osflag + nowFileNameSMF);
				int seq = getSeqCountVal(nowPathSMSMF + osflag + nowFileNameSMF);

				// 在判断sum值是否为零之前先校验文本格式是否正确
				boolean resultSMSMF = FileValidator.validate(uploadFileName, nowPathSMSMF);
				// 先判定 sum 是否为 0
				if (sum <= 0 || sum != seq) {
					// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
					String rightFileStr = rightFirstStrSMSMF + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
					OperateFile.copyFile(new File(rightMonthPath + osflag + rightFileStr), new File(uploadPath + osflag
							+ nowFileNameSMF));

					// 将新文件 中的时间全部改成 当前日期时间
					changeXMLDate(uploadPath + osflag + nowFileNameSMF);
				} else {// 直接拷贝到 upload 文件夹中
					OperateFile.copyFile(new File(nowPathSMSMF + osflag + nowFileNameSMF), new File(uploadPath + osflag
							+ nowFileNameSMF));
				}
			} else {// 直接拷贝到 upload 文件夹中
				OperateFile.copyFile(new File(nowPathSMSMF + osflag + nowFileNameSMF), new File(uploadPath + osflag
						+ nowFileNameSMF));
			}
		} catch (Exception e) {
			String nowFileNameSMF = "";
			uploadFileName = nowPathSMSMF + osflag + nowFileNameSMF;
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
			SMSMFFileNotExits();
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));		}
	}

	private static void safeguardSMHAF() {
		String uploadFileName = "";
		try {
			// 得到SMHAF的 XML文件名
			String nowFileNameHAF = OperateFile.searchEndFile(nowPathSMHAF, "xml");
			uploadFileName = nowPathSMHAF + osflag + nowFileNameHAF;
			if (nowFileNameHAF != null) { // 防止抓不住 文件
				// 空指针异常
				String[] nowStrs = nowFileNameHAF.split("_");
				String dateStr = nowStrs[3];// 20120829
				int sum = getSumVal(nowPathSMHAF + osflag + nowFileNameHAF);
				int seq = getSeqCountVal(nowPathSMHAF + osflag + nowFileNameHAF);

				// 在判断sum值是否为零之前先校验文本格式是否正确
				boolean resultSMHAF = FileValidator.validate(uploadFileName, nowPathSMHAF);
				System.out.println("SMHAF文件格式是否正常，true为正常，false为不正常    " + resultSMHAF);
				// 先判定 sum 是否为 0
				if (sum <= 0 || sum != seq) {
					// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
					String rightFileStr = rightFirstStrSMHAF + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
					OperateFile.copyFile(new File(rightMonthPath + osflag + rightFileStr), new File(uploadPath + osflag
							+ nowFileNameHAF));

					// 将新文件 中的时间全部改成 当前日期时间
					changeXMLDate(uploadPath + osflag + nowFileNameHAF);
				} else {// 直接拷贝到 upload 文件夹中
					OperateFile.copyFile(new File(nowPathSMHAF + osflag + nowFileNameHAF), new File(uploadPath + osflag
							+ nowFileNameHAF));
				}
			} else {// 直接拷贝到 upload 文件夹中
				OperateFile.copyFile(new File(nowPathSMHAF + osflag + nowFileNameHAF), new File(uploadPath + osflag
						+ nowFileNameHAF));
			}
		} catch (Exception e) {
			String nowFileNameHAF = "";
			uploadFileName = nowPathSMHAF + osflag + nowFileNameHAF;
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
			SMHAFFileNotExits();
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));		}
	}

	private static void safeguardSMMAF() {
		String uploadFileName = "";
		try {
			// 得到SMMAF的 XML文件名
			String nowFileNameMAF = OperateFile.searchEndFile(nowPathSMMAF, "xml");
			uploadFileName = nowPathSMMAF + osflag + nowFileNameMAF;
			if (nowFileNameMAF != null) { // 防止抓不住 文件
				// 空指针异常
				String[] nowStrs = nowFileNameMAF.split("_");
				String dateStr = nowStrs[3];// 20120829
				int sum = getSumVal(nowPathSMMAF + osflag + nowFileNameMAF);
				int seq = getSeqCountVal(nowPathSMMAF + osflag + nowFileNameMAF);

				// 在判断sum值是否为零之前先校验文本格式是否正确
				boolean resultSMMAF = FileValidator.validate(uploadFileName, nowPathSMMAF);
				System.out.println("SMMAF文件格式是否正常，true为正常，false为不正常    " + resultSMMAF);
				// 先判定 sum 是否为 0
				if (sum <= 0 || sum != seq) {
					// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
					String rightFileStr = rightFirstStrSMMAF + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
					OperateFile.copyFile(new File(rightMonthPath + osflag + rightFileStr), new File(uploadPath + osflag
							+ nowFileNameMAF));

					// 将新文件 中的时间全部改成 当前日期时间
					changeXMLDate(uploadPath + osflag + nowFileNameMAF);
				} else {// 直接拷贝到 upload 文件夹中
					OperateFile.copyFile(new File(nowPathSMMAF + osflag + nowFileNameMAF), new File(uploadPath + osflag
							+ nowFileNameMAF));
				}
			} else {// 直接拷贝到 upload 文件夹中
				OperateFile.copyFile(new File(nowPathSMMAF + osflag + nowFileNameMAF), new File(uploadPath + osflag
						+ nowFileNameMAF));
			}
		} catch (Exception e) {
			String nowFileNameMAF = "";
			uploadFileName = nowPathSMMAF + osflag + nowFileNameMAF;
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
			SMMAFFileNotExits();
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
		}
	}

	private static void safeguardSMMSF() {
		String uploadFileName = "";
		try {
			// 得到SMMSF的 XML文件名
			String nowFileNameMSF = OperateFile.searchEndFile(nowPathSMMSF, "xml");
			uploadFileName = nowPathSMMSF + osflag + nowFileNameMSF;
			if (nowFileNameMSF != null) { // 防止抓不住 文件
				// 空指针异常
				String[] nowStrs = nowFileNameMSF.split("_");
				String dateStr = nowStrs[3];// 20120829
				int sum = getSumVal(nowPathSMMSF + osflag + nowFileNameMSF);
				int seq = getSeqCountVal(nowPathSMMSF + osflag + nowFileNameMSF);

				// 在判断sum值是否为零之前先校验文本格式是否正确
				boolean resultSMMSF = FileValidator.validate(uploadFileName, nowPathSMMSF);
				System.out.println("SMMSF文件格式是否正常，true为正常，false为不正常    " + resultSMMSF);
				// 先判定 sum 是否为 0
				if (sum <= 0 || sum != seq) {
					// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
					String rightFileStr = rightFirstStrSMMSF + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
					OperateFile.copyFile(new File(rightMonthPath + osflag + rightFileStr), new File(uploadPath + osflag
							+ nowFileNameMSF));

					// 将新文件 中的时间全部改成 当前日期时间
					changeXMLDate(uploadPath + osflag + nowFileNameMSF);
				} else {// 直接拷贝到 upload 文件夹中
					OperateFile.copyFile(new File(nowPathSMMSF + osflag + nowFileNameMSF), new File(uploadPath + osflag
							+ nowFileNameMSF));
				}
			} else {// 直接拷贝到 upload 文件夹中
				OperateFile.copyFile(new File(nowPathSMMSF + osflag + nowFileNameMSF), new File(uploadPath + osflag
						+ nowFileNameMSF));
			}
		} catch (Exception e) {
			String nowFileNameMSF = "";
			uploadFileName = nowPathSMMSF + osflag + nowFileNameMSF;
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
			SMMSFFileNotExits();
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));	
		}
	}

	private static void safeguardSMAAF() {
		String uploadFileName = "";
		try {
			// 得到SMAAF的 XML文件名
			String nowFileNameAAF = OperateFile.searchEndFile(nowPathSMAAF, "xml");
			uploadFileName = nowPathSMAAF + osflag + nowFileNameAAF;
			if (nowFileNameAAF != null) { // 防止抓不住 文件
				// 空指针异常
				String[] nowStrs = nowFileNameAAF.split("_");
				String dateStr = nowStrs[3];// 20120829
				int sum = getSumVal(nowPathSMAAF + osflag + nowFileNameAAF);
				int seq = getSeqCountVal(nowPathSMAAF + osflag + nowFileNameAAF);

				// 在判断sum值是否为零之前先校验文本格式是否正确
				boolean resultSMAAF = FileValidator.validate(uploadFileName, nowPathSMAAF);
				System.out.println("SMAAF文件格式是否正常，true为正常，false为不正常    " + resultSMAAF);
				// 先判定 sum 是否为 0
				if (sum <= 0 || sum != seq) {
					// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
					String rightFileStr = rightFirstStrSMAAF + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
					OperateFile.copyFile(new File(rightMonthPath + osflag + rightFileStr), new File(uploadPath + osflag
							+ nowFileNameAAF));

					// 将新文件 中的时间全部改成 当前日期时间
					changeXMLDate(uploadPath + osflag + nowFileNameAAF);
				} else {// 直接拷贝到 upload 文件夹中
					OperateFile.copyFile(new File(nowPathSMAAF + osflag + nowFileNameAAF), new File(uploadPath + osflag
							+ nowFileNameAAF));
				}
			} else {// 直接拷贝到 upload 文件夹中
				OperateFile.copyFile(new File(nowPathSMAAF + osflag + nowFileNameAAF), new File(uploadPath + osflag
						+ nowFileNameAAF));
			}
		} catch (Exception e3) {
			String nowFileNameAAF = "";
			uploadFileName = nowPathSMAAF + osflag + nowFileNameAAF;
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
			SMAAFFileNotExits();
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));		}
	}

	private static void SafeguardSMCRF() {
		String uploadFileName = "";
		try {
			// 得到SMCRF的 XML文件名
			String nowFileNameSMCRF = OperateFile.searchEndFile(nowPathSMCRF, "xml");
			uploadFileName = nowPathSMCRF + osflag + nowFileNameSMCRF;
			// 空指针异常
			String[] nowStrs = nowFileNameSMCRF.split("_");
			String dateStr = nowStrs[3];// 20120829
			int sum = getSumVal(uploadFileName);
			int seq = getSeqCountVal(uploadFileName);

			// 在判断sum值是否为零之前先校验文本格式是否正确
			boolean resultSMCRF = FileValidator.validate(uploadFileName, nowPathSMCRF);
			// 先判定 sum 是否为 0
			if (sum <= 0 || sum != seq) {
				// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
				String rightFileStr = rightFirstStrSMCRF + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
				OperateFile.copyFile(new File(rightMonthPath + osflag + rightFileStr), new File(uploadPath + osflag
						+ nowFileNameSMCRF));

				// 将新文件 中的时间全部改成 当前日期时间
				changeXMLDate(uploadPath + osflag + nowFileNameSMCRF);
			} else {// 直接拷贝到 upload 文件夹中
				OperateFile.copyFile(new File(uploadFileName), new File(uploadPath + osflag + nowFileNameSMCRF));
			}
			// 删除操作
			OperateFile.deleteFileOrDir(uploadFileName);
		} catch (Exception e2) {
			String nowFileNameSMCRF = "";
			uploadFileName = nowPathSMCRF + osflag + nowFileNameSMCRF;
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
			SMCRFFileNotExits();
			DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
		}
	}

	//替换SMCRF
	private static void SMCRFFileNotExits() {
		try {
			File safeFileNameSMCRF = new File(rightMonthPath + osflag + "SMCRF\\SMCRF_371_01MO_20120801_000_000.xml");
			String[] nowStrs = safeFileNameSMCRF.getName().split("_");
			// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
			Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendar.add(Calendar.DATE, -1); // 得到前一天
			String rightFileStr = "SMCRF_371_01MO_" + now_begin + "_" + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
			OperateFile.copyFile(safeFileNameSMCRF, new File(uploadPath + osflag + rightFileStr));

			// 将新文件 中的时间全部改成 当前日期时间
			changeXMLDate(uploadPath + osflag + rightFileStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//替换SMMSF
	private static void SMMSFFileNotExits() {
		try {
			File safeFileNameSMMSF = new File(rightMonthPath + osflag + "SMMSF\\SMMSF_371_01MO_20120801_000_000.xml");
			String[] nowStrs = safeFileNameSMMSF.getName().split("_");
			// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
			Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendar.add(Calendar.DATE, -1); // 得到前一天
			String rightFileStr = "SMMSF_371_01MO_" + now_begin + "_" + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
			OperateFile.copyFile(safeFileNameSMMSF, new File(uploadPath + osflag + rightFileStr));

			// 将新文件 中的时间全部改成 当前日期时间
			changeXMLDate(uploadPath + osflag + rightFileStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//替换SMAAF
	private static void SMAAFFileNotExits() {
		try {
			File safeFileNameSMAAF = new File(rightMonthPath + osflag + "SMAAF\\SMAAF_371_01MO_20120801_000_000.xml");
			String[] nowStrs = safeFileNameSMAAF.getName().split("_");
			// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
			Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendar.add(Calendar.DATE, -1); // 得到前一天
			String rightFileStr = "SMAAF_371_01MO_" + now_begin + "_" + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
			OperateFile.copyFile(safeFileNameSMAAF, new File(uploadPath + osflag + rightFileStr));

			// 将新文件 中的时间全部改成 当前日期时间
			changeXMLDate(uploadPath + osflag + rightFileStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//替换SMMAF
	private static void SMMAFFileNotExits() {
		try {
			File safeFileNameSMMAF = new File(rightMonthPath + osflag + "SMMAF\\SMMAF_371_01MO_20120801_000_000.xml");
			String[] nowStrs = safeFileNameSMMAF.getName().split("_");
			// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
			Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendar.add(Calendar.DATE, -1); // 得到前一天
			String rightFileStr = "SMMAF_371_01MO_" + now_begin + "_" + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
			OperateFile.copyFile(safeFileNameSMMAF, new File(uploadPath + osflag + rightFileStr));

			// 将新文件 中的时间全部改成 当前日期时间
			changeXMLDate(uploadPath + osflag + rightFileStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//替换SMHAF
	private static void SMHAFFileNotExits() {
		try {
			File safeFileNameSMHAF = new File(rightMonthPath + osflag + "SMHAF\\SMHAF_371_01MO_20120801_000_000.xml");
			String[] nowStrs = safeFileNameSMHAF.getName().split("_");
			// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
			Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendar.add(Calendar.DATE, -1); // 得到前一天
			String rightFileStr = "SMHAF_371_01MO_" + now_begin + "_" + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
			OperateFile.copyFile(safeFileNameSMHAF, new File(uploadPath + osflag + rightFileStr));

			// 将新文件 中的时间全部改成 当前日期时间
			changeXMLDate(uploadPath + osflag + rightFileStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//替换SMSMF
	private static void SMSMFFileNotExits() {
		try {
			File safeFileNameSMHAF = new File(rightMonthPath + osflag + "SMSMF\\SMSMF_371_01MO_20120801_000_000.xml");
			String[] nowStrs = safeFileNameSMHAF.getName().split("_");
			// 将相应正确路径的文件 直接改变成当前名字 拷贝到 上传路径
			Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendar.add(Calendar.DATE, -1); // 得到前一天
			String rightFileStr = "SMSMF_371_01MO_" + now_begin + "_" + nowStrs[4] + "_" + nowStrs[5];// 相应的正确文件的位置
			OperateFile.copyFile(safeFileNameSMHAF, new File(uploadPath + osflag + rightFileStr));

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
			SAXReader saxReader = new SAXReader();
			File file = new File(filename);
			System.out.println(file.getAbsolutePath());
			document = saxReader.read(new File(filename));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return document;
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

	// 将xml文档内容转为String
	public static String doc2String(Document document) {
		String s = "";
		try {
			// 使用输出流来进行转化
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputFormat format = new OutputFormat("", true, "UTF-8");
			XMLWriter writer = new XMLWriter(out, format);
			writer.write(document);
			s = out.toString("UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}

	// 将字符串转为Document
	public static Document string2Document(String s) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(s);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return doc;
	}

	public static void changeXMLDate(String filePath) {
		Document doc = load(filePath);

		List list = doc.selectNodes("/bomc/createtime");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Date, now_Date2));
		}
		list = doc.selectNodes("/bomc/begintime");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Datebegin, now_Datebegin));
		}
		list = doc.selectNodes("/bomc/endtime");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element timeElement = (Element) iter.next();
			timeElement.setText(timeElement.getText().replace(right_Date, now_Date));
		}

		list = doc.selectNodes("/bomc/data/rcd");
		iter = list.iterator();
		while (iter.hasNext()) {
			Element rcdElement = (Element) iter.next();
			Iterator ltIter = rcdElement.elementIterator("logintime");
			while (ltIter.hasNext()) {
				Element ltElement = (Element) ltIter.next();
				ltElement.setText(ltElement.getText().replace(right_Date, now_Date));
			}
		}
		createXML(doc, filePath);
	}

	public static void createXML(Document doc, String filePath) {
		/** 将document中的内容写入文件中 */
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)));
			writer.write(doc);
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String lastMonFirstDay() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String months = "";
		String days = "";
		if (month > 1) {
			month--;
		} else {
			year--;
			month = 12;
		}
		if (!(String.valueOf(month).length() > 1)) {
			months = "0" + month;
		} else {
			months = String.valueOf(month);
		}
		if (!(String.valueOf(day).length() > 1)) {
			days = "0" + day;
		} else {
			days = String.valueOf(day);
		}
		String firstDay = "" + year + "-" + months + "-01";
		String[] lastMonth = new String[2];
		lastMonth[0] = firstDay;
		return firstDay;
	}
}
