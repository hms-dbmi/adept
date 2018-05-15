package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

@Entity
@IdClass(CandidateValidation.Key.class)
public class CandidateValidation {
	
	@Id
	private int candidateId; 
	
	@Id
	private int userId;
	
	private int score; // -1 = unrelated, 0 = unsure 1 = related
	
	public int getCandidateId() {
		return candidateId;
	}

	public CandidateValidation setCandidateId(int candidateId) {
		this.candidateId = candidateId;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public CandidateValidation setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public int getScore() {
		return score;
	}

	public CandidateValidation setScore(int score) {
		this.score = score;
		return this;
	}

	public static class Key implements Serializable {
		private static final long serialVersionUID = -8671998684445383515L;
		protected int candidateId;
		protected int userId;
		
		public Key(){
			
		}
		
		public Key(int candidateId, int userId){
			this.candidateId = candidateId;
			this.userId = userId;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + candidateId;
			result = prime * result + userId;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (candidateId != other.candidateId)
				return false;
			if (userId != other.userId)
				return false;
			return true;
		}
	}
}
