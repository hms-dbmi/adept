package edu.harvard.hms.dbmi.avillach.cliniscope;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.security.SecurityContext;
import org.springframework.stereotype.Service;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.UserInteraction;

@Service
@Path("interaction")
public class UserInteractionService {
	
	private HashMap<String, Activity> userActivityMap = new HashMap<>();
	
	@POST
	@Consumes("application/json")
	public Response recordInteraction(UserInteraction interaction) {
		String username = JAXRSUtils.getCurrentMessage().get(
				SecurityContext.class).getUserPrincipal().getName();
		Activity currentActivity = userActivityMap.get(username);
		if(currentActivity == null) {
			// No activity has been recorded for this user yet
			userActivityMap.put(username, new Activity(interaction.getDescription()));
		} else if( ! currentActivity.description.equals(interaction.getDescription())) {
			// The user transitioned to a new activity
			if(currentActivity.expirationTime > System.currentTimeMillis()) {
				// The last activity was less than expiration time
				System.out.println("User : " + username + " spent " + (System.currentTimeMillis() - currentActivity.startTime) + " " + interaction.getDescription());
				currentActivity.description = interaction.getDescription();
				currentActivity.startTime = System.currentTimeMillis();
			} else {
				// The last activity had expired
				System.out.println(expirationMessage(interaction, username, currentActivity));
				userActivityMap.put(username, new Activity(interaction.getDescription()));
			}
		} else {
			// The user is still in the same activity
			if(currentActivity.expirationTime > System.currentTimeMillis()) {
				// The last activity was less than expiration time
				currentActivity.updateExpirationTime();				
			} else {
				// The user has been idle beyond the limits of the activity, restarting the activity now
				System.out.println(expirationMessage(interaction, username, currentActivity));
				userActivityMap.put(username, new Activity(interaction.getDescription()));
			}
		}
		return Response.accepted().build();
	}

	private String expirationMessage(UserInteraction interaction, String username, Activity currentActivity) {
		return "User : " + username + " spent " + (currentActivity.expirationTime - currentActivity.startTime) + " " + interaction.getDescription() + " (timeout)";
	}
}
