package edu.harvard.hms.dbmi.avillach.cliniscope.transfer;

import java.util.List;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.Candidate;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CandidateValidation;

public class PatientHistoryDTO {
	private String patientId;
	private List<PatientNoteDTO> patientNotes;
	private List<CandidateDTO> candidateRelations;
	private List<CandidateValidationDTO> candidateValidations;
	
	public String getPatientId() {
		return patientId;
	}
	public PatientHistoryDTO setPatientId(String patientId) {
		this.patientId = patientId;
		return this;
	}
	public List<PatientNoteDTO> getPatientNotes() {
		return patientNotes;
	}
	public PatientHistoryDTO setPatientNotes(List<PatientNoteDTO> patientNotes) {
		this.patientNotes = patientNotes;
		return this;
	}
	public List<CandidateDTO> getRelations() {
		return candidateRelations;
	}
	public PatientHistoryDTO setRelations(List<CandidateDTO> candidateRelations) {
		this.candidateRelations = candidateRelations;
		return this;
	}
	public List<CandidateValidationDTO> getCandidateValidations() {
		return candidateValidations;
	}
	public PatientHistoryDTO setCandidateValidations(List<CandidateValidationDTO> candidateValidations) {
		this.candidateValidations = candidateValidations;
		return this;
	}
}
