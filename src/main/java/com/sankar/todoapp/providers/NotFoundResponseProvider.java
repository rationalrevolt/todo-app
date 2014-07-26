package com.sankar.todoapp.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.inject.Singleton;
import com.sankar.todoapp.NotFoundException;

@Singleton
@Provider
public class NotFoundResponseProvider implements ExceptionMapper<NotFoundException> {

	@Override
	public Response toResponse(NotFoundException e) {
		return Response.status(Status.NOT_FOUND).build();
	}	
	
}
