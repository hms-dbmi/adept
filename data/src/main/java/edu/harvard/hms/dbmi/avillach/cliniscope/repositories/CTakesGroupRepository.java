package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesGroup;

@Repository
public class CTakesGroupRepository extends BaseRepository<CTakesGroup>{
	
	public CTakesGroupRepository(){
		super(new CTakesGroup());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public List<CTakesGroup> getCTakesGroupByClinicalNoteId(String documentId){
		CriteriaQuery<CTakesGroup> query = query();
		Root root = query.from(type);
		query.select(root);
		return em.createQuery(query.where(
				eq(root, "clinicalNoteId", documentId))).getResultList();
	}
	
}
