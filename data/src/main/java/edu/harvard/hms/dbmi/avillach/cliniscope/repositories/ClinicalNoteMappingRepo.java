package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableList;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.ClinicalNoteMapping;

@Repository
@Transactional
public class ClinicalNoteMappingRepo extends BaseRepository<ClinicalNoteMapping>{

	public ClinicalNoteMappingRepo(){
		super(new ClinicalNoteMapping());
	}

	public ClinicalNoteMapping getBySourceId(String noteId) {
		CriteriaQuery<ClinicalNoteMapping> query = cb().createQuery(ClinicalNoteMapping.class);
		Root<ClinicalNoteMapping> root = query.from(ClinicalNoteMapping.class);
		try{
			return em.createQuery(query.where(
				eq(root, "sourceId", noteId))).getSingleResult();
		}catch(NoResultException e) {
			return this.ensureExists(ImmutableList.of(new ClinicalNoteMapping().setSourceId(noteId).setUuid(UUID.randomUUID().toString()))).get(0);
		}
	}

}
