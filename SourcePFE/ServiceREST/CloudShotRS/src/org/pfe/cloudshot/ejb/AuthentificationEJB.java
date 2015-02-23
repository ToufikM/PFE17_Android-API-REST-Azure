package org.pfe.cloudshot.ejb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.pfe.cloudshot.azure.BlobStorage;
import org.pfe.cloudshot.model.User;



@Stateless
public class AuthentificationEJB {
	
	
	@PersistenceContext(unitName="jpa-cloudshot")
	private EntityManager em;

	
	//Methode qui gere la création de nouveaux users.
	public User createUser(User user) {
		
		if(this.findbyIdentifiant(user.getIdentifiant())==null)
		{
			String nomContainer="";
			
			user.initDate();
			user.setContainer(getContainerName(user.getIdentifiant()));
			nomContainer+=getContainerName(user.getIdentifiant());
			
			
			try {	
				BlobStorage blob = new BlobStorage();		
				
				em.persist(user);
				
				blob.createContainer(nomContainer);		
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}		
			
			return user;
		}
		else 
			return null;
	}


	//Methode qui renvoie un user à partir de son id sinon null
	public User findbyID(Long id) {
		
		User user = new User();
		user = em.find(User.class, id);
		
		return user;
	}


	//Methode qui verifiee si un user est bien enregistré 
	public User login(User user) {
		
		User user_found = this.findbyIdentifiant(user.getIdentifiant());
		
		if(user_found!=null && !user.getPassword().isEmpty())
		{
			if(user_found.getPassword().equals(user.getPassword()))
				return user_found;
			else 
				return null;
		}
		else 
			return null;
		
	}


	private User findbyIdentifiant(String identifiant) {
		
		if(!identifiant.isEmpty())
		{
			//Creation de la requete nomée
			Query query = 
					em.createNamedQuery("User.findByIdent",User.class);
			//Passage de parametre de la recherche à la requete créee
			
			@SuppressWarnings("unchecked")
			List<User>  users = query.setParameter("identifiant",identifiant).getResultList();
			
			//Renvoyant le user trouvé sinn null 
			return users.isEmpty() ? null : users.get(0);
		}
		else
			return null;
		
	}
	
	private String getContainerName(String identifiant)
	{
		String chaineSansPoints=identifiant.replaceAll("\\.", ""); 
		String containerName=chaineSansPoints.replaceAll("\\@", "-");
		
		
		return containerName;
	}

}
