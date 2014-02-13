package com.ailk.uap.makefile4new;


import junit.framework.Assert;

import org.junit.Test;
import com.ailk.uap.config.PropertiesUtil;

public class MainAcctFullFileTest  extends AbstractMakeMonthFileTestCase{
	
	@Override
	public AbstractMakeFile getAbstractMakeFile() {
		return new MainAcctFullFile();
	}

	@Override
	public String getFileType() {
		return "SMMAF";
	}
	
	@Test
	public void generateMainAcctFullFileTest() {
		String main_acct_month_table = PropertiesUtil.getValue("main_acct_month_table");
		Assert.assertEquals("a4_main_acct_jt", main_acct_month_table);
	}
}
