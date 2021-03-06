package com.sankar.todoapp;

import io.searchbox.annotations.JestId;

import java.util.Date;

@GsonWritable
public class TodoItem {
	
	@JestId
	private String id;
	private String title;
	private String body;
	private boolean done;
	private Date created;
	
	public TodoItem() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	public boolean getDone() {
		return done;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
}
