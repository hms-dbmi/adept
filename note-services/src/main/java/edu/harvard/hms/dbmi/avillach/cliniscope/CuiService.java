package edu.harvard.hms.dbmi.avillach.cliniscope;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CTakesHitRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CandidateRepository;

@Service
@Path("cui")
public class CuiService {

	@Autowired
	private CandidateRepository candidateRepo;

	@Autowired
	private CTakesHitRepository hitRepo;
	
	@GET
	@Produces("application/json")
	@RolesAllowed("ROLE_ADMIN ROLE_ADJUDICATE ROLE_VALIDATE")
	public Response getAllCuis(){
		return Response.ok(hitRepo.listUniqueTerms()).build();
	}
	
	@GET
	@Path("candidates")
	@Produces("applicaiton/json")
	@RolesAllowed("ROLE_ADMIN ROLE_ADJUDICATE ROLE_VALIDATE")
	public Response getCandidateRelations(){
		return Response.ok(candidateRepo.listPatientsByCandidateRelation()).build();
	}
}
