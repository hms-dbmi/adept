package edu.harvard.hms.dbmi.avillach.cliniscope.security;

import java.io.IOException;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.security.SecurityContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.User;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

@PreMatching
@Component
@Provider
public class CliniscopeAuthFilter implements ContainerRequestFilter {
	
	@Value("${auth0.client_secret}")
	private String clientSecret;
	
	@Autowired
	UserRepository userRepo;
	
	ObjectMapper mapper = new ObjectMapper();

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorization = requestContext.getHeaderString("Authorization");
		if (authorization == null) { 
			attachSecurityContext(null);
		}else{
	        String[] parts = authorization.split(" ");
	        if (parts.length != 2 || !"Bearer".equals(parts[0])) {
	            requestContext.abortWith(malformedOrNonExistantAuthorizationHeader());
	            return;
	        }
	        Jwt<JwsHeader, Claims> claims = Jwts.parser()
	        		.setSigningKey(clientSecret.getBytes())
	        		.parseClaimsJws(parts[1]);
	        
	        String sub = (String) claims.getBody().get("sub");
	        
	        User user = retrieveUser(sub);
			
	        attachSecurityContext(user);
		}
	}

	private void attachSecurityContext(User user) {
		JAXRSUtils.getCurrentMessage().put(
				SecurityContext.class, new CliniscopeSecurityContext(user));
	}

	private User retrieveUser(String sub) {
		try {
			return userRepo.getUserByAuthentication(sub);
		}catch(NoResultException e) {
			return null;
		}
	}
	
	private Map<String, Object> retrieveUserInfo(String authToken){
		WebClient client = WebClient.
				create("https://avillachlab.auth0.com/userinfo", 
						ImmutableList.of(new JacksonJsonProvider())).
				header("Content-Type", MediaType.APPLICATION_JSON).
				header("Authorization", "Bearer " + authToken);
		Map<String, Object> userinfo = client.get(Map.class);
		return userinfo;
	}

	private Response malformedOrNonExistantAuthorizationHeader() {
		return Response.status(401).header("Content-Type", "application/json").entity(ImmutableMap.of(
				"status", "unauthenticated", 
				"message", "A bearer scheme Authorization header is required on all requests.")).build();
	}

	private Response userNotAuthorized() {
		return Response.status(401).header("Content-Type", "application/json").entity(ImmutableMap.of(
				"status", "unauthorized", 
				"message", "Sorry, your user has not been authorized for access to this system.")).build();
	}
	
}
