package com.sankar.todoapp.service;

import java.util.Collection;
import java.util.Date;

import com.google.inject.Inject;
import com.sankar.todoapp.TodoItem;
import com.sankar.todoapp.dao.TodoDAO;

public class TodoService {
	
	private TodoDAO dao;
	
	@Inject
	public TodoService(TodoDAO dao) {
		this.dao = dao;
	}
	
	public TodoItem get(String id) {
		return dao.findById(id);
	}
	
	public Collection<TodoItem> getAllTodos() {
		return dao.findAll();
	}
	
	public void add(TodoItem item) {
		if(item.getCreated() == null) {
			item.setCreated(new Date());
		}
		
		dao.persist(item);
	}

	public void update(TodoItem item) {
		dao.update(item);
	}
	
	public void delete(String id) {
		dao.delete(dao.findById(id));
	}
	
}
