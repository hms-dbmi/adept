package edu.harvard.hms.dbmi.avillach.cliniscope.transfer;

public class PatientsForCandidateDTO {

	private String drug, event;
	private String drugCui, eventCui;
	private String[] patientIds;
	
	public String getDrug() {
		return drug;
	}
	public PatientsForCandidateDTO setDrug(String drug) {
		this.drug = drug;
		return this;
	}
	
	public String getEvent() {
		return event;
	}
	public PatientsForCandidateDTO setEvent(String event) {
		this.event = event;
		return this;
	}

	public String getDrugCui() {
		return drugCui;
	}
	public PatientsForCandidateDTO setDrugCui(String drugCui) {
		this.drugCui = drugCui;
		return this;
	}
	
	public String getEventCui() {
		return eventCui;
	}
	public PatientsForCandidateDTO setEventCui(String eventCui) {
		this.eventCui = eventCui;
		return this;
	}
	
	public String[] getPatientIds() {
		return patientIds;
	}
	public PatientsForCandidateDTO setPatientIds(String[] patientIds) {
		this.patientIds = patientIds;
		return this;
	}
}
