package com.ailk.jt.validate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.XMLUtil;

public class FileValidatorOldGoodTest {

	private static String uploadPath = PropertiesUtil.getValue("uap_file_uapload");
	private static String osflag = PropertiesUtil.getValue("os_flag");
	private static String nowPathSMCRF = PropertiesUtil.getValue("uap_file_uapload_for_smcrf_db_now");
	// 应用资源账号月全量文件
	private static String nowPathSMAAF = PropertiesUtil.getValue("uap_file_uapload_for_smaaf_db_now");
	// 主从账号绑定关系月全量文件
	private static String nowPathSMMSF = PropertiesUtil.getValue("uap_file_uapload_for_smmsf_db_now");
	// 主账号月全量文件
	private static String nowPathSMMAF = PropertiesUtil.getValue("uap_file_uapload_for_smmaf_db_now");
	// 系统资源账号月全量文件
	private static String nowPathSMHAF = PropertiesUtil.getValue("uap_file_uapload_for_smhaf_db_now");
	// 前台具备超级权限月全量文件
	private static String nowPathSMSMF = PropertiesUtil.getValue("uap_file_uapload_for_smsmf_db_now");
	
	@Test
	@Ignore("to do move file from genearte month dir to upload dir")
	public void moveFile() {
		
	}
	
	@Test
	public void validateMonfth() {
	try {
		HashMap<String, String> config = XMLUtil.readConfig();
		File xmlLocation = XMLUtil.getXMLLocation();
		if (!"".equals(xmlLocation.getAbsolutePath())) {
			validate(config);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	
	
	private static void validate(Map<String, String> configMap) {
		Set<String> xmlConfig = configMap.keySet();
		for (String xmlFileName : xmlConfig) {
			String xmlData = xmlFileName;
			String schemaData = configMap.get(xmlFileName);
			try {
				XMLReader reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
				reader.setFeature("http://xml.org/sax/features/validation", true);
				reader.setFeature("http://apache.org/xml/features/validation/schema", true);
				reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
						schemaData);

				MyDefaultHandler handler = new MyDefaultHandler();
				reader.setContentHandler(handler);
				reader.setErrorHandler(handler);
				reader.parse(xmlData);
			} catch (Exception e) {
				e.printStackTrace();
				DBUtil.notice(e.getMessage());
			}
		}
	}
}
