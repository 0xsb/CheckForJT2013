package com.ailk.uap.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* @ClassName: PropertiesUtil 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author huangpumm@asiainfo-linkage.com
* @date Jul 13, 2012 12:29:36 PM
*/
public class PropertiesUtil {

	private static Log logger =  LogFactory.getLog(PropertiesUtil.class);
	private static Properties prop = new Properties(); 
	
	static { 
        InputStream in = Object.class.getResourceAsStream("/config.properties"); 
        try { 
            prop.load(in); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    } 

	public static String getValue(String key) {
		return prop.getProperty(key).trim();	
	}
	
	public static void main(String[] args)
	{
		Set set = prop.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			logger.error("==="+prop.getProperty((String)it.next()));
		}
	}
}
