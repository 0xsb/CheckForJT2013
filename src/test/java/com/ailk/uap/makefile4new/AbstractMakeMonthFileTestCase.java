package com.ailk.uap.makefile4new;

import junit.framework.Assert;
import org.junit.Test;

public abstract class AbstractMakeMonthFileTestCase extends AbstractMakeFileTestCase{

	@Test
	public void getIntervalTest() {
		Assert.assertEquals("01MO",abstractMakeFile.getIntval());
	}


	@Override
	public String getFileKind() {
		return "month";
	}
	
}
