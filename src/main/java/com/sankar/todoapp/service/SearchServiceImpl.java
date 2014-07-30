package com.sankar.todoapp.service;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.google.inject.Inject;
import com.sankar.todoapp.TodoItem;

public class SearchServiceImpl implements SearchService {
	
	private JestClient client;
	
	@Inject
	public SearchServiceImpl(JestClient client) {
		this.client = client;
	}

	@Override
	public void index(TodoItem todo) {
		try {
			Index indexAction = new Index.Builder(todo).index("todos").type("todo").build();
			client.execute(indexAction);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void update(TodoItem todo) {
		index(todo);
	}

	@Override
	public void delete(String id) {
		try {
			Delete deleteAction = new Delete.Builder(id).index("todos").type("todo").build();
			client.execute(deleteAction);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
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
	
}
