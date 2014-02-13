package com.ailk.jt.validate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ailk.jt.util.DateUtil;

public class MainAcctAddDayFileSafeguard extends  AbstractValidateDayFile{
	
	    public static void main(String[] args) {
	        String nowDateStr = DateUtil.formatDateyyyyMMDD(new Date());
	        now_Date = DateUtil.ymdToStr();

	        Calendar calendar = Calendar.getInstance();
	        calendar.add(Calendar.DATE, -1);
	        now_Datebegin = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	        rightdate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
//	        validateMAI();
	        AbstractValidateDayFile AbstractValidateDayFile = new MainAcctAddDayFileSafeguard();
	        AbstractValidateDayFile.validate();
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

		@Override
		public void chanageXmlData(String fileName) {
			
		}

		@Override
		public String getFileType() {
			return "SMMAI";
		}

		@Override
		public void replaceFile() {
			
		}
	    
}
