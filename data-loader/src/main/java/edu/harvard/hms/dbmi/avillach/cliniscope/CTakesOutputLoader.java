package edu.harvard.hms.dbmi.avillach.cliniscope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.opencsv.CSVParser;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesGroup;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.CTakesHit;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.Candidate;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.ClinicalNote;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.ClinicalNoteMapping;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.Patient;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.PatientMapping;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CTakesGroupRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CTakesHitRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CandidateRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.ClinicalNoteMappingRepo;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.ClinicalNoteRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.PatientMappingRepo;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.PatientRepository;

@Component
public class CTakesOutputLoader {

	private class Concept {

		public Concept(String name, String cui, String[] cuis, String[] synonyms){
			this.name = name;
			this.cui = cui;
			this.cuis = cuis;
			this.synonyms = synonyms;
		}
		public String name;
		public String cui;
		public String[] cuis;
		public String[] synonyms;
	}

	private class SynMapper {
		Concept[] synonymousDrugs = {
				new Concept("Ambrisentan", "C1176329", new String[]{"C1176329", "C1949323"}, new String[]{"Ambrisentan","Letairis"}),
				new Concept("Bosentan", "C0252643", new String[]{"C0252643", "C1101331"}, new String[]{"Bosentan","Tracleer"}),
				new Concept("Iloprost", "C0079594", new String[]{"C0079594", "C1541936"}, new String[]{"Iloprost","Ventavis"}),
				new Concept("Treprostinil", "C0079594", new String[]{"C0079594", "C0079594", "C3700977", "C1101501", "C2718336","C1145760", "C1169828"}, new String[]{"Treprostinil","Orenitram","Remodulin","Tyvaso"}),
				new Concept("Sildenafil", "C0529793", new String[]{"C0529793", "C1614029", "C0663448"}, new String[]{"Sildenafil","Revatio","Viagra"}),
				new Concept("Tadalafil", "C1176316", new String[]{"C1176316", "C2709986", "C0967376"}, new String[]{"Tadalafil","Adcirca","Cialis"}),
				new Concept("Eproprostenol", "C0033567", new String[]{"C0033567", "C0376357","C1174787","C0033567"}, new String[]{"Eproprostenol","Flolan","Veletri","Prostacyclin", "PGI2"}),
		}; 

		private Concept findConceptForCui(String drugCui) {
			return Arrays.stream(synonymousDrugs).filter( drug -> {
				return Arrays.stream(drug.cuis).anyMatch(
						cui->{
							return cui.equalsIgnoreCase(drugCui);
						});
			}).collect(Collectors.toList()).get(0);
		}

		public String[] synonymCuisForDrug(String cui){
			try{
				return findConceptForCui(cui).cuis;
			}catch(IndexOutOfBoundsException exception){
				return new String[]{cui};
			}
		}

		public String mapCuiToSynonymCui(String cui){
			try{
				return findConceptForCui(cui).cui;
			}catch(IndexOutOfBoundsException exception){
				return cui;
			}
		}

		public String nameForCui(String cui, String name){
			try{
				return findConceptForCui(cui).name;
			}catch(IndexOutOfBoundsException exception){
				return cui;
			}
		}
	}


	private SynMapper synMapper = new SynMapper();

	private static final int MEDICATION = 1;

	private static final int EVENT = 2;

	@Autowired
	private PatientMappingRepo patientMappingRepo;
	
	@Autowired
	private ClinicalNoteMappingRepo noteMappingRepo;
	
	@Autowired
	private PatientRepository patientRepo;

	@Autowired
	private ClinicalNoteRepository clinicalNoteRepo;

	@Autowired
	private CandidateRepository candidateRepo;

	@Autowired
	private CTakesGroupRepository ctakesGroupRepo;

	@Autowired
	private CTakesHitRepository ctakesHitRepo;

	public CTakesOutputLoader(){

	}

