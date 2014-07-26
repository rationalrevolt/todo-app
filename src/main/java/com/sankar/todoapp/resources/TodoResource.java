package com.sankar.todoapp.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sankar.todoapp.Message;
import com.sankar.todoapp.TodoItem;
import com.sankar.todoapp.service.TodoService;

@RequestScoped
@Resource
@Path("todos")
@Produces("application/json")
public class TodoResource {
	
	private TodoService service;
	
	@Inject
	public TodoResource(TodoService service) {
		this.service = service;
	}
	
	@GET
	public Collection<TodoItem> getAllTodos() {
		return service.getAllTodos();
	}
	
	@GET @Path("{id}")
	public TodoItem getTodoItem(@PathParam("id") String id) {
		return service.get(id);			
	}
	
	@POST @Consumes("application/json")
	public Response addTodoItem(TodoItem item) throws URISyntaxException {
		if (item.getId() == null) {
			service.add(item);
			
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
			service.update(item);
			return null;
		} else 
			return Response
					.status(Status.FORBIDDEN)
					.entity(Message.error("\"id\" does not match resource"))
					.build();
	}
	
	@DELETE @Path("{id}")
	public void deleteTodoItem(@PathParam("id") String id) {
		service.delete(id);
	}

}
