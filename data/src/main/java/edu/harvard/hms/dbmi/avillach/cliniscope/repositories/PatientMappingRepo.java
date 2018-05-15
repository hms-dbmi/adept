package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import org.springframework.stereotype.Repository;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.PatientMapping;

@Repository
public class PatientMappingRepo extends BaseRepository<PatientMapping>{

	public PatientMappingRepo(){
		super(new PatientMapping());
	}

}
