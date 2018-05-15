package edu.harvard.hms.dbmi.avillach.cliniscope.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CTakesHit implements Comparable<CTakesHit>{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer hitId;
	
	private Integer groupId; 
	
	private String cui;
	
	private String term;
	
	private int start_index;
	
	private int end_index;
	
	private int term_class;

	public int getHitId() {
		return hitId;
	}

	public CTakesHit setHitId(int hitId) {
		this.hitId = hitId;
		return this;
	}

	public int getGroupId() {
		return groupId;
	}

	public CTakesHit setGroupId(int groupId) {
		this.groupId = groupId;
		return this;
	}

	public String getCui() {
		return cui;
	}

	public CTakesHit setCui(String cui) {
		this.cui = cui;
		return this;
	}

	public String getTerm() {
		return term;
	}

	public CTakesHit setTerm(String term) {
		this.term = term;
		return this;
	}

	public int getStart_index() {
		return start_index;
	}

	public CTakesHit setStart_index(int start_index) {
		this.start_index = start_index;
		return this;
	}

	public int getEnd_index() {
		return end_index;
	}

	public CTakesHit setEnd_index(int end_index) {
		this.end_index = end_index;
		return this;
	}

	public int getTerm_class() {
		return term_class;
	}

	public CTakesHit setTerm_class(int term_class) {
		this.term_class = term_class;
		return this;
	}

	public int compareTo(CTakesHit o) {
		return hitId.compareTo(o.getHitId());
	}

	@Override
	public String toString() {
		return "CTakesHit [hitId=" + hitId + ", groupId=" + groupId + ", cui=" + cui + ", term=" + term
				+ ", start_index=" + start_index + ", end_index=" + end_index + ", term_class=" + term_class + "]";
	}
}
