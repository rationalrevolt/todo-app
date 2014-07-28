package com.sankar.todoapp.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.inject.Singleton;
import com.sankar.todoapp.NotFoundException;

@Singleton
@Provider
public class ExceptionResponseProvider implements ExceptionMapper<RuntimeException> {

	@Override
	public Response toResponse(RuntimeException e) {
		if (e instanceof NotFoundException)
			return Response.status(Status.NOT_FOUND).build();
		else
			return Response.serverError().build();
	}	
	
}
