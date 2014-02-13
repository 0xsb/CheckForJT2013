package com.ailk.jt.validate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SQLUtil;

//
// create table a4_smjkr_for_jk 
// ( 
//   calculateTime timestamp(6) 
//   beginTime timestamp(6) , 
//   endTime   timestamp(6) ,
//   contextId   number(7,0),   
//   qqvalue   varchar2(20),   
//   yxvalue   varchar2(20),   
//   jjvalue   varchar2(20),   
//   csvalue   varchar2(20),   
//   czvalue   varchar2(20) 
// )
public class CalcSMJKR {
	private final static Logger log = Logger.getLogger(CalcSMJKR.class);
	private static String fileFoder = PropertiesUtil.getValue("uap_file_uapload_for_smjkr_db_now");
	// 1、获取当前时间
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		Long beginTime = System.currentTimeMillis();
		try {
			log.debug(" run CalcSMJKR......,current time is:" + beginTime);
			CalcSMJKR cs = new CalcSMJKR();

			// 1、从文件保存目录里面查找文件,并保存到文件集合中
			Collection<File> fileCollection = FileUtils.listFiles(new File(fileFoder), new String[] { "xml" }, false);

			if (fileCollection.size() <= 0) {// 在目录中未找到任何xml文件
				log.debug(" not found  SMJKR file !!");
			} else {
				for (File file : fileCollection) {// 循环遍历文件集合中的文件
					if (file.getName().contains("JKR")) {// 目前只计算SMJKR文件数值
						log.debug(" SMJKR file path is:" + file.getAbsolutePath());

						ArrayList<SMJKRBean> arrayList = cs.isTrueQQ(cs.load(file));
						log.debug("【4A】截止到:" + sdf.format(Calendar.getInstance().getTime()) + ",SMJKR文件:"
								+ arrayList.toString());
						// 2、将记录保存到数据库中
						cs.saveResult(sdf.format(Calendar.getInstance().getTime()), arrayList);
						// 3、从表中查询截止到目前为止金库使用情况
						// 因为数据已经保存到表中，只需要写一个sql分组并求一下和即可。
						HashMap<String, String> pareMap = new HashMap<String, String>();
						pareMap.put("qqvalueCount", "请求量为0的场景");
						pareMap.put("yxvalueCount", "允许量为0的场景");
						String tempSql = SQLUtil.getSql("a4_smjkr_from_file");
						HashMap<String, ArrayList<String>> resultMap = queryResult(pareMap, tempSql);
						// 发送短信
						Set<String> jkzbResultSet = resultMap.keySet();
						StringBuffer noticeMessage = new StringBuffer(300);
						noticeMessage.append("【4A】截止到:" + sdf.format(Calendar.getInstance().getTime()) + ",");
						for (Iterator iterator = jkzbResultSet.iterator(); iterator.hasNext();) {
							// 循环遍历监控指标集合
							String zbName = (String) iterator.next();
							// 获取指标对应为空的列表
							ArrayList<String> resultArrayList = resultMap.get(zbName);
							noticeMessage.append(pareMap.get(zbName) + ":" + resultArrayList + " ");
						}
						DBUtil.notice(noticeMessage.toString());
						System.out.println(noticeMessage);
					}
				}
			}
			Long endTime = System.currentTimeMillis();
			log.debug("end program:" + endTime + " ,program cost time:" + (endTime - beginTime));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据Map中传过来的监控要求，遍历数据库中数据
	 * 
	 * @param paraMap
	 *            需要监控的指标
	 * @param querySql
	 *            监控指标用到的SQL
	 * @return
	 */
	public static HashMap<String, ArrayList<String>> queryResult(HashMap<String, String> paraMap, String tempSql) {
		HashMap<String, ArrayList<String>> jkContextList = new HashMap<String, ArrayList<String>>();
		Connection connection = DBUtil.getAiuap20Connection();
		PreparedStatement ps = null;
		ResultSet result = null;
		try {
			Set<String> jkzbSet = paraMap.keySet();
			for (Iterator iterator = jkzbSet.iterator(); iterator.hasNext();) {
				ArrayList<String> tempArrayList = new ArrayList<String>();
				// 循环遍历监控指标集合
				String zbName = (String) iterator.next();
				HashMap<String, String> parHashMap = new HashMap<String, String>();
				parHashMap.put("valueCount", zbName);
				// 获取当月第一天
				String monthBeginTime = getFirstDayOfMonth();
				String endTime = getYesterday();
				parHashMap.put("monthBeginTime","'" + monthBeginTime + " 00:00:00'");
				parHashMap.put("endTime", "'" + endTime + "'");
				String querySql = SQLUtil.replaceParameter(tempSql, parHashMap, false);
				log.debug("querySql====" + querySql);
				// 从库中查询该指标的使用情况
				ps = connection.prepareStatement(querySql);
				result = ps.executeQuery();

				while (result.next()) {
					String contextid = result.getString("contextid");
					tempArrayList.add(contextid);
				}
				jkContextList.put(zbName, tempArrayList);
			}
			log.debug(jkContextList);
			return jkContextList;
		} catch (Exception e) {
			DBUtil.closeConnection(connection);
			e.printStackTrace();
		} finally {
			DBUtil.closeConnection(connection);
		}
		return null;
	}

	private static String getYesterday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DAY_OF_MONTH, -1);
		return sdf.format(cd.getTime())+" 00:00:00";
	}

	public void saveResult(String time, ArrayList<SMJKRBean> arrayList) {
		Connection connection = DBUtil.getAiuap20Connection();
		try {
			for (SMJKRBean bean : arrayList) {
				StringBuffer sBuffer = new StringBuffer(300);// (calcateTime,contextId,qqvalue,yxvalue,jjvalue,csvalue,czvalue)
				sBuffer.append("insert into a4_smjkr_for_jk values");
				sBuffer.append(" (to_date('" + time + "','yyyy-MM-dd hh24:Mi:ss')").append(
						",to_date('" + bean.getBeginTime() + "','yyyy-MM-dd hh24:Mi:ss')").append(
						",to_date('" + bean.getEndTime() + "','yyyy-MM-dd hh24:Mi:ss')").append(",").append(
						bean.getContextId()).append(",").append(bean.getQqvalue()).append(",")
						.append(bean.getYxvalue()).append(",").append(bean.getJjvalue()).append(",").append(
								bean.getCsvalue()).append(",").append(bean.getCzvalue()).append(")");
				System.out.println(sBuffer);
				DBUtil.executeSQL(connection, sBuffer.toString());
			}
		} catch (Exception e) {
			DBUtil.closeConnection(connection);
			e.printStackTrace();
		} finally {
			DBUtil.closeConnection(connection);
		}
	}

	/***************************************************************************
	 * <rcd> <seq>4</seq> <contextid>1</contextid> <num>4</num> <qqvalue>0</qqvalue>
	 * <yxvalue>0</yxvalue> <jjvalue>0</jjvalue> <csvalue>0</csvalue>
	 * <czvalue>0</czvalue> </rcd> List list = doc.selectNodes(
	 * "/bomc/data/rcd[qqvalue=yxvalue+jjvalue+csvalue and yxvalue=czvalue]");
	 */
	private ArrayList<SMJKRBean> isTrueQQ(Document doc) {
		// 查找所有元素的场景ID
		List<Element> contextIdsList = doc.selectNodes("/bomc/data/rcd/contextid");
		TreeSet<Integer> valueSet = new TreeSet<Integer>();
		for (Element element : contextIdsList) {
			valueSet.add(Integer.valueOf(element.getText()));
		}
		log.debug("contextId set:" + valueSet);

		ArrayList<SMJKRBean> arrayList = new ArrayList<SMJKRBean>();
		// 按照文件中扫描到的场景，计算指标
		for (int contextId : valueSet) {
			SMJKRBean temp = new SMJKRBean();
			temp = genarateBean(doc, temp, contextId, "qqvalue");
			temp = genarateBean(doc, temp, contextId, "yxvalue");
			temp = genarateBean(doc, temp, contextId, "jjvalue");
			temp = genarateBean(doc, temp, contextId, "csvalue");
			temp = genarateBean(doc, temp, contextId, "czvalue");
			log.debug(temp);
			arrayList.add(temp);// 临时保存
		}
		return arrayList;
	}

	public SMJKRBean genarateBean(Document doc, SMJKRBean bean, int contextId, String value) {
		// 计算请求值QQValue
		List<Element> valueList = doc.selectNodes("/bomc/data/rcd[contextid=" + contextId + " and " + value + "!=0 ]");
		int valueFromFile = 0;
		for (Element element : valueList) {
			Element valueDetails = element.element(value);
			valueFromFile += Integer.valueOf(valueDetails.getText());
		}
		// 设置开始时间，结束时间
		String beginTimeString = doc.selectSingleNode("/bomc/begintime").getText();
		bean.setBeginTime(beginTimeString.replace("T", " "));

		String endTimeString = doc.selectSingleNode("/bomc/endtime").getText();
		bean.setEndTime(endTimeString.replace("T", " "));

		// 设置场景ID
		bean.setContextId(contextId);
		// 设置集中操作类型
		if (value.contains("qqvalue")) {
			bean.setQqvalue(valueFromFile);
		} else if (value.contains("yxvalue")) {
			bean.setYxvalue(valueFromFile);
		} else if (value.contains("jjvalue")) {
			bean.setJjvalue(valueFromFile);
		} else if (value.contains("csvalue")) {
			bean.setCsvalue(valueFromFile);
		} else if (value.contains("czvalue")) {
			bean.setCzvalue(valueFromFile);
		}

		return bean;
	}

	// 载入一个xml文档
	public Document load(File filename) {
		Document document = null;
		try {
			SAXReader saxReader = new SAXReader();
			document = saxReader
					.read(new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8")));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return document;
	}

	/**
	 * 获取当月第一天
	 * 
	 * @return firstDay
	 */
	public static String getFirstDayOfMonth() {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, 1);
		str = sdf.format(lastDate.getTime());
		return str;
	}
}

