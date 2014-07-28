package com.sankar.todoapp;

import java.util.Map;

public class EnvironmentDBConfig implements DBConfig {
	
	private String host;
	private int port;
	
	private String database;
	
	private String user;
	private String password;
	
	public EnvironmentDBConfig() {
		Map<String,String> env =  System.getenv();
		
		host = env.get("MONGO_HOST");
		port = Integer.valueOf(env.get("MONGO_PORT"));
		database = env.get("MONGO_SCHEMA");
		user = env.get("MONGO_USER");
		password = env.get("MONGO_PASSWORD");
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public int getPort() {
		return port;
	}
	
	@Override
	public String getSchema() {
		return database;
	}
	
	@Override
	public String getUser() {
		return user;
	}
	
	@Override
	public String getPassword() {
		return password;
	}

}
