package org.pfe.cloudshot.model;


import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.owlike.genson.annotation.JsonDateFormat;



@Entity(name="User")//on fixe le nom de l'entité a Commune car en default c org.mengu...commune
@Table(name="t_user")
@Access(AccessType.FIELD) //JPA

//Query: 
@NamedQueries({
	@NamedQuery(
			name="User.findByIdent",
			query="select u from User u WHERE u.identifiant = :identifiant"			
			)
})



@XmlRootElement //JAX-B
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@XmlAttribute(name="Id")
	private Long Id;
	
	@Column(name="nom",length=50)
	@XmlElement(name="nom")
	private String nom;
	
	@Column(name="prenom",length=50)
	@XmlElement(name="prenom")
	private String prenom;
	
	@Column(name="identifiant",length=50)
	@XmlElement(name="identifiant")
	private String identifiant;
	
	@Column(name="password",length=50)
	@XmlElement(name="password")
	private String password;

	@Column(name="dateInscription")
	@XmlElement(name="dateInscription")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonDateFormat("mmm dd,yyyy")
	private Date dateInscription;

	@Column(name="container",length=50)
	@XmlElement(name="container")
	private String container;
	
	
	 public User() {
	}
	
	@PrePersist
	public void initDate()
	{
		this.dateInscription=new Date();
	}
	
	// getters & setters de la la classe User
	
	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public Date getDateInscription() {
		return dateInscription;
	}

	public void setDateInscription(Date dateInscription) {
		this.dateInscription = dateInscription;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
