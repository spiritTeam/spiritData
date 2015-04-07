package com.spiritdata.dataanal.expreport.word.model;

import java.io.Serializable;

public class SubSeg implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String content;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
