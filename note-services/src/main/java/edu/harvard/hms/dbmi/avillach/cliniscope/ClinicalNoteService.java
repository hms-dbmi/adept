package edu.harvard.hms.dbmi.avillach.cliniscope;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.RateLimiter;

import edu.harvard.hms.dbmi.avillach.cliniscope.ctakes.CTakesDocument;
import edu.harvard.hms.dbmi.avillach.cliniscope.ctakes.CTakesDocumentParser;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesGroup;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesHit;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.ClinicalNote;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CTakesGroupRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CTakesHitRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.ClinicalNoteRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.security.ClinicalNoteDecryptor;
import edu.harvard.hms.dbmi.avillach.cliniscope.transfer.CTakesGroupDTO;
import edu.harvard.hms.dbmi.avillach.cliniscope.transfer.ClinicalNoteDTO;

@Service
public class ClinicalNoteService {
	@Autowired
	public ClinicalNoteRepository clinicalNoterepo;
	@Autowired
	public CTakesGroupRepository ctakesGroupRepo;
	@Autowired
	public CTakesHitRepository ctakesHitRepo;
	@Autowired
	private ClinicalNoteDecryptor decryptor;

	private HashMap<String, RateLimiter> rateLimiters = new HashMap<>();

	private HashMap<String, Integer> rateLimitExceededCounters = new HashMap<>();

	@PUT
	@Path("rateLimitReset/{user}")
	@RolesAllowed("ROLE_ADMIN")
	public Response resetRateLimit(@PathParam("user") String userName) {
		rateLimitExceededCounters.put(userName, 0);
		return Response.ok().build();
	}
	
	@GET
	@Path("document/{documentId}")
    @Produces("application/json")
	@RolesAllowed("ROLE_ADMIN ROLE_ADJUDICATE ROLE_VALIDATE")
	public Response getNote(@PathParam("documentId") String documentId){
		if(userHasNotExceededRateLimit()) {
			ClinicalNote clinicalNote = clinicalNoterepo.getById(documentId);
			System.out.println("User : " + username() + " retrieved note " + documentId + " for patient " + clinicalNote.getPatientId());
			if(clinicalNote==null){
				return Response.status(404).build();
			}
			ClinicalNoteDTO note = new ClinicalNoteDTO(clinicalNote);
			try {
				HashMap<CTakesGroup, List<CTakesHit>> groupMap = new HashMap<>();
				List<CTakesGroup> groups = ctakesGroupRepo.getCTakesGroupByClinicalNoteId(documentId);
				List<CTakesGroupDTO> groupDTOs = new ArrayList<CTakesGroupDTO>();
				for(CTakesGroup group : groups){
					List<CTakesHit> cTakesHitsByGroupId = ctakesHitRepo.getCTakesHitsByGroupId(group.getGroupId());
					groupMap.put(group, cTakesHitsByGroupId);
					CTakesGroupDTO groupDTO = new CTakesGroupDTO(group).setHits(cTakesHitsByGroupId);
					groupDTOs.add(groupDTO);
				}
				String noteText;
				boolean gibbify = false;
				if(gibbify){
					noteText = gibbify(clinicalNote.getNoteText());
				}else{
					noteText = decryptor.decryptNoteText(clinicalNote.getNoteText());
				}
				ByteArrayInputStream text = new ByteArrayInputStream(noteText.getBytes("UTF-8"));
				CTakesDocument parseDocument = new CTakesDocumentParser().parseDocument(groupMap, text, documentId);
				note.setMarkup(parseDocument.getMarkup());
				note.setGroups(groupDTOs);
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(404).entity("Document not found or error occurred. \n" + e.getMessage()).build();
			}
			return Response.ok(note).build();
		}else {
			return Response.status(403).entity(
					"You have exceeded the rate limit for retrieving notes. "
					+ "This has been recorded. "
					+ "Further abuse will result in your account being suspended.").build();
		}
	}

	private boolean userHasNotExceededRateLimit() {
		String username = username();
		Integer rateLimitExceededCount = rateLimitExceededCounters.get(username);
		if(rateLimitExceededCount == null) {
			rateLimitExceededCount = 0;
			rateLimitExceededCounters.put(username, rateLimitExceededCount);
		}
		if(rateLimitExceededCount > 3) {
			return false;
		}else {
			RateLimiter limiter = rateLimiters.get(username);
			if(limiter == null) {
				limiter = RateLimiter.create(2);
				rateLimiters.put(username, limiter);
			}
			if(limiter.tryAcquire()) {
				return true;
			}

			rateLimitExceededCounters.put(username, rateLimitExceededCount + 1);
			return false;
		}
	}

	private String username() {
		return JAXRSUtils.getCurrentMessage().get(
				SecurityContext.class).getUserPrincipal().getName();
	}

	static char[] alpha = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	public static String gibbify(String line) {
		char[] out = new char[line.length()+1];
		int x = 0;
		for(Character c : line.toCharArray()){
			if(c.isAlphabetic(c)){
				out[x++] = alpha[(int) (Math.random()*26)];
			}else if(c.isDigit(c)){
				out[x++] = ((Math.random()*10) + "").toCharArray()[0];
			}else{
				out[x++] = c;
			}
		}
		out[line.length()] = '\n';
		return new String(out);
	}
	
}
