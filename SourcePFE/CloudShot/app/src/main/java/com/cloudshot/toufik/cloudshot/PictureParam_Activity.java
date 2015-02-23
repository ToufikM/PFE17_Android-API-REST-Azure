package com.cloudshot.toufik.cloudshot;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudshot.toufik.classes.MydataBase;
import com.cloudshot.toufik.classes.Outils;
import com.cloudshot.toufik.classes.Photo;
import com.cloudshot.toufik.classes.ServiceRest.AddPicture;
import com.cloudshot.toufik.classes.ServiceRest.GetAllPictures;
import com.cloudshot.toufik.classes.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PictureParam_Activity extends Activity {

    private static String logtag = "Prendre photo";
    private static int PRENDRE_PHOTO=1;
    private Uri imageUrl;
    private String dateCourante="";
    private EditText nom_et,date_et,lieu_et;
    private Bitmap bitmap;
    private MydataBase db ;
    private User user = new User();
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_param);

        nom_et=(EditText)findViewById(R.id.nomPhoto_et);
        lieu_et=(EditText)findViewById(R.id.LieuPhoto_et);

        //db = new MydataBase(this) ;

        context = getApplicationContext();

        String identUser = getIntent().getStringExtra("User_Identifiant");
        String containerUser = getIntent().getStringExtra("User_Container");
        Long idUser = getIntent().getLongExtra("User_ID", Long.MIN_VALUE);

        if(!identUser.equals("") && !containerUser.equals("") && idUser!=Long.MIN_VALUE) {

            user.setIdentifiant(identUser);
            user.setId(idUser);
            user.setContainer(containerUser);

            prendrePhoto(this.getCurrentFocus());
        }
        else
            Toast.makeText(this,"Erreur au chargement... ",Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture_param, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prendrePhoto(View v)
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMyyyy-hhmmss");
        Date date = new Date();

        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "photo"+dateFormat.format(date)+".jpg");
        imageUrl = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUrl);
        startActivityForResult(intent,PRENDRE_PHOTO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent inent) {
        super.onActivityResult(requestCode, resultCode, inent);

        if(resultCode == Activity.RESULT_OK)
        {
            Uri selectedImage = imageUrl;
            getContentResolver().notifyChange(selectedImage,null);

            ImageView imageview = (ImageView) findViewById(R.id.image_camera);
            ContentResolver cr = getContentResolver();

            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(cr,selectedImage);
                imageview.setImageBitmap(bitmap);
                Toast.makeText(PictureParam_Activity.this, selectedImage.toString(), Toast.LENGTH_LONG).show();

            }catch (Exception e)
            {
                Log.e(logtag, e.toString());
            }

        }

    }

    public void confirmChargPhotoOnClick(View v)
    {
        Log.w("PictureParam bouton confirm: ", "Au debut..");
        Photo newPic = new Photo();
        Log.w("PictureParam bouton confirm: ", "Au debut..");

        Log.w("PictureParam bouton confirm: ", "Au debut..");
        String nomPhoto="";
        String lieuPhoto="";

        Log.w("PictureParam bouton confirm: ", "avant conversion de la photo");
        //Recuperation de la photo renvoyée par la camera et conversion en byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte imageInByte[] = stream.toByteArray();

        Log.w("PictureParam bouton confirm: ", ""+imageInByte.length);

        //Création d'un objet Photo
        if(nom_et.getText().toString().equals("")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy-hh:mm:ss");
            Date date = new Date();

            nomPhoto = "photo"+dateFormat.format(date)+".jpg";
        }
        else
            nomPhoto= nom_et.getText().toString() + ".jpg";

        if(lieu_et.getText().toString().equals(""))
             lieuPhoto="lieu vide";
        else
            lieuPhoto = lieu_et.getText().toString();

        //Remplissage des elements de la photo
        newPic.setNom(nomPhoto);
        newPic.setLieu(lieuPhoto);
        newPic.setUser(user);
        Log.w("PictureParam : ", "Avant appel a encodeImage ..");
        newPic.setImageBase64(Outils.encodeImage(imageInByte));


        Log.w("PictureParam Insert: ", "Insertion d'une Photo ..");

        try {
            Log.w("PictureParam : ", "Avant appel a AddPicture ..");
            AddPicture  addPhoto = new AddPicture(PictureParam_Activity.this);
            Log.w("PictureParam : ", "Avant execution de execute ..");
            addPhoto.execute(newPic);
                Log.w("PictureParam", "Apres execution getpic ");
                if(addPhoto.get()==200)
                {
                    Log.w("PictureParam", "Dans 200 : " );
                    Toast.makeText(PictureParam_Activity.this, "La photo est dans les nuages!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, Applications.class);

                    intent.putExtra("User_ID", user.getId());
                    intent.putExtra("User_Identifiant", user.getIdentifiant());
                    intent.putExtra("User_Container", user.getContainer());

                    startActivity(intent);
                }
                else if(addPhoto.get()==417)
                    Toast.makeText(PictureParam_Activity.this, "Erreur -> user : introuvable ...", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(PictureParam_Activity.this, "CloudShot indisponible...", Toast.LENGTH_LONG).show();

            stream.close();
            }
            catch (Exception e)
            {
                Log.w("PictureParam", "Exception : "+e.getMessage() );
            }
        finally {

        }

    }


}
