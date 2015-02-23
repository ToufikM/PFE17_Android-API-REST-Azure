package org.pfe.cloudshot.rest;


import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.pfe.cloudshot.ejb.GestionPhotosEJB;
import org.pfe.cloudshot.model.Photo;

import com.owlike.genson.Genson;



@Path("photos")
public class GestionPhotoRS {

	@EJB
	private GestionPhotosEJB gestionPhotoEJB;
	
	private Genson genson = new Genson();
	
	@SuppressWarnings("unused")
	private static String imageBase64String="";
	
	
	@PUT @Path("add/{id_user}") 
	public Response addPicture(@PathParam("id_user") Long idUser,Photo photo)
	{		
		Long new_photoId;
		
		if(photo!=null)
		{

		photo.getUser().setId(idUser);
				
			new_photoId = gestionPhotoEJB.addPhoto(photo);
		}
		else
			return Response.status(Status.NO_CONTENT).build(); 
		
		if(new_photoId!=null)
			return Response.ok().entity(genson.serialize(new_photoId)).build();
		else
			return Response.status(Status.NOT_FOUND).build(); //Erreur 417 
		
}
	
	@PUT @Path("astuceAddPic") 
	public Response addPicture(String imageString)
	{		
		
		if(imageString!=null)
		{
			imageBase64String=imageString;
			return Response.ok().build(); 
		}
		else
			return Response.status(Status.NO_CONTENT).build(); 
		
	}
	
	@GET @Path("get/{id_user}/{id_photo}") 
	public Response getPicture(@PathParam("id_user") Long idUser,
			@PathParam("id_photo") Long idPhoto)
	{	
		Photo photo=new Photo();
		
		if(idUser!=null && idPhoto!=null)
			photo = gestionPhotoEJB.getPhoto(idUser,idPhoto);
		else
			return Response.status(Status.NO_CONTENT).build(); //Erreur 204
			
		if(photo!=null)
			return Response.ok().entity(genson.serialize(photo)).build();
		else
			return Response.status(Status.NOT_FOUND).build(); //Erreur 404
	}
	
	@GET @Path("all/{id_user}") 
	public Response getAllPictures(@PathParam("id_user") Long idUser)
	{	
		List<Photo> photos;
		
		if(idUser!=null)
			photos = gestionPhotoEJB.getAllPhotos(idUser);
		else
			return Response.status(Status.NO_CONTENT).build(); //Erreur 204
			
		if(!photos.isEmpty())
			return Response.ok().entity(genson.serialize(photos)).build();
		else
			return Response.status(Status.NOT_FOUND).build(); //Erreur 404
		
	}
	
	@DELETE @Path("delete/{id_user}/{id_photo}") 
	public Response deletePicture(@PathParam("id_user") Long idUser,
			@PathParam("id_photo") Long idPhoto)
	{	
		boolean resultat=false;
		
		if(idPhoto!=null && idUser!=null)
		    resultat = gestionPhotoEJB.deletePhoto(idUser,idPhoto);
		else
			return Response.status(Status.NO_CONTENT).build(); //Erreur 204
			
		if(resultat)
			return Response.ok().entity(genson.serialize(resultat)).build();
		else
			return Response.status(Status.SEE_OTHER).build(); //Erreur 303
		
	}
	
	@POST @Path("update/{id_user}/{id_photo}") 
	public Response updatePicture(@PathParam("id_user") Long idUser,
			@PathParam("id_photo") Long idPhoto,
			@FormParam("nomPhoto") String nomPicture,
			@FormParam("lieuPhoto") String lieuPhoto
			)
	{	
		Photo picture=new Photo(); 
		boolean succee=false;
		
		if(idUser!=null && idPhoto!=null && nomPicture!=null)
		{
		picture.setId(idPhoto);
		picture.setNom(nomPicture);
		picture.setLieu(lieuPhoto);
		
		}
		
		if(picture!=null)
			succee=gestionPhotoEJB.updatePhoto(idUser,picture);			
			
		if(succee)
			return Response.ok().build();
		else
			return Response.status(Status.NOT_MODIFIED).build(); //Erreur 304
		
	}
	
}