	public void loadRelationsFile(InputStream ctakesRelationsFile) throws IOException{
		String currentDocumentId = "";
		CSVParser parser = new CSVParser();
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(ctakesRelationsFile), 1048576);
		String[] line = parser.parseLine(reader.readLine());
		int lineNumber = 0;
		Patient currentPatient = null;
		ClinicalNote currentNote = null;
		while(line != null){
			if(line[0].equalsIgnoreCase("PATIENT_ID") || line[0].matches("Out of .*") || line.length < 4 || line[3].length()==0){
				//skip header or empty line
			}
			else if(line[5].equalsIgnoreCase("begin_offset_arg1")){
				// document header line
				if(currentDocumentId.equalsIgnoreCase("") || line[1].trim()!=""){
					currentDocumentId = line[1];
				}
				String patientId = line[0];
				String patientUUID = UUID.randomUUID().toString();
				PatientMapping mapping = new PatientMapping().setSourceId(patientId).setUuid(patientUUID);
				patientMappingRepo.ensureExists(ImmutableList.of(mapping));
				currentPatient = 
						new Patient()
						.setPatientId(patientUUID);
				String noteUUID = UUID.randomUUID().toString();
				ClinicalNoteMapping noteMapping = new ClinicalNoteMapping().setSourceId(currentDocumentId).setUuid(noteUUID);
				noteMappingRepo.ensureExists(ImmutableList.of(noteMapping));
				currentNote = 
						new ClinicalNote()
						.setClinicalNoteId(noteUUID)
						.setPatientId(patientUUID)
						.setDateOfNote(parseDate(line, lineNumber));
				synchronized(this){
					patientRepo.ensureExists(ImmutableList.of(currentPatient));
					clinicalNoteRepo.ensureExists(ImmutableList.of(currentNote));
				};
			}else{
				// data line
				CTakesGroup group = new CTakesGroup()
						.setClinicalNoteId(currentNote.getClinicalNoteId());
				CTakesHit hitA = new CTakesHit()
						.setTerm_class(MEDICATION)
						.setCui(synMapper.mapCuiToSynonymCui(line[4]))
						.setStart_index(parseIndexSafely(lineNumber, line[5]))
						.setEnd_index(parseIndexSafely(lineNumber, line[6]))
						.setTerm(synMapper.nameForCui(line[4], line[3]));
				CTakesHit hitB = new CTakesHit()
						.setTerm_class(EVENT)
						.setCui(line[8])
						.setStart_index(parseIndexSafely(lineNumber, line[9]))
						.setEnd_index(parseIndexSafely(lineNumber, line[10]))
						.setTerm(line[7]);
				Candidate candidate = candidateRepo.ensureExists(ImmutableList.of(
						new Candidate()
						.setDrugCui(hitA.getCui())
						.setEventCui(hitB.getCui())
						.setDrug(WordUtils.capitalize(hitA.getTerm()))
						.setEvent(WordUtils.capitalize(hitB.getTerm()))
						.setPatientId(currentPatient.getPatientId()))).get(0);
				group.setCandidateId(candidate.getId());
				group = ctakesGroupRepo.ensureExists(ImmutableList.of(group)).get(0);
				hitA.setGroupId(group.getGroupId());
				hitB.setGroupId(group.getGroupId());
				ctakesHitRepo.ensureExists(ImmutableList.of(hitA, hitB));
			}
			line = parser.parseLine(reader.readLine());
			lineNumber++;
		}
		IOUtils.closeQuietly(reader);

	}

	public void loadUnrelatedMentionsFile(String filename, InputStream ctakesRelationsFile) throws IOException{
		String currentDocumentId = "";
		CSVParser parser = new CSVParser();
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(ctakesRelationsFile));
		String[] line = parser.parseLine(reader.readLine());
		int lineNumber = 0;
		Patient currentPatient = null;
		ClinicalNote currentNote = null;
		int currentType = -1;
		while(line != null){
			try{
				if(line[0].equalsIgnoreCase("PATIENT_ID") || line[0].matches("Out of .*") || line.length < 4 || line[3].length()==0){
					//skip header or empty line
				}
				else if(line[5].equalsIgnoreCase("begin_offset")){
					if(line[3].toLowerCase().startsWith("medication")){
						// document header line
						currentType = MEDICATION;
						if(currentDocumentId.equalsIgnoreCase("") || line[1].trim()!=""){
							currentDocumentId = line[1];
						}
						String patientId = line[0];
						String patientUUID = UUID.randomUUID().toString();
						PatientMapping patientMapping = new PatientMapping().setSourceId(patientId).setUuid(patientUUID);
						patientMappingRepo.ensureExists(ImmutableList.of(patientMapping));
						currentPatient = new Patient()
								.setPatientId(patientUUID);
						String noteUUID = UUID.randomUUID().toString();
						ClinicalNoteMapping noteMapping = new ClinicalNoteMapping().setSourceId(currentDocumentId).setUuid(noteUUID);
						noteMappingRepo.ensureExists(ImmutableList.of(noteMapping));
						currentNote = 
								new ClinicalNote()
								.setClinicalNoteId(noteUUID)
								.setPatientId(patientUUID)
								.setDateOfNote(parseDate(line, lineNumber));
					}else{
						currentType = EVENT;
					}
					synchronized(this){
						patientRepo.ensureExists(ImmutableList.of(currentPatient));
						clinicalNoteRepo.ensureExists(ImmutableList.of(currentNote));
					};
				}else{
					// data line
					CTakesGroup group = new CTakesGroup()
							.setClinicalNoteId(currentNote.getClinicalNoteId());
					CTakesHit hitA = new CTakesHit()
							.setTerm_class(currentType)
							.setCui(currentType == MEDICATION ? synMapper.mapCuiToSynonymCui(line[3]) : line[3])
							.setStart_index(parseIndexSafely(lineNumber, line[5]))
							.setEnd_index(parseIndexSafely(lineNumber, line[6]))
							.setTerm(currentType == MEDICATION ? synMapper.nameForCui(line[3], line[4]) : line[4]);
					group = ctakesGroupRepo.ensureExists(ImmutableList.of(group)).get(0);
					hitA.setGroupId(group.getGroupId());
					ctakesHitRepo.ensureExists(ImmutableList.of(hitA));
				}
			}catch(Exception e){
				System.out.println(filename + " - " + e.getMessage());
			}finally{
				line = parser.parseLine(reader.readLine());
				lineNumber++;

			}
		}
		IOUtils.closeQuietly(reader);
	}

	private int parseIndexSafely(int lineNumber, String value) {
		try{
			return Integer.parseInt(value);
		}catch(NumberFormatException e){
			throw new RuntimeException(lineNumber + ": " + value);
		}
	}

	private Date parseDate(String[] line, int lineNumber) {
		try {
			DateFormat df = new SimpleDateFormat("dd-MMM-yy hh.mm.ss.SSSSSSSSS aa");
			return df.parse(line[2]);
		} catch (ParseException e) {
			throw new RuntimeException("Unparsable date at line " + lineNumber);
		}
	}
}
