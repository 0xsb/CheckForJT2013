package com.ailk.uap.makefile4new;

import com.ailk.uap.util.DatetimeServices;

/**
 * 抽象文天件创建公共基类
 * 		基于模板模式建造公共文件基类,公共文件创建流程相同，具体实现
 * 细节不同.
 * @date 2014-01-21
 * @author liujie 
 * 		   liujie_09_24@163.com
 * 
 * @version 4.0
 * @since 4.0
 * @see com.ailk.uap.makefile4new.AbstractMakeFile
 */
public abstract class AbstractMakeDayFile extends AbstractMakeFile{

	public static final String TYPE_SMMAI = "SMMAI";
	public static final String TYPE_SM4AI = "SM4AI";
	public static final String TYPE_SMAAI = "SMAAI";
	public static final String TYPE_SM4AR = "SM4AR";
	public static final String TYPE_SMAAR = "SMAAR";
	public static final String TYPE_SMBHR = "SMBHR";
	public static final String TYPE_SMDAR = "SMDAR";
	public static final String TYPE_SMFLO = "SMFLO";
	public static final String TYPE_SMFWL = "SMFWL";
	public static final String TYPE_SMJKR = "SMJKR";
	public static final String TYPE_SMJKA = "SMJKA";
	
	public String getBeginTime() throws Exception {
		return DatetimeServices.getLastDayStartTimeStr(conn);
	}

	public String getBeginTimeWithT() throws Exception {
		return DatetimeServices.getLastDayStartTimeStrWithT(conn);
	}

	public String getCreateTime() throws Exception {
		return DatetimeServices.getNowDateTimeStrWithT(conn);
	}

	public String getEndTime() throws Exception {
		return DatetimeServices.getTodayStartTimeStr(conn);
	}

	public String getEndTimeWithT() throws Exception {
		return  DatetimeServices.getTodayStartTimeStrWithT(conn);
	}

	public String getFileNameDate() throws Exception {
		return DatetimeServices.getLastDayStr(conn);
	}

	@Override
	public String getIntval() {
		return "01DY";
	}

}
