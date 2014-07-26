package com.sankar.todoapp;

@GsonWritable
public class Message {
	
	private String id;
	private String success;
	private String error;
	
	public String getId() {
		return id;
	}
	
	public String getSuccess() {
		return success;
	}

	public String getError() {
		return error;
	}
	
	public static Message created(String id) {
		Message response = new Message();
		response.id = id;
		return response;
	}

	public static Message success(String message) {
		Message response = new Message();
		response.success = message;
		return response;
	}
	
	public static Message error(String message) {
		Message response = new Message();
		response.error = message;
		return response;
	}
	
}
