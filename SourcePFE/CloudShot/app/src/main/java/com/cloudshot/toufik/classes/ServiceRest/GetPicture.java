package com.cloudshot.toufik.classes.ServiceRest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudshot.toufik.classes.Photo;
import com.cloudshot.toufik.cloudshot.EditionPhoto;
import com.cloudshot.toufik.cloudshot.PictureParam_Activity;
import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Toufik on 17/02/2015.
 */
public class GetPicture extends AsyncTask<Photo, Void, Integer> {

    private HttpClient client = LiensCloudShotREST.client;
    private HttpResponse response;
    private HttpGet getRequest;
    private Genson genson = new Genson();
    public static Photo photo = new Photo();
    private ProgressDialog progressDialog;


    public GetPicture(Context activity) {

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("CloudShot");
        progressDialog.setMessage("Chargement en cours...");
    }

    @Override
    protected Integer doInBackground(Photo... params) {
        Log.w("getPhoto : ", "Debut... ");
        if (params[0] != null)
            return getPicture(params[0]);
        else
            return 0;
    }

    public int getPicture(Photo maphoto) {

        int statusCode = 0;

        try {
            Log.w("getPhoto : ", "Avant recuperation des id ");
            String idUser = String.valueOf(maphoto.getUser().getId());
            String idPhoto = String.valueOf(maphoto.getId());

            String requete = LiensCloudShotREST.urlGetPicture + idUser + "/" + idPhoto;
            getRequest = new HttpGet(requete);

            Log.w("getPhoto : ", "Avant setHeader iduser : "+idUser+" idPhoto: "+idPhoto);
            getRequest.setHeader("Content-type", "application/json");

            Log.w("getPhoto : ", "Avant client.Execute iduser : "+idUser+" idPhoto: "+idPhoto);
            response = client.execute(getRequest);

            Log.w("getPhoto : ", "Avant getStatusCode ");
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("getPhoto", "Error " + statusCode + " while creating User to Update Service");
                return statusCode;
            }


            Log.w("getPhoto : ", "Avant getEntity ");
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                inputStream = entity.getContent();

                Photo photo_reçu = (Photo) genson.deserialize(inputStream, Photo.class);
                Log.w("getPhoto : ", "code : "+statusCode+"Deserialisation URL: "+photo_reçu.getUrl().toString());
                if (photo_reçu != null)
                        photo = photo_reçu;

                inputStream.close();

                return statusCode;
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
            Log.w("updatePhoto", "Error while updating photo to the service" + " : " + e.toString());
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