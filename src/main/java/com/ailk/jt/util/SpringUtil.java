package com.ailk.jt.util;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class SpringUtil extends SqlMapClientDaoSupport implements ApplicationContextAware {
	private static final Logger logger = Logger.getLogger(SpringUtil.class);
	private static ClassPathXmlApplicationContext context;

	static {
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
		if (context != null) {
		} else {
			logger.error("<!--error load applicationContext.xml-->");
		}
	}

	/**
	 * @Title: readXML
	 * @Description: 从spring配置文件中加载信息
	 * @param
	 * @return 设定文件
	 * @return ClassPathXmlApplicationContext 返回类型
	 * @throws
	 */
	public static ClassPathXmlApplicationContext readXML() {
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
		if (context != null) {
			return context;
		} else {
			logger.error("<!--error load applicationContext.xml-->");
			return null;
		}
	}

	public static Object getBean(String beanName) {
		try {
			logger.info("<!-- beanName is: "+context.containsBean(beanName)+"-->");
			System.out.println(context.containsBean(beanName));
			return context.getBean(beanName);
		} catch (Exception e) {
			// TODO: handle exception
			context.containsBean(beanName);
		}
		return context.getBean(beanName);
	}

	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// TODO Auto-generated method stub

	}
}