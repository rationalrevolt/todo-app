package com.sankar.todoapp;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoTest {
	
	public static void main(String[] args) throws UnknownHostException {
		ServerAddress addr = new ServerAddress("kahana.mongohq.com",10081);
		MongoCredential auth = MongoCredential.createMongoCRCredential("todoapp","app27809520","getitdone".toCharArray());
		MongoClient cli = new MongoClient(addr,Arrays.asList(auth));
		
		DB db = cli.getDB("app27809520");
		
		DBCollection coll = db.getCollection("todos");
		DBObject o = coll.findOne(new BasicDBObject().append("_id", new ObjectId("53d52adc300449eb27383405")));
		System.out.println(o.toString());
		
		cli.close();
	}
	
}
