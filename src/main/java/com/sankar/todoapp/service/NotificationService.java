package com.sankar.todoapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sankar.todoapp.TodoItem;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;

public class NotificationService {
	
	private static Logger log = LogManager.getLogger();
	
	private TwilioRestClient twilio;
	
	private String smsFrom;
	private String smsTo;
	
	private Executor executor;
	
	@Inject
	public NotificationService(
			TwilioRestClient twilio,
			
			@Named("SMS_FROM_NUMBER") 
			String smsFrom, 
			
			@Named("SMS_TO_NUMBER") 
			String smsTo,
			
			Executor executor) {
		
		this.twilio = twilio;
		this.smsFrom = smsFrom;
		this.smsTo = smsTo;
		this.executor = executor;
	}
	
	public void notifyCompleted(final TodoItem item) {
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("To", smsTo));
		params.add(new BasicNameValuePair("From", smsFrom));
		params.add(new BasicNameValuePair("Body", "Task complete: " + item.getTitle()));
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Account account = twilio.getAccount();
					MessageFactory msgFactory = account.getMessageFactory();
					msgFactory.create(params);
					log.info("SMS sent for ID {}, Title {} ", item.getId(), item.getTitle());
				} catch (TwilioRestException e) {
					log.error(new ParameterizedMessage("Error sending SMS for ID: {}, Title: {}", item.getId(), item.getTitle()), e);
				}
			}
		});
	}

}
