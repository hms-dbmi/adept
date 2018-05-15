package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ClinicalNote {

	@Id
	private String clinicalNoteId;
	
	private String patientId;
	
	private Date dateOfNote;
	
	@JsonIgnore
	private String noteText;

	public String getClinicalNoteId() {
		return clinicalNoteId;
	}

	public ClinicalNote setClinicalNoteId(String clinicalNoteId) {
		this.clinicalNoteId = clinicalNoteId;
		return this;
	}

	public String getPatientId() {
		return patientId;
	}

	public ClinicalNote setPatientId(String patientUUID) {
		this.patientId = patientUUID;
		return this;
	}

	public Date getDateOfNote() {
		return dateOfNote;
	}

	public ClinicalNote setDateOfNote(Date dateOfNote) {
		this.dateOfNote = dateOfNote;
		return this;
	}

	public String getNoteText() {
		return noteText;
	}

	public ClinicalNote setNoteText(String noteText) {
		this.noteText = noteText;
		return this;
	}
	
}
