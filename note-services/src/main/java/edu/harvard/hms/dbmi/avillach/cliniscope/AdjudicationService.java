package edu.harvard.hms.dbmi.avillach.cliniscope;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CandidateRepository;

@Service
@RolesAllowed("ROLE_ADMIN ROLE_ADJUDICATE")
public class AdjudicationService {

	@Autowired
	private CandidateRepository candidateRepo;
	
	@PUT
	@Path("adjudicateCandidate/{candidateId}/user/{userId}/score/{score}")
	public Response adjudicateCandidateScore(@PathParam("candidateId") int candidateId, @PathParam("userId") int userId, @PathParam("score") int score){	
		candidateRepo.adjudicateCandidateScore(candidateId, score, userId);
		return Response.ok().build();
	}

}
