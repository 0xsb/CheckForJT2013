package com.ailk.jt.validate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SaveErrorFileUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

public abstract class AbstractValidateDayFile extends AbstractValidateFile{

	protected static final Configuration configuration = ConfigurationManager.getConfiguration();
	protected static Logger log = Logger.getLogger(AbstractValidateDayFile.class);
	protected static Properties tran = PropertiesUtil.getProperties("/tran.properties");
	protected static String right_Date = PropertiesUtil.getValue("right_Date").trim();
	protected static String right_Datebegin = PropertiesUtil.getValue("right_Datebegin").trim();
	protected static String now_Date = "";
	protected static String now_Datebegin = "";
	protected static String rightdate = "";
	protected static String uploadPath = PropertiesUtil.getValue("uap_file_uapload");
	protected static String osflag = PropertiesUtil.getValue("os_flag");
	protected static String dayFilePath = PropertiesUtil.getValue("uap_file_uapload_for_day_dir_safe");
	
	protected  String nowPath = PropertiesUtil.getValue("uap_file_uapload_for_"+ getFileType().toLowerCase()+ "_db_now");
	protected  String rightFirstStr = getFileType() +"_371_01DY_"+right_Datebegin.replace("-", "")+"_";

	 protected  void validate() {
	        String uploadFileName = "";
	        try {
	            String nowFileName = OperateFile.searchEndFile(nowPath, "xml");
	            uploadFileName = nowPath + osflag + nowFileName;
	            String[] nowStrs = nowFileName.split("_");

	            boolean resultSMMAI = FileValidator.validate(uploadFileName, nowPath);

	            if (!resultSMMAI) {
	                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("validateError"));
	                replaceFile();
	                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
	                //
	                HashMap<String,String> dateMap = new HashMap<String,String>();
	                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
	                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
	                dateMap.put("file_name", nowFileName);
	                dateMap.put("file_sum", "0");
	                dateMap.put("file_error_reason",tran.getProperty("validateError").trim().substring(0, 4));
	                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
	                SaveErrorFileUtil.saveErrorFile(dateMap);
	            }
	            if (resultSMMAI) {
	                int sum = getSumVal(uploadFileName);
	                int seq = getSeqCountVal(uploadFileName);
	                if ((sum <= 0) || (sum != seq)) {
	                    DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("sumNotEqualSeq"));

	                    String rightFileStr = rightFirstStr + nowStrs[4] + "_" + nowStrs[5];
	                    OperateFile.copyFile(new File(dayFilePath + osflag + rightFileStr), new File(uploadPath + osflag
	                            + nowFileName));

	                    chanageXmlData(uploadPath + osflag + nowFileName);
	                    DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSum"));
	                    //
	                    HashMap<String,String> dateMap = new HashMap<String,String>();
	                    dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
	                    dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
	                    dateMap.put("file_name", nowFileName);
	                    dateMap.put("file_sum", sum+"");
	                    dateMap.put("file_error_reason",tran.getProperty("sumNotEqualSeq").trim().substring(0, 11));
	                    dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
	                    SaveErrorFileUtil.saveErrorFile(dateMap);
	                } else {
	                    OperateFile.copyFile(new File(uploadFileName), new File(uploadPath + osflag + nowFileName));
	                }
	            }

	            OperateFile.deleteFileOrDir(uploadFileName);
	        } catch (Exception e) {
	            try {
	                String nowFileNameSafe = getFileType()+"_371_01DY_" + rightdate + "_000_000.xml";
	                uploadFileName = nowPath + osflag + nowFileNameSafe;
	                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
	                replaceFile();
	                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
	                //
	                HashMap<String,String> dateMap = new HashMap<String,String>();
	                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
	                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
	                dateMap.put("file_name", nowFileNameSafe);
	                dateMap.put("file_sum", "0");
	                dateMap.put("file_error_reason",tran.getProperty("notGernerated").trim().substring(0, 5));
	                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
	                SaveErrorFileUtil.saveErrorFile(dateMap);
	            } catch (Exception e1) {
	                e1.printStackTrace();
	            }
	        }
	    }

	  public static int getSumVal(String filePath) {
	        Document doc = load(filePath);

	        List list = doc.selectNodes("/smp/sum");
	        Iterator iter = list.iterator();
	        if (iter.hasNext()) {
	            Element sumElement = (Element) iter.next();
	            if ("".equals(sumElement.getText())) {
	                return 0;
	            }
	            return Integer.valueOf(sumElement.getText()).intValue();
	        }

	        return 0;
	    }

	    public static int getSeqCountVal(String filePath) {
	        Document doc = load(filePath);
	        List list = doc.selectNodes("/smp/data/rcd/seq");
	        return list.size();
	    }
	    
	    public static Document load(String filename) {
	        Document document = null;
	        try {
	            SAXReader reader = new SAXReader();
	            document = reader.read(new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8")));
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return document;
	    }

	 public abstract String getFileType();
	 public abstract void replaceFile();
	 public abstract void chanageXmlData(String fileName);
}
