package com.ailk.jt.validate;

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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SaveErrorFileUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

public class DayFileSafeForI {
    private static final Logger log = Logger.getLogger(DayFileSafeForI.class);

    private static Properties tran = PropertiesUtil.getProperties("/tran.properties");

    private static String right_Date = PropertiesUtil.getValue("right_Date").trim();
    private static String right_Datebegin = PropertiesUtil.getValue("right_Datebegin").trim();
    private static String now_Date = "";
    private static String rightTime = "";

    private static String dayFilePath = PropertiesUtil.getValue("uap_file_uapload_for_day_dir_safe");
    private static String allPath = PropertiesUtil.getValue("uap_file_uapload");

    private static String rootPathI = PropertiesUtil.getValue("uap_file_uapload_for_I_db_now");

    private static String _4ApathI = PropertiesUtil.getValue("uap_file_uapload_for_sm4ai_db_now");

    private static String _AApathI = PropertiesUtil.getValue("uap_file_uapload_for_smaai_db_now");

    private static String osflag = PropertiesUtil.getValue("os_flag");

    private static String a4AAIXMLFile = "";
    private static String a4AARXMLFile = "";
    private static String uploadFileName = "";

    public static void main(String[] args) {
        try {
            String nowDateStr = DateUtil.formatDateyyyyMMDD(new Date());
            now_Date = DateUtil.ymdToStr();
            Calendar calendar = Calendar.getInstance();
            calendar.add(5, -1);
            Date date = calendar.getTime();
            rightTime = DateUtil.formatDateyyyyMMDD(date);
            a4AAIXMLFile = "SM4AI_371_01DY_" + rightTime + "_000_000.xml";
            log.info("a4AAIXMLFile= " + a4AAIXMLFile);

            safeGuardI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void safeGuardI() throws Exception {
        try {
            File parentFile = new File(rootPathI);
            Collection fileSet = FileUtils.listFiles(parentFile, new String[] { "xml" }, false);
            if (fileSet.size() <= 0) {
                uploadFileName = rootPathI + osflag + a4AAIXMLFile;
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
                replaceI();
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                // 若未找到文件，则说明文件未生成
                HashMap<String, String> dateMap = new HashMap<String, String>();
                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                dateMap.put("file_name", a4AAIXMLFile);
                dateMap.put("file_sum", "0");
                dateMap.put("file_error_reason", parentFile.getAbsolutePath() + tran.getProperty("nofileError"));
                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                SaveErrorFileUtil.saveErrorFile(dateMap);
            } else {
                Iterator iterator = fileSet.iterator();
                for (Iterator iterator2 = fileSet.iterator(); iterator2.hasNext();) {
                    File file = (File) iterator2.next();
                    if (file.getName().contains(rightTime)) {
                        log.info("this file is: " + file.getName());
                        boolean resultI = FileValidator.validate(file.getAbsolutePath(), rootPathI);
                        log.info("validate file:" + file.getAbsolutePath() + " ruesult:" + resultI);
                        if (!resultI) {
                            DBUtil.notice(tran.getProperty("a4File") + file.getAbsolutePath()
                                    + tran.getProperty("validateError"));
                            replaceI();
                            DBUtil.notice(tran.getProperty("a4File") + file.getAbsolutePath()
                                    + tran.getProperty("replaceSuccess"));
                            //
                            HashMap<String, String> dateMap = new HashMap<String, String>();
                            dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                            dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                            dateMap.put("file_name", a4AAIXMLFile);
                            dateMap.put("file_sum", "0");
                            dateMap.put("file_error_reason", tran.getProperty("validateError").trim().substring(0, 4));
                            dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                            SaveErrorFileUtil.saveErrorFile(dateMap);
                        }
                        if (resultI) {
                            int sumR = getSumVal(file.getAbsolutePath());
                            int seqR = getSeqCountVal(file.getAbsolutePath());

                            if ((sumR != seqR) || (sumR == 0)) {
                                DBUtil.notice(tran.getProperty("a4File") + file.getAbsolutePath()
                                        + tran.getProperty("dataError"));
                                replaceI();
                                DBUtil.notice(tran.getProperty("a4File") + file.getAbsolutePath()
                                        + tran.getProperty("repairData"));
                                //
                                HashMap<String, String> dateMap = new HashMap<String, String>();
                                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                                dateMap.put("file_name", a4AAIXMLFile);
                                dateMap.put("file_sum", sumR + "");
                                dateMap.put("file_error_reason", tran.getProperty("sumNotEqualSeq").trim().substring(0,
                                        11));
                                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                                SaveErrorFileUtil.saveErrorFile(dateMap);
                            } else {
                                dealI(a4AAIXMLFile);
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
                if (file.getName().contains("SM4AI")) {
                    DBUtil.notice(tran.getProperty("a4File") + file.getName() + tran.getProperty("programeError"));
                    replaceI();
                    DBUtil.notice(tran.getProperty("a4File") + file.getName() + tran.getProperty("replaceSuccess"));
                    //
                    HashMap<String, String> dateMap = new HashMap<String, String>();
                    dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                    dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                    dateMap.put("file_name", a4AAIXMLFile);
                    dateMap.put("file_sum", getSumVal(file.getAbsolutePath()) + "");
                    dateMap.put("file_error_reason", tran.getProperty("programeError"));
                    dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                    SaveErrorFileUtil.saveErrorFile(dateMap);
                }
            }
        }
    }

    private static void replaceI() {
        try {
            File safeFileNameSM4AI = new File(dayFilePath + osflag + "SM4AI_371_01DY_"+right_Datebegin.replace("-", "")+"_000_000.xml");
            String[] nowStrs = safeFileNameSM4AI.getName().split("_");
            String dateStr = nowStrs[3];

            Calendar calendar = Calendar.getInstance();
            calendar.add(5, -1);
            String now_Datebegin = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            String rightFileStr = "SM4AI_371_01DY_" + now_Datebegin  + "_" + nowStrs[4] + "_" + nowStrs[5];
            OperateFile.copyFile(safeFileNameSM4AI, new File(rootPathI + osflag + rightFileStr));
            changeXMLDate(rootPathI + osflag + rightFileStr);
            dealI(a4AAIXMLFile);
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

    private static String returnFind(Element element, String childName, String childValue) {
        List elements = element.elements();
        String result = "";
        for (Iterator it = elements.iterator(); it.hasNext();) {
            Element currentelement = (Element) it.next();
            if (!currentelement.getName().equals(childName)) { // 当前元素并不是我们要查找的父元素时，继续查找
                returnFind(currentelement, childName, childValue);// 递归调用
            } else {
                result = currentelement.getParent().getStringValue().substring(0, 1);
                System.out.println("++++++++++ " + "seq:" + result);
            }
        }
        return result;
    }

    public static void dealI(String a4AAIXMLFile) {
        try {
            String _4AIFileName = a4AAIXMLFile;
            String _AAIFileName = _4AIFileName.replace("SM4AI", "SMAAI");
            String _4ApathFile = _4ApathI + osflag + _4AIFileName;
            String _AApathFile = _AApathI + osflag + _AAIFileName;

            String _4AallFile = allPath + osflag + _4AIFileName;
            String _AAallFile = allPath + osflag + _AAIFileName;

            String srcFile = rootPathI + osflag + _4AIFileName;
            String targetFile = _4ApathFile;
            // 将I目录文件拷贝一份到4AI目录
            OperateFile.copyFile(new File(srcFile), new File(targetFile));

            String initFile = rootPathI + osflag + _4AIFileName;
            // 删除I目录文件
            OperateFile.deleteFile(initFile);

            OperateFile.copyFile(new File(_4ApathFile), new File(_AApathFile));
            // 改变AAI目录中文件SMAAI的类型
            xmlDealTypeI(_AApathFile);

            //此部分暂时未上生产-----------begin
            // 比对入库
//			File A4IFile = new File(_4ApathFile);
//			File AAIFile = new File(_AApathFile);
//
//			Document A4IFileDoc = load(A4IFile.getAbsolutePath());
//			Document AAIFileDoc = load(AAIFile.getAbsolutePath());
//
//			Element A4IRootElement = A4IFileDoc.getRootElement();
//			Element AAIRootElement = AAIFileDoc.getRootElement();
//
////			String A4I = xmlContent(A4IRootElement.asXML()).substring(63);
//			String A4I = xmlContent(A4IRootElement.asXML());
//			log.info("A4I= " + A4I);
////			String AAI = xmlContent(AAIRootElement.asXML()).substring(63);
//			String AAI = xmlContent(AAIRootElement.asXML());
//			log.info("AAI= " + AAI);
//
//			Document document = DocumentHelper.parseText(AAI);
//			Element element = document.getRootElement();
//
//			DetailedDiff myDiff = new DetailedDiff(new Diff(A4I, AAI));
//			List allDifferences = myDiff.getAllDifferences();
//			int i = 1;
//			String errorString = "";
//			String errElement = "";
//			String errElementString = "";
//			String errElementVal = "";
//			String errSingle = "";
//			if (allDifferences.size() > 0) {
//				for (Object object : allDifferences) {
//					errorString = errorString
//							+ "Error"
//							+ (i++)
//							+ ":"
//							+ object.toString().split(" at ")[0].replace(" ...", "").replaceAll("- comparing", "at")
//									.replaceAll("Expected text value", "SMAAI expected:").replaceAll("but was ", "not:")
//							+ ";";
//				}
//				System.out.println("########## " + errorString);
//				String[] errArry = errorString.split(";");
//				for (int j = 0; j < errArry.length; j++) {
//					errSingle = errArry[j].substring(errArry[j].indexOf("<"), errArry[j].length());
//					System.out.println("errSingle="+errSingle);
////					errElement = errSingle.substring(errorString.indexOf("<"), errSingle.length());
//					errElementString = errSingle.substring(1, errSingle.indexOf(">", 1));
//					System.out.println("***********  " + errElementString);
//					errElementVal = errSingle.substring(errSingle.indexOf(">"), errSingle.indexOf("<",2)).substring(1);
//					System.out.println("*******" + errElementVal);
//					returnFind(element, errElementString, errElementVal);
//				}
//
//
//
//				System.out.println("=======" + errorString);
//				log.info("insert a4_watch_4ai begein");
//				HashMap<String, String> hashMap = new HashMap<String, String>();
//				hashMap.put("count", allDifferences.size() + "");
//				hashMap.put("fileName", _4AIFileName + " , " + _AAIFileName);
//				hashMap.put("errdesc", errorString.replaceAll("'", ""));
//				hashMap.put("createTime", TimeAndOtherUtil.getCurrentDateTimeStr());
//				Insert4AIOr4ARFileUtil.Insert4AI(hashMap);
//				log.info("insert a4_watch_4ai end");
//
////				replaceI();
//			}
            //此部分未上生产-----end
            // 将4AI目录文件拷贝到upload目录
            OperateFile.copyFile(new File(_4ApathFile), new File(_4AallFile));
            log.info("copy " + new File(_4ApathFile) + " to " + new File(_4AallFile) + "!!!");
            // 将AAI目录文件拷贝到upload目录
            OperateFile.copyFile(new File(_AApathFile), new File(_AAallFile));
            log.info("copy " + new File(_AApathFile) + " to " + new File(_AAallFile) + "!!!");
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

    public static void xmlDealTypeI(String filePath) {
        Document doc = load(filePath);

        List list = doc.selectNodes("/smp/type");
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element typeElement = (Element) iter.next();
            typeElement.setText("SMAAI");
        }

        createXML(doc, filePath);
    }

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