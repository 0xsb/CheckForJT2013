package com.ailk.check.utils;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-7-10
 * Time: 下午2:29
 */
public class XmlUtilTest {

    @Test
    public void testDealXmlContent() {
        System.out.println(XmlUtil.dealXmlContent("& & < 123 < 456"));
    }
}
