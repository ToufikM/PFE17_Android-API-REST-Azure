package com.cloudshot.toufik.classes.ServiceRest;

import android.app.ProgressDialog;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudshot.toufik.classes.User;
import com.cloudshot.toufik.cloudshot.ConnexionActivity;
import com.cloudshot.toufik.cloudshot.MainActivity;
import com.owlike.genson.Genson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import java.io.InputStream;

/**
 * Created by Toufik on 15/02/2015.
 */
public class LogUser extends AsyncTask<User, Void, Integer> {

    private static String urlLoging = LiensCloudShotREST.urlLogUser;
    private static HttpClient client =LiensCloudShotREST.client;
    public static User utilisateur = new User();
    private ProgressDialog progressDialog;


    public LogUser(ConnexionActivity activity)
    {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("CloudShot");
        progressDialog.setMessage("Connexion en cours...");
    }

    @Override
    protected Integer doInBackground(User... params) {
        return getUser(params[0]);
    }

    public static int getUser(User user) {
        HttpResponse response;
        HttpPost postRequest = new HttpPost(urlLoging);
        Genson genson = new Genson();
        String userString = genson.serialize(user);
        int statusCode=0;

        Log.w("addUser : ", "Avant try ");

        try {
            StringEntity se = new StringEntity(userString.toString());

            postRequest.setHeader("Content-type", "application/json");
            postRequest.setEntity(se);

            Log.w("addUser : ", "Avant client.Execute ");
            response = client.execute(postRequest);

            Log.w("addUser : ", "Avant getStatusCode ");
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("addUser", "Error " + statusCode + " while creating User to " + urlLoging);
                return statusCode;
            }

            Log.w("addUser : ", "Avant getEntity ");
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();

                    utilisateur = JsonHandel.deserialisationUser(inputStream);

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
            postRequest.abort();
            Log.w("AddUser", "Error while adding user to " + urlLoging + " : " + e.toString());
        } finally {
            if (client != null) {
                //client.close();
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
