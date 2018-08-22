package edu.harvard.hms.dbmi.avillach.cliniscope;

public class Activity {

	public Activity(String description2) {
		this.description = description2;
		this.startTime = System.currentTimeMillis();
		this.expirationTime = this.startTime + (1000 * 60 * 5);
	}

	String description;
	
	long startTime;
	
	long expirationTime;

	public void updateExpirationTime() {
		this.expirationTime = System.currentTimeMillis() + (1000 * 60 * 5);
	}
	
}