class SMJKRBean {
	private int contextId;
	private String beginTime;
	private String endTime;
	private int qqvalue;
	private int yxvalue;
	private int jjvalue;
	private int csvalue;
	private int czvalue;

	public SMJKRBean() {
		super();
	}

	public int getContextId() {
		return contextId;
	}

	public void setContextId(int contextId) {
		this.contextId = contextId;
	}

	public int getQqvalue() {
		return qqvalue;
	}

	public void setQqvalue(int qqvalue) {
		this.qqvalue = qqvalue;
	}

	public int getYxvalue() {
		return yxvalue;
	}

	public void setYxvalue(int yxvalue) {
		this.yxvalue = yxvalue;
	}

	public int getJjvalue() {
		return jjvalue;
	}

	public void setJjvalue(int jjvalue) {
		this.jjvalue = jjvalue;
	}

	public int getCsvalue() {
		return csvalue;
	}

	public void setCsvalue(int csvalue) {
		this.csvalue = csvalue;
	}

	public int getCzvalue() {
		return czvalue;
	}

	public void setCzvalue(int czvalue) {
		this.czvalue = czvalue;
	}

	public String toString() {
		return "contextId->" + this.contextId + ":qqvalue->" + this.qqvalue + ":yxvalue->" + this.yxvalue
				+ ":jjvalue->" + this.jjvalue + ":csvalue->" + this.csvalue + ":czvalue->" + this.czvalue;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
