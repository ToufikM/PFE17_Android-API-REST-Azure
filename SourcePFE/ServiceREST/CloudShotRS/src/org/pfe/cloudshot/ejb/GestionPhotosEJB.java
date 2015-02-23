package org.pfe.cloudshot.ejb;



import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.pfe.cloudshot.azure.BlobStorage;
import org.pfe.cloudshot.model.Photo;
import org.pfe.cloudshot.model.User;

import com.microsoft.azure.storage.core.Base64;

@Stateless
public class GestionPhotosEJB {

	@PersistenceContext(unitName="jpa-cloudshot")
	private EntityManager em;
	
	@EJB
	private AuthentificationEJB authentificationEJB;
	

	public Long addPhoto(Photo photo) {
		System.out.println("A l'interieur de addPhoto");
		
		User user = new User();
		
		Long userID = photo.getUserId();
		
		if(userID==null)
		{
			userID=photo.getUser().getId();
		}
	
		System.out.println("addPhoto: "+String.valueOf(userID));
		
		
		if(userID!=null)
		{
			user = authentificationEJB.findbyID(userID);
			photo.setUser(user);
		}
		
		if(user!=null)
		{
			String container = photo.getUser().getContainer();
			
			String imageString = photo.getImageBase64();
			photo.setImageBase64(null);
			System.out.println("addPhoto: Apres getImageBase64 : "+imageString.length());
			
			photo.initDate();
			System.out.println("Avant init Date : ");
			try {
				
				BlobStorage blob = new BlobStorage(container);
				
				byte[] imageEnbyte= decodePicture(imageString);
				
				em.persist(photo);
				
				String nomImage=nameImageDsAzure(photo.getNom(),photo.getId());
				URI url = blob.uploadPicture(imageEnbyte, nomImage);		
				photo.setUrl(url);
				
							
				photo = em.merge(photo);

				return photo.getId();
			    
			} catch (Exception e) {
				
				e.printStackTrace();
				
				return null;
			}
		}
		else
			return null;
	}


	public Photo getPhoto(Long idUser,Long idPhoto) {

		Photo photo = findByIdPicAndUserId(idUser,idPhoto);
		
		if(photo!=null)
		{
			
			Photo picToSend = new Photo();
			String namePicAzure = nameImageDsAzure(photo.getNom(), photo.getId());
			
			try {
				BlobStorage blob = new BlobStorage(photo.getUser().getContainer());

				byte[] imageByte = blob.downloadPicture(namePicAzure);	
				
				String imageString = encodePicture(imageByte);			
				picToSend.setImageBase64(imageString);
				picToSend.setId(photo.getId());
				picToSend.setNom(photo.getNom());
				picToSend.setUrl(photo.getUrl());
				picToSend.setUser(photo.getUser());
				picToSend.setDateCreation(photo.getDateCreation());
				picToSend.setLieu(photo.getLieu());
				
			   return picToSend;
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
		}
		else
			return null;
		
	}

	public Photo findByIdPicAndUserId(Long idUser,Long idPhoto)
	{
		Query query = 
				em.createNamedQuery("Photo.getPhoto",Photo.class).
				setParameter("id_picture", idPhoto).setParameter("id_user", idUser);;	
		
		@SuppressWarnings("unchecked")
		List<Photo>  photos =  query.getResultList();
		return photos.get(0);
	}
	

	public List<Photo> getAllPhotos(Long idUser) {
		
		List<Photo> listPhotos = findAllpicByIdUser(idUser);
		System.out.println("Apres avoir retrouvee toutes les photos de la bDd");
		List<Photo> picToSend = new ArrayList<Photo>();
		
		try {
			
			if(listPhotos!=null)
			{
				Photo pic = listPhotos.get(0);
				String container = pic.getUser().getContainer();
				
				if(container!=null)
				{
					BlobStorage blob = new BlobStorage(container);
					System.out.println("Apres avoir ouvert le container");
					
					for (Photo photo : listPhotos) 
					{					
						Photo photoCopie = new Photo();
						
						//Reconstruction du nom de l'image tel quel est ds Azure
						String namePicAzure = nameImageDsAzure(photo.getNom(), photo.getId());				
						byte[] imageByte = blob.downloadPicture(namePicAzure);	
						
						String imageString = encodePicture(imageByte);	
						photoCopie.setImageBase64(imageString);
						photoCopie.setId(photo.getId());
						photoCopie.setNom(photo.getNom());
						photoCopie.setUrl(photo.getUrl());
						photoCopie.setUser(photo.getUser());
						photoCopie.setDateCreation(photo.getDateCreation());
						photoCopie.setLieu(photo.getLieu());
						
						picToSend.add(photoCopie);
					} 
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return picToSend;
	}
	
	
	public List<Photo> findAllpicByIdUser(Long idUser)
	{
		Query query = 
				em.createNamedQuery("Photo.getAllPhotos").
				setParameter("id_user", idUser);;	
		
		@SuppressWarnings("unchecked")
		List<Photo>  photos =  Collections.checkedList(query.getResultList(),Photo.class);	
		
		//Renvoyant la photo trouvée sinn null 
		return photos.isEmpty() ? null : photos;
	}


	@SuppressWarnings("unused")
	public boolean deletePhoto(Long idUser,Long idPhoto) 
	{
		boolean etatBlob=false;
		
			try 
			{
				//Je recupere la bonne photo (id user/id photo) si existe pas null -> false
				Photo picture = findByIdPicAndUserId(idUser,idPhoto);
				
				if(picture!=null)
				{
					BlobStorage blob = new BlobStorage(picture.getUser().getContainer());
					String namePicAzure = nameImageDsAzure(picture.getNom(), picture.getId());
					
					if(picture !=null)
					{
					    etatBlob = blob.deletePicture(namePicAzure);
						
						em.remove(picture);
						return etatBlob;
					}
				}
				else
					return false;
				
				
			} catch (Exception e) {
				e.printStackTrace();
				
				return false;
			}
			
			return  etatBlob;
	}


	public boolean updatePhoto(Long idUser,Photo photo) 
	{
		Long idPhoto = photo.getId();
		Photo photo_trouvee=new Photo();
		
		System.out.println("updatePhoto: idUser : "+idUser+" idPhoto: "+idPhoto);
		
		if(idPhoto!=null && idUser!=null)
			  photo_trouvee = findByIdPicAndUserId(idUser,idPhoto);
		
			if(photo_trouvee!=null)
			{
				photo_trouvee.editDate();
				photo_trouvee.setNom(photo.getNom());
				photo_trouvee.setLieu(photo.getLieu());
			
				@SuppressWarnings("unused")
				Photo photo_modifiee = em.merge(photo_trouvee);
				return true;
			}
			else
				return false;
	}
	
	public static byte[] decodePicture(String imageDataString) {
        return Base64.decode(imageDataString);
    }
	
	public static String encodePicture(byte[] imageByteArray) {
        return Base64.encode(imageByteArray);
    }
	
	public String nameImageDsAzure(String qualifiedName,Long Id)
	{
			
			String extension = qualifiedName.split(Pattern.quote("."))[1];
			String nomImage=Id.toString()+"."+extension;
		
			return nomImage;

	}
}
