package com.sankar.todoapp.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.sankar.todoapp.NotFoundException;
import com.sankar.todoapp.TodoItem;

@Singleton
public class TodoServiceImpl implements TodoService {
	
	private int max_id;
	private Map<String,TodoItem> items = new HashMap<String,TodoItem>();

	@Override
	public TodoItem get(String id) {
		if (items.containsKey(id))
			return items.get(id);
		else
			throw new NotFoundException();
	}
	
	@Override
	public Collection<TodoItem> getAllTodos() {
		return items.values();
	}

	@Override
	public void add(TodoItem item) {
		String id = String.valueOf(max_id);
		item.setId(id);
		items.put(id,item);
		
		max_id += 1;
	}

	@Override
	public void update(TodoItem item) {
		if (items.containsKey(item.getId()))
			items.put(item.getId(), item);
		else
			throw new NotFoundException();
	}
	
	@Override
	public void delete(String id) {
		if (items.containsKey(id))
			items.remove(id);
		else
			throw new NotFoundException();
	}

}
