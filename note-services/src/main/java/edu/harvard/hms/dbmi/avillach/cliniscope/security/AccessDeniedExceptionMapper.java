package edu.harvard.hms.dbmi.avillach.cliniscope.security;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.interceptor.security.AccessDeniedException;

import com.google.common.collect.ImmutableMap;

@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException>{

	@Override
	public Response toResponse(AccessDeniedException arg0) {
		return Response.status(401).type(MediaType.APPLICATION_JSON).entity(ImmutableMap.of("message", 
				"You must authenticate with a valid JWT using the Bearer Authorization scheme."))
				.build();
	}

}
