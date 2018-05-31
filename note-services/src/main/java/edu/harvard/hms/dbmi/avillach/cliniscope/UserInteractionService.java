package edu.harvard.hms.dbmi.avillach.cliniscope;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.UserInteraction;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.UserInteractionRepo;

@Service
@Path("interaction")
public class UserInteractionService {

	@Autowired
	private UserInteractionRepo interactionRepo;
	
	@POST
	public Response recordInteraction(UserInteraction interaction) {
		interaction.setTimestamp(System.currentTimeMillis());
		interaction.setUser(JAXRSUtils.getCurrentMessage().get(
				SecurityContext.class).getUserPrincipal().getName());
		interactionRepo.record(interaction);
		return Response.accepted().build();
	}
}
