package edu.harvard.hms.dbmi.avillach.cliniscope.security;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableMap;

import io.jsonwebtoken.ExpiredJwtException;

@Provider
public class ExpiredJwtExceptionMapper implements ExceptionMapper<ExpiredJwtException>{

	@Override
	public Response toResponse(ExpiredJwtException arg0) {
		return Response.status(401).type(MediaType.APPLICATION_JSON).entity(ImmutableMap.of("message", 
				"Your JWT is expired, please reauthenticate."))
				.build();
	}

}
