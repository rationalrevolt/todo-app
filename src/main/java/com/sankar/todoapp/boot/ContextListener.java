package com.sankar.todoapp.boot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sankar.todoapp.providers.GsonProvider;
import com.sankar.todoapp.providers.NotFoundResponseProvider;
import com.sankar.todoapp.resources.TodoResource;
import com.sankar.todoapp.service.TodoService;
import com.sankar.todoapp.service.TodoServiceImpl;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class ContextListener extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(
			createJerseyServletModule(),
			createTodoAppModule()
		);
	}
	
	private Module createJerseyServletModule() {
		return new JerseyServletModule() {
			@Override
			protected void configureServlets() {
				bind(TodoResource.class);
				bind(GsonProvider.class);
				bind(NotFoundResponseProvider.class);
				bind(Gson.class).toInstance(new GsonBuilder().create());
                serve("/apiv1/*").with(GuiceContainer.class);
			}
		};
	}
	
	private Module createTodoAppModule() {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(TodoService.class).to(TodoServiceImpl.class);
			}
		};
	}

}
