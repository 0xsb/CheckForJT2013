package com.ailk.uap.makefile4new;

public class AuthorFullFileTest  extends AbstractMakeMonthFileTestCase{
	
	@Override
	public AbstractMakeFile getAbstractMakeFile() {
		return new AuthorFullFile();
	}

	@Override
	public String getFileType() {
		return "SMMSF";
	}
}
