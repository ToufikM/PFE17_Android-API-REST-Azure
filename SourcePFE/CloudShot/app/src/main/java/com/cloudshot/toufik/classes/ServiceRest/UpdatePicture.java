package com.cloudshot.toufik.classes.ServiceRest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudshot.toufik.classes.Photo;
import com.cloudshot.toufik.classes.User;
import com.cloudshot.toufik.cloudshot.EditionPhoto;
import com.cloudshot.toufik.cloudshot.PictureParam_Activity;
import com.owlike.genson.Genson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toufik on 17/02/2015.
 */
public class UpdatePicture  extends AsyncTask<Photo, Void, Integer> {

    private HttpClient client = LiensCloudShotREST.client;
    private HttpResponse response;
    private HttpPost postRequest;
    private Genson genson = new Genson();
    private ProgressDialog progressDialog;


    public UpdatePicture(EditionPhoto activity) {

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("CloudShot");
        progressDialog.setMessage("Mise à jour en cours...");
    }


    @Override
    protected Integer doInBackground(Photo... params) {
        if(params[0]!=null)
            return updatePicture(params[0]);
        else
            return 0;
    }

    public int updatePicture(Photo photo) {

        //String photoString = genson.serialize(photo.getNom());
        int statusCode=0;

        try {

            postRequest = new HttpPost(LiensCloudShotREST.urlUpdatePicture+photo.getUserId()+"/"+photo.getId());

            postRequest.setHeader("Content-type", "application/x-www-form-urlencoded");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("nomPhoto", photo.getNom()));
            nameValuePairs.add(new BasicNameValuePair("lieuPhoto", photo.getLieu()));

            postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            Log.w("updatePhoto : ", "Avant client.Execute Requete : ");
            response = client.execute(postRequest);

            Log.w("updatePhoto : ", "Avant getStatusCode ");
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("updatePhoto", "Error " + statusCode + " while creating User to Update Service");
                return statusCode;
            }

            return  statusCode;

        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            postRequest.abort();
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
