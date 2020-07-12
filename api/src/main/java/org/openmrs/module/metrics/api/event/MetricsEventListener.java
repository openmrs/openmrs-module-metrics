package org.openmrs.module.metrics.api.event;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.metrics.MetricEvent;
import org.openmrs.module.metrics.api.model.EventConfiguration;
import org.openmrs.module.metrics.api.service.EventConfigurationService;
import org.openmrs.module.metrics.api.service.MetricService;
import org.openmrs.module.metrics.api.utils.EventsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MetricsEventListener implements EventListener {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
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
			Event.Action action;
			Class<? extends OpenmrsObject> eventClass;
			try {
				uuid = mapMessage.getString("uuid");
				action = Event.Action.valueOf(mapMessage.getString("action"));
				eventClass = (Class<? extends OpenmrsObject>) Class.forName(mapMessage.getString("classname"));
				if (eventClass != null && action != null && uuid != null) {
					String cname = eventClass.getName();
					EventConfiguration eventConfiguration = getEventConfigurationService()
					        .getEventsConfigurationByOpenMrsClass(cname);
					
					if (eventConfiguration != null) {
						MetricEvent metricEvent = EventsUtils.buildEventObject(eventClass.getSimpleName(), uuid, action,
						    eventConfiguration);
						if (metricEvent != null) {
							getMetricService().saveMetricEvent(metricEvent);
							log.info("An event from class {} has been saved in Events DB", eventClass.getSimpleName());
						}
					} else {
						log.debug("Skipped serving hibernate operation on '{}' because "
						        + "object configuration has not been found", eventClass.getName());
						return;
					}
				}
				
			}
			catch (JMSException e) {
				logMessageFailure(message, e);
				;
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
	
	private MetricService getMetricService() {
		return Context.getService(MetricService.class);
	}
	
	private EventConfigurationService getEventConfigurationService() {
		return Context.getService(EventConfigurationService.class);
	}
	
	private void logMessageFailure(Message message, Exception e) {
		log.error("Error while handling message {}", message, e);
	}
	
	public void setToken(DaemonToken token) {
		this.token = token;
	}
}
