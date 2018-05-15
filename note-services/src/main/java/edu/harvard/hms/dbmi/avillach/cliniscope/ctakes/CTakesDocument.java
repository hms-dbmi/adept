package edu.harvard.hms.dbmi.avillach.cliniscope.ctakes;

import java.util.List;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesGroup;

public class CTakesDocument {
	private String patientId;
	private String documentId;
	private String markup;
	private List<CTakesGroup> groups;
	
	public String getDocumentId() {
		return documentId;
	}
	public CTakesDocument setDocumentId(String documentId) {
		this.documentId = documentId;
		return this;
	}
	
	public String getPatientId() {
		return patientId;
	}
	public CTakesDocument setPatientId(String patientId) {
		this.patientId = patientId;
		return this;
	}
	
	public List<CTakesGroup> getGroups() {
		return groups;
	}
	public CTakesDocument setGroups(List<CTakesGroup> groups) {
		this.groups = groups;
		return this;
	}
	
	public String getMarkup() {
		return markup;
	}
	public CTakesDocument setMarkup(String markup) {
		this.markup = markup;
		return this;
	}
}
