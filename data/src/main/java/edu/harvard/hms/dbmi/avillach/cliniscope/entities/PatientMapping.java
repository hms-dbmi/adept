package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PatientMapping {

	@Id
	private String uuid;
	private String sourceId;
	
	public String getUuid() {
		return uuid;
	}
	public PatientMapping setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	
	public String getSourceId() {
		return sourceId;
	}
	public PatientMapping setSourceId(String sourceId) {
		this.sourceId = sourceId;
		return this;
	}
	
}
