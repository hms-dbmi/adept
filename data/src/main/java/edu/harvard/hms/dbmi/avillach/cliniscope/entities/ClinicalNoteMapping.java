package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ClinicalNoteMapping {

	@Id
	private String uuid;
	private String sourceId;
	
	public String getUuid() {
		return uuid;
	}
	public ClinicalNoteMapping setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	
	public String getSourceId() {
		return sourceId;
	}
	public ClinicalNoteMapping setSourceId(String sourceId) {
		this.sourceId = sourceId;
		return this;
	}
	
}
