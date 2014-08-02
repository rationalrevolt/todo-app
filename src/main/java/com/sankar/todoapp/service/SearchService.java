package com.sankar.todoapp.service;

import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.google.inject.Inject;
import com.sankar.todoapp.TodoItem;

public class SearchService {
	
	private static Logger log = LogManager.getLogger();
	
	private JestClient client;
	private Executor executor;
	
	@Inject
	public SearchService(JestClient client, Executor executor) {
		this.client = client;
		this.executor = executor;
	}
	
	public void index(TodoItem todo) {
		Index indexAction = new Index.Builder(todo).index("todos").type("todo").build();
		executeAsync(indexAction, "index", todo);
	}
	
	public void update(TodoItem todo) {
		Index updateAction = new Index.Builder(todo).index("todos").type("todo").build();
		executeAsync(updateAction, "update", todo);
	}
	
	public void delete(String id) {
		Delete deleteAction = new Delete.Builder(id).index("todos").type("todo").build();
		executeAsync(deleteAction, "delete", id);
	}
	
	public List<String> hits(String search) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.multiMatchQuery(search, "title^3", "body"));
		
		Search searchAction = new Search.Builder(searchSourceBuilder.toString()).addIndex("todos").addType("todo").build();
		
		try {
			JestResult searchResult = client.execute(searchAction);
			List<String> results = mapIds(searchResult.getSourceAsObjectList(TodoItem.class));
			return results;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private List<String> mapIds(List<TodoItem> items) {
		List<String> results = new ArrayList<String>();
		for(TodoItem item : items) results.add(item.getId());
		return results;
	}
	
	private void executeAsync(final Action<?> action, final String actionString, final Object attachment) {
		//TODO: Index updates for a given TodoItem need to happen in the order of arrival
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					client.execute(action);
					if (attachment instanceof TodoItem) {
						TodoItem t = (TodoItem)attachment;
						log.info("Search index operation '{}' errored for ID: {}, Title: {}", actionString, t.getId(), t.getTitle());
					} else if (attachment instanceof String) {
						log.info("Search index operation '{}' errored for ID: {}", actionString, attachment);
					}
				} catch(Exception e) {
					if (attachment instanceof TodoItem) {
						TodoItem t = (TodoItem)attachment;
						log.error(new ParameterizedMessage("Search index operation '{}' errored for ID: {}, Title: {}", new Object[]{actionString, t.getId(), t.getTitle()}), e);
					} else if (attachment instanceof String) {
						log.error(new ParameterizedMessage("Search index operation '{}' errored for ID: {}", new Object[]{actionString, attachment}), e);
					}
				}
			}
		});
	}
	
}
