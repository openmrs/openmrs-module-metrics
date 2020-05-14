package org.openmrs.module.metrics.api.event;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.metrics.api.model.EventConfiguration;
import org.openmrs.module.metrics.api.service.EventConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MetricsEventListener implements EventListener {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private EventConfigurationService eventConfigurationService;
	
	private DaemonToken token;
	
	@Override
	public void onMessage(final Message message) {
		log.trace("Received message {}", message);
		
		Daemon.runInDaemonThread(new Runnable() {
			
			@Override
			public void run() {
				processMessage(message);
			}
		}, token);
	}
	
	protected void processMessage(final Message message) {
		// the event module only sends MapMessages, but just in case
		if (message instanceof MapMessage) {
			MapMessage mapMessage = (MapMessage) message;
			
			String uuid;
			try {
				uuid = mapMessage.getString("uuid");
			}
			catch (JMSException e) {
				logMessageFailure(message, e);
				;
				return;
			}
			
			Class<? extends OpenmrsObject> eventClass;
			try {
				eventClass = (Class<? extends OpenmrsObject>) Class.forName(mapMessage.getString("classname"));
				EventConfiguration eventConfiguration = eventConfigurationService
				        .getEventsConfigurationByOpenMrsClass(eventClass.getClass().getName());
				
				if (eventConfiguration != null) {
					//service to create events object and save to db
				}
			}
			catch (JMSException e) {
				logMessageFailure(message, e);
				return;
			}
			catch (ClassNotFoundException e) {
				logMessageFailure(message, e);
				;
				return;
			}
			catch (ClassCastException e) {
				logMessageFailure(message, e);
				;
				return;
			}
			
			Event.Action action;
			try {
				action = Event.Action.valueOf(mapMessage.getString("action"));
			}
			catch (JMSException e) {
				logMessageFailure(message, e);
				return;
			}
			catch (IllegalArgumentException e) {
				logMessageFailure(message, e);
				return;
			}
			catch (NullPointerException e) {
				logMessageFailure(message, e);
				return;
			}
			
			// do something with the event
		}
	}
	
	private void logMessageFailure(Message message, Exception e) {
		log.error("Error while handling message {}", message, e);
	}
	
	public void setToken(DaemonToken token) {
		this.token = token;
	}
}
