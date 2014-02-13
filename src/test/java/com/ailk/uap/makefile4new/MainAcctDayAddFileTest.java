package com.ailk.uap.makefile4new;

public class MainAcctDayAddFileTest  extends AbstractMakeDayFileTestCase{

	@Override
	public AbstractMakeFile getAbstractMakeFile() {
		AbstractMakeFile makeFile = new MainAcctDayAddFile();
		return makeFile;
	}

	@Override
	public String getFileType() {
		return "SMMAI";
	}
	
}
