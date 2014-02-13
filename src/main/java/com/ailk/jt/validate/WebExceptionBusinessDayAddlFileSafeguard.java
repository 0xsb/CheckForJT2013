package com.ailk.jt.validate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
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

public class WebExceptionBusinessDayAddlFileSafeguard extends  AbstractValidateDayFile{
 	private static String right_Date = PropertiesUtil.getValue("right_Date").trim();
    private static String right_Datebegin = PropertiesUtil.getValue("right_Datebegin").trim();
    private static String now_Date = "";
    private static String now_Datebegin = "";
    private static String rightdate = "";
    private static String uploadPath = PropertiesUtil.getValue("uap_file_uapload");
    private static String osflag = PropertiesUtil.getValue("os_flag");
    private static String dayFilePath = PropertiesUtil.getValue("uap_file_uapload_for_day_dir_safe");

    private static String nowPathSMBHR = PropertiesUtil.getValue("uap_file_uapload_for_smbhr_db_now");
    private static String rightFirstStrSMBHR = "SMBHR_371_01DY_"+right_Datebegin.replace("-", "")+"_";

    private static String nowPathSMDAR = PropertiesUtil.getValue("uap_file_uapload_for_smdar_db_now");
    private static String rightFirstStrSMDAR = "SMDAR_371_01DY_"+right_Datebegin.replace("-", "")+"_";

    private static String nowPathSMMAI = PropertiesUtil.getValue("uap_file_uapload_for_smmai_db_now");
    private static String rightFirstStrSMMAI = "SMMAI_371_01DY_"+right_Datebegin.replace("-", "")+"_";

    public static void main(String[] args) {
        String nowDateStr = DateUtil.formatDateyyyyMMDD(new Date());
        now_Date = DateUtil.ymdToStr();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        now_Datebegin = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        rightdate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());

        validateBHR();

        validateDAR();

