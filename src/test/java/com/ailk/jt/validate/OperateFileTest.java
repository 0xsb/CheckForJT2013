package com.ailk.jt.validate;

import org.junit.Ignore;
import org.junit.Test;

import com.ailk.jt.util.PropertiesUtil;

public class OperateFileTest {
	
	private static String nowPathSMSMF = PropertiesUtil.getValue("uap_file_uapload_for_smsmf_db_now");
	
	@Test
	@Ignore("when some files in one dir, OperateFile.searchEndFile method will not search right file")
	public void searchEndFileTest() throws Exception {
		String nowFileNameSMF = OperateFile.searchEndFile(nowPathSMSMF, "xml");
		System.out.println("nowFileNameSMF=" + nowFileNameSMF);
	}
}
