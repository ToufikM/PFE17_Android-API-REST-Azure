package com.cloudshot.toufik.classes.ServiceRest;

import android.net.http.AndroidHttpClient;

import org.apache.http.client.HttpClient;

/**
 * Created by Toufik on 17/02/2015.
 */
public class LiensCloudShotREST {
    final static String urlLogUser = "http://cbc552a99b5e478b84c3350f9985a6e1.cloudapp.net/CloudShotRS/cloudShot/login/authentifier";
    final static String urlUpdatePicture = "http://cbc552a99b5e478b84c3350f9985a6e1.cloudapp.net/CloudShotRS/cloudShot/photos/update/";
    final static String urlGetPicture = "http://cbc552a99b5e478b84c3350f9985a6e1.cloudapp.net/CloudShotRS/cloudShot/photos/get/";
    final static String urlDeletePicture = "http://cbc552a99b5e478b84c3350f9985a6e1.cloudapp.net/CloudShotRS/cloudShot/photos/delete/";
    final static String urlGetAllPictures = "http://cbc552a99b5e478b84c3350f9985a6e1.cloudapp.net/CloudShotRS/cloudShot/photos/all/";
    final static String urlCreateUser = "http://cbc552a99b5e478b84c3350f9985a6e1.cloudapp.net/CloudShotRS/cloudShot/login/create";
    final static String urlAddPicture = "http://cbc552a99b5e478b84c3350f9985a6e1.cloudapp.net/CloudShotRS/cloudShot/photos/add";
    final static HttpClient client = AndroidHttpClient.newInstance("cloudShotTest");
}
