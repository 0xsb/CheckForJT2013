package com.ailk.uap.makefile4new;

import com.ailk.uap.util.DatetimeServices;

/**
 * 抽象文件创建基类，月文件创建基类
 * 		月文件创建文件开始时间相同
 * 		月文件创建文件结束时间相同
 * 		月文件创建文件文件统计周期相同
 * 		
 * @date 2013-12-25
 * @author liujie 
 * 		   liujie_09_24@163.com
 * @version 4.0
 * @since 4.0
 * @see com.ailk.uap.makefile4new.AbstractMakeFile
 */
public abstract  class AbstractMakeMonthFile extends AbstractMakeFile{

	public static final String TYPE_SMMAF = "SMMAF";
	public static final String TYPE_SMAAF = "SMAAF";
	public static final String TYPE_SMMSF = "SMMSF";
	public static final String TYPE_SMSMF = "SMSMF";
	public static final String TYPE_SMCRF = "SMCRF";
	public static final String TYPE_SMHAF = "SMHAF";
	
	public  String getCreateTime() throws Exception {
		return DatetimeServices.getNowDateTimeStrWithT(conn);
	}

	public  String getBeginTime() throws Exception {
		return DatetimeServices.getLastMonthFirstDayStr(conn);
	}

	public  String getBeginTimeWithT() throws Exception {
		return DatetimeServices.getLastMonthFirstDayStrWithT(conn);
	}

	public  String getEndTimeWithT() throws Exception {
		return DatetimeServices.getCurrentMonthFirstDayStrWithT(conn);
	}

	public  String getEndTime() throws Exception {
		return DatetimeServices.getCurrentMonthFirstDayStr(conn);
	}
	
	public  String getFileNameDate() throws Exception{
		return DatetimeServices.getLastMonthFirstDayDateStr(conn);
	}
	
	public  String getIntval() {
		return "01MO";
	}
}
