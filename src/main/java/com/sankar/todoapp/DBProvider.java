package com.sankar.todoapp;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class DBProvider implements Provider<DB> {
	
	private MongoClient cli;
	private DBConfig conf;
	
	@Inject
	public DBProvider(MongoClient cli, DBConfig conf) {
		this.cli = cli;
		this.conf = conf;
	}
	
	@Override
	public DB get() {
		return cli.getDB(conf.getSchema());
	}
}
