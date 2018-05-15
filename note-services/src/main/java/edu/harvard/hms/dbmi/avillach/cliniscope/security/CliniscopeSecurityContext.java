package edu.harvard.hms.dbmi.avillach.cliniscope.security;

import java.security.Principal;
import java.util.Map;

import org.apache.cxf.security.SecurityContext;

import com.google.common.collect.ImmutableMap;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.User;

public class CliniscopeSecurityContext implements SecurityContext {

	class CliniscopePrincipal implements Principal {

		private String username;
		
		public CliniscopePrincipal(String username) {
			this.username = username;
		}
		
		@Override
		public String getName() {
			return username;
		}
		
	}
	
	private CliniscopePrincipal userPrincipal;
	
	private Map<String, Boolean> userRoleMap;
	
	public CliniscopeSecurityContext(User user){
		if(user!=null){
			this.userPrincipal = new CliniscopePrincipal(user.getAuthenticationName());
			this.userRoleMap = ImmutableMap.of(
					"ROLE_ADMIN", user.getIsAdmin(), 
					"ROLE_VALIDATE", user.getCanValidate(), 
					"ROLE_ADJUDICATE", user.getCanAdjudicate());			
		}else{
			this.userPrincipal = new CliniscopePrincipal("Unauthenticated");
			this.userRoleMap = ImmutableMap.of();
		}
	}
	
	private static final String BEARER = "Bearer";

	@Override
	public Principal getUserPrincipal() {
		return userPrincipal;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		Boolean userIsInRole = userRoleMap.get(arg0);
		return userIsInRole == null ? false : userIsInRole;
	}

}
