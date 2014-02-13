package com.ailk.jt.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @ClassName: PropertiesUtil
 * @Description: 【读取properties配置文件工具类】
 * @author huangpumm@asiainfo-linkage.com
 * @date May 25, 2012 5:50:11 PM
 */
public class PropertiesUtil {

	private static Logger log = Logger.getLogger(PropertiesUtil.class);

	/**
	  * @Title: getProperties 
	  * @Description: 根据指定的路径返回配置文件对象
	  * @param @param propertiesFileLocation
	  * @param @return    设定文件 
	  * @return Properties    返回类型 
	  * @throws
	 */
	public static Properties getProperties(String propertiesFileLocation) {
		Properties prop = new Properties();
		InputStream in = Object.class.getResourceAsStream(propertiesFileLocation);
		try {
			prop.load(in);
			return prop;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("<!--error read properties file-->" + propertiesFileLocation);
			return null;
		}
	}
	/**
	  * @Title: getdefaultProperties 
	  * @Description: 读取默认的配置文件
	  * @return Properties    返回类型 
	  * @throws
	 */
	public static Properties getdefaultProperties() {
		Properties prop = new Properties();
		InputStream in = Object.class.getResourceAsStream("/config.properties");
		try {
			prop.load(in);
			return prop;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("<!--error read  config.properties file-->");
			return null;
		}
	}

	/**
	 * @Title: getValue
	 * @Description:通过给定的参数名称获取参数值
	 * @param key
	 *            参数名称
	 * @return String 返回参数值
	 */
	public static String getValue(String key) {
		return PropertiesUtil.getdefaultProperties().getProperty(key).trim();
	}

	public static void main(String[] args) {
		Set set = PropertiesUtil.getdefaultProperties().keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			String nextElement = (String) it.next();
			log.debug("<!-- " + nextElement + "=" + PropertiesUtil.getdefaultProperties().getProperty(nextElement) + "-->");
		}
	}
}
