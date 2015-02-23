package com.cloudshot.toufik.classes;


import com.owlike.genson.annotation.JsonDateFormat;
import com.owlike.genson.annotation.JsonProperty;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Photo {

	private Long Id;
	private User user;
	private String nom;
	private String imageBase64;
	private String dateCreation;
	private Date dateModification;

    public Photo() {

    }

    private URI url;
    private String lieu;

	// getters & setters de la la classe User
	
	public URI getUrl() {
		return url;
	}
	public void setUrl(URI url) {
		this.url = url;
	}

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }


    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

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

    @Override
    public String toString() {
        return "Photo{" +
                "Id=" + Id +
                ", user=" + user +
                ", nom='" + nom + '\'' +
                ", imageBase64='" + imageBase64 + '\'' +
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                ", url=" + url +
                '}';
    }
}
