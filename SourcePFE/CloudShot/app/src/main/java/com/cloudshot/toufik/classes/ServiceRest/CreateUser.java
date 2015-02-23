package com.cloudshot.toufik.classes.ServiceRest;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebResourceRequest;

import com.cloudshot.toufik.classes.User;
import com.cloudshot.toufik.cloudshot.Inscription;
import com.cloudshot.toufik.cloudshot.PictureParam_Activity;
import com.owlike.genson.Genson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Toufik on 14/02/2015.
 */
public class CreateUser extends AsyncTask<User, Void, Integer> {

    private static String urlCreateUser = LiensCloudShotREST.urlCreateUser;
    private static HttpClient client = LiensCloudShotREST.client;
    public static User utilisateur = new User();
    private ProgressDialog progressDialog;


    public CreateUser(Inscription activity) {

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("CloudShot");
        progressDialog.setMessage("Inscription en cours...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
    }



    @Override
    protected Integer doInBackground(User... params) {
        return addUser(params[0]);
    }


    public static int addUser(User user) {
        HttpResponse response;
        HttpPut putRequest = new HttpPut(urlCreateUser);
        Genson genson = new Genson();
        String userString = genson.serialize(user);
        int statusCode=0;

        Log.w("addUser : ", "Avant try ");

        try {
            StringEntity se = new StringEntity(userString.toString());
            //se.setContentType("application/json");
            //se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            putRequest.setHeader("Content-type", "application/json");
            putRequest.setEntity(se);

            Log.w("addUser : ", "Avant client.Execute ");
            response = client.execute(putRequest);

            Log.w("addUser : ", "Avant getStatusCode ");
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("addUser", "Error " + statusCode + " while creating User to " + urlCreateUser);
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
            putRequest.abort();
            Log.w("AddUser", "Error while adding user to " + urlCreateUser + " : " + e.toString());
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
