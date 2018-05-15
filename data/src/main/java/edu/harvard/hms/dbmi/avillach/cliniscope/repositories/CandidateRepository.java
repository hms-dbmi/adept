package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesHit;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.Candidate;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.GroupValidation;
import edu.harvard.hms.dbmi.avillach.cliniscope.transfer.PatientsForCandidateDTO;

@Repository
public class CandidateRepository extends BaseRepository<Candidate>{

	private static final String DRUG = "drug";
	private static final String EVENT = "event";
	private static final String DRUG_CUI = "drugCui";
	private static final String EVENT_CUI = "eventCui";
	private static final String PATIENTS_BY_CANDIDATE = "PATIENTS_BY_CANDIDATE";

	public CandidateRepository(){
		super(new Candidate());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public List<Candidate> getCandidatesByPatientId(String patientId){
		CriteriaQuery<Candidate> query = query();
		Root<Candidate> root = query.from(type);
		query.select(root);
		return em.createQuery(query.where(
				eq(root, "patientId", patientId))).getResultList();
	}

	public Object listUniqueCandidateRelations() {
		CriteriaQuery<Tuple> query = cb().createTupleQuery();
		@SuppressWarnings("unchecked")
		Root<CTakesHit> root = query.from(type);
		Path<String> drugPath = root.get(DRUG);
		Path<String> eventPath = root.get(EVENT);
		Path<String> drugCuiPath = root.get(DRUG_CUI);
		Path<String> eventCuiPath = root.get(EVENT_CUI);
		query.multiselect(drugPath, eventPath, drugCuiPath, eventCuiPath).distinct(true);
		return em.createQuery(query).getResultList().stream().map(tuple -> { 
			return ImmutableMap.of(
					DRUG , tuple.get(drugPath), 
					EVENT, tuple.get(eventPath), 
					DRUG_CUI, tuple.get(drugCuiPath), 
					EVENT_CUI, tuple.get(eventCuiPath));
		}).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public List<PatientsForCandidateDTO> listPatientsByCandidateRelation() {
		StoredProcedureQuery hitsQuery = 
				em.createStoredProcedureQuery(PATIENTS_BY_CANDIDATE);
		hitsQuery.execute();
		List<Object[]> resultList = hitsQuery.getResultList();
		
		return resultList.stream().map(entry -> {
			return new PatientsForCandidateDTO()
					.setDrug((String)entry[0])
					.setEvent((String)entry[1])
					.setDrugCui((String)entry[2])
					.setEventCui((String)entry[3])
					.setPatientIds(((String)entry[4]).split(","));
		}).collect(Collectors.toList());
	}

	@Transactional
	public void adjudicateCandidateScore(int candidateId, int score, int userId) {
		em.merge(this.getById(candidateId).setAdjudicatedScore(score).setAdjudicated(true).setAdjudicatorId(userId));
	}
}
