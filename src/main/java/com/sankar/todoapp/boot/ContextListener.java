package com.sankar.todoapp.boot;

import java.net.UnknownHostException;
import java.util.Arrays;

import javax.servlet.ServletContextEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.sankar.todoapp.DBConfig;
import com.sankar.todoapp.DBProvider;
import com.sankar.todoapp.EnvironmentDBConfig;
import com.sankar.todoapp.dao.TodoDAO;
import com.sankar.todoapp.providers.ExceptionResponseProvider;
import com.sankar.todoapp.providers.GsonProvider;
import com.sankar.todoapp.resources.TodoResource;
import com.sankar.todoapp.service.TodoService;
import com.sankar.todoapp.service.TodoServiceImpl;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class ContextListener extends GuiceServletContextListener {
	
	private DBConfig dbconf;
	private MongoClient mongoClient;
	
	public ContextListener() {
		dbconf = new EnvironmentDBConfig();
		
		try {
			ServerAddress addr = new ServerAddress(dbconf.getHost(), dbconf.getPort());
			MongoCredential auth = MongoCredential.createMongoCRCredential(dbconf.getUser(), dbconf.getSchema(), dbconf.getPassword().toCharArray());
			
			mongoClient = new MongoClient(addr,Arrays.asList(auth));
		} catch(UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(
			new JerseyServletModule() {
				@Override
				protected void configureServlets() {
					bind(TodoResource.class);
					bind(GsonProvider.class);
					bind(ExceptionResponseProvider.class);
					bind(Gson.class).toInstance(new GsonBuilder().setPrettyPrinting().create());
					bind(MongoClient.class).toInstance(mongoClient);
					bind(DBConfig.class).toInstance(dbconf);
					bind(DB.class).toProvider(DBProvider.class);
					bind(TodoDAO.class);
					bind(TodoService.class).to(TodoServiceImpl.class);
					
	                serve("/apiv1/*").with(GuiceContainer.class);
				}
			}
		);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		super.contextDestroyed(servletContextEvent);
		mongoClient.close();
	}

}
