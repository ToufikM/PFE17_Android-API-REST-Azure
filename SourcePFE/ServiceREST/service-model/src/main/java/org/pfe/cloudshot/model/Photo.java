package org.pfe.cloudshot.model;


import java.net.URI;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.owlike.genson.annotation.JsonDateFormat;


@Entity(name="Photo")
@Table(name="t_photo")
@Access(AccessType.FIELD) //JPA


//Query: 
@NamedQueries({
	@NamedQuery(
			name="Photo.getPhoto",
			query="SELECT p FROM Photo p WHERE p.Id = :id_picture AND p.user.Id = :id_user"			
			),
	@NamedQuery(
			name="Photo.getAllPhotos",
			query="SELECT p FROM Photo p WHERE p.user.Id = :id_user"			
			)
})


@XmlRootElement //JAX-B
@XmlAccessorType(XmlAccessType.FIELD)
public class Photo {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@XmlAttribute(name="Id")
	private Long Id;
	
	@OneToOne(cascade=CascadeType.MERGE,fetch=FetchType.LAZY)
	@JoinColumn(name="USER_ID",insertable=true,updatable=true,nullable=false,unique=false)
	@XmlElement(name="user")
	private User user;
	
	@Column(name="nom",length=50)
	@XmlElement(name="nom")
	private String nom;


	@XmlElement(name="imageBase64")
	private String imageBase64; 
	
	@Column(name="dateCreation")
	@XmlElement(name="dateCreation")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonDateFormat("mmm dd,yyyy") 
	private Date dateCreation;

	@Column(name="dateModification",length=50)
	@XmlElement(name="dateModification")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonDateFormat("mmm dd,yyyy")
	private Date dateModification;
	
	@Column(name="url")
	private URI url;
	
	@Column(name="lieu")
	private String lieu;
	
	
	
	public Photo(){}
	
	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getLieu() {
		return lieu;
	}
	public void setLieu(String lieu) {
		this.lieu = lieu;
	}
	
	public URI getUrl() {
		return url;
	}
	public void setUrl(URI url) {
		this.url = url;
	}
	@PrePersist
	public void initDate()
	{
		this.dateCreation=new Date();
		this.dateModification=this.dateCreation;
	}
	@PreUpdate
	public void editDate()
	{
		this.dateModification=new Date();	
    }
	
	public String getImageBase64() {
		return imageBase64;
	}

	public void setImageBase64(String imageBase64) {
		this.imageBase64 = imageBase64;
	}

	public Long getUserId() {
		return user == null ? null : user.getId();
	}
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
		
	
}
