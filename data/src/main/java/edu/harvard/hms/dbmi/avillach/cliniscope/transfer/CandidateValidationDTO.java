package edu.harvard.hms.dbmi.avillach.cliniscope.transfer;

import org.springframework.beans.BeanUtils;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CandidateValidation;

public class CandidateValidationDTO {

	private int candidateId; 

	private int userId;
	
	private String userEmail;
	
	private int score; // -1 = unrelated, 0 = unsure 1 = related
	
	public CandidateValidationDTO(CandidateValidation candidateValidation) {
		BeanUtils.copyProperties(candidateValidation, this);
		candidateId = candidateValidation.getCandidateId();
	}

	public int getCandidateId() {
		return candidateId;
	}

	public CandidateValidationDTO setCandidateId(int candidateId) {
		this.candidateId = candidateId;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public CandidateValidationDTO setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public int getScore() {
		return score;
	}

	public CandidateValidationDTO setScore(int score) {
		this.score = score;
		return this;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public CandidateValidationDTO setUserEmail(String userEmail) {
		this.userEmail = userEmail;
		return this;
	}
}
