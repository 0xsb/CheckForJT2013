package com.ailk.jt.validate;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * @ClassName: MyDefaultHandler
 * @Description: xml文件校验失败处理类
 * @author huangpumm@asiainfo-linkage.com
 * @date Jul 13, 2012 4:01:37 PM
 */
class MyDefaultHandler extends DefaultHandler2 {

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// System.out.print("<" + qName + ">");
		// System.out.println("tag by attribute:" + attributes.getValue(0));
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// System.out.print("</" + qName + ">");
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String result = new String(ch, start, length);
		// System.out.print(result);
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		throw new SAXException(getMessage("Warning", e));
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		throw new SAXException(getMessage("Error", e));
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		throw new SAXException(getMessage("Fatal Error", e));
	}

	private String getMessage(String level, SAXParseException e) {
		return ("解析级别 " + level + "\t" + "出错行:" + e.getLineNumber() + "\t" + "文件目录:" + e.getSystemId() + "\t" + "出错消息:" + e
				.getMessage());
	}
}