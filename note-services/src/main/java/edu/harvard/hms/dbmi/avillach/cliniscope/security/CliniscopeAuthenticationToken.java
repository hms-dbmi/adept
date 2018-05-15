package edu.harvard.hms.dbmi.avillach.cliniscope.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.ImmutableList;

public class CliniscopeAuthenticationToken extends AbstractAuthenticationToken {

	private final String jwt;
	
	public CliniscopeAuthenticationToken(String jwt){
		super(ImmutableList.of());
		this.jwt = jwt;
	}
	
	@Override
	public Object getCredentials() {
		return jwt;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

}
