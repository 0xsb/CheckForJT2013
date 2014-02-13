package com.ailk.uap.makefile4new;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.ailk.jt.util.PropertiesUtil;

public abstract class AbstractMakeFileTestCase {
	
	protected AbstractMakeFile abstractMakeFile ;
	
	@Before
	public void setUp() {
		abstractMakeFile = getAbstractMakeFile();
	}
	
	@Test
	public  void readConfig() {
		assertEquals("D:\\work\\work2012\\db\\"+ getFileKind() +"\\"+getFileType()+ "",PropertiesUtil.getValue("uap_file_uapload_for_"+getFileType().toLowerCase()+"_db_now"));
		assertEquals("uap2.0",PropertiesUtil.getValue("uap_version"));
		assertEquals("371",PropertiesUtil.getValue("prov_code"));
		assertEquals("D:\\work\\work2012\\temp",PropertiesUtil.getValue("uap_file_uapload_temp"));
	}
	
	@Test
	public void makeFileTest() {
		abstractMakeFile.makeFile();
	}
	
	public abstract AbstractMakeFile getAbstractMakeFile();
	public abstract String getFileType();
	public abstract String getFileKind();
}
