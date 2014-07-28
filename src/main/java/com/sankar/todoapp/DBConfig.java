package com.sankar.todoapp;

public interface DBConfig {

	String getHost();

	int getPort();

	String getSchema();

	String getUser();

	String getPassword();

}