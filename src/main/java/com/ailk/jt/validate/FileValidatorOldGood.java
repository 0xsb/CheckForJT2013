package com.ailk.jt.validate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.XMLUtil;

/**
 * @ClassName: FileValidator
 * @Description: 校验XML文件格式良好性工具类
 * @author huangpumm@asiainfo-linkage.com
 * @date Jul 13, 2012 4:00:59 PM
 */
public class FileValidatorOldGood {
	private final static Logger log = Logger.getLogger(FileValidatorOldGood.class);

	private static void validate(Map<String, String> configMap) {
		Set<String> xmlConfig = configMap.keySet();
		for (String xmlFileName : xmlConfig) {
			log.debug("<!--xmlFileName=" + xmlFileName + "\t schemaFileName=" + configMap.get(xmlFileName) + "-->");
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
				log.error("<!--validator error-->");
				e.printStackTrace();
				DBUtil.notice(e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		try {
			HashMap<String, String> config = XMLUtil.readConfig();
			File xmlLocation = XMLUtil.getXMLLocation();
			if (!"".equals(xmlLocation.getAbsolutePath())) {
				validate(config);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("<!--validator error-->");
		}
	}
}