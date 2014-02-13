package com.ailk.jt.util;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.ResourceUtils;

/**
 * @ClassName: SQLUtil
 * @Description: 主要用户从SQL.xml文件中读取SQL查询数据库
 * @author huangpumm@asiainfo-linkage.com
 * @date Jul 12, 2012 5:39:22 AM
 */
public class SQLUtil {
	private static Logger log = Logger.getLogger(SQLUtil.class); // 获取打印日志工具类对象

	public static void main(String[] args) {
		// SQLUtil sUtil = new SQLUtil();
		// String sqlData = getSql("a4_boss_add_part1");
		//		
		// HashMap<String, String> parameterMap = new HashMap<String, String>();
		// String time = TimeAndOtherUtil.getCurrentMonth();
		// parameterMap.put("dataBasePart", "PART_APP_LOG_"+time);
		// parameterMap.put("beginTime",
		// parameterMap.put("endTime", TimeAndOtherUtil.getTodayStartTimeStr());
		// String afterReplace = replaceParameter(sqlData, parameterMap,false);
		// log.debug(afterReplace);
		// sUtil.exec(afterReplace);
		// HashMap<String, String> parMap = new HashMap<String, String>();
		// parMap.put("beginTime", "2012-07-09 00:00:00");
		// parMap.put("endTime", "2012-07-09 00:00:00");
		// String afterReplace = replaceParameter(sqlData, parMap, false);
		// log.debug("afterReplace======="+afterReplace);

		try {
			HashMap<String, String> czMap = new HashMap<String, String>();
			String sql = "select t.num,WMSYS.WM_CONCAT(T.Czvalue) as czvalue from a4_4ar t group by t.num";
			Connection conn = DBUtil.getAiuap20Connection();
			ResultSet rs_cz = DBUtil.getQueryResultSet(conn, sql);
			while (rs_cz.next()) {
				String num = rs_cz.getString("num");
				String czvalue = rs_cz.getString("czvalue");
				czMap.put(num, czvalue);
			}
			Set set = czMap.keySet();
			Iterator it = set.iterator();
			StringBuffer messageBuffer = new StringBuffer();
			while (it.hasNext()) {
				String num = (String) it.next();
				if ("".equals(czMap.get(num)) || czMap.get(num) == null) {
					messageBuffer.append(" " + num + ",");
				}
			}
			DBUtil.notice("【4A】请注意登录和操作文件中第" + messageBuffer + "个区间操作值为0！开始启动应急措施~~~");

		} catch (Exception e) {
		}
	}

	public void exec(String sql) {
		// log.debug("sql is============" + sql);
		try {
			Connection connection = DBUtil.getAiuap20Connection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				log.debug("result=:" + resultSet.getString(3));
			} else {
				log.debug("====sorry,noting queryed ! ");
			}
		} catch (Exception e) {
		}
	}

	/**
	 * @Title: replaceParameter
	 * @Description: 根据参数名称替换SQL里面对应的值
	 * @param sql
	 * @param parameterMap
	 * @param 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String replaceParameter(String sql, HashMap<String, String> parameterMap) {
		Set set = parameterMap.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			String nextParameter = (String) it.next();
			if (nextParameter.equals("dataBasePart") || nextParameter.equals("timeForTable")) {
				sql = sql.replaceAll("#" + nextParameter.trim() + "#", parameterMap.get(nextParameter).trim());
			} else {
				sql = sql.replaceAll("#" + nextParameter.trim() + "#", "'" + parameterMap.get(nextParameter).trim()
						+ "'");
			}
		}
		// log.debug("===after replace ,SQL is ====" + sql);
		return sql;
	}

	/**
	 * @Title: replaceParameter
	 * @Description: 根据参数名称替换SQL里面对应的值,替换的时候是否带引号替换
	 * @param sql
	 *            :替换的SQL语句
	 * @param parameterMap
	 *            ：要替换SQL里面的参数值
	 * @param quote：参数是否用引号替换，true代表带引号替换，false代表不带引号替换
	 * @return String 返回替换后的 SQL語句
	 */
	public static String replaceParameter(String sql, HashMap<String, String> parameterMap, boolean quote) {
		if (quote) {
			return replaceParameter(sql, parameterMap);
		} else {
			Set set = parameterMap.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String nextParameter = (String) it.next();
				sql = sql.replaceAll("#" + nextParameter.trim() + "#", parameterMap.get(nextParameter).trim());
			}
			return sql;
		}
	}

	/**
	 * @Title: getSql
	 * @Description: 从输入文件名和SQL名称，读取XML，反馈该SQL语句
	 * @param sqlFile
	 *            指定存放SQL的文件名称
	 * @param sqlName
	 *            指定SQL名称
	 * @return String SQL语句
	 */
	public static String getSql(String sqlFile, String sqlName) {
		try {
			// 加载SQL.xml文件找到需要使用的SQL文件
			File sqlXml = ResourceUtils.getFile(sqlFile);
			log.debug("===sql file path=====" + sqlXml.getAbsolutePath());
			// 按照XML文件的格式解析该文件
			SAXReader reader = new SAXReader();
			Document document = reader.read(sqlXml);
			Element root = document.getRootElement();
			List elementList = root.elements();
			for (int i = 0; i < elementList.size(); i++) {
				Element element = (Element) elementList.get(i);
				if (element.attributeValue("name").equals(sqlName)) {
					return element.getData().toString().trim();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("===exception occured in read sql file======" + e.getLocalizedMessage());
		}
		log.debug("=====sorry! sqlName：" + sqlName + " not find ======");
		return null;
	}

	/**
	 * @Title: getSql
	 * @Description: 从默认的SQL文件中根据指定的SQL名称，读取XML，返回该SQL语句
	 * @param sqlName
	 * @return String 去除该SQL语句的空白字符，并返回
	 */
	public static String getSql(String sqlName) {
		try {
			// 加载SQL.xml文件找到需要使用的SQL文件
			File sqlXml = ResourceUtils.getFile(PropertiesUtil.getValue("sql_file_path"));
			log.debug("===sql file path=====" + sqlXml.getAbsolutePath());
			// 按照XML文件的格式解析该文件
			SAXReader reader = new SAXReader();
			Document document = reader.read(sqlXml);
			Element root = document.getRootElement();
			List elementList = root.elements();
			for (int i = 0; i < elementList.size(); i++) {
				Element element = (Element) elementList.get(i);
				if (element.attributeValue("name").equals(sqlName)) {
					return element.getData().toString().trim();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("===exception occured in read sql file======" + e.getLocalizedMessage());
		}
		log.debug("=====sorry! sqlName \t" + sqlName + " not find ======");
		return null;
	}

	/**
	 * @Title: valueMap
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param sql
	 * @throws SQLException
	 *             设定文件
	 * @return HashMap<String,String> 返回类型
	 */
	public static HashMap<String, String> valueMap(String sql) throws SQLException {
		HashMap<String, String> czMap = new HashMap<String, String>();
		// String sql = "select t.num,WMSYS.WM_CONCAT(T.Czvalue) as czvalue from
		// a4_4ar t group by t.num";
		Connection conn = DBUtil.getAiuap20Connection();
		ResultSet rs_cz = DBUtil.getQueryResultSet(conn, sql);
		while (rs_cz.next()) {
			String num = rs_cz.getString("1");
			String czvalue = rs_cz.getString("2");
			czMap.put(num, czvalue);
		}
		return czMap;
	}
}
