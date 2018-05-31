USE adept
DROP TABLE IF EXISTS User;
CREATE TABLE User (
	userId INT auto_increment, 
	authenticationName VARCHAR(128) NOT NULL, 
	authenticationSource VARCHAR(128) NOT NULL, 
	isAdmin TINYINT(3) DEFAULT 0, 
	canValidate TINYINT(3) DEFAULT 0, 
	canAdjudicate TINYINT(3) DEFAULT 0,
	PRIMARY KEY(userId));

DROP TABLE IF EXISTS UserInteraction;
CREATE TABLE UserInteraction (
	id BIGINT NOT NULL auto_increment,
	user VARCHAR(120) NOT NULL,
	description VARCHAR(250) NOT NULL, 
	timestamp BIGINT,
	PRIMARY KEY(id));
	
DROP TABLE IF EXISTS Mapping;
CREATE TABLE Mapping (
	uuid VARCHAR(36) NOT NULL,
	sourceId VARCHAR(255) NOT NULL,
	PRIMARY KEY (uuid),
	INDEX (sourceId)
);	
DROP TABLE IF EXISTS PatientMapping;
CREATE TABLE PatientMapping (
	uuid VARCHAR(36) NOT NULL,
	sourceId VARCHAR(255) NOT NULL,
	PRIMARY KEY (uuid),
	INDEX (sourceId)
);

DROP TABLE IF EXISTS ClinicalNoteMapping;
CREATE TABLE ClinicalNoteMapping (
	uuid VARCHAR(36) NOT NULL,
	sourceId VARCHAR(255) NOT NULL,
	PRIMARY KEY (uuid),
	INDEX (sourceId)
);
	
DROP TABLE IF EXISTS Patient;
CREATE TABLE Patient (
	patientId VARCHAR(36) NOT NULL, 
	name VARCHAR(128),
	FOREIGN KEY (patientId) REFERENCES PatientMapping(uuid),
	PRIMARY KEY(patientId));
	
DROP TABLE IF EXISTS ClinicalNote;
CREATE TABLE ClinicalNote (
	noteText MEDIUMTEXT,
	dateOfNote DATETIME,
	patientId VARCHAR(36) NOT NULL, 
	clinicalNoteId VARCHAR(64) NOT NULL,
	FOREIGN KEY (patientId) REFERENCES Patient(patientId),
	FOREIGN KEY (clinicalNoteId) REFERENCES ClinicalNoteMapping(uuid),
	PRIMARY KEY(clinicalNoteId));


DROP TABLE IF EXISTS Candidate;
CREATE TABLE Candidate (
	id INT NOT NULL AUTO_INCREMENT,
	drug VARCHAR(64),
	event VARCHAR(64),
	drugCui VARCHAR(64),
	eventCui VARCHAR(64),
	patientId VARCHAR(36) NOT NULL, 
	isAdjudicated TINYINT(3) DEFAULT 0,
	adjudicatorId INT NULL,
	adjudicatedScore INT,
	FOREIGN KEY (patientId) REFERENCES Patient(patientId),
	FOREIGN KEY (adjudicatorId) REFERENCES User(userId),
	PRIMARY KEY(id));

DROP TABLE IF EXISTS CandidateValidation;
CREATE TABLE CandidateValidation (
	candidateId INT NOT NULL, 
	userId INT NOT NULL, 
	score INT NOT NULL, 
	FOREIGN KEY (userId) REFERENCES User(userId),
	FOREIGN KEY (candidateId) REFERENCES Candidate(id),
	PRIMARY KEY(candidateId, userId));

DROP TABLE IF EXISTS CTakesGroup;
CREATE TABLE CTakesGroup (
	groupId INT NOT NULL auto_increment, 
	candidateId INT NULL, 
	clinicalNoteId VARCHAR(64) NOT NULL, 
	FOREIGN KEY (candidateId) REFERENCES Candidate(id),
	FOREIGN KEY (clinicalNoteId) REFERENCES ClinicalNote(clinicalNoteId),
	PRIMARY KEY(groupId));

DROP TABLE IF EXISTS CTakesHit;
CREATE TABLE CTakesHit (
	hitId INT NOT NULL auto_increment,
	groupId INT NOT NULL,
	cui VARCHAR(64),
	term VARCHAR(128),
	start_index INT NOT NULL,
	end_index INT NOT NULL,
	term_class INT NOT NULL,
	FOREIGN KEY (groupId) REFERENCES CTakesGroup(groupId),
	UNIQUE KEY (groupId, start_index, cui),
	PRIMARY KEY(hitId));

DROP TABLE IF EXISTS GroupValidation;
CREATE TABLE GroupValidation (
	groupId INT NOT NULL,
	userId INT NOT NULL,
	score INT NOT NULL,
	FOREIGN KEY (groupId) REFERENCES CTakesGroup(groupId),
	FOREIGN KEY (userId) REFERENCES User(userId),
	PRIMARY KEY (userId, groupId));

