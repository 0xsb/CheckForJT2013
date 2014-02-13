package com.ailk.uap.makefile4new;

import junit.framework.Assert;

import org.junit.Test;

public class DeviceSubAcctLoginFileTest {

	@Test
	public void monthLastHourTest() {
		String createTime = "2013-12-01 01:03:02";
		String beginTime = "2013-12-01 00:00:00";
		String endTime = "2013-12-01 01:00:00";
		String partitionMonth = beginTime.substring(0,7).replaceAll("-", "");
		Assert.assertEquals("201312", partitionMonth);
	}
	
	@Test
	public void monthFirstHourTest() {
		String createTime = "2013-12-01 00:03:01";
		String beginTime = "2013-11-30 23:00:00";
		String endTime = "2013-12-01 00:00:00";
		String partitionMonth = beginTime.substring(0,7).replaceAll("-", "");
		Assert.assertEquals("201311", partitionMonth);
	}
	
	@Test
	public void yearLastMonthLastHourTest() {
		String createTime = "2014-01-01 00:03:01";
		String beginTime = "2013-12-31 23:00:00";
		String endTime =  "2014-01-01 00:00:01";
		String partitionMonth = beginTime.substring(0,7).replaceAll("-", "");
		Assert.assertEquals("201312", partitionMonth);
	}
}
