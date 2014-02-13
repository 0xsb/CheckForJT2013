package com.ailk.jt.validate;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.XMLUtil;
/*import com.sun.org.apache.regexp.internal.recompile;*/

/**
 * @ClassName: FileValidator
 * @Description: 校验XML文件格式良好性工具类
 * @author huangpumm@asiainfo-linkage.com
 * @date Jul 13, 2012 4:00:59 PM
 */
public class FileValidator {
	private final static Logger log = Logger.getLogger(FileValidator.class);

	public static boolean validate(String xmlFileName, String uap_file_uapload_xml_forder) {
		try {
			HashMap<String, String> configMap = XMLUtil.readConfig(uap_file_uapload_xml_forder);
			File xmlLocation = XMLUtil.getXMLLocation();
			Set<String> xmlConfig = configMap.keySet();
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
				return true;
			} catch (Exception e) {
				log.error("<!--validator error-->", e);
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	public static void main(String[] args) {
        System.out.println("*********************************");
        System.out.println("SMFLO");
		System.out.println(validate("D:\\work\\work2012\\upload\\SMFLO.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SMFWL");
        System.out.println(validate("D:\\work\\work2012\\upload\\SMFWL.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SMJKR");
        System.out.println(validate("D:\\work\\work2012\\upload\\SMJKR.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SMJKA");
        System.out.println(validate("D:\\work\\work2012\\upload\\SMJKA.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SMMAI");
        System.out.println(validate("D:\\work\\work2012\\upload\\SMMAI.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SMBHR");
        System.out.println(validate("D:\\work\\work2012\\upload\\SMBHR.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SMDAR");
        System.out.println(validate("D:\\work\\work2012\\upload\\SMDAR.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SM4AI");
        System.out.println(validate("D:\\work\\work2012\\upload\\SM4AI.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SMAAI");
        System.out.println(validate("D:\\work\\work2012\\upload\\SMAAI.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SM4AR");
        System.out.println(validate("D:\\work\\work2012\\upload\\SM4AR.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SMAAR");
        System.out.println(validate("D:\\work\\work2012\\upload\\SMAAR.xml", "D:\\work\\work2012\\upload"));

        System.out.println("*********************************");
        System.out.println("SMMAL");
        System.out.println(validate("D:\\work\\work2012\\hour_upload\\SMMAL.xml", "D:\\work\\work2012\\hour_upload"));

        System.out.println("*********************************");
        System.out.println("SMSAL");
        System.out.println(validate("D:\\work\\work2012\\hour_upload\\SMSAL.xml", "D:\\work\\work2012\\hour_upload"));

		// try {
		// HashMap<String, String> config =
		// XMLUtil.readConfig("D:/work/work2012/day/I");
		// File xmlLocation = XMLUtil.getXMLLocation();
		// if (!"".equals(xmlLocation.getAbsolutePath())) {
		// boolean result =
		// validate("D:/work/work2012/day/I/SM4AI_371_01DY_20120811_000_000.xml","D:/work/work2012/day/I");
		// System.out.println(result);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// log.error("<!--validator error-->");
		// }

	}
}