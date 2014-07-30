package com.sankar.todoapp.boot;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletContextEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.sankar.todoapp.DBProvider;
import com.sankar.todoapp.dao.TodoDAO;
import com.sankar.todoapp.providers.ExceptionResponseProvider;
import com.sankar.todoapp.providers.GsonProvider;
import com.sankar.todoapp.resources.TodoResource;
import com.sankar.todoapp.service.SearchService;
import com.sankar.todoapp.service.SearchServiceImpl;
import com.sankar.todoapp.service.TodoService;
import com.sankar.todoapp.service.TodoServiceImpl;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class ContextListener extends GuiceServletContextListener {
	
	private String mongoDatabaseName;
	private MongoClient mongoClient;
	private JestClient jestClient;
	
	public ContextListener() {
		Map<String,String> env = System.getenv();
		
		configureMongoDB(env);
		configureSearchBox(env);
	}
	
	private void configureMongoDB(Map<String,String> env) {
		String mongo_host = env.get("MONGO_HOST");
		String mongo_port = env.get("MONGO_PORT");
		String mongo_schema = env.get("MONGO_SCHEMA");
		String mongo_user = env.get("MONGO_USER");
		String mongo_password = env.get("MONGO_PASSWORD");
		
		mongoDatabaseName = mongo_schema;
		
		try {
			ServerAddress addr = new ServerAddress(mongo_host, Integer.valueOf(mongo_port));
			MongoCredential auth = MongoCredential.createMongoCRCredential(mongo_user, mongo_schema, mongo_password.toCharArray());
			
			mongoClient = new MongoClient(addr,Arrays.asList(auth));
		} catch(UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void configureSearchBox(Map<String,String> env) {
		String searchbox_url = env.get("SEARCHBOX_URL");
		
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig
		                        .Builder(searchbox_url)
		                        .multiThreaded(true)
		                        .build());
		
		jestClient = factory.getObject();
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
					
					bind(TodoDAO.class);
					bind(TodoService.class).to(TodoServiceImpl.class);
					bind(SearchService.class).to(SearchServiceImpl.class);
					
					bind(Gson.class).toInstance(new GsonBuilder().setPrettyPrinting().create());
					
					bindConstant().annotatedWith(Names.named("MONGO_DATABASE_NAME")).to(mongoDatabaseName);
					bind(MongoClient.class).toInstance(mongoClient);
					bind(DB.class).toProvider(DBProvider.class);
					
					bind(JestClient.class).toInstance(jestClient);
					
	                serve("/apiv1/*").with(GuiceContainer.class);
				}
			}
		);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		super.contextDestroyed(servletContextEvent);
		
		mongoClient.close();
		jestClient.shutdownClient();
	}

}
