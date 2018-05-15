package edu.harvard.hms.dbmi.avillach.cliniscope.transfer;

public class PatientNoteDTO {

	private String date;
	private String noteId;
	private String[] drugCuis;
	private String[] eventCuis;
	private String[] candidateIds;
	
	public String getDate() {
		return date;
	}
	public PatientNoteDTO setDate(String date) {
		this.date = date;
		return this;
	}
	public String getNoteId() {
		return noteId;
	}
	public PatientNoteDTO setNoteId(String noteId) {
		this.noteId = noteId;
		return this;
	}
	public String[] getDrugCuis() {
		return drugCuis;
	}
	public PatientNoteDTO setDrugCuis(String[] drugCuis) {
		this.drugCuis = drugCuis;
		return this;
	}
	public String[] getEventCuis() {
		return eventCuis;
	}
	public PatientNoteDTO setEventCuis(String[] eventCuis) {
		this.eventCuis = eventCuis;
		return this;
	}
	public String[] getCandidateIds() {
		return candidateIds;
	}
	public PatientNoteDTO setCandidateIds(String[] candidateIds) {
		this.candidateIds = candidateIds;
		return this;
	}
}
