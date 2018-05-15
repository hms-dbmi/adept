package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.ClinicalNote;
import edu.harvard.hms.dbmi.avillach.cliniscope.transfer.PatientNoteDTO;

@Repository
public class ClinicalNoteRepository extends BaseRepository<ClinicalNote>{

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");
	
	@Autowired
	private PatientMappingRepo patientMappingRepo;
	
	@Autowired
	private ClinicalNoteMappingRepo noteMappingRepo;
	
	public ClinicalNoteRepository(){
		super(new ClinicalNote());
	}

	@Transactional
	public List<PatientNoteDTO> getPatientNotesForPatient(String patientId) {
		StoredProcedureQuery patientNoteDTOQuery = em.createStoredProcedureQuery("PATIENT_NOTES_WITH_CUIS_AND_CANDIDATES");
		patientNoteDTOQuery.registerStoredProcedureParameter("patientId", String.class, ParameterMode.IN).setParameter("patientId", patientId);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = patientNoteDTOQuery.getResultList();
		return (List<PatientNoteDTO>) resultList.stream().map(entry -> {
			return new PatientNoteDTO()
					.setNoteId((String)entry[0])
					.setDate(DATE_FORMAT.format(entry[1]))
					.setDrugCuis(splitGroup(entry[2]))
					.setEventCuis(splitGroup(entry[3]))
					.setCandidateIds(splitGroup(entry[4]));
		}).collect(Collectors.toList());
	}

	private String[] splitGroup(Object entry) {
		return entry == null ? new String[0] : ((String)entry).split(",");
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ClinicalNote> listNotes() {
		CriteriaQuery<ClinicalNote> note = query();
		note.select(note.from(type));
		return em.createQuery(note).getResultList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<ClinicalNote> getNotesForPatient(String patientId) {
		CriteriaQuery<ClinicalNote> query = query();
		Root root = query.from(type);
		query.select(root);
		return em.createQuery(query.where(
				eq(root, "patientId", patientId))).getResultList();
	}



}
