package com.ailk.uap.common.util.xmlutil;

import junit.framework.Assert;

import org.junit.Test;

public class TagNodeTest {
	
	@Test
	public void addSimpleTagTest() {
		TagNode root = new TagNode("smp");
		String expected="<smp></smp>";
		Assert.assertEquals(expected, root.toString());
	}
	
	@Test
	public void addAttributesTest() {
		TagNode tagNode = new TagNode("smp");
		tagNode.addAttributes("id","1");
		String expected="<smp id='1'></smp>";
		Assert.assertEquals(expected, tagNode.toString());
	}
	
	@Test
	public void addValueTest() {
		TagNode tagNode = new TagNode("smp");
		tagNode.addValue("smp");
		String expected="<smp>smp</smp>";
		Assert.assertEquals(expected, tagNode.toString());
	}
	
	@Test
	public void addChildTest() {
		TagNode tagNode = new TagNode("smp");
		TagNode sumValue=new TagNode("sum");
		tagNode.addChild(sumValue);
		String expected="<smp><sum></sum></smp>";
		Assert.assertEquals(expected, tagNode.toString());
	}
}
