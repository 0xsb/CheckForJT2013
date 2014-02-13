package com.ailk.check.utils;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-7-10
 * Time: 下午2:22
 */
public class XmlUtil {

    public static String dealXmlContent(String xmlContent) {
        String result = "";
        if (xmlContent != null) {
            result = xmlContent.replaceAll("&", "&amp;");
            result = result.replaceAll("<", "&lt;");
            result = result.replaceAll(">", "&gt;");
        }
        return result;
    }
}
