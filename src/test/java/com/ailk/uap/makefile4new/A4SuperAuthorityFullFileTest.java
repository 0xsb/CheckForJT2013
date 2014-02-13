package com.ailk.uap.makefile4new;

import junit.framework.Assert;
import org.junit.Test;

public class A4SuperAuthorityFullFileTest extends AbstractMakeMonthFileTestCase{
	
	@Override
	public AbstractMakeFile getAbstractMakeFile() {
		return new A4SuperAuthorityFullFile();
	}

	@Override
	public String getFileType() {
		return "SMSMF";
	}
	
	@Test
	public void lowerCaseTest() {
		Assert.assertEquals("abc", "ABC".toLowerCase());
	}
}
