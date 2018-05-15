package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.UserInteraction;

@Repository
@Transactional
public class UserInteractionRepo extends BaseRepository<UserInteraction>{

	protected UserInteractionRepo() {
		super(new UserInteraction());
	}

	public void record(UserInteraction interaction) {
		em.persist(interaction);
	}
	
}
