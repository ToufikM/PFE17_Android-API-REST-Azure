package org.pfe.cloudshot.rest;


import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.pfe.cloudshot.ejb.AuthentificationEJB;
import org.pfe.cloudshot.model.User;

import com.owlike.genson.Genson;

import javax.ws.rs.core.Response.Status;


@Path("login")
public class AuthentificationCS {
	
	@EJB
	private AuthentificationEJB authentificationEJB;
	
	private Genson genson = new Genson();
	
	
	@PUT @Path("create") 
	public Response createUser(User user)
	{
		User userCreer = null ;
		
		if(user!=null)
			userCreer = authentificationEJB.createUser(user);
		else
			return Response.status(Status.NO_CONTENT).build(); //Erreur HTTP 204
		
		if(userCreer!=null)
			return Response.ok().entity(genson.serialize(userCreer)).build(); //OK HTTP 200
		else
			return Response.status(Status.CONFLICT).build(); //Erreur HTTP 409
	}
	
	
	@POST @Path("authentifier") 
	public Response login(User user)
	{
		User userFound = new User();

		if(user!=null)
		 userFound = authentificationEJB.login(user); 
		else
			return Response.status(Status.NO_CONTENT).build(); //Erreur HTTP 204
		
		
		if(userFound !=null)
			return Response.ok().entity(genson.serialize(userFound)).build();
		else
			return Response.status(Status.NOT_FOUND).build(); //Erreur HTTP 404
		
	}
	
	
}
