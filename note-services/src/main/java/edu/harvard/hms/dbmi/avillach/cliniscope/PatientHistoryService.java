package edu.harvard.hms.dbmi.avillach.cliniscope;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.security.SecurityContext;
import org.bouncycastle.crypto.tls.HashAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.Candidate;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CandidateRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CandidateValidationRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.ClinicalNoteRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.UserRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.transfer.CandidateDTO;
import edu.harvard.hms.dbmi.avillach.cliniscope.transfer.CandidateValidationDTO;
import edu.harvard.hms.dbmi.avillach.cliniscope.transfer.PatientHistoryDTO;
import edu.harvard.hms.dbmi.avillach.cliniscope.transfer.PatientValidationDTO;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

import java.util.HashMap;

@Service
@Path("patientHistory")
public class PatientHistoryService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ClinicalNoteRepository clinicalNoteRepo;

	@Autowired
	private CandidateValidationRepository candidateValidationRepo;

	@Autowired
	private CandidateRepository candidateRepo;

	@GET
	@Path("{patientId}")
	@Produces("application/json")
	@RolesAllowed("ROLE_ADMIN ROLE_ADJUDICATE ROLE_VALIDATE")
	public Response getPatientHistory(@PathParam("patientId") String patientId){		
		
		System.out.println("User : " + username() + " retrieved history for patient " + patientId);

		PatientHistoryDTO history = new PatientHistoryDTO()
				.setPatientId(patientId)
				.setRelations(
						candidateRepo.getCandidatesByPatientId(patientId).stream().collect(Collectors.mapping(candidate -> {
					return new CandidateDTO(candidate).setAdjudicatorEmail(candidate.getAdjudicatorId() == null ? null : userRepo.getById(candidate.getAdjudicatorId()).getAuthenticationName());
				}, Collectors.toList())))
				.setPatientNotes(clinicalNoteRepo.getPatientNotesForPatient(patientId))
				.setCandidateValidations(candidateValidationRepo.getValidationsForPatient(patientId).stream().collect(Collectors.mapping(candidateValidation -> {
					return new CandidateValidationDTO(candidateValidation).setUserEmail(userRepo.getById(candidateValidation.getUserId()).getAuthenticationName());
				}, Collectors.toList())));
		return Response.ok(history).build();
	}

	@GET
	@Produces("application/json")
	@RolesAllowed("ROLE_ADMIN ROLE_ADJUDICATE ROLE_VALIDATE")
	public Response listPatients(){
		System.out.println("User : " + username() + " listed patients ");
		return Response.ok(
				candidateRepo.list().stream().collect(
						groupingBy(Candidate::getPatientId, toList())).
				entrySet().stream().map(
						entry-> new PatientValidationDTO().
						setPatientId(entry.getKey()).
						setCandidateRelations(entry.getValue())).
				sorted(comparing(PatientValidationDTO::getPatientId)).
				collect(toList())).build();
	}

	private String username() {
		return JAXRSUtils.getCurrentMessage().get(
				SecurityContext.class).getUserPrincipal().getName();
	}
}
