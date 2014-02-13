package com.ailk.uap.common.util.xmlutil;

import junit.framework.Assert;

import org.junit.Test;

public class TagBuildTest {

	@Test
	public void addRootTagTest() {
		TagBuild rootTag = new TagBuild("smp");
		String expected="<smp></smp>";
		Assert.assertEquals(expected, rootTag.toXml());
	}
	
	@Test
	public void addChildTest() {
		TagBuild tagBuild = new TagBuild("smp");
		TagNode createtimeTag = new TagNode("createtime");
		tagBuild.addChild("createtime");
		String expected="<smp><createtime></createtime></smp>";
		Assert.assertEquals(expected, tagBuild.toXml());
	}
	
	@Test
	public void addSiblingTest(){
		TagBuild tagBuild = new TagBuild("smp");
		tagBuild.addChild("createTime");
		TagNode findTagNode = tagBuild.findParentTagNodeByName("root");
		tagBuild.addSibling("sum");
		Assert.assertNull(findTagNode);
	}
	
	@Test(expected=RuntimeException.class)
	public void addToParentNotExistsTest(){
		TagBuild tagBuild = new TagBuild("smp");
		TagNode createtimeTag = new TagNode("createtime");
		tagBuild.addToParent("smpTest");
	}
	
	@Test(expected=RuntimeException.class)
	public void findParentTagNodeByNameTest() {
		TagBuild tagBuild = new TagBuild("smp");
		tagBuild.addChild("createTime");
		TagNode findTagNode = tagBuild.findParentTagNodeByName("root");
		Assert.assertNull(findTagNode);
	}
	
	@Test
	public void findParentTagNodeByNameExistsTest() {
		TagBuild tagBuild = new TagBuild("smp");
		tagBuild.addChild("createTime");
		TagNode findTagNode = tagBuild.findParentTagNodeByName("smp");
		Assert.assertNotNull(findTagNode);
		Assert.assertEquals("smp", findTagNode.getTagName());
	}
	
	@Test
	public void addToParentTest() {
	}
}
