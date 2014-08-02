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

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.google.inject.Inject;
import com.sankar.todoapp.TodoItem;

public class SearchServiceImpl implements SearchService {
	
	private JestClient client;
	private Executor executor;
	
	@Inject
	public SearchServiceImpl(JestClient client, Executor executor) {
		this.client = client;
		this.executor = executor;
	}

	@Override
	public void index(TodoItem todo) {
		Index indexAction = new Index.Builder(todo).index("todos").type("todo").build();
		executeAsync(indexAction);
	}
	
	@Override
	public void update(TodoItem todo) {
		index(todo);
	}

	@Override
	public void delete(String id) {
		Delete deleteAction = new Delete.Builder(id).index("todos").type("todo").build();
		executeAsync(deleteAction);
	}

	@Override
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
	
	private void executeAsync(final Action<?> action) {
		//TODO: Index updates for a given TodoItem need to happen in the order of arrival
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					client.execute(action);
				} catch(Exception e) {
					System.err.printf("Failed to update search index, Cause: %s%n", e.getMessage());
				}
			}
		});
	}
	
}
