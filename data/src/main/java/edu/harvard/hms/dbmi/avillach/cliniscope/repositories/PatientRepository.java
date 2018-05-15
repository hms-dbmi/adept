package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import org.springframework.stereotype.Repository;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.Patient;

@Repository
public class PatientRepository extends BaseRepository<Patient>{
	
	public PatientRepository(){
		super(new Patient());
	}
}
