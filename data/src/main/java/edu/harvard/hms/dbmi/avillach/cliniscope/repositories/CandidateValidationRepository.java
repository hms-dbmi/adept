package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.Candidate;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CandidateValidation;
import edu.harvard.hms.dbmi.avillach.cliniscope.transfer.PatientNoteDTO;

@Repository
@Transactional
public class CandidateValidationRepository extends BaseRepository<CandidateValidation>{
	
	@Autowired
	private CandidateRepository candidateRepo;
	
	public CandidateValidationRepository(){
		super(new CandidateValidation());
	}

	public boolean setScoreForCandidate(int candidateId, int userId, int score) {
		CandidateValidation validation = new CandidateValidation().setCandidateId(candidateId).setUserId(userId).setScore(score);
		em.merge(validation);
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<CandidateValidation> getValidationsForPatient(String patientId) {
		StoredProcedureQuery candidateValidationQuery = em.createStoredProcedureQuery("CANDIDATE_VALIDATIONS_FOR_PATIENT_ID", CandidateValidation.class);
		candidateValidationQuery.registerStoredProcedureParameter("patientId", String.class, ParameterMode.IN).setParameter("patientId", patientId);
		return candidateValidationQuery.getResultList();
	}
	
	public List<CandidateValidation> getValidationsForPatientCrit(String patientId) {
		candidateRepo.getCandidatesByPatientId(patientId);
		CriteriaQuery<CandidateValidation> query = query();
		Root<CandidateValidation> root = query.from(type);
		Root<Candidate> candidateRoot = query.from(Candidate.class);
		Join<CandidateValidation, Candidate> validationCandidate = root.join("candidateId");
		query.select(root);
		return em.createQuery(query.where(
				eq(candidateRoot, "patientId", patientId))).getResultList();
	}

	public List<Integer> getScoresForCandidate(int candidateId) {
		CriteriaQuery<Integer> query = cb().createQuery(Integer.class);
		Root<CandidateValidation> root = query.from(CandidateValidation.class);
		return em.createQuery(query.where(eq(root, "candidateId", candidateId)).select(root.get("score"))).getResultList();
	}
}
