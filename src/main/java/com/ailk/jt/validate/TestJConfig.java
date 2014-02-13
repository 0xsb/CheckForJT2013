package com.ailk.jt.validate;

import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

public class TestJConfig {

	private static final Configuration configuration = ConfigurationManager.getConfiguration();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		readConfig();
	}

	private static void readConfig() {

		String[] resTypes = configuration.getProperty("restype", "", "dar").split(",");
		long randomInteger = configuration.getIntProperty("avg", 1000, "dar");
		System.out.println("size:==="+resTypes.length);
		for (String string : resTypes) {
			System.out.println("resTypes:=="+string);
		}
		System.out.println("randomInteger:===="+randomInteger);
		
		String czValue = configuration.getProperty(String.valueOf(11), String
				.valueOf(Math.round(Math.random() * 100.0D) + randomInteger), "dar");
		System.out.println(czValue);
		
	}

	
}
