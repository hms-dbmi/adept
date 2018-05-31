package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableList;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.PatientMapping;

@Repository
@Transactional
public class PatientMappingRepo extends BaseRepository<PatientMapping>{

	public PatientMappingRepo(){
		super(new PatientMapping());
	}

	public PatientMapping getBySourceId(String patientId) {
		CriteriaQuery<PatientMapping> query = cb().createQuery(PatientMapping.class);
		Root<PatientMapping> root = query.from(PatientMapping.class);
		try{
			return em.createQuery(query.where(
				eq(root, "sourceId", patientId))).getSingleResult();
		}catch(NoResultException e) {
			return this.ensureExists(ImmutableList.of(new PatientMapping().setSourceId(patientId).setUuid(UUID.randomUUID().toString()))).get(0);
		}
	}

}
