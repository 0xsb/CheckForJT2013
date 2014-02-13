package com.ailk.jt.util;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Properties;

/**
 * @ClassName: XMLUtil
 * @Description: XML文件校验工具类
 * @author huangpumm@asiainfo-linkage.com
 */
public class XMLUtil {
	// 4A侧负责生产的13个文件
	public static String SM4AI = "smp_sm4ai.xsd";
	public static String SM4AR = "smp_sm4ar.xsd";
	public static String SMAAF = "smp_smaaf.xsd";
	public static String SMBHR = "smp_smbhr.xsd";
	public static String SMCRF = "smp_smcrf.xsd";
	public static String SMDAR = "smp_smdar.xsd";
	public static String SMHAF = "smp_smhaf.xsd";
	public static String SMJKR = "smp_smjkr.xsd";
	public static String SMJKA = "smp_smjka.xsd";
	public static String SMFWL = "smp_smfwl.xsd";
	public static String SMFLO = "smp_smflo.xsd";
	public static String SMMAF = "smp_smmaf.xsd";
	public static String SMMAI = "smp_smmai.xsd";
	public static String SMMAL = "smp_smmal.xsd";
	public static String SMMSF = "smp_smmsf.xsd";
	public static String SMSAL = "smp_smsal.xsd";

	// 应用侧负责生产的3个文件
	public static String SMAAI = "smp_smaai.xsd";
	public static String SMSMF = "smp_smsmf.xsd";
	public static String SMAAR = "smp_smaar.xsd";

	/**
	 * @Title: getXMLLocation
	 * @author huangpumm@asiainfo-linkage.com
	 * @return 将schema
	 */
	public static File getXMLLocation() throws Exception {

		Properties p = PropertiesUtil.getdefaultProperties();
		String uap_file_uapload = p.getProperty("uap_file_uapload_schema");
		File xmlLocation = new File(uap_file_uapload);
		return xmlLocation;
	}

	/**
	 * @Title: readConfig
	 * @Description: 从配置文件中加载对应的xml文件和schema文件对应关系，保存到Map中
	 * @param propertiesFile
	 *            资源文件名称
	 * @throws Exception
	 * @return HashMap<String,String> 返回类型
	 * @throws
	 */
	public static HashMap<String, String> readConfig(String uap_file_uapload_xml_forder) throws Exception {
		Properties p = PropertiesUtil.getdefaultProperties();
		// String uap_file_uapload = p.getProperty(uap_file_uapload_xml_forder);
		String uap_file_uapload = uap_file_uapload_xml_forder;
		String uap_file_uapload_schema = p.getProperty("uap_file_uapload_schema");
		File fileUapload = new File(uap_file_uapload_xml_forder);
		String OSFlag = PropertiesUtil.getValue("os_flag");
		String[] fileUaploadName = fileUapload.list();
		HashMap<String, String> configMap = new HashMap<String, String>();
		for (int j = 0; j < fileUaploadName.length; j++) {
			for (int i = 0; i < fileUaploadName.length; i++) {

				if (fileUaploadName[i].contains("SM4AI")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SM4AI);
				}
				if (fileUaploadName[i].contains("SM4AR")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SM4AR);
				}
				if (fileUaploadName[i].contains("SMAAF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMAAF);
				}
				if (fileUaploadName[i].contains("SMBHR")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMBHR);
				}
				if (fileUaploadName[i].contains("SMCRF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMCRF);
				}
				if (fileUaploadName[i].contains("SMDAR")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMDAR);
				}
				if (fileUaploadName[i].contains("SMHAF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMHAF);
				}
				if (fileUaploadName[i].contains("SMJKR")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMJKR);
				}
				if (fileUaploadName[i].contains("SMJKA")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMJKA);
				}
				if (fileUaploadName[i].contains("SMFWL")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMFWL);
				}
				if (fileUaploadName[i].contains("SMFLO")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMFLO);
				}
				if (fileUaploadName[i].contains("SMMAF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMMAF);
				}
				if (fileUaploadName[i].contains("SMMAI")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMMAI);
				}
				if (fileUaploadName[i].contains("SMMAL")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMMAL);
				}
				if (fileUaploadName[i].contains("SMMSF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMMSF);
				}
				if (fileUaploadName[i].contains("SMSAL")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMSAL);
				}
				if (fileUaploadName[i].contains("SMAAI")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMAAI);
				}
				if (fileUaploadName[i].contains("SMSMF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMSMF);
				}
				if (fileUaploadName[i].contains("SMAAR")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMAAR);
				}
			}

		}
		return configMap;
	}

	/**
	 * @Title: readConfig
	 * @Description: 从配置文件中加载对应的xml文件和schema文件对应关系，保存到Map中
	 * @param propertiesFile
	 *            资源文件名称
	 * @throws Exception
	 * @return HashMap<String,String> 返回类型
	 * @throws
	 */
	public static HashMap<String, String> readConfig() throws Exception {
		Properties p = PropertiesUtil.getdefaultProperties();
		String uap_file_uapload = p.getProperty("uap_file_uapload");
		String uap_file_uapload_schema = p.getProperty("uap_file_uapload_schema");
		File fileUapload = new File(uap_file_uapload);
		String OSFlag = PropertiesUtil.getValue("os_flag");
		String[] fileUaploadName = fileUapload.list();
		HashMap<String, String> configMap = new HashMap<String, String>();
		for (int j = 0; j < fileUaploadName.length; j++) {
			for (int i = 0; i < fileUaploadName.length; i++) {

				if (fileUaploadName[i].contains("SM4AI")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SM4AI);
				}
				if (fileUaploadName[i].contains("SM4AR")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SM4AR);
				}
				if (fileUaploadName[i].contains("SMAAF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMAAF);
				}
				if (fileUaploadName[i].contains("SMBHR")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMBHR);
				}
				if (fileUaploadName[i].contains("SMCRF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMCRF);
				}
				if (fileUaploadName[i].contains("SMDAR")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMDAR);
				}
				if (fileUaploadName[i].contains("SMHAF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMHAF);
				}
				if (fileUaploadName[i].contains("SMJKR")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMJKR);
				}
				if (fileUaploadName[i].contains("SMJKA")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMJKA);
				}
				if (fileUaploadName[i].contains("SMFWL")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMFWL);
				}
				if (fileUaploadName[i].contains("SMFLO")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMFLO);
				}
				if (fileUaploadName[i].contains("SMMAF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMMAF);
				}
				if (fileUaploadName[i].contains("SMMAI")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMMAI);
				}
				if (fileUaploadName[i].contains("SMMAL")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMMAL);
				}
				if (fileUaploadName[i].contains("SMMSF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMMSF);
				}
				if (fileUaploadName[i].contains("SMSAL")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMSAL);
				}
				if (fileUaploadName[i].contains("SMAAI")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMAAI);
				}
				if (fileUaploadName[i].contains("SMSMF")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMSMF);
				}
				if (fileUaploadName[i].contains("SMAAR")) {
					configMap.put(uap_file_uapload + OSFlag + fileUaploadName[i], uap_file_uapload_schema + OSFlag
							+ XMLUtil.SMAAR);
				}
			}

		}
		return configMap;
	}
}
