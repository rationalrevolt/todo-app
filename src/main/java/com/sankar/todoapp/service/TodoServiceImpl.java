package com.sankar.todoapp.service;

import java.util.Collection;

import com.google.inject.Inject;
import com.sankar.todoapp.TodoItem;
import com.sankar.todoapp.dao.TodoDAO;

public class TodoServiceImpl implements TodoService {
	
	private TodoDAO dao;
	
	@Inject
	public TodoServiceImpl(TodoDAO dao) {
		this.dao = dao;
	}

	@Override
	public TodoItem get(String id) {
		return dao.findById(id);
	}

	@Override
	public Collection<TodoItem> getAllTodos() {
		return dao.findAll();
	}

	@Override
	public void add(TodoItem item) {
		dao.persist(item);
	}

	@Override
	public void update(TodoItem item) {
		dao.update(item);
	}

	@Override
	public void delete(String id) {
		dao.delete(dao.findById(id));
	}
	
}
