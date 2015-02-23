package com.cloudshot.toufik.classes.ServiceRest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudshot.toufik.classes.Photo;
import com.cloudshot.toufik.cloudshot.EditionPhoto;
import com.cloudshot.toufik.cloudshot.PictureParam_Activity;
import com.owlike.genson.Genson;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

/**
 * Created by Toufik on 17/02/2015.
 */
public class DeletePicture  extends AsyncTask<Photo, Void, Integer> {

    private HttpClient client = LiensCloudShotREST.client;
    private HttpResponse response;
    private HttpDelete delteRequest;
    private Genson genson = new Genson();
    private ProgressDialog progressDialog;


    public DeletePicture(EditionPhoto activity) {

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("CloudShot");
        progressDialog.setMessage("Supprission en cours...");
    }


    @Override
    protected Integer doInBackground(Photo... params) {
        if(params[0]!=null)
            return deletePicture(params[0]);
        else
            return 0;
    }

    public int deletePicture(Photo photo) {
        int statusCode=0;

        try {
            String idUser = String.valueOf(photo.getUser().getId());
            String idPhoto = String.valueOf(photo.getId());

            delteRequest = new HttpDelete(LiensCloudShotREST.urlDeletePicture+idUser+"/"+idPhoto);

            delteRequest.setHeader("Content-type", "application/json");

            Log.w("deletePhoto : ", "Avant client.Execute ");
            response = client.execute(delteRequest);

            Log.w("deletePhoto : ", "Avant getStatusCode ");
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("deletePhoto", "Error " + statusCode + " while creating User to Update Service");
                return statusCode;
            }

            return  statusCode;

        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            delteRequest.abort();
            Log.w("deletePhoto", "Error while deleting photo :" + " : " + e.toString());
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
