package org.openmrs.module.metrics;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.openmrs.BaseOpenmrsObject;

public class MetricEvent extends BaseOpenmrsObject implements Serializable {
	
	private int id;
	
	private String uuid;
	
	private String title;
	
	private LocalDateTime timeStamp;
	
	private URI uri;
	
	private String serializedContents;
	
	private String category;
	
	private String tags;
	
	private LocalDateTime dateCreated;
	
	public MetricEvent(String uuid, String title, LocalDateTime timeStamp, URI uri, String serializedContents,
	    String category, LocalDateTime dateCreated) {
		this.uuid = uuid;
		this.timeStamp = timeStamp;
		this.uri = uri;
		this.serializedContents = serializedContents;
		this.title = title;
		this.category = category;
		this.dateCreated = dateCreated;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	
	public URI getUri() {
		return uri;
	}
	
	public String getSerializedContents() {
		return serializedContents;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getTags() {
		return tags;
	}
	
	public LocalDateTime getDateCreated() {
		return dateCreated;
	}
	
}
