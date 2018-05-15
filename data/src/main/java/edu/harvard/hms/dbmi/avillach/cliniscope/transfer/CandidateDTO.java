package edu.harvard.hms.dbmi.avillach.cliniscope.transfer;

import org.springframework.beans.BeanUtils;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.Candidate;

public class CandidateDTO {

	private int candidateId; 

	private int patientId;

	private String drug;

	private String event;

	private String drugCui;

	private String eventCui;

	private boolean isAdjudicated;

	private Integer adjudicatorId;

	private Integer adjudicatedScore;
	
	private String adjudicatorEmail;

	public CandidateDTO(Candidate candidate) {
		BeanUtils.copyProperties(candidate, this);
		this.candidateId = candidate.getId();
	}

	public int getCandidateId() {
		return candidateId;
	}

	public CandidateDTO setCandidateId(int candidateId) {
		this.candidateId = candidateId;
		return this;
	}

	public int getPatientId() {
		return patientId;
	}

	public CandidateDTO setPatientId(int patientId) {
		this.patientId = patientId;
		return this;
	}

	public String getDrug() {
		return drug;
	}

	public CandidateDTO setDrug(String drug) {
		this.drug = drug;
		return this;
	}

	public String getEvent() {
		return event;
	}

	public CandidateDTO setEvent(String event) {
		this.event = event;
		return this;
	}

	public String getDrugCui() {
		return drugCui;
	}

	public CandidateDTO setDrugCui(String drugCui) {
		this.drugCui = drugCui;
		return this;
	}

	public String getEventCui() {
		return eventCui;
	}

	public CandidateDTO setEventCui(String eventCui) {
		this.eventCui = eventCui;
		return this;
	}

	public boolean isAdjudicated() {
		return isAdjudicated;
	}

	public CandidateDTO setAdjudicated(boolean isAdjudicated) {
		this.isAdjudicated = isAdjudicated;
		return this;
	}

	public Integer getAdjudicatorId() {
		return adjudicatorId;
	}

	public CandidateDTO setAdjudicatorId(Integer adjudicatorId) {
		this.adjudicatorId = adjudicatorId;
		return this;
	}

	public Integer getAdjudicatedScore() {
		return adjudicatedScore;
	}

	public CandidateDTO setAdjudicatedScore(Integer adjudicatedScore) {
		this.adjudicatedScore = adjudicatedScore;
		return this;
	}

	public String getAdjudicatorEmail() {
		return adjudicatorEmail;
	}

	public CandidateDTO setAdjudicatorEmail(String adjudicatorEmail) {
		this.adjudicatorEmail = adjudicatorEmail;
		return this;
	}

}
