package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CTakesGroup {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer groupId; 
	
	private Integer candidateId;
	
	private String clinicalNoteId;
	
	public int getGroupId() {
		return groupId;
	}

	public CTakesGroup setGroupId(int groupId) {
		this.groupId = groupId;
		return this;
	}

	public Integer getCandidateId() {
		return candidateId;
	}

	public CTakesGroup setCandidateId(Integer candidateId) {
		this.candidateId = candidateId;
		return this;
	}

	public String getClinicalNoteId() {
		return clinicalNoteId;
	}

	public CTakesGroup setClinicalNoteId(String clinicalNoteId) {
		this.clinicalNoteId = clinicalNoteId;
		return this;
	}
	
	
}
