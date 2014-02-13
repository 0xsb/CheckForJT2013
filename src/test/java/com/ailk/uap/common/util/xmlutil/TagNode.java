package com.ailk.uap.common.util.xmlutil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class TagNode{
	
	private String tagName;
	private StringBuffer attributes = new StringBuffer("");
	private String value = "";
	private List<TagNode> childNodeList;
	
	public TagNode(String tagName) {
		this.tagName = tagName;
	}
	
	public void addAttributes(String name, String value) {
		attributes.append(" ");
		attributes.append(name);
		attributes.append("='");
		attributes.append(value );
		attributes.append("'" );
	}

	public String toString() {
		StringBuffer result = new StringBuffer("");
		appendContentTo(result);
		return result.toString();
	}

	private void appendContentTo(StringBuffer result) {
		writeOpenTagTo(result);
		writeChildrenTo(result);
		writeValueTo(result);
		writeEndTagTo(result);
	}

	private void writeEndTagTo(StringBuffer result) {
		if(!"".equals(value))
			result.append(value);
	}

	private void writeValueTo(StringBuffer result) {
		result.append("</");
		result.append(tagName);
		result.append(">");
	}

	private StringBuffer writeChildrenTo(StringBuffer result) {
		Iterator it = getChildNodeList().iterator();
		while(it.hasNext()) {
			TagNode node = (TagNode)it.next();
			node.appendContentTo(result);
		}
		return result;
	}

	private void writeOpenTagTo(StringBuffer result) {
		result.append("<");
		result.append(tagName);
		result.append(attributes.toString());
		result.append(">");
	}

	private List<TagNode> getChildNodeList() {
		if(childNodeList == null)
			childNodeList = new ArrayList<TagNode>();
		return childNodeList;
	}

	public void addChild(TagNode child) {
		getChildNodeList().add(child);
	}
	
	public void addValue(String value) {
		this.value = value;
	}
	
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}


	public StringBuffer getAttributes() {
		return attributes;
	}

	public void setAttributes(StringBuffer attributes) {
		this.attributes = attributes;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
