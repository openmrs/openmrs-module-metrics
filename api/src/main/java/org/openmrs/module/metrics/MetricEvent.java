package org.openmrs.module.metrics;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.util.Date;

import org.openmrs.BaseOpenmrsData;

@Entity(name = "metric.Event")
@Table(name = "metric_event_records")
@Inheritance(strategy = InheritanceType.JOINED)
public class MetricEvent extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "metric_event_id")
	private Integer id;
	
	@Basic
	@Column(name = "title")
	private String title;
	
	@Basic
	@Column(name = "time_stamp")
	private Date timeStamp;
	
	@Basic
	@Column(name = "object_uuid")
	private String objectUuid;
	
	@Basic
	@Column(name = "object")
	private String serializedContents;
	
	@Basic
	@Column(name = "category")
	private String category;
	
	@Basic
	@Column(name = "tags")
	private String tags;
	
	public MetricEvent(String title, Date timeStamp, String object_uuid, String serializedContents, String category,
	    String tags) {
		this.timeStamp = timeStamp;
		this.objectUuid = object_uuid;
		this.serializedContents = serializedContents;
		this.title = title;
		this.category = category;
		this.tags = tags;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Date getTimeStamp() {
		return timeStamp;
	}
	
	public String getObjectUuid() {
		return objectUuid;
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
}
