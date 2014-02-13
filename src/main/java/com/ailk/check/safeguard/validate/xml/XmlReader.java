package com.ailk.check.safeguard.validate.xml;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-6-3
 * Time: 上午10:06
 * <p/>
 * XML文件读取者
 */
public class XmlReader {
    private String xmlName;
    private String xmlPath;
    private Document document;

    public XmlReader(String xmlPath) throws DocumentException {
        this.xmlPath = xmlPath;
        init(xmlPath);
    }

    private void init(String filePath) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        File xml = new File(filePath);
        this.xmlName = xml.getName();
        this.document = saxReader.read(xml);
    }

    public String getXmlName() {
        return xmlName;
    }

    public String getXmlPath() {
        return xmlPath;
    }

    public Document getDocument() {
        return document;
    }

    // 得到XML中的 sum 的值
    public int getSumVal() {
        return Integer.valueOf(getUniqueElement("/smp/sum").getText());
    }

    public void setSumValWithSeq() {
        getUniqueElement("/smp/sum").setText(String.valueOf(getSeqCountVal()));
    }

    // 得到XML中的 seq累计 的值
    public int getSeqCountVal() {
        List list = document.selectNodes("/smp/data/rcd/seq");
        return list.size();
    }

    // 得到XML中头部的 createtime 的值中的日期
    public String getHeadCreateTimeDate() {
        return getDateFromXmlTime(getHeadCreateTime().getText());
    }

    // 得到XML中头部的 createtime
    public Element getHeadCreateTime() {
        return getUniqueElement("/smp/createtime");
    }

    // 得到XML中头部的 begintime 的值中的日期
    public String getHeadBeginTimeDate() {
        return getDateFromXmlTime(getHeadBeginTime().getText());
    }

    // 得到XML中头部的 begintime
    public Element getHeadBeginTime() {
        return getUniqueElement("/smp/begintime");
    }

    // 得到XML中头部的 endtime 的值中的日期
    public String getHeadEndTimeDate() {
        return getDateFromXmlTime(getHeadEndTime().getText());
    }

    // 得到XML中头部的 endtime
    public Element getHeadEndTime() {
        return getUniqueElement("/smp/endtime");
    }

    /**
     * 替换XML指定路径的值
     *
     * @param key   需要替换的地方的关键字
     * @param text  替换为的值
     * @param xpath xml路径
     */
    public void replace(String key, String text, String xpath) {
        List list = document.selectNodes(xpath);
        for (Object o : list) {
            Element element = (Element) o;
            element.setText(element.getText().replace(key, text));
        }
    }

    /**
     * 将document写入文件
     *
     * @param filePath 文件路径
     * @return File
     * @throws IOException
     */
    public File touch(String filePath) throws IOException {
        // touch xml
        File xml = new File(filePath);
        FileUtils.touch(xml);

        // 写入格式化后的XML
        OutputStream out = null;
        XMLWriter writer = null;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setNewLineAfterDeclaration(false);
            out = new FileOutputStream(xml);
            writer = new XMLWriter(out, format);
            writer.write(document);
        } finally {
            if (out != null) {
                out.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
        return xml;
    }

    // 获取唯一元素
    public Element getUniqueElement(String xpath) {
        List list = document.selectNodes(xpath);
        return (Element) list.iterator().next();
    }

    // 获取所有元素
    public List getElements(String xpath) {
        return document.selectNodes(xpath);
    }

    // 从XML时间获取日期
    private String getDateFromXmlTime(String xmlTime) {
        String date = "";
        if (xmlTime != null && xmlTime.length() > 10) {
            date = xmlTime.substring(0, 10);
        }
        return date;
    }
}
