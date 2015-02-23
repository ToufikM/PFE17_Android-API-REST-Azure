package org.pfe.cloudshot.azure;


import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;



public class BlobStorage {

	public static final String storageConnectionString =
	        "DefaultEndpointsProtocol=http;"
	        + "AccountName=cloudshotbloob;"
	        + "AccountKey=mpTgrm7m6GuTC7jAVB+4A/F6L4Fqd7pmEgCOSVSXa5znRP1qQFMUWx6hL4O0G3m2utTyA8hQvGTljtOTiKhKCQ==";
	
	
	private static CloudStorageAccount account;
    private CloudBlobClient serviceClient;
	private CloudBlobContainer container;
    
    
	public BlobStorage() throws Exception{
		
		System.out.println("Debut constructeur");
		connexion();
		System.out.println("fin constructeur");
	}
	
	public BlobStorage(String containerName) throws Exception{
		
		connexion();
		
	    @SuppressWarnings("unused")
		boolean status = createContainer(containerName);
	}
	
	//Une méthode qui va gerer la connexion au compte de stockage
	private void connexion() throws FileNotFoundException, InvalidKeyException, URISyntaxException
	{
		System.out.println("avant create account");
		account = CloudStorageAccount.parse(storageConnectionString);
		
		System.out.println(account.getEndpointSuffix());
		
		serviceClient = account.createCloudBlobClient();
		
		System.out.println(serviceClient.getEndpoint().toString());
	}
	
	private void changePermissions() throws StorageException
	{
		BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
		containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
		container.uploadPermissions(containerPermissions);
	}
	
	//Une méthode qui va s'occuper de la creation des container
	public boolean createContainer(String containerName) throws FileNotFoundException,URISyntaxException, 
	StorageException
	{
		container = serviceClient.getContainerReference(containerName);
        container.createIfNotExists();   
        changePermissions();
        
        return container.exists();
	}
	
	public URI uploadPicture(byte[] picSource, String imageName) throws Exception
	{
		CloudBlockBlob blob = container.getBlockBlobReference(imageName);
	    blob.uploadFromByteArray(picSource, 0, picSource.length);
		
	    return blob.getUri();
	}
	
	
	public byte[] downloadPicture(String imageName) throws StorageException, URISyntaxException
	{
		CloudBlockBlob blob = container.getBlockBlobReference(imageName);
		blob.downloadAttributes();
		long fileByteLength = blob.getProperties().getLength();
		byte[] myByteArray = new byte[(int) fileByteLength];
		blob.downloadToByteArray(myByteArray, 0);
		
		return myByteArray;
	}
	
	public boolean deletePicture(String imageName) throws StorageException, URISyntaxException
	{
		CloudBlockBlob blobSource = container.getBlockBlobReference(imageName);
		boolean status = blobSource.deleteIfExists();
		
		return status;
	}

		  
}
