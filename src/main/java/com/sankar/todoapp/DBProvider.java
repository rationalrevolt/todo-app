package com.sankar.todoapp;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class DBProvider implements Provider<DB> {
	
	private MongoClient cli;
	private String dbName;
	
	@Inject
	public DBProvider(MongoClient cli, @Named("MONGO_DATABASE_NAME") String dbName) {
		this.cli = cli;
		this.dbName = dbName;
	}
	
	@Override
	public DB get() {
		return cli.getDB(dbName);
	}
}
