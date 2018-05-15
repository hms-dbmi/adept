package edu.harvard.hms.dbmi.avillach.cliniscope.transfer;

import java.util.List;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.Candidate;

public class PatientValidationDTO {

	private String patientId;
	private List<Candidate> candidateRelations;
	
	public String getPatientId() {
		return patientId;
	}
	public PatientValidationDTO setPatientId(String patientId) {
		this.patientId = patientId;
		return this;
	}
	
	public List<Candidate> getCandidateRelations() {
		return candidateRelations;
	}
	public PatientValidationDTO setCandidateRelations(List<Candidate> candidateRelations) {
		this.candidateRelations = candidateRelations;
		return this;
	}
	
}
