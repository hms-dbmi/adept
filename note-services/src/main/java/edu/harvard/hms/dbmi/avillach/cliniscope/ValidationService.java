package edu.harvard.hms.dbmi.avillach.cliniscope;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CandidateValidationRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.GroupValidationRepository;

@Service
@Path("validation")
public class ValidationService {

	@Autowired
	GroupValidationRepository groupRepo;
	
	@Autowired
	CandidateValidationRepository candidateRepo;
	
	@Autowired
	AdjudicationService adjudicationService;
	
	@Path("group/{groupId}/{score}")
	@PUT
	@RolesAllowed("ROLE_VALIDATE")
	public Response setValidationForGroup(@PathParam("groupId") int groupId, @PathParam("score") int score){
		groupRepo.setScoreForGroup(groupId, score);
		return Response.ok().build();
	}
	
	@Path("candidate/{candidateId}/user/{userId}/score/{score}")
	@PUT
	@RolesAllowed("ROLE_VALIDATE")
	public Response setValidationForCandidate(@PathParam("candidateId") int candidateId, @PathParam("userId") int userId, @PathParam("score") int score){
		candidateRepo.setScoreForCandidate(candidateId, userId, score);
		List<Integer> scores = candidateRepo.getScoresForCandidate(candidateId);
		if(scores.size()>1 && scores.stream().distinct().collect(Collectors.toList()).size() == 1) {
			adjudicationService.adjudicateCandidateScore(candidateId, -1, scores.get(0));
		}
		return Response.ok().build();
	}
	
	@Path("patient/{patientId}")
	@GET
	@RolesAllowed("ROLE_VALIDATE ROLE_ADJUDICATE ROLE_ADMIN")
	public Response getValidationsForPatient(@PathParam("patientId") String patientId){
		return Response.ok(candidateRepo.getValidationsForPatient(patientId)).build();
	}
}
