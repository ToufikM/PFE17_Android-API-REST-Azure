package com.cloudshot.toufik.classes.ServiceRest;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudshot.toufik.cloudshot.PictureParam_Activity;
import com.owlike.genson.Genson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import java.io.InputStream;
import com.cloudshot.toufik.classes.Photo;
/**
 * Created by Toufik on 16/02/2015.
 */
public class AddPicture extends AsyncTask<Photo, Void, Integer> {

    private static String urlAddPicture = LiensCloudShotREST.urlAddPicture;
    final static HttpClient client = LiensCloudShotREST.client;
    public static Long pictureID ;
    //private Context context;
    private ProgressDialog progressDialog;


    public AddPicture(PictureParam_Activity activity) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("CloudShot");
            progressDialog.setMessage("Chargement en cours...");
    }

    public AddPicture() {

    }

    @Override
    protected Integer doInBackground(Photo... params) {
        int resultat = addPhoto(params[0]);

        return resultat;
    }

    public static int addPhoto(Photo photo) {
        HttpResponse response;
        HttpPut putRequest = new HttpPut(urlAddPicture+"/"+String.valueOf(photo.getUser().getId()));
        Genson genson = new Genson();
        int statusCode=0;


        try {
            Log.w("AddPic : ", "Debut try" );
                String photoString = genson.serialize(photo);

                Log.w("AddPic  Length photo: ", "" + photoString.length());
                Log.w("AddPic Length photo:  ", "" + photo.getImageBase64().length());

                StringEntity se = new StringEntity(photoString.toString());


                putRequest.setHeader("Content-type", "application/json");
                putRequest.setEntity(se);

                Log.w("AddPic : ", "Avant client.Execute ");
                response = client.execute(putRequest);

                Log.w("AddPic : ", "Avant getStatusCode ");
                statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("AddPic", "Error " + statusCode + " while creating Photo to " + urlAddPicture);
                    return statusCode;
                }

                Log.w("AddPic : ", "Avant getEntity ");
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = entity.getContent();

                        pictureID = genson.deserialize(inputStream, Long.class);
                        Log.w("AddPic : ", String.valueOf(pictureID));

                        return statusCode;

                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }

        } catch (Exception e) {
            putRequest.abort();
            Log.w("AddPic", "Error while adding picture to " + urlAddPicture + " : " + e.toString());
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
