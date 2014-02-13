package com.ailk.uap.makefile4new;

public class A4ResourceCoverFullFileTest extends AbstractMakeMonthFileTestCase{
	
	@Override
	public AbstractMakeFile getAbstractMakeFile() {
		return new A4ResourceCoverFullFile();
	}

	@Override
	public String getFileType() {
		return "SMCRF";
	}
	
}
