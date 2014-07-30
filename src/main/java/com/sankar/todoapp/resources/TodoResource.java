package com.sankar.todoapp.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sankar.todoapp.Message;
import com.sankar.todoapp.TodoItem;
import com.sankar.todoapp.service.SearchService;
import com.sankar.todoapp.service.TodoService;

@RequestScoped
@Resource
@Path("todos")
@Produces("application/json")
public class TodoResource {
	
	private TodoService todoService;
	private SearchService searchService;
	
	@Inject
	public TodoResource(TodoService todoService, SearchService searchService) {
		this.todoService = todoService;
		this.searchService = searchService;
	}
	
	@GET
	public Collection<TodoItem> getAllTodos() {
		return todoService.getAllTodos();
	}
	
	@GET @Path("{id}")
	public TodoItem getTodoItem(@PathParam("id") String id) {
		return todoService.get(id);			
	}
	
	@POST @Consumes("application/json")
	public Response addTodoItem(TodoItem item) throws URISyntaxException {
		if (item.getId() == null) {
			todoService.add(item);
			searchService.index(item);
			
			String id = item.getId();
			URI location = new URI(id);
			return Response
					.created(location)
					.entity(Message.created(id))
					.build();
		} else 
			return Response
					.status(Status.FORBIDDEN)
					.entity(Message.error("\"id\" should not be provided when creating a new resource"))
					.build();
	}
	
	@PUT @Path("{id}") @Consumes("application/json")
	public Response updateTodoItem(@PathParam("id") String id, TodoItem item) {
		if (item.getId() == null || id.equals(item.getId())) {
			item.setId(id);
			todoService.update(item);
			searchService.update(item);
			return null;
		} else 
			return Response
					.status(Status.FORBIDDEN)
					.entity(Message.error("\"id\" does not match resource"))
					.build();
	}
	
	@DELETE @Path("{id}")
	public void deleteTodoItem(@PathParam("id") String id) {
		todoService.delete(id);
		searchService.delete(id);
	}
	
	@GET @Path("search")
	public List<String> search(@QueryParam("q") String query) {
		return searchService.hits(query);
	}

}
