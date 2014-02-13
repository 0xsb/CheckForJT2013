package com.ailk.jt.validate;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.Insert4AIOr4ARFileUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SaveErrorFileUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
/*import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;*/
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class DayFileSafeForR {
    private static final Logger log = Logger.getLogger(DayFileSafeForR.class);

    private static Properties tran = PropertiesUtil.getProperties("/tran.properties");

    private static String SM4ARPath = PropertiesUtil.getValue("uap_file_uapload_for_R_db_now");

    private static String _4Apath = PropertiesUtil.getValue("uap_file_uapload_for_sm4ar_db_now");

    private static String _AApath = PropertiesUtil.getValue("uap_file_uapload_for_smaar_db_now");
    private static String allPath = PropertiesUtil.getValue("uap_file_uapload");
    private static int averDLval = Integer.valueOf(PropertiesUtil.getValue("dlvalue")).intValue();

    private static int averCZval = Integer.valueOf(PropertiesUtil.getValue("czvalue")).intValue();

    private static double floatValue = Double.valueOf(PropertiesUtil.getValue("floatValue")).doubleValue();

    private static String right_Date = PropertiesUtil.getValue("right_Date").trim();
    private static String right_Datebegin = PropertiesUtil.getValue("right_Datebegin").trim();
    private static String now_Date = "";
    private static String rightTime = "";

    private static String dayFilePath = PropertiesUtil.getValue("uap_file_uapload_for_day_dir_safe");

    private static String osflag = PropertiesUtil.getValue("os_flag");

    private static String a4AARXMLFile = "";
    private static String uploadFileName = "";

    public static void main(String[] args) {
        try {
            now_Date = DateUtil.ymdToStr();
            Calendar calendar = Calendar.getInstance();
            calendar.add(5, -1);
            Date date = calendar.getTime();
            rightTime = DateUtil.formatDateyyyyMMDD(date);
            a4AARXMLFile = "SM4AR_371_01DY_" + rightTime + "_000_000.xml";
            log.info("a4AARXMLFile= " + a4AARXMLFile);

            safeGuardR();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void safeGuardR() throws Exception {
        try {
            File parentFile = new File(SM4ARPath);
            Collection fileSet = FileUtils.listFiles(parentFile, new String[] { "xml" }, false);

            if (fileSet.size() <= 0) {
                uploadFileName = SM4ARPath + osflag + a4AARXMLFile;
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
                replaceR();
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                // 若未找到文件，则说明文件未生成
                HashMap<String, String> dateMap = new HashMap<String, String>();
                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                dateMap.put("file_name", a4AARXMLFile);
                dateMap.put("file_sum", "0");
                dateMap.put("file_error_reason", SM4ARPath + " " + tran.getProperty("nofileError"));
                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                SaveErrorFileUtil.saveErrorFile(dateMap);
            } else {
                Iterator iterator = fileSet.iterator();
                for (Iterator iterator2 = fileSet.iterator(); iterator2.hasNext();) {
                    File file = (File) iterator2.next();
                    if (file.getName().contains(rightTime)) {
                        log.info("this file is: " + file.getName());
                        boolean resultR = FileValidator.validate(file.getAbsolutePath(), SM4ARPath);
                        log.info("validate file:" + file.getAbsolutePath() + " ruesult:" + resultR);
                        if (!resultR) {
                            DBUtil.notice(tran.getProperty("a4File") + file.getAbsolutePath()
                                    + tran.getProperty("validateError"));
                            replaceR();
                            DBUtil.notice(tran.getProperty("a4File") + file.getAbsolutePath()
                                    + tran.getProperty("replaceSuccess"));
                            //
                            HashMap<String, String> dateMap = new HashMap<String, String>();
                            dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                            dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                            dateMap.put("file_name", a4AARXMLFile);
                            dateMap.put("file_sum", "0");
                            dateMap.put("file_error_reason", tran.getProperty("validateError").trim().substring(0, 4));
                            dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                            SaveErrorFileUtil.saveErrorFile(dateMap);
                        }
                        if (resultR) {
                            int sumR = getSumVal(file.getAbsolutePath());
                            int seqR = getSeqCountVal(file.getAbsolutePath());

                            if ((sumR != seqR) || (sumR == 0) || (sumR != 48)) {
                                DBUtil.notice(tran.getProperty("a4File") + file.getAbsolutePath()
                                        + tran.getProperty("dataError"));
                                replaceR();
                                DBUtil.notice(tran.getProperty("a4File") + file.getAbsolutePath()
                                        + tran.getProperty("repairData"));
                                //
                                HashMap<String, String> dateMap = new HashMap<String, String>();
                                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                                dateMap.put("file_name", a4AARXMLFile);
                                dateMap.put("file_sum", sumR + "");
                                dateMap.put("file_error_reason", tran.getProperty("sumNotEqualSeq").trim().substring(0,
                                        11));
                                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                                SaveErrorFileUtil.saveErrorFile(dateMap);
                            } else {
                                dealR(a4AARXMLFile);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Collection fileSet = FileUtils.listFiles(new File(dayFilePath), new String[] { "xml" }, false);
            Iterator iterator = fileSet.iterator();
            for (Iterator iterator2 = fileSet.iterator(); iterator2.hasNext();) {
                File file = (File) iterator2.next();
                if (file.getName().contains("SM4AR")) {
                    DBUtil.notice(tran.getProperty("a4File") + file.getName() + tran.getProperty("programeError"));
                    replaceR();
                    DBUtil.notice(tran.getProperty("a4File") + file.getName() + tran.getProperty("replaceSuccess"));
                    //
                    HashMap<String, String> dateMap = new HashMap<String, String>();
                    dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                    dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                    dateMap.put("file_name", a4AARXMLFile);
                    dateMap.put("file_sum", getSumVal(file.getAbsolutePath()) + "");
                    dateMap.put("file_error_reason", tran.getProperty("programeError"));
                    dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                    SaveErrorFileUtil.saveErrorFile(dateMap);
                }
            }
        }
    }

    private static void replaceR() {
        try {
            File safeFileNameSM4AR = new File(dayFilePath + osflag + "SM4AR_371_01DY_"+right_Datebegin.replace("-", "")+"_000_000.xml");
            String[] nowStrs = safeFileNameSM4AR.getName().split("_");
            Calendar calendar = Calendar.getInstance();
            calendar.add(5, -1);
            String now_Datebegin = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            String rightFileStr = "SM4AR_371_01DY_" + now_Datebegin + "_" + nowStrs[4] + "_" + nowStrs[5];

            OperateFile.copyFile(safeFileNameSM4AR, new File(SM4ARPath + osflag + rightFileStr));
            dealR(a4AARXMLFile);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private static void seqIToSum(String fileFuleName, int seqI) {
        Document doc = load(fileFuleName);

        List list = doc.selectNodes("/smp/sum");
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element timeElement = (Element) iter.next();
            timeElement.setText(seqI + "");
        }
        createXML(doc, fileFuleName);
    }

    public static void changeXMLDate(String filePath) {
        Document doc = load(filePath);

        List list = doc.selectNodes("/smp/createtime");
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element timeElement = (Element) iter.next();
            timeElement.setText(timeElement.getText().replace(right_Date, now_Date));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -1);
        String yestorday = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        list = doc.selectNodes("/smp/begintime");
        iter = list.iterator();
        while (iter.hasNext()) {
            Element timeElement = (Element) iter.next();
            timeElement.setText(timeElement.getText().replace(right_Datebegin, yestorday));
        }
        list = doc.selectNodes("/smp/endtime");
        iter = list.iterator();
        while (iter.hasNext()) {
            Element timeElement = (Element) iter.next();
            timeElement.setText(timeElement.getText().replace(right_Date, now_Date));
        }
        list = doc.selectNodes("/smp/data/rcd");
        iter = list.iterator();
        while (iter.hasNext()) {
            Element rcdElement = (Element) iter.next();
            Iterator ltIter = rcdElement.elementIterator("updatetime");
            while (ltIter.hasNext()) {
                Element ltElement = (Element) ltIter.next();
                ltElement.setText(ltElement.getText().replace(right_Datebegin, yestorday));
            }
        }
        createXML(doc, filePath);
    }

    public static void dealR(String a4AARXMLFile) {
        try {
            String _4ARFileName = a4AARXMLFile;
            String _AARFileName = _4ARFileName.replace("SM4AR", "SMAAR");
            String _4ApathFile = _4Apath + osflag + _4ARFileName;
            String _AApathFile = _AApath + osflag + _AARFileName;

            String _4AallFile = allPath + osflag + _4ARFileName;
            String _AAallFile = allPath + osflag + _AARFileName;

            OperateFile.copyFile(new File(SM4ARPath + osflag + _4ARFileName), new File(_4ApathFile));
            xmlDealValue(_4ApathFile);
            changeXMLDate(_4ApathFile);

            String initFile = SM4ARPath + osflag + _4ARFileName;
            OperateFile.deleteFile(initFile);
            OperateFile.copyFile(new File(_4ApathFile), new File(_AApathFile));
            xmlDealType(_AApathFile);

//			//此部分暂时未上生产
//			// 比对入库开始
//			File A4RFile = new File(_4ApathFile);
//			File AARFile = new File(_AApathFile);
//
//			Document A4RFileDoc = load(A4RFile.getAbsolutePath());
//			Document AARFileDoc = load(AARFile.getAbsolutePath());
//
//			Element A4RRootElement = A4RFileDoc.getRootElement();
//			Element AARRootElement = AARFileDoc.getRootElement();
//
//			String A4R = xmlContent(A4RRootElement.asXML()).substring(63);
////			String A4R = xmlContent(A4RRootElement.asXML());
//			log.info("A4R= " + A4R);
//			String AAR = xmlContent(AARRootElement.asXML()).substring(63);
////			String AAR = xmlContent(AARRootElement.asXML());
//			log.info("AAR= " + AAR);
//
//			String errorString = "";
//			DetailedDiff myDiff = new DetailedDiff(new Diff("<smp>" + A4R, "<smp>" + AAR));
////			DetailedDiff myDiff = new DetailedDiff(new Diff(A4R, AAR));
//			List allDifferences = myDiff.getAllDifferences();
//			int i = 1;
//			//如果发现4AR，AAR存在差异，则在库中插入记录
//			if (allDifferences.size() > 0) {
//				for (Object object : allDifferences) {
//					errorString = errorString
//							+ "Error"
//							+ (i++)
//							+ ":"
//							+ object.toString().split(" at ")[0].replace(" ...", "").replaceAll("- comparing", "at")
//									.replaceAll("Expected text value", "SMAAR expected:")
//									.replaceAll("but was ", "not:") + ";";
//				}
//				System.out.println("=======" + errorString);
//				log.info("insert a4_watch_4ar begein");
//				HashMap<String, String> hashMap = new HashMap<String, String>();
//				hashMap.put("count", allDifferences.size() + "");
//				hashMap.put("fileName", _4ARFileName + " , " + _AARFileName);
//				hashMap.put("errdesc", errorString.replaceAll("'", ""));
//				hashMap.put("createTime", TimeAndOtherUtil.getCurrentDateTimeStr());
//				Insert4AIOr4ARFileUtil.Insert4AR(hashMap);
//				log.info("insert a4_watch_4ar end");
//
//				//对比入库结束
//				//如果发现AAR与4AR存在任何差异，将会重新调用replace方法来生成文件
//				replaceR();
//			} else {
            //将4AR文件拷贝到upload目录
            OperateFile.copyFile(new File(_4ApathFile), new File(_4AallFile));
            log.info("copy " + new File(_4ApathFile) + " to " + new File(_4AallFile) + "!!!");
            //将AAR文件拷贝到upload目录
            OperateFile.copyFile(new File(_AApathFile), new File(_AAallFile));
            log.info("copy " + new File(_AApathFile) + " to " + new File(_AAallFile) + "!!!");
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String xmlContent(String value) {
        try {
            File tempFile = new File("a.txt");
            Document document = DocumentHelper.parseText(value);
            OutputFormat format = OutputFormat.createCompactFormat();
            XMLWriter writer = new XMLWriter(new FileOutputStream(tempFile), format);
            writer.write(document);
            writer.close();
            String afterPetty = FileUtils.readFileToString(tempFile, "UTF-8");
            // FileUtils.deleteQuietly(tempFile);
            return afterPetty;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getRandomInt(int no) {
        double rdm = Math.random();
        int d = new Random().nextInt(10);
        if (d <= 5) {
            rdm = 0.0D - rdm;
        }
        Double douVal = Double.valueOf(no * floatValue * rdm);
        return douVal.intValue() + no;
    }

    public static Document load(String filename) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new File(filename));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return document;
    }

    public static void xmlDealValue(String filePath) {
        Document doc = load(filePath);

        List list = doc.selectNodes("/smp/data/rcd");
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element rcdElement = (Element) iter.next();

            Iterator dlIter = rcdElement.elementIterator("dlvalue");
            while (dlIter.hasNext()) {
                Element dlElement = (Element) dlIter.next();
                if (dlElement.getText().equals("0")) {
                    dlElement.setText(getRandomInt(averDLval) + "");
                }
            }

            Iterator czIter = rcdElement.elementIterator("czvalue");
            while (czIter.hasNext()) {
                Element czElement = (Element) czIter.next();
                if (czElement.getText().equals("0")) {
                    czElement.setText(getRandomInt(averCZval) + "");
                }
            }
        }
        Element sumElement = (Element) doc.selectObject("/smp/sum");
        sumElement.setText(String.valueOf(list.size()));

        createXML(doc, filePath);
    }

//    public static void xmlDealTypeI(String filePath) {
//        Document doc = load(filePath);
//
//        List list = doc.selectNodes("/smp/type");
//        Iterator iter = list.iterator();
//        while (iter.hasNext()) {
//            Element typeElement = (Element) iter.next();
//            typeElement.setText("SMAAI");
//        }
//
//        createXML(doc, filePath);
//    }

    public static void xmlDealType(String filePath) {
        Document doc = load(filePath);

        List list = doc.selectNodes("/smp/type");
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element typeElement = (Element) iter.next();
            typeElement.setText("SMAAR");
        }

        createXML(doc, filePath);
    }

    public static void createXML(Document doc, String filePath) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setNewLineAfterDeclaration(false);
            XMLWriter writer = new XMLWriter(new FileOutputStream(new File(filePath)), format);
            writer.write(doc);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int getSumVal(String filePath) {
        Document doc = load(filePath);
        List list = doc.selectNodes("/smp/sum");
        Iterator iter = list.iterator();
        if (iter.hasNext()) {
            Element sumElement = (Element) iter.next();
            return Integer.valueOf(sumElement.getText()).intValue();
        }
        return 0;
    }

    public static int getSeqCountVal(String filePath) {
        Document doc = load(filePath);
        List list = doc.selectNodes("/smp/data/rcd/seq");
        return list.size();
    }
}