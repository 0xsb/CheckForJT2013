package com.ailk.uap.makefile4new;

public class A4AppAcctDayAddFileTest extends AbstractMakeDayFileTestCase{

	@Override
	public AbstractMakeFile getAbstractMakeFile() {
		AbstractMakeFile makeFile = new A4AppAcctDayAddFile();
		return makeFile;
	}

	@Override
	public String getFileType() {
		return "SM4AI";
	}

}
