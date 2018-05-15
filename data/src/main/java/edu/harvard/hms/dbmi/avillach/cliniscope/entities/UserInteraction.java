package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserInteraction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private BigInteger id;
	
	private int userId;
	
	private long startTime;
	
	private long endTime;

	public BigInteger getId() {
		return id;
	}

	public UserInteraction setId(BigInteger id) {
		this.id = id;
		return this;
	}

	public int getUserId() {
		return userId;
	}

	public UserInteraction setUserId(int userId) {
		this.userId = userId;
		return this;
	}
	
	public long getEndTime() {
		return endTime;
	}

	public UserInteraction setEndTime(long endTime) {
		this.endTime = endTime;
		return this;
	}
	
	public long getStartTime() {
		return startTime;
	}

	public UserInteraction setStartTime(long startTime) {
		this.startTime = startTime;
		return this;
	}

}
