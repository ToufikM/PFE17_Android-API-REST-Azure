package com.cloudshot.toufik.classes.ServiceRest;

import android.app.ProgressDialog;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.provider.Contacts;
import android.util.Log;

import com.cloudshot.toufik.classes.Photo;
import com.cloudshot.toufik.classes.User;
import com.cloudshot.toufik.cloudshot.AlbumPhoto;
import com.cloudshot.toufik.cloudshot.ConnexionActivity;
import com.google.gson.Gson;
import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.google.gson.reflect.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpParams;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toufik on 15/02/2015.
 */
public class GetAllPictures extends AsyncTask<User, Void, Integer> {

    private static String urlGetAll = LiensCloudShotREST.urlGetAllPictures;
    private static HttpClient client = LiensCloudShotREST.client;
    public static List<Photo> listePhotoReçus = new ArrayList<Photo>();
    private ProgressDialog progressDialog;


    public GetAllPictures(AlbumPhoto activity)
    {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("CloudShot");
        progressDialog.setMessage("Chargement en cours...");
    }


    @Override
    protected Integer doInBackground(User... params) {
        return getAllPic(params[0]);
    }

    public static int getAllPic(User user) {
        HttpResponse response;
        HttpGet getRequest = new HttpGet(urlGetAll+user.getId().toString());
        Genson genson = new Genson();


        String userString = genson.serialize(user);
        int statusCode=0;

        Log.w("GetAllpic : ", "Avant try ");

        try {
            getRequest.setHeader("Content-type", "application/json");
            //getRequest.setParams();

            Log.w("GetAllpic : ", "Avant client.Execute ");
            response = client.execute(getRequest);

            Log.w("GetAllpic : ", "Avant getStatusCode ");
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("GetAllpic", "Error " + statusCode + " while getting pcture " + urlGetAll);
                return statusCode;
            }

            Log.w("GetAllpic : ", "Avant getEntity ");
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();

                    Reader reader = new InputStreamReader(inputStream);

                    List<Photo> photos = genson.deserialize(inputStream, new GenericType<List<Photo>>(){});

                    listePhotoReçus=photos;

                    inputStream.close();

                    Log.w("GetAllpic : ", "Avant d afficher les Dates : ");
                    for(Photo p : photos) {
                        Log.w("GetAllpic : ", "Dates : " + p.getDateCreation());
                        Log.w("GetAllpic : ", "Dates : " + p.getLieu());
                    }

                    return statusCode;

                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
            Log.w("GetAllpic", "Error while adding user to " + urlGetAll + " : " + e.toString());
        } finally {
            if (client != null) {
                ///client.close();
            }
            return statusCode;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }


    @Override
    protected void onPostExecute(Integer integer) {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
