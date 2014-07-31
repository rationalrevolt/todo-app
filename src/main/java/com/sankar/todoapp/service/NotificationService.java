package com.sankar.todoapp.service;

import com.sankar.todoapp.TodoItem;

public interface NotificationService {
	void notifyCompleted(TodoItem item);
}
