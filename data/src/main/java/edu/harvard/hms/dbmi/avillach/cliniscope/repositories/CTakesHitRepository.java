package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import java.util.List;
import java.util.Map;
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
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.ClinicalNote;

@Repository
public class CTakesHitRepository extends BaseRepository<CTakesHit>{
	
	private static final String TERM_CLASS = "term_class";
	private static final String CUI = "cui";
	private static final String TERM = "term";

	public CTakesHitRepository(){
		super(new CTakesHit());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public List<CTakesHit> getCTakesHitsByGroupId(int groupId){
		CriteriaQuery<CTakesHit> query = query();
		Root<CTakesHit> root = query.from(type);
		query.select(root);
		return em.createQuery(query.where(
				eq(root, "groupId", groupId))).getResultList();
	}

	public List<CTakesHit> getCTakesHitsByPatientId(String patientId) {
		CriteriaQuery<CTakesHit> query = query();
		Root<CTakesHit> root = query.from(type);
		query.select(root);
		return em.createQuery(query.where(
				eq(root, "patientId", patientId))).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> listUniqueTerms() {
		CriteriaQuery<Tuple> query = cb().createTupleQuery();
		Root<CTakesHit> root = query.from(type);
		Path<String> termPath = root.get(TERM);
		Path<String> cuiPath = root.get(CUI);
		Path<String> termClassPath = root.get(TERM_CLASS);
		query.multiselect(termPath, cuiPath, termClassPath).distinct(true);
		return em.createQuery(query).getResultList().stream().map(tuple -> { 
			return ImmutableMap.of(
					TERM , tuple.get(termPath), 
					CUI, tuple.get(cuiPath), 
					TERM_CLASS, tuple.get(termClassPath));
		}).collect(Collectors.toList());
	}
	
	public List<Object[]> getHitsByNoteIdAndClass(ClinicalNote t) {
		StoredProcedureQuery hitsQuery = 
				em.createStoredProcedureQuery("HITS_BY_NOTE_ID_AND_CLASS")
		.registerStoredProcedureParameter("clinicalNoteId", String.class, ParameterMode.IN)
		.setParameter("clinicalNoteId", t.getClinicalNoteId());
		hitsQuery.execute();
		List<Object[]> cuiLists = hitsQuery.getResultList();
		return cuiLists;
	}

}