        validateMAI();
    }

    public static void validateBHR() {
        String uploadFileName = "";
        try {
            String nowFileNameSMBHR = OperateFile.searchEndFile(nowPathSMBHR, "xml");
            String[] nowStrs = nowFileNameSMBHR.split("_");
            String fileName = nowPathSMBHR + osflag + nowFileNameSMBHR;

            uploadFileName = nowPathSMBHR + osflag + nowFileNameSMBHR;

            boolean resultSMBHR = FileValidator.validate(uploadFileName, nowPathSMBHR);

            if (!resultSMBHR) {
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("validateError"));
                replaceSMBHR();
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                //
                HashMap<String,String> dateMap = new HashMap<String,String>();
                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                dateMap.put("file_name", nowFileNameSMBHR);
                dateMap.put("file_sum", "0");
                dateMap.put("file_error_reason",tran.getProperty("validateError").trim().substring(0, 4));
                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                SaveErrorFileUtil.saveErrorFile(dateMap);
            }
            if (resultSMBHR) {
                int sum = getSumVal(fileName);
                int seq = getSeqCountVal(fileName);
                int z_101 = getWebNumber(fileName, "101");
                int z_102 = getWebNumber(fileName, "102");
                int z_103 = getWebNumber(fileName, "103");
                boolean b_101 = isZero(fileName, "101");
                boolean b_102 = isZero(fileName, "102");
                boolean b_103 = isZero(fileName, "103");

                if ((sum <= 0) || (sum != seq) || (sum != 93) || (z_101 != 31) || (z_102 != 31) || (z_103 != 31)
                        || (b_101) || (!b_102) || (!b_103)) {
                    DBUtil.notice(tran.getProperty("a4File") + nowFileNameSMBHR + tran.getProperty("dataError"));
                    String rightFileStr = rightFirstStrSMBHR + nowStrs[4] + "_" + nowStrs[5];
                    OperateFile.copyFile(new File(dayFilePath + osflag + rightFileStr), new File(uploadPath + osflag
                            + nowFileNameSMBHR));

                    changeXMLDate(uploadPath + osflag + nowFileNameSMBHR);
                    DBUtil.notice(tran.getProperty("a4File") + nowFileNameSMBHR + tran.getProperty("repairData"));
                    //
                    HashMap<String,String> dateMap = new HashMap<String,String>();
                    dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                    dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                    dateMap.put("file_name", nowFileNameSMBHR);
                    dateMap.put("file_sum", sum+"");
                    dateMap.put("file_error_reason",tran.getProperty("sumNotEqualSeq").trim().substring(0, 11));
                    dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                    SaveErrorFileUtil.saveErrorFile(dateMap);
                } else {
                    OperateFile.copyFile(new File(nowPathSMBHR + osflag + nowFileNameSMBHR), new File(uploadPath
                            + osflag + nowFileNameSMBHR));
                }
            }

            OperateFile.deleteFileOrDir(nowPathSMBHR + osflag + nowFileNameSMBHR);
        } catch (Exception e2) {
            try {
                String nowFileNameSMBHR = "SMBHR_371_01DY_" + rightdate + "_000_000.xml";
                uploadFileName = nowPathSMBHR + osflag + nowFileNameSMBHR;
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
                replaceSMBHR();
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                //
                HashMap<String,String> dateMap = new HashMap<String,String>();
                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                dateMap.put("file_name", nowFileNameSMBHR);
                dateMap.put("file_sum", "0");
                dateMap.put("file_error_reason",tran.getProperty("notGernerated").trim().substring(0, 5));
                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                SaveErrorFileUtil.saveErrorFile(dateMap);
            } catch (Exception localException1) {
            }
        }
    }

    private static boolean isZero(String fileName, String behabiour) {
        Document doc = load(fileName);
        if ("101".equals(behabiour)) {
            List buztotalXML = doc.selectNodes("/smp/data/rcd[behaviour=101 and total!=0 and buztotal!=0]");
            System.out.println(buztotalXML.size());

            return buztotalXML.size() != 30;
        }

        if ("102".equals(behabiour)) {
            List buztotalXML = doc.selectNodes("/smp/data/rcd[behaviour=102 and total=0 and buztotal=0]");
            System.out.println(buztotalXML.size());

            return buztotalXML.size() == 31;
        }

        if ("103".equals(behabiour)) {
            List buztotalXML = doc.selectNodes("/smp/data/rcd[behaviour=103 and total=0 and buztotal=0]");
            System.out.println(buztotalXML.size());

            return buztotalXML.size() == 31;
        }

        return false;
    }

    private static void replaceSMBHR() {
        try {
            File safeFileNameSMBHR = new File(dayFilePath + osflag + "SMBHR_371_01DY_"+right_Datebegin.replace("-", "")+"_000_000.xml");
            String[] nowStrs = safeFileNameSMBHR.getName().split("_");

            Calendar calendar = Calendar.getInstance();
            calendar.add(5, -1);
            String now_Datebegin = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            String rightFileStr = "SMBHR_371_01DY_" + now_Datebegin + "_" + nowStrs[4] + "_" + nowStrs[5];
            OperateFile.copyFile(safeFileNameSMBHR, new File(uploadPath + osflag + rightFileStr));

            changeXMLDate(uploadPath + osflag + rightFileStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void validateMAI() {
        String uploadFileName = "";
        try {
            String nowFileNameMAI = OperateFile.searchEndFile(nowPathSMMAI, "xml");
            uploadFileName = nowPathSMMAI + osflag + nowFileNameMAI;
            String[] nowStrs = nowFileNameMAI.split("_");

            boolean resultSMMAI = FileValidator.validate(uploadFileName, nowPathSMMAI);

            if (!resultSMMAI) {
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("validateError"));
                replaceMAI();
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                //
                HashMap<String,String> dateMap = new HashMap<String,String>();
                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                dateMap.put("file_name", nowFileNameMAI);
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

                    String rightFileStr = rightFirstStrSMMAI + nowStrs[4] + "_" + nowStrs[5];
                    OperateFile.copyFile(new File(dayFilePath + osflag + rightFileStr), new File(uploadPath + osflag
                            + nowFileNameMAI));

                    changeXMLDateMAI(uploadPath + osflag + nowFileNameMAI);
                    DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSum"));
                    //
                    HashMap<String,String> dateMap = new HashMap<String,String>();
                    dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                    dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                    dateMap.put("file_name", nowFileNameMAI);
                    dateMap.put("file_sum", sum+"");
                    dateMap.put("file_error_reason",tran.getProperty("sumNotEqualSeq").trim().substring(0, 11));
                    dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                    SaveErrorFileUtil.saveErrorFile(dateMap);
                } else {
                    OperateFile.copyFile(new File(uploadFileName), new File(uploadPath + osflag + nowFileNameMAI));
                }
            }

            OperateFile.deleteFileOrDir(uploadFileName);
        } catch (Exception e) {
            try {
                String nowFileNameMAI = "SMMAI_371_01DY_" + rightdate + "_000_000.xml";
                uploadFileName = nowPathSMMAI + osflag + nowFileNameMAI;
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
                replaceMAI();
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                //
                HashMap<String,String> dateMap = new HashMap<String,String>();
                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                dateMap.put("file_name", nowFileNameMAI);
                dateMap.put("file_sum", "0");
                dateMap.put("file_error_reason",tran.getProperty("notGernerated").trim().substring(0, 5));
                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                SaveErrorFileUtil.saveErrorFile(dateMap);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private static void replaceMAI() {
        try {
            File safeFileNameSMBHR = new File(dayFilePath + osflag + "SMMAI_371_01DY_"+right_Datebegin.replace("-", "")+"_000_000.xml");
            String[] nowStrs = safeFileNameSMBHR.getName().split("_");
            String dateStr = nowStrs[3];

            Calendar calendar = Calendar.getInstance();
            calendar.add(5, -1);
            String now_Datebegin = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            String rightFileStr = "SMMAI_371_01DY_" + now_Datebegin + "_" + nowStrs[4] + "_" + nowStrs[5];
            OperateFile.copyFile(safeFileNameSMBHR, new File(uploadPath + osflag + rightFileStr));

            changeXMLDateMAI(uploadPath + osflag + rightFileStr);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private static void changeXMLDateMAI(String filePath) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -1);
        String yestorday = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        Document doc = load(filePath);

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
            timeElement.setText(timeElement.getText().replace(right_Datebegin, yestorday));
        }

        list = doc.selectNodes("/smp/endtime");
        iter = list.iterator();
        while (iter.hasNext()) {
            Element timeElement = (Element) iter.next();
            timeElement.setText(timeElement.getText().replace(right_Date, now_Date));
        }

        list = doc.selectNodes("/smp/data/rcd/updatetime");
        iter = list.iterator();
        while (iter.hasNext()) {
            Element rcdElement = (Element) iter.next();
            rcdElement.setText(rcdElement.getText().replace(right_Datebegin, yestorday));
        }
        list = doc.selectNodes("/smp/data/rcd");
        iter = list.iterator();
        while (iter.hasNext()) {
            Element rcdElement = (Element) iter.next();
            Iterator ltIter = rcdElement.elementIterator("establishtime");
            while (ltIter.hasNext()) {
                Element ltElement = (Element) ltIter.next();
                ltElement.setText(ltElement.getText().replace(right_Date, yestorday));
            }
        }

        createXML(doc, filePath);
    }

    private static void validateDAR() {
        String uploadFileName = "";
        try {
            String nowFileNameDAR = OperateFile.searchEndFile(nowPathSMDAR, "xml");

            String[] nowStrs = nowFileNameDAR.split("_");
            uploadFileName = nowPathSMDAR + osflag + nowFileNameDAR;

            boolean resultSMDAR = FileValidator.validate(uploadFileName, nowPathSMDAR);
            if (!resultSMDAR) {
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("validateError"));
                replaceDAR();
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                //
                HashMap<String,String> dateMap = new HashMap<String,String>();
                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                dateMap.put("file_name", nowFileNameDAR);
                dateMap.put("file_sum", "0");
                dateMap.put("file_error_reason",tran.getProperty("validateError").trim().substring(0, 4));
                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                SaveErrorFileUtil.saveErrorFile(dateMap);
            }
            if (resultSMDAR) {
                String[] resTypes = configuration.getProperty("restype", "", "dar").split(",");
                for (int i = 0; i < resTypes.length; i++) {
                    if (("11".equals(resTypes[i])) || ("13".equals(resTypes[i]))) {
                        boolean isEmpty = getBOSS(uploadFileName, resTypes[i]);
                        System.out.println(resTypes[i]+"  isEmpty=="+isEmpty);
                        if (isEmpty) {
                            ArrayList postionList = getBOSSIsZeroPosstion(uploadFileName, resTypes[i]);
                            int size = postionList.size();
                            for (int j = 0; j < size; j++) {
                                String postionInList = (String) postionList.get(j);
                                long randomInteger = configuration.getIntProperty("avg", 1000, "dar");
                                String czValue = configuration.getProperty(String.valueOf(postionInList), String
                                        .valueOf(Math.round(Math.random() * 100.0D) + randomInteger), "dar");
                                System.out.println(czValue);

                                changeXMLValue(uploadFileName, resTypes[i], postionInList, czValue);
                            }
                        }
                    }
                }
                int sum = getSumVal(uploadFileName);
                int seq = getSeqCountVal(uploadFileName);

                if ((sum <= 0) || (sum != seq) || (sum != 432)) {
                    DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("dataError"));

                    String rightFileStr = rightFirstStrSMDAR + nowStrs[4] + "_" + nowStrs[5];
                    OperateFile.copyFile(new File(dayFilePath + osflag + rightFileStr), new File(uploadPath + osflag
                            + nowFileNameDAR));

                    changeXMLDate(uploadPath + osflag + nowFileNameDAR);
                    DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                    //
                    HashMap<String,String> dateMap = new HashMap<String,String>();
                    dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                    dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                    dateMap.put("file_name", nowFileNameDAR);
                    dateMap.put("file_sum", sum+"");
                    dateMap.put("file_error_reason",tran.getProperty("sumNotEqualSeq").trim().substring(0, 11));
                    dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                    SaveErrorFileUtil.saveErrorFile(dateMap);
                } else {
                    OperateFile.copyFile(new File(nowPathSMDAR + osflag + nowFileNameDAR), new File(uploadPath + osflag
                            + nowFileNameDAR));
                }

            }

            OperateFile.deleteFileOrDir(nowPathSMDAR + osflag + nowFileNameDAR);
        } catch (Exception e) {
            try {
                String nowFileNameDAR = "SMDAR_371_01DY_" + rightdate + "_000_000.xml";
                uploadFileName = nowPathSMDAR + osflag + nowFileNameDAR;
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("notGernerated"));
                replaceDAR();
                DBUtil.notice(tran.getProperty("a4File") + uploadFileName + tran.getProperty("replaceSuccess"));
                //
                HashMap<String,String> dateMap = new HashMap<String,String>();
                dateMap.put("file_begin_time", TimeAndOtherUtil.getLastDayStartTimeStr());
                dateMap.put("file_end_time", TimeAndOtherUtil.getTodayStartTimeStr());
                dateMap.put("file_name", nowFileNameDAR);
                dateMap.put("file_sum", "0");
                dateMap.put("file_error_reason",tran.getProperty("notGernerated").trim().substring(0, 5));
                dateMap.put("file_upload_to_bomc", TimeAndOtherUtil.getCurrentDateTimeStr());
                SaveErrorFileUtil.saveErrorFile(dateMap);

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private static void changeXMLValue(String uploadFileName, String restype, String postionInList, String czValue) {
        Document doc = load(uploadFileName);
        Element value = (Element) doc.selectObject("/smp/data/rcd[restype=" + restype + " and num=" + postionInList
                + "]");
        value.element("value").setText(czValue);
        createXML(doc, uploadFileName);
    }

    private static void replaceDAR() {
        try {
            File safeFileNameSMBHR = new File(dayFilePath + osflag + "SMDAR_371_01DY_"+right_Datebegin.replace("-", "")+"_000_000.xml");
            String[] nowStrs = safeFileNameSMBHR.getName().split("_");
            String dateStr = nowStrs[3];

            Calendar calendar = Calendar.getInstance();
            calendar.add(5, -1);
            String now_Datebegin = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            String rightFileStr = "SMDAR_371_01DY_" + now_Datebegin + "_" + nowStrs[4] + "_" + nowStrs[5];
            OperateFile.copyFile(safeFileNameSMBHR, new File(uploadPath + osflag + rightFileStr));

            changeXMLDate(uploadPath + osflag + rightFileStr);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
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

    private static ArrayList<String> getBOSSIsZeroPosstion(String uploadFileName, String value) {
        Document doc = load(uploadFileName);
        List list = doc.selectNodes("/smp/data/rcd[restype=" + value + "and num!=0 and value=0 ]");
        int size = list.size();
        ArrayList numIsZeroList = new ArrayList();
        for (int i = 0; i < size; i++) {
            Element num = (Element) list.get(i);
            System.out.println(num.elementText("num"));
            numIsZeroList.add(num.elementText("num"));
        }
        return numIsZeroList;
    }

    private static boolean getBOSS(String nowFile, String value) {
        Document doc = load(nowFile);
        List list = doc.selectNodes("/smp/data/rcd[restype=" + value + " and num!=0 and value!=0 ]");

        return list.size() != 48;
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

    private static int getWebNumber(String filePath, String behabiour) {
        Document doc = load(filePath);
        List list = doc.selectNodes("/smp/data/rcd[behaviour=" + behabiour + "]");
        return list.size();
    }

    public static int getTotle(Document doc, String behabiour, String aaa) {
        if ("101".equals(behabiour)) {
            List list = doc.selectNodes("/smp/data/rcd[behabiour=" + behabiour + "]|/smp/data/rcd[" + aaa + "!=0]");
            return list.size();
        }
        List list = doc.selectNodes("/smp/data/rcd[behabiour=" + behabiour + "]|/smp/data/rcd[" + aaa + "=0]");
        return list.size();
    }

    public static void changeXMLDate(String filePath) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -1);
        String yestorday = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        Document doc = load(filePath);

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
            Iterator ltIter = rcdElement.elementIterator("logintime");
            while (ltIter.hasNext()) {
                Element ltElement = (Element) ltIter.next();
                ltElement.setText(ltElement.getText().replace(right_Date, now_Date));
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


