package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import java.util.List;

import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.GroupValidation;

@Repository
@Transactional
public class GroupValidationRepository extends BaseRepository<GroupValidation>{

	private static final String VALIDATIONS_FOR_NOTE = "VALIDATIONS_FOR_NOTE";

	public GroupValidationRepository(){
		super(new GroupValidation());
	}

	public boolean setScoreForGroup(int groupId, int score) {
		GroupValidation validation = new GroupValidation().setGroupId(groupId).setUserId(0).setScore(score);
		em.merge(validation);
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<GroupValidation> getValidationsForPatient(String noteId) {
		StoredProcedureQuery validationsQuery = 
				createQueryFor(VALIDATIONS_FOR_NOTE, GroupValidation.class, 
						inParam(String.class).name("clinicalNoteId").value(noteId));
		validationsQuery.execute();
		List<GroupValidation> groupValidations = validationsQuery.getResultList();
		return groupValidations;
	}
}
