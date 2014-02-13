package com.ailk.uap.makefile4new;

public class SysResourceAcctFullFileTest extends AbstractMakeMonthFileTestCase{
	
	@Override
	public AbstractMakeFile getAbstractMakeFile() {
		return new SysResourceAcctFullFile();
	}

	@Override
	public String getFileType() {
		return "SMHAF";
	}
}
