package com.ailk.uap.makefile4new;

import java.sql.Timestamp;
import java.util.Calendar;

import com.ailk.uap.util.DatetimeServices;

public abstract class AbstractMakeHourFile extends AbstractMakeFile{

	public static final String TYPE_SMMAL = "SMMAL";
	public static final String TYPE_SMSAL = "SMSAL";
	
	private Calendar calendar = Calendar.getInstance();
	private Timestamp a;
	
//	public void init() {
//		calendar.set(12, 0);
//		calendar.set(13, 0);
//		calendar.set(14, 0);
//		 a = new Timestamp(calendar.getTimeInMillis());
//		String end = a.toString().replaceAll("\\.0", "");
//		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
//		a = new Timestamp(calendar.getTimeInMillis());
//		String begin = a.toString().replaceAll("\\.0", "");
//		String tab_month = begin.substring(0, 7).replace("-", "");
//	}
	
	
	@Override
	public String getBeginTime() throws Exception {
		return a.toString().replaceAll("\\.0", "");
	}

	@Override
	public String getBeginTimeWithT() throws Exception {
		return null;
	}

	@Override
	public String getCreateTime() throws Exception {
		return DatetimeServices.getNowDateTimeStr(conn).replaceAll(" ", "T");
	}

	@Override
	public String getEndTime() throws Exception {
		return  a.toString().replaceAll("\\.0", "");
	}

	@Override
	public String getEndTimeWithT() throws Exception {
		return null;
	}

	@Override
	public String getFileNameDate() throws Exception {
		return getBeginTime().substring(0, 10).replaceAll("-", "");
	}

	@Override
	public String getIntval() {
		return "01HR";
	}

}
