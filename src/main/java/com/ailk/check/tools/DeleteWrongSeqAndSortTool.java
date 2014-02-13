package com.ailk.check.tools;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-7-16
 * Time: 下午5:24
 */
public class DeleteWrongSeqAndSortTool {
    private static Logger logger = LoggerFactory.getLogger(DeleteWrongSeqAndSortTool.class);

    public static void main(String[] args) throws DocumentException, IOException {
        // 设置要删掉的错误的seq
        List<String> wrongSeqList = new ArrayList<String>();
        wrongSeqList.add("1");

        logger.info("wrongSeqList is : " + wrongSeqList);

        // 获取Document
        SAXReader saxReader = new SAXReader();
        File xml = new File("D:\\work\\work2012\\upload\\SMJKR.xml");
        Document document = saxReader.read(xml);

        // 取除去错误的seq的element集合
        List rcdList = document.selectNodes("/smp/data/rcd");
        List<Element> wrongList = new ArrayList<Element>();
        for (Object o : rcdList) {
            Element rcdElement = (Element) o;
            Element seqElement = rcdElement.element("seq");
            if (wrongSeqList.contains(seqElement.getTextTrim())) {
                wrongList.add(rcdElement);
            }

            /*// 如果需要把某字段是空的也删除掉，请使用这段代码
            Element element = rcdElement.element("XXX");
            if (element.getTextTrim().equals("")) {
                wrongList.add(rcdElement);
            }*/
        }

        // 取data元素
        List dataList = document.selectNodes("/smp/data");
        Element dataElement = (Element) dataList.iterator().next();
        for (Element element : wrongList) {
            dataElement.remove(element);
        }

        logger.info("dataElement remove wrongSeqList.");

        // 重新排序
        List newList = document.selectNodes("/smp/data/rcd/seq");
        for (Object o : newList) {
            Element seqElement = (Element) o;
            seqElement.setText(String.valueOf(newList.indexOf(o) + 1));
        }

        logger.info("sort ...");

        // 设置sum
        Element sum = (Element) document.selectNodes("/smp/sum").iterator().next();
        sum.setText(String.valueOf(newList.size()));

        logger.info("set sum ...");

        // 生成
        touch("D:\\work\\work2012\\upload\\SMJKR_bak.xml", document);

        logger.info("make success!");
    }

    /**
     * 将document写入文件
     *
     * @param filePath 文件路径
     * @param document XML
     * @return File
     * @throws java.io.IOException
     */
    private static File touch(String filePath, Document document) throws IOException {
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
}
