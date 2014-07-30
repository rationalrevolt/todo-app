package com.sankar.todoapp.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sankar.todoapp.GsonWritable;
import com.sankar.todoapp.TodoItem;

@Singleton
@Provider
@Produces("application/json")
public class GsonProvider implements MessageBodyWriter<Object>, MessageBodyReader<TodoItem> {
	
	private Gson gson;
	
	@Inject
	public GsonProvider(Gson gson) {
		this.gson = gson;
	}

	@Override
	public long getSize(Object entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (type.getAnnotation(GsonWritable.class) != null) 
			return true;
		
		if (Collection.class.isAssignableFrom(type) && genericType instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType)genericType;
			Type typeParam = ptype.getActualTypeArguments()[0];
			if (typeParam instanceof Class) {
				if ( ((Class<?>)typeParam).equals(String.class) )
					return true;
				if ( ((Class<?>)typeParam).isAnnotationPresent(GsonWritable.class) ) 
					return true;
			}
		}
		return false;
	}

	@Override
	public void writeTo(
			Object entity, 
			Class<?> type, 
			Type genericType, 
			Annotation[] annotations, 
			MediaType mediaType, 
			MultivaluedMap<String,Object> httpHeaders, 
			OutputStream entityStream) 
					
					throws IOException, WebApplicationException {
		
		entityStream.write(gson.toJson(entity).getBytes());
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(TodoItem.class);
	}

	@Override
	public TodoItem readFrom(
			Class<TodoItem> type, 
			Type genericType, 
			Annotation[] annotations, 
			MediaType mediaType, 
			MultivaluedMap<String,String> httpHeaders, 
			InputStream entityStream)
			
					throws IOException, WebApplicationException {
		
		return gson.fromJson(new InputStreamReader(entityStream), TodoItem.class);
	}

}
