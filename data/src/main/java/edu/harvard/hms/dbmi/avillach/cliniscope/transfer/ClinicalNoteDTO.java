package edu.harvard.hms.dbmi.avillach.cliniscope.transfer;

import java.util.List;
import javax.persistence.Transient;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.ClinicalNote;

public class ClinicalNoteDTO extends ClinicalNote{

	public ClinicalNoteDTO(ClinicalNote note){
		this.setClinicalNoteId(note.getClinicalNoteId());
		this.setDateOfNote(note.getDateOfNote());
		this.setNoteText(note.getNoteText());
		this.setPatientId(note.getPatientId());
	}
	
	
	@Transient
	private List<CTakesGroupDTO> groups;
	
	@Transient
	private String markup;


	public String getMarkup() {
		return markup;
	}


	public ClinicalNoteDTO setMarkup(String markup) {
		this.markup = markup;
		return this;
	}


	public List<CTakesGroupDTO> getGroups() {
		return groups;
	}


	public ClinicalNoteDTO setGroups(List<CTakesGroupDTO> groups) {
		this.groups = groups;
		return this;
	}
	
	
}
