package com.sankar.todoapp.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Singleton;
import com.sankar.todoapp.Message;
import com.sankar.todoapp.NotFoundException;

@Singleton
@Provider
public class ExceptionResponseProvider implements ExceptionMapper<RuntimeException> {
	
	private static Logger log = LogManager.getLogger();

	@Override
	public Response toResponse(RuntimeException e) {
		if (e instanceof NotFoundException)
			return Response.status(Status.NOT_FOUND).build();
		else {
			log.error("Unhandled exception",e);
			Message error = Message.error(e.getMessage());
			return Response.serverError().entity(error).build();
		}
	}	
	
}
