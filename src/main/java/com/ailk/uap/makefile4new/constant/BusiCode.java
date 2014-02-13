package com.ailk.uap.makefile4new.constant;

import com.ailk.uap.config.PropertiesUtil;

public abstract interface BusiCode {
	public static final String BOSS_APPCODE = PropertiesUtil
			.getValue("boss_appcode");

	public static final String CRM_APPCODE = PropertiesUtil
			.getValue("crm_appcode");

	public static final String JF_APPCODE = PropertiesUtil
			.getValue("jf_appcode");

	public static final String BOMC_APPCODE = PropertiesUtil
			.getValue("bomc_appcode");
	
	public static final String SMP_APPCODE = PropertiesUtil
	.getValue("smp_appcode");

	public static final String VGOP_APPCODE = PropertiesUtil
			.getValue("vgop_appcode");

	public static final String JFVGOP_APPCODE = PropertiesUtil
			.getValue("jfvgop_appcode");

	public static final String ESOP_APPCODE = PropertiesUtil
			.getValue("esop_appcode");

	public static final String CRMESOP_APPCODE = PropertiesUtil
			.getValue("crmesop_appcode");

	public static final String OTHER_APPCODE = PropertiesUtil
			.getValue("other_appcode");

	public static final String STR_SPLIT_SEPARATOR = ",";

	//SMAAF需要
	public static final String APP_COMMON_SQL_OF_RESTYPE = "\tCASE WHEN INSTR('"
			+ BOSS_APPCODE
			+ "' ,APP.APP_CODE,1,1)>0 THEN '11' "
			+ "WHEN INSTR('"
			+ CRM_APPCODE
			+ "'  ,APP.APP_CODE,1,1)>0 THEN '12'" 
			+ "WHEN INSTR('"
			+ JF_APPCODE
			+ "'  ,APP.APP_CODE,1,1)>0 THEN '13'" 
			+ "WHEN INSTR('"
			+ BOMC_APPCODE
			+ "'  ,APP.APP_CODE,1,1)>0 THEN '14'" 
			+ "WHEN INSTR('"
			+ VGOP_APPCODE
			+ "'  ,APP.APP_CODE,1,1)>0 THEN '15'" 
			+ "WHEN INSTR('"
			+ JFVGOP_APPCODE
			+ "'  ,APP.APP_CODE,1,1)>0 THEN '16'" 
			+ "WHEN INSTR('"
			+ ESOP_APPCODE
			+ "'  ,APP.APP_CODE,1,1)>0 THEN '17'" 
			+ "WHEN INSTR('"
			+ CRMESOP_APPCODE
			+ "'  ,APP.APP_CODE,1,1)>0 THEN '18'" 
			+ "WHEN INSTR('"
			+ SMP_APPCODE
			+ "'  ,APP.APP_CODE,1,1)>0 THEN '19'" 
			+ "  ELSE '99' END   ";
	
	//SMCRF需要
	public static final String APP_SMCRF_SQL_OF_RESTYPE = "\tCASE WHEN INSTR('"
		+ BOSS_APPCODE
		+ "' ,APP.APP_CODE,1,1)>0 THEN '11' "
		+ "WHEN INSTR('"
		+ CRM_APPCODE
		+ "'  ,APP.APP_CODE,1,1)>0 THEN '12'" 
		+ "WHEN INSTR('"
		+ JF_APPCODE
		+ "'  ,APP.APP_CODE,1,1)>0 THEN '13'" 
		+ "WHEN INSTR('"
		+ SMP_APPCODE
		+ "'  ,APP.APP_CODE,1,1)>0 THEN '14'" 
		+ "WHEN INSTR('"
		+ VGOP_APPCODE
		+ "'  ,APP.APP_CODE,1,1)>0 THEN '15'" 
		+ "WHEN INSTR('"
		+ JFVGOP_APPCODE
		+ "'  ,APP.APP_CODE,1,1)>0 THEN '16'" 
		+ "WHEN INSTR('"
		+ ESOP_APPCODE
		+ "'  ,APP.APP_CODE,1,1)>0 THEN '17'" 
		+ "WHEN INSTR('"
		+ CRMESOP_APPCODE
		+ "'  ,APP.APP_CODE,1,1)>0 THEN '18'" 
		+ "  ELSE '99' END   ";
	
	public static final String APP_ONLY_CRM_SQL_OF_RESTYPE = "\tCASE WHEN INSTR('"
		+ BOSS_APPCODE
		+ "' ,APP.APP_CODE,1,1)>0 THEN '12' "
		+ "WHEN INSTR('"
		+ CRM_APPCODE
		+ "'  ,APP.APP_CODE,1,1)>0 THEN '12'" 
		+ "  ELSE '99' END   ";
}