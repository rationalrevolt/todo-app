package com.sankar.todoapp.service;

import java.util.List;

import com.sankar.todoapp.TodoItem;

public interface SearchService {
	void index(TodoItem todo);
	void update(TodoItem todo);
	void delete(String id);
	List<String> hits(String search);
}
