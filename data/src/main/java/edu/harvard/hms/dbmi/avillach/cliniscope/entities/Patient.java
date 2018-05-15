package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Patient {

	@Id
	private String patientId;
	
	private String name;

	public String getPatientId() {
		return patientId;
	}

	public Patient setPatientId(String patientId) {
		this.patientId = patientId;
		return this;
	}

	public String getName() {
		return name;
	}

	public Patient setName(String name) {
		this.name = name;
		return this;
	}
	
}
