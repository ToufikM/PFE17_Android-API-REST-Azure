package com.cloudshot.toufik.cloudshot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudshot.toufik.classes.Outils;
import com.cloudshot.toufik.classes.Photo;
import com.cloudshot.toufik.classes.ServiceRest.GetPicture;
import com.cloudshot.toufik.classes.User;

import java.io.ByteArrayInputStream;


public class Visionneur extends Activity {

    private Photo photo = new Photo();
    private User user = new User();
    private ImageView imageVue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visionneur);

        imageVue=(ImageView)findViewById(R.id.imageVisionneur);

        //Recuperation des donnée venu de l'intent
        Long idPhoto = getIntent().getLongExtra("Photo_ID", Long.MIN_VALUE);
        String lieuPhoto = getIntent().getStringExtra("Photo_Lieu");
        String nomPhoto = getIntent().getStringExtra("Photo_Nom");

        Long idUser = getIntent().getLongExtra("User_ID", Long.MIN_VALUE);
        String identifiantUser = getIntent().getStringExtra("User_Identifiant");
        String containerUser = getIntent().getStringExtra("User_Container");


        if(idUser!=Long.MIN_VALUE  && idPhoto!=Long.MIN_VALUE  && !nomPhoto.equals(""))
        {
            photo.setId(idPhoto);
            photo.setNom(nomPhoto);
            photo.setLieu(lieuPhoto);
            user.setId(idUser);
            user.setContainer(containerUser);
            user.setIdentifiant(identifiantUser);
            photo.setUser(user);

            Log.w("EditionPhoto : ", "Id user " + idUser + " id photo " + idPhoto);

            recupererPhoto();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_visionneur, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.edit:
                lacnerEditionPhoto(photo);
                return true;
            case R.id.goAccueil:
                accueilApplication();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void recupererPhoto()
    {
        try {

            GetPicture getPic = new GetPicture(Visionneur.this);
            getPic.execute(photo);
            int code = getPic.get();

            if(code==200) {
                //convert byte to bitmap take from Photo class
                byte[] outImage = Outils.decodePicture(GetPicture.photo.getImageBase64());

                if (outImage != null) {
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    Bitmap theImage = BitmapFactory.decodeStream(imageStream, null, options);

                    //Placement de l'image recuperer dans la vue correspondante
                    imageVue.setImageBitmap(theImage);

                } else
                    Toast.makeText(Visionneur.this, "Contenu image vide...", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(Visionneur.this, "Erreur connexion "+code, Toast.LENGTH_LONG).show();

        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
    }


    private void lacnerEditionPhoto(Photo picItem)
    {
        //Je passe le id et identifiant a l activity connexion
        Intent intent = new Intent(this, EditionPhoto.class);

        intent.putExtra("Photo_ID", picItem.getId());
        intent.putExtra("Photo_Nom", picItem.getNom());
        intent.putExtra("Photo_Lieu", picItem.getLieu());
        intent.putExtra("User_ID", user.getId());
        intent.putExtra("User_Identifiant", user.getIdentifiant());
        intent.putExtra("User_Container", user.getContainer());

        startActivity(intent);
    }

    private void accueilApplication()
    {
        //Je passe le id et identifiant a l activity connexion
        Intent intent = new Intent(this, Applications.class);

        intent.putExtra("User_ID", user.getId());
        intent.putExtra("User_Identifiant", user.getIdentifiant());
        intent.putExtra("User_Container", user.getContainer());

        startActivity(intent);
    }
}
