package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import org.springframework.stereotype.Repository;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.ClinicalNoteMapping;

@Repository
public class ClinicalNoteMappingRepo extends BaseRepository<ClinicalNoteMapping>{

	public ClinicalNoteMappingRepo(){
		super(new ClinicalNoteMapping());
	}

}
