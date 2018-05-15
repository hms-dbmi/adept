package edu.harvard.hms.dbmi.avillach.cliniscope.repositories;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;
import edu.harvard.hms.dbmi.avillach.cliniscope.entities.User;

@Repository
@Transactional
public class UserRepository extends BaseRepository<User>{

	public UserRepository(){
		super(new User());
	}

	public User getUserByAuthentication(String authenticationSource){
		CriteriaQuery<User> query = query();
		Root<User> root = query.from(type);
		query.select(root);
		return em.createQuery(query.where(
				cb().and(
						eq(root, "authenticationSource", authenticationSource)))).getSingleResult();
	}

	public User getUserByUsername(String userName){
		CriteriaQuery<User> query = query();
		Root<User> root = query.from(type);
		query.select(root);
		return em.createQuery(query.where(
				eq(root, "authenticationName", userName))).getSingleResult();
	}

	public User updateAuthenticationSourceForUser(User user, String sub) {
		user.setAuthenticationSource(sub);
		return em.merge(user);
	}

	public User updateUserPermission(int userId, String permissionName, boolean permissionValue) {
		User user = getById(userId);
		switch(permissionName) {
		case "canValidate" : 
			user.setCanValidate(permissionValue);
			break;
		case "canAdjudicate" :
			user.setCanAdjudicate(permissionValue);
			break;
		case "isAdmin" :
			user.setIsAdmin(permissionValue);
			break;
		}
		return em.merge(user);
	}
}
