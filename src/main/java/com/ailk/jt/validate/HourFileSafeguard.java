package com.ailk.jt.validate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ailk.jt.util.DBUtil;
import com.ailk.jt.util.DateUtil;
import com.ailk.jt.util.PropertiesUtil;
import com.ailk.jt.util.SaveErrorFileUtil;
import com.ailk.jt.util.TimeAndOtherUtil;

public class HourFileSafeguard {
    private static Logger log = Logger.getLogger(HourFileSafeguard.class);
    private static Properties tran = PropertiesUtil.getProperties("/tran.properties");

    private static String right_Date = PropertiesUtil.getValue("right_Date").trim();
    private static String right_Datebegin = PropertiesUtil.getValue("right_Datebegin").trim();
    private static String now_Date = "";
    private static String uploadPath = PropertiesUtil.getValue("uap_file_uapload_hour");
    private static String osflag = PropertiesUtil.getValue("os_flag");

    private static String nowPathSMMAL = PropertiesUtil.getValue("uap_file_uapload_for_smmal_db_now");
    private static String rightPathSMMAL = PropertiesUtil.getValue("uap_file_uapload_for_smmal_dir_safe");
    private static String rightFirstStrSMMAL = "SMMAL_371_01HR_"+right_Date.replace("-", "")+"_";

    private static String nowfilenameSMMALString;

    private static String nowPathSMSAL = PropertiesUtil.getValue("uap_file_uapload_for_smsal_db_now");
    private static String rightPathSMSAL = PropertiesUtil.getValue("uap_file_uapload_for_smsal_dir_safe");
    private static String rightFirstStrSMSAL = "SMSAL_371_01HR_"+right_Date.replace("-", "")+"_";

    public static void main(String[] args) {
        String nowDateStr = DateUtil.formatDateyyyyMMDD(new Date());
        now_Date = DateUtil.ymdToStr();

        log.info("validate SMMAL begin--------------------------------------------------------------");
        dealSMMAL(nowDateStr);
        log.info("validate SMMAL end--------------------------------------------------------------");

        log.info("validate SMSAL begin--------------------------------------------------------------");
        dealSMSAL(nowDateStr);
        log.info("validate SMSAL end--------------------------------------------------------------");
    }

