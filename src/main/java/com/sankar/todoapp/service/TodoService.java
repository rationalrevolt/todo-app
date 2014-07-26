package com.sankar.todoapp.service;

import java.util.Collection;

import com.sankar.todoapp.TodoItem;

public interface TodoService {

	TodoItem get(String id);
	
	Collection<TodoItem> getAllTodos();
	
	void add(TodoItem item);
	
	void update(TodoItem item);
	
	void delete(String id);
	
}
