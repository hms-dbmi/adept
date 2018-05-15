USE adept
		
DROP VIEW IF EXISTS notes_groups_cuis;
CREATE VIEW notes_groups_cuis 
AS 
SELECT clinicalNoteId, dh.cui AS drugCui, eh.cui AS eventCui, CTakesGroup.groupId, candidateId
FROM CTakesGroup 
LEFT JOIN (
    SELECT groupId, cui 
    FROM CTakesHit 
    WHERE term_class=1) AS dh 
ON dh.groupId=CTakesGroup.groupId 
LEFT JOIN (
    SELECT groupId, cui 
    FROM CTakesHit 
    WHERE term_class=2) AS eh 
ON eh.groupId=CTakesGroup.groupId;

USE adept
	
DELIMITER //

DROP PROCEDURE IF EXISTS HITS_BY_NOTE_ID_AND_CLASS//
CREATE PROCEDURE HITS_BY_NOTE_ID_AND_CLASS (IN clinicalNoteId VARCHAR(64))
BEGIN
    SELECT DISTINCT term_class, group_concat(cui) 
    FROM ClinicalNote 
    LEFT JOIN CTakesGroup 
        ON ClinicalNote.clinicalNoteId=ctakesgroup.clinicalNoteId 
    LEFT JOIN CTakesHit 
        ON CTakesHit.groupid=CTakesGroup.groupid 
    WHERE ClinicalNote.clinicalnoteid=clinicalNoteId
    GROUP BY term_class ORDER BY term_class;
END//

DROP PROCEDURE IF EXISTS NOTE_IDS_BY_CANDIDATE_FOR_PATIENT//
CREATE PROCEDURE NOTE_IDS_BY_CANDIDATE_FOR_PATIENT (IN patientId VARCHAR(64))
BEGIN
    SELECT CTakesGroup.candidateId, group_concat(clinicalNoteId) 
    FROM CTakesGroup 
    JOIN Candidate 
    ON Candidate.candidateId=CTakesGroup.candidateId 
    WHERE Candidate.patientId=patientId 
    GROUP BY CTakesGroup.candidateId;
END//

DROP PROCEDURE IF EXISTS VALIDATIONS_FOR_NOTE//
CREATE PROCEDURE VALIDATIONS_FOR_NOTE (IN clinicalNoteId VARCHAR(64))
BEGIN
    SELECT g.groupId, userId, score 
    FROM GroupValidation gv 
    JOIN CTakesGroup g 
    ON gv.groupId=g.groupId 
    WHERE g.clinicalNoteid = clinicalNoteId AND candidateId is not null;
END//

DROP PROCEDURE IF EXISTS PATIENTS_BY_CANDIDATE//
CREATE PROCEDURE PATIENTS_BY_CANDIDATE ()
BEGIN
    SELECT drug, event,drugCui, eventCui, group_concat(patientId) 
    FROM Candidate 
    GROUP BY drug, event, drugCui, eventCui;
END//

DROP PROCEDURE IF EXISTS CANDIDATE_VALIDATIONS_FOR_PATIENT_ID//
CREATE PROCEDURE CANDIDATE_VALIDATIONS_FOR_PATIENT_ID (IN patientId VARCHAR(64))
BEGIN
    SELECT CandidateValidation.* 
    FROM CandidateValidation join Candidate 
    ON CandidateValidation.candidateId = Candidate.id
    WHERE Candidate.patientId = patientId;
END//

DROP PROCEDURE IF EXISTS PATIENT_NOTES_WITH_CUIS_AND_CANDIDATES//
CREATE PROCEDURE PATIENT_NOTES_WITH_CUIS_AND_CANDIDATES (IN patientId VARCHAR(64))
BEGIN
	SELECT ClinicalNote.clinicalnoteid, 
	       dateofnote, 
	       group_concat(DISTINCT dh.cui)      AS drugCui, 
	       group_concat(DISTINCT eh.cui)      AS eventCui, 
	       group_concat(DISTINCT candidateid) AS candidateId 
	FROM   ClinicalNote 
	       LEFT JOIN CTakesGroup 
	              ON ClinicalNote.clinicalnoteid = CTakesGroup.clinicalnoteid 
	       LEFT JOIN (SELECT groupid, 
	                         cui 
	                  FROM   CTakesHit 
	                  WHERE  term_class = 1) AS dh 
	              ON dh.groupid = CTakesGroup.groupid 
	       LEFT JOIN (SELECT groupid, 
	                         cui 
	                  FROM   CTakesHit 
	                  WHERE  term_class = 2) AS eh 
	              ON eh.groupid = CTakesGroup.groupid 
	WHERE ClinicalNote.patientId = patientId
	GROUP  BY ClinicalNote.clinicalnoteid, 
	          dateofnote; 
END//

DELIMITER ;


