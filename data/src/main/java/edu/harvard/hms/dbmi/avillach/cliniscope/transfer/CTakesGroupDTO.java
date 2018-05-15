package edu.harvard.hms.dbmi.avillach.cliniscope.transfer;

import java.util.List;

import javax.persistence.Transient;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesGroup;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesHit;

public class CTakesGroupDTO extends CTakesGroup {
	
	public CTakesGroupDTO(CTakesGroup group){
		this.setCandidateId(group.getCandidateId());
		this.setClinicalNoteId(group.getClinicalNoteId());
		this.setGroupId(group.getGroupId());
	}
	
	@Transient
	private List<CTakesHit> hits;

	public List<CTakesHit> getHits() {
		return hits;
	}

	public CTakesGroupDTO setHits(List<CTakesHit> hits) {
		this.hits = hits;
		return this;
	}
	
}