    private static void dealSMSAL(String nowDateStr) {
        String uploadFileName = "";
        Calendar calendar = Calendar.getInstance();
        int hourFlag = calendar.get(Calendar.HOUR_OF_DAY);
        String file_begin_time = "";
        if (hourFlag == 0) {
            file_begin_time = TimeAndOtherUtil.getLastDayStartTimeStr().trim().substring(0, 11) + 23 + ":00:00";
        } else {
            file_begin_time = TimeAndOtherUtil.timeToStringFormater(new Date()).trim().substring(0, 11)
                    + (hourFlag - 1) + ":00:00";
        }
        try {
            String nowFileName = OperateFile.searchEndFile(nowPathSMSAL, "xml");

            String[] nowStrs = nowFileName.split("_");
            String dateStr = nowStrs[3];
            int hourNO = Integer.valueOf(nowStrs[4]).intValue();
            uploadFileName = nowPathSMSAL + osflag + nowFileName;

            boolean resultSMSAL = FileValidator.validate(uploadFileName, nowPathSMSAL);
            if (!resultSMSAL) {
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("validateError"));
                SMSALFileNotExits(nowDateStr);
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                //
                HashMap<String, String> dateMap = new HashMap<String, String>();
                dateMap.put("file_begin_time", file_begin_time);
                dateMap.put("file_end_time", TimeAndOtherUtil.timeToStringFormater(new Date()).trim().substring(0, 13)
                        + ":00:00");
                dateMap.put("file_name", nowFileName);
                dateMap.put("file_sum", "0");
                dateMap.put("file_error_reason", tran.getProperty("validateError").trim().substring(0, 4));
                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                SaveErrorFileUtil.saveErrorFile(dateMap);
                /*//为了测试，把所有小时都放开，正式上线时，还是按照早八点到晚八点之间
            } else if ((resultSMSAL) && (nowDateStr.equals(dateStr))) {*/
            } else if ((resultSMSAL) && (hourNO >= 9) && (hourNO <= 20) && (nowDateStr.equals(dateStr))) {
                int sum = getSumVal(nowPathSMSAL + osflag + nowFileName);
                int seq = getSeqCountVal(nowPathSMSAL + osflag + nowFileName);

                if ((sum <= 0) || (sum != seq)) {
                    DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("hourInWork"));

                    String rightFileStr = rightFirstStrSMSAL + nowStrs[4] + "_" + nowStrs[5];
                    OperateFile.copyFile(new File(rightPathSMSAL + osflag + rightFileStr), new File(uploadPath + "/"
                            + nowFileName));

                    changeXMLDate(uploadPath + osflag + nowFileName);
                    DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("hourInWorkSum"));
                    //
                    HashMap<String, String> dateMap = new HashMap<String, String>();
                    dateMap.put("file_begin_time", file_begin_time);
                    dateMap.put("file_end_time", TimeAndOtherUtil.timeToStringFormater(new Date()).trim().substring(0,
                            13)
                            + ":00:00");
                    dateMap.put("file_name", nowFileName);
                    dateMap.put("file_sum", sum + "");
                    dateMap.put("file_error_reason", tran.getProperty("sumNotEqualSeq").trim().substring(0, 11));
                    dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                    SaveErrorFileUtil.saveErrorFile(dateMap);
                } else {
                    OperateFile.copyFile(new File(nowPathSMSAL + osflag + nowFileName), new File(uploadPath + "/"
                            + nowFileName));
                }
            } else {
                OperateFile.copyFile(new File(nowPathSMSAL + osflag + nowFileName), new File(uploadPath + "/"
                        + nowFileName));
            }

            OperateFile.deleteFileOrDir(nowPathSMSAL + osflag + nowFileName);
            log.info(nowPathSMSAL + osflag + nowFileName + " has success deleted!");
        } catch (Exception e) {
            String nowhour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "";
            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.DATE, -1);
            Date date = calendar2.getTime();
            if (nowhour.equals("0")) {
                nowhour = "24";
                nowDateStr = new SimpleDateFormat("yyyyMMdd").format(date);
            } else if (nowhour.length() < 2) {
                nowhour = "0" + nowhour;
            }

            String nowFileName = "SMSAL_371_01HR_" + nowDateStr + "_0" + nowhour + "_000.xml";
            uploadFileName = nowPathSMSAL + osflag + nowFileName;
            DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
            SMSALFileNotExits(nowDateStr);
            DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
            //
            HashMap<String, String> dateMap = new HashMap<String, String>();
            dateMap.put("file_begin_time", file_begin_time);
            dateMap.put("file_end_time", TimeAndOtherUtil.timeToStringFormater(new Date()).trim().substring(0, 13)
                    + ":00:00");
            dateMap.put("file_name", nowFileName);
            dateMap.put("file_sum", "0");
            dateMap.put("file_error_reason", tran.getProperty("notGernerated").trim().substring(0, 5));
            dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
            SaveErrorFileUtil.saveErrorFile(dateMap);
        }
    }

    private static void dealSMMAL(String nowDateStr) {
        String uploadFileName = "";
        Calendar calendar = Calendar.getInstance();
        int hourFlag = calendar.get(Calendar.HOUR_OF_DAY);
        String file_begin_time = "";
        if (hourFlag == 0) {
            file_begin_time = TimeAndOtherUtil.getLastDayStartTimeStr().trim().substring(0, 11) + 23 + ":00:00";
        } else {
            file_begin_time = TimeAndOtherUtil.timeToStringFormater(new Date()).trim().substring(0, 11)
                    + (hourFlag - 1) + ":00:00";
        }
        try {
            String nowFileName = OperateFile.searchEndFile(nowPathSMMAL, "xml");
            nowfilenameSMMALString = nowFileName;
            String[] nowStrs = nowFileName.split("_");
            String dateStr = nowStrs[3];
            int hourNO = Integer.valueOf(nowStrs[4]).intValue();
            uploadFileName = nowPathSMMAL + osflag + nowFileName;

            boolean resultSMMAL = FileValidator.validate(uploadFileName, nowPathSMMAL);
            if (!resultSMMAL) {
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("validateError"));
                SMMALFileNotExits(nowDateStr);
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                //
                HashMap<String, String> dateMap = new HashMap<String, String>();
                dateMap.put("file_begin_time", file_begin_time);
                dateMap.put("file_end_time", TimeAndOtherUtil.timeToStringFormater(new Date()).trim().substring(0, 13)
                        + ":00:00");
                dateMap.put("file_name", nowFileName);
                dateMap.put("file_sum", "0");
                dateMap.put("file_error_reason", tran.getProperty("validateError").trim().substring(0, 4));
                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                SaveErrorFileUtil.saveErrorFile(dateMap);
                /*//为了测试，把所有小时都放开，正式上线时，还是按照早八点到晚八点之间
            } else if ((resultSMMAL) && (nowDateStr.equals(dateStr))) {*/
            } else if ((resultSMMAL) && (hourNO >= 9) && (hourNO <= 20) && (nowDateStr.equals(dateStr))) {
                int sum = getSumVal(nowPathSMMAL + osflag + nowFileName);
                int seq = getSeqCountVal(nowPathSMMAL + osflag + nowFileName);

                if ((sum <= 0) || (sum != seq)) {
                    DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("hourInWork"));

                    String rightFileStr = rightFirstStrSMMAL + nowStrs[4] + "_" + nowStrs[5];
                    OperateFile.copyFile(new File(rightPathSMMAL + osflag + rightFileStr), new File(uploadPath + "/"
                            + nowFileName));

                    changeXMLDate(uploadPath + osflag + nowFileName);
                    DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("hourInWorkSum"));
                    //
                    HashMap<String, String> dateMap = new HashMap<String, String>();
                    dateMap.put("file_begin_time", file_begin_time);
                    dateMap.put("file_end_time", TimeAndOtherUtil.timeToStringFormater(new Date()).trim().substring(0,
                            13)
                            + ":00:00");
                    dateMap.put("file_name", nowFileName);
                    dateMap.put("file_sum", sum + "");
                    dateMap.put("file_error_reason", tran.getProperty("sumNotEqualSeq").trim().substring(0, 11));
                    dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                    SaveErrorFileUtil.saveErrorFile(dateMap);
                } else {
                    OperateFile.copyFile(new File(nowPathSMMAL + osflag + nowFileName), new File(uploadPath + "/"
                            + nowFileName));
                }
            } else {
                OperateFile.copyFile(new File(nowPathSMMAL + osflag + nowFileName), new File(uploadPath + "/"
                        + nowFileName));
            }

            OperateFile.deleteFileOrDir(nowPathSMMAL + osflag + nowFileName);
        } catch (Exception e) {
            String nowhour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "";
            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.DATE, -1);
            Date date = calendar2.getTime();
            if (nowhour.equals("0")) {
                nowhour = "24";
                nowDateStr = new SimpleDateFormat("yyyyMMdd").format(date);
            } else if (nowhour.length() < 2) {
                nowhour = "0" + nowhour;
            }

            String nowFileName = "SMMAL_371_01HR_" + nowDateStr + "_0" + nowhour + "_000.xml";
            uploadFileName = nowPathSMMAL + osflag + nowFileName;
            DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
            SMMALFileNotExits(nowDateStr);
            DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
            //
            HashMap<String, String> dateMap = new HashMap<String, String>();
            dateMap.put("file_begin_time", file_begin_time);
            dateMap.put("file_end_time", TimeAndOtherUtil.timeToStringFormater(new Date()).trim().substring(0, 13)
                    + ":00:00");
            dateMap.put("file_name", nowFileName);
            dateMap.put("file_sum", "0");
            dateMap.put("file_error_reason", tran.getProperty("notGernerated").trim().substring(0, 5));
            dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
            SaveErrorFileUtil.saveErrorFile(dateMap);
        }
    }

    private static void SMMALFileNotExits(String now_Datebegin) {
        try {
            Calendar calendar = Calendar.getInstance();
            String hourFlag = calendar.get(Calendar.HOUR_OF_DAY) + "";

            String filePath = "";

            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.DATE, -1);
            Date date = calendar2.getTime();

            if (hourFlag.equals("0")) {
                filePath = rightPathSMMAL + osflag + rightFirstStrSMMAL + "024_000.xml";
                now_Datebegin = new SimpleDateFormat("yyyyMMdd").format(date);
            } else {
                if (hourFlag.length() < 2) {
                    hourFlag = "0" + hourFlag;
                }
                filePath = rightPathSMMAL + osflag + rightFirstStrSMMAL + "0" + hourFlag + "_000.xml";
            }
            File safeFileNameSMMAL = new File(filePath);
            String[] nowStrs = safeFileNameSMMAL.getName().split("_");

            // String now_Datebegin = new SimpleDateFormat("yyyyMMdd")
            // .format(calendar.getTime());
            String rightFileStr = "SMMAL_371_01HR_" + now_Datebegin + "_" + nowStrs[4] + "_" + nowStrs[5];

            OperateFile.copyFile(safeFileNameSMMAL, new File(uploadPath + osflag + rightFileStr));

            changeXMLDate(uploadPath + osflag + rightFileStr);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private static void SMSALFileNotExits(String now_Datebegin) {
        try {
            Calendar calendar = Calendar.getInstance();
            String hourFlag = calendar.get(Calendar.HOUR_OF_DAY) + "";
            String filePath = "";

            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.DATE, -1);
            Date date = calendar2.getTime();

            if (hourFlag.equals("0")) {
                filePath = rightPathSMSAL + osflag + rightFirstStrSMSAL + "024_000.xml";
                now_Datebegin = new SimpleDateFormat("yyyyMMdd").format(date);
            } else {
                if (hourFlag.length() < 2) {
                    hourFlag = "0" + hourFlag;
                }
                filePath = rightPathSMSAL + osflag + rightFirstStrSMSAL + "0" + hourFlag + "_000.xml";
            }

            File safeFileNameSMMAL = new File(filePath);
            String[] nowStrs = safeFileNameSMMAL.getName().split("_");

            // String now_Datebegin = new SimpleDateFormat("yyyyMMdd")
            // .format(calendar.getTime());
            String rightFileStr = "SMSAL_371_01HR_" + now_Datebegin + "_" + nowStrs[4] + "_" + nowStrs[5];

            OperateFile.copyFile(safeFileNameSMMAL, new File(uploadPath + osflag + rightFileStr));

            changeXMLDate(uploadPath + osflag + rightFileStr);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static Document load(String filename) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader
                    .read(new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8")));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("你错了！");
        }
        return document;
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

    public static String doc2String(Document document) {
        String s = "";
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputFormat format = new OutputFormat("", true, "UTF-8");
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(document);
            s = out.toString("UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }

    public static Document string2Document(String s) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;
    }

    public static void changeXMLDate(String filePath) {
        Document doc = load(filePath);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        String lastday = new SimpleDateFormat("yyyy-MM-dd").format(date);

        List list = doc.selectNodes("/smp/createtime");
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element timeElement = (Element) iter.next();
            timeElement.setText(timeElement.getText().replace(right_Date, now_Date));
        }
        list = doc.selectNodes("/smp/begintime");
        iter = list.iterator();
        while (iter.hasNext()) {
            Element timeElement = (Element) iter.next();
            if (timeElement.getText().trim().toString().contains(right_Datebegin)) {
                timeElement.setText(timeElement.getText().replace(right_Datebegin, lastday));
            } else {
                timeElement.setText(timeElement.getText().replace(right_Date, now_Date));
            }
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
            Iterator ltIter = rcdElement.elementIterator("logintime");
            while (ltIter.hasNext()) {
                Element ltElement = (Element) ltIter.next();
                if (ltElement.getText().trim().toString().contains(right_Datebegin)) {
                    ltElement.setText(ltElement.getText().replace(right_Datebegin, lastday));
                } else {
                    ltElement.setText(ltElement.getText().replace(right_Date, now_Date));
                }
            }
        }

        Node sum = (Node) doc.selectObject("/smp/sum");
        sum.setText(String.valueOf(list.size()));
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
}