package com.ailk.uap.common.util.xmlutil;


public class TagBuild {

	private TagNode rootTagNode;
	private TagNode currentTagNode;
	private TagNode parentNode;
	
	
	public TagBuild(String tagName) {
		rootTagNode = new TagNode(tagName);
		currentTagNode = rootTagNode;
	}

	
	public String toXml() {
		return rootTagNode.toString();
	}


	public void addToParent(String parntentTagNode) {
		addToParent(findParentTagNodeByName(parntentTagNode),parntentTagNode);
	}
	
	public void addChild(String child) {
		parentNode = currentTagNode;
		currentTagNode = new TagNode(child);
	}
	
	public void addSibling(String tagName) {
		addToParent(getParentNode(),tagName);
	}
	
	private void addToParent(TagNode parentTagNode, String tagName) {
		parentTagNode=parentTagNode;
		TagNode child = new TagNode(tagName);
		parentTagNode.addChild(child);
	}

	public TagNode findParentTagNodeByName(String tagName) {
		TagNode parentTagNode = currentTagNode;
		while(parentTagNode != null) {
			if(parentTagNode.getTagName().equals(tagName)) 
				return parentTagNode;
			parentTagNode = getParentNode();
			
		}
		if(parentTagNode == null) 
			throw new RuntimeException("parent tagname not find, parent name is " + tagName);
		return parentTagNode;
	}


	public TagNode getRootTagNode() {
		return rootTagNode;
	}


	public void setRootTagNode(TagNode rootTagNode) {
		this.rootTagNode = rootTagNode;
	}


	public TagNode getCurrentTagNode() {
		return currentTagNode;
	}


	public void setCurrentTagNode(TagNode currentTagNode) {
		this.currentTagNode = currentTagNode;
	}


	public TagNode getParentNode() {
		return parentNode;
	}


	public void setParentNode(TagNode parentNode) {
		this.parentNode = parentNode;
	}


	
	
}
