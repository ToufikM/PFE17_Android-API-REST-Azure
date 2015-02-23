package com.cloudshot.toufik.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.microsoft.azure.storage.core.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Toufik on 14/02/2015.
 */
public class Outils {

    public static String encodeImage(byte[] imageByteArray) {
       return Base64.encode(imageByteArray);
    }

    public static byte[] decodePicture(String imageDataString) {
        return Base64.decode(imageDataString);
    }
}
