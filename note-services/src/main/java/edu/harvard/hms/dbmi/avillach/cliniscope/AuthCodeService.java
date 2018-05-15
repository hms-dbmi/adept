package edu.harvard.hms.dbmi.avillach.cliniscope;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.User;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.UserRepository;

import static com.google.common.collect.ImmutableMap.of;

@Service
public class AuthCodeService {
	
	@Autowired
	private UserRepository userRepo;
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Context
	private UriInfo uriInfo;
	
	@Value("${applicationBaseUri}")
	private String applicationBaseUri;
	
	@POST
	@Path("auth")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateAuthCode(Map<String, String> authCodeRequest){
		try {
			Map<String, String> tokenMap = retrieveIdToken(authCodeRequest.get("code"));
			String accessToken = tokenMap.get("access_token");
			Map<String, Object> userInfo = retrieveUserInfo(accessToken);
			String username = (String) userInfo.get("email");
			String sub = (String) userInfo.get("sub");
			User user = userRepo.getUserByUsername(username);
			userRepo.updateAuthenticationSourceForUser(user, sub);
			return ok(of(
				"username" , username,
				"userId", user.getUserId(),
				"permissions" , buildPermissionsMap(user),
				"token", tokenMap.get("id_token"),
				"access_token", retrieveAccessToken().get("access_token")
				)).build();
		} catch (Exception e) {
			logger.error("Exception attempting to validate auth code : ", e);
		}
		return status(401).build();
	}

	private Map<String, Boolean> buildPermissionsMap(User user) {
		return of(
				"admin", user.getIsAdmin(), 
				"adjudicate", user.getCanAdjudicate(), 
				"validate", user.getCanValidate());
	}

	private Map<String, String> retrieveIdToken(String authCode){
		WebClient client = WebClient.
				create("https://avillachlab.auth0.com/oauth/token", ImmutableList.of(new JacksonJsonProvider())).
				header("Content-Type", MediaType.APPLICATION_JSON);
		Logger.getLogger(this.getClass()).error(getRedirectUri());
		Map<String, String> tokenResponse = client.post(
				new ImmutableMap.Builder<String, String>()
					.put("grant_type", "authorization_code")
					.put("client_id", "MUPJoktRm8irc1yOqCfbP5IvAONQtK4W")
					.put("client_secret", "BTjvwFbIucipB5DF1zarLA7P7_nnd0LEEEMhW8QAdxMFTeiR26RVQidS7-6jr6kD")
					.put("code", authCode)
					.put("scope", "admin")
					.put("redirect_uri", getRedirectUri())
							.build() , Map.class);
		return tokenResponse;
	}

	private String getRedirectUri() {
		URI requestUri = uriInfo.getRequestUri();
		return applicationBaseUri + "/login.html" ;
	}

	private Map<String, String> retrieveAccessToken(){
		WebClient client = WebClient.
				create("https://avillachlab.auth0.com/oauth/token", ImmutableList.of(new JacksonJsonProvider())).
				header("Content-Type", MediaType.APPLICATION_JSON);
		Map<String, String> tokenResponse = client.post(
				new ImmutableMap.Builder<String, String>()
					.put("grant_type", "client_credentials")
					.put("client_id", "MUPJoktRm8irc1yOqCfbP5IvAONQtK4W")
					.put("scopes", "offline-access")
					.put("client_secret", "BTjvwFbIucipB5DF1zarLA7P7_nnd0LEEEMhW8QAdxMFTeiR26RVQidS7-6jr6kD")
					.put("audience", "https://cliniscope-dev.hms.harvard.edu")
							.build() , Map.class);
		return tokenResponse;
	}
	
	private Map<String, Object> retrieveUserInfo(String authToken){
		WebClient client = WebClient.
				create("https://avillachlab.auth0.com/userinfo", ImmutableList.of(new JacksonJsonProvider())).
				header("Content-Type", MediaType.APPLICATION_JSON).
				header("Authorization", "Bearer " + authToken);
		Map<String, Object> userinfo = client.get(Map.class);
		return userinfo;
	}
	
}
