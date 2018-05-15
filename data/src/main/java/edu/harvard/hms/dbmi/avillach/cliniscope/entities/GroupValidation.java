package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(GroupValidation.Key.class)
public class GroupValidation implements Serializable {
	
	@Id
	private int groupId; 
	
	@Id
	private int userId;

	private int score; // -1 = unrelated, 0 = unsure 1 = related
	
	public Key key(){
		return new Key(groupId, userId);
	}
	
	public int getGroupId() {
		return groupId;
	}

	public GroupValidation setGroupId(int groupId) {
		this.groupId = groupId;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public GroupValidation setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public int getScore() {
		return score;
	}

	public GroupValidation setScore(int score) {
		this.score = score;
		return this;
	}

	public static class Key implements Serializable {
		private static final long serialVersionUID = 2491851685793073771L;
		protected int groupId;
		protected int userId;
		
		public Key(){
			
		}
		
		public Key(int groupId, int userId) {
			this.groupId = groupId;
			this.userId = userId;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + groupId;
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
			if (groupId != other.groupId)
				return false;
			if (userId != other.userId)
				return false;
			return true;
		}
		
	}
}
