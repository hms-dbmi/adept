package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
	
	@Id
	private int userId;

	private boolean canValidate;
	
	private boolean canAdjudicate;
	
	private boolean isAdmin;
	
	private String authenticationName;

	private String authenticationSource;
	
	public int getUserId() {
		return userId;
	}

	public User setUserId(int userId) {
		this.userId = userId;
		return this;
	}

	public boolean getCanValidate() {
		return canValidate;
	}

	public User setCanValidate(boolean canValidate) {
		this.canValidate = canValidate;
		return this;
	}

	public boolean getCanAdjudicate() {
		return canAdjudicate;
	}

	public User setCanAdjudicate(boolean canAdjudicate) {
		this.canAdjudicate = canAdjudicate;
		return this;
	}

	public boolean getIsAdmin() {
		return isAdmin;
	}

	public User setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
		return this;
	}
	
	public String getAuthenticationName() {
		return authenticationName;
	}

	public User setAuthenticationName(String authenticationName) {
		this.authenticationName = authenticationName;
		return this;
	}

	public String getAuthenticationSource() {
		return authenticationSource;
	}

	public User setAuthenticationSource(String authenticationSource) {
		this.authenticationSource = authenticationSource;
		return this;
	}


}
