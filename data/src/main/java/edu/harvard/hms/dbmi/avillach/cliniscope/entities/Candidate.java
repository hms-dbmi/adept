package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Candidate {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String patientId;
	
	private String drug;
	
	private String event;
	
	private String drugCui;
	
	private String eventCui;
	
	private boolean isAdjudicated;
	
	private Integer adjudicatorId;
	
	private Integer adjudicatedScore;

	public String getPatientId() {
		return patientId;
	}

	public Candidate setPatientId(String string) {
		this.patientId = string;
		return this;
	}

	public String getDrug() {
		return drug;
	}

	public Candidate setDrug(String drug) {
		this.drug = drug;
		return this;
	}

	public String getEvent() {
		return event;
	}

	public Candidate setEvent(String event) {
		this.event = event;
		return this;
	}

	public String getDrugCui() {
		return drugCui;
	}

	public Candidate setDrugCui(String drugCui) {
		this.drugCui = drugCui;
		return this;
	}

	public String getEventCui() {
		return eventCui;
	}

	public Candidate setEventCui(String eventCui) {
		this.eventCui = eventCui;
		return this;
	}

	public boolean isAdjudicated() {
		return isAdjudicated;
	}

	public Candidate setAdjudicated(boolean isAdjudicated) {
		this.isAdjudicated = isAdjudicated;
		return this;
	}

	public Integer getAdjudicatorId() {
		return adjudicatorId;
	}

	public Candidate setAdjudicatorId(Integer adjudicatorId) {
		this.adjudicatorId = adjudicatorId;
		return this;
	}

	public Integer getAdjudicatedScore() {
		return adjudicatedScore;
	}

	public Candidate setAdjudicatedScore(Integer adjudicatedScore) {
		this.adjudicatedScore = adjudicatedScore;
		return this;
	}
	
	public int getId() {
		return id;
	}

	public Candidate setId(int id) {
		this.id = id;
		return this;
	}

}
