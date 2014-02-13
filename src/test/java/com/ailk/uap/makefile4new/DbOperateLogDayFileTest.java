package com.ailk.uap.makefile4new;

public class DbOperateLogDayFileTest extends AbstractMakeDayFileTestCase{

	@Override
	public AbstractMakeFile getAbstractMakeFile() {
		AbstractMakeFile makeFile = new DbOperateLogDayFile();
		return makeFile;
	}

	@Override
	public String getFileType() {
		return "SMDAR";
	}

}
