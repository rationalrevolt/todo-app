package com.sankar.todoapp.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.sankar.todoapp.NotFoundException;
import com.sankar.todoapp.PersistException;
import com.sankar.todoapp.TodoItem;

public class TodoDAO {
	
	private DB db;

	@Inject
	public TodoDAO(DB db) {
		this.db = db;
	}
	
	public TodoItem findById(String id) {
		DBCollection coll = getTodosCollection();
		DBObject o = new BasicDBObject().append("_id", new ObjectId(id));
		DBObject found = coll.findOne(o);
		
		if (found == null)
			throw new NotFoundException();
		else
			return createTodoFromDBO(found);
	}
	
	public void persist(TodoItem todo) {
		DBCollection coll = getTodosCollection();
		DBObject o = createDBOFromTodo(todo);
		coll.insert(o);
		
		if (o.get("_id") == null) throw new PersistException();
		
		todo.setId(o.get("_id").toString());
	}
	
	public void update(TodoItem todo) {
		DBCollection coll = getTodosCollection();
		
		DBObject o = new BasicDBObject().append("_id", new ObjectId(todo.getId()));
		DBObject found = coll.findOne(o);
		
		if (found == null)
			throw new NotFoundException();
		
		o = createDBOFromTodo(todo);
		WriteResult wr = coll.save(o);
		
		if (wr.getN() == 0) throw new PersistException();
	}
	
	public void delete(TodoItem todo) {
		DBCollection coll = getTodosCollection();
		DBObject o = new BasicDBObject().append("_id", new ObjectId(todo.getId()));
		WriteResult wr = coll.remove(o);
		
		if (wr.getN() == 0) throw new NotFoundException(); 
	}
	
	public Collection<TodoItem> findAll() {
		List<TodoItem> todos = new ArrayList<TodoItem>();
		
		DBCollection coll = getTodosCollection();
		DBCursor cursor = coll.find();
		
		cursor.sort(new BasicDBObject("created",1));
		while (cursor.hasNext()) {
			DBObject o = cursor.next();
			todos.add(createTodoFromDBO(o));
		}
		
		cursor.close();
		
		return todos;
	}
	
	private TodoItem createTodoFromDBO(DBObject o) {
		if (o == null) return null;
		
		TodoItem todo = new TodoItem();
		
		todo.setId(((ObjectId)o.get("_id")).toString());
		todo.setTitle((String)o.get("title"));
		todo.setBody((String)o.get("body"));
		todo.setDone((Boolean)o.get("done"));
		todo.setCreated((Date)o.get("created"));
		
		return todo;
	}
	
	private DBObject createDBOFromTodo(TodoItem todo) {
		DBObject o = new BasicDBObject();
		
		if(todo.getId() != null) {
			o.put("_id", new ObjectId(todo.getId()));
		}
		o.put("title", todo.getTitle());
		o.put("body", todo.getBody());
		o.put("done", todo.getDone());
		o.put("created", todo.getCreated());
		
		return o;
	}
	
	private DBCollection getTodosCollection() {
		return db.getCollection("todos");
	}
	
}
