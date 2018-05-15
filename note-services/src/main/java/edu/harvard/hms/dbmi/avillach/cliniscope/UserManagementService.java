package edu.harvard.hms.dbmi.avillach.cliniscope;

import java.util.Arrays;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.User;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.UserRepository;

@Path("user")
@Service
@RolesAllowed("ROLE_ADMIN")
public class UserManagementService {

	@Autowired
	private UserRepository userRepo;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> listUsers(){
		return userRepo.list();
	}
	
	@PUT
	@Path("{userId}/{permissionName}/{permissionValue}")
	@Produces(MediaType.APPLICATION_JSON)
	public User updateUser(@PathParam("userId") int userId, @PathParam("permissionName") String permissionName, @PathParam("permissionValue") boolean permissionValue) {
		return userRepo.updateUserPermission(userId, permissionName, permissionValue);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public User addUser(User user){
		return userRepo.ensureExists(Arrays.asList(new User[]{user})).get(0);
	}
}
