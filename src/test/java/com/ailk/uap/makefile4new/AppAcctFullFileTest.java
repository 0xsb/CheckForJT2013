package com.ailk.uap.makefile4new;


public class AppAcctFullFileTest extends AbstractMakeMonthFileTestCase{
	
	@Override
	public AbstractMakeFile getAbstractMakeFile() {
		return new AppAcctFullFile();
	}

	@Override
	public String getFileType() {
		return "SMAAF";
	}
	
}
