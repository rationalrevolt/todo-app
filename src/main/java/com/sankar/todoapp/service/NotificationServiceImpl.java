package com.sankar.todoapp.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sankar.todoapp.TodoItem;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;

public class NotificationServiceImpl implements NotificationService {
	
	private TwilioRestClient twilio;
	
	private String smsFrom;
	private String smsTo;
	
	@Inject
	public NotificationServiceImpl(TwilioRestClient twilio, @Named("SMS_FROM_NUMBER") String smsFrom, @Named("SMS_TO_NUMBER") String smsTo) {
		this.twilio = twilio;
		this.smsFrom = smsFrom;
		this.smsTo = smsTo;
	}

	@Override
	public void notifyCompleted(TodoItem item) {
		Account account = twilio.getAccount();
		MessageFactory msgFactory = account.getMessageFactory();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("To", smsTo));
		params.add(new BasicNameValuePair("From", smsFrom));
		params.add(new BasicNameValuePair("Body", "Task complete: " + item.getTitle()));
		
		try {
			msgFactory.create(params);
		} catch (TwilioRestException e) {
			throw new RuntimeException(e);
		}
	}

}
