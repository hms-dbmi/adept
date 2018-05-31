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
	
	private String user;
	
	private String description;

	private long timestamp;

	public BigInteger getId() {
		return id;
	}

	public UserInteraction setId(BigInteger id) {
		this.id = id;
		return this;
	}

	public String getUser() {
		return user;
	}

	public UserInteraction setUser(String user) {
		this.user = user;
		return this;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public UserInteraction setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public UserInteraction setDescription(String description) {
		this.description = description;
		return this;
	}

}
