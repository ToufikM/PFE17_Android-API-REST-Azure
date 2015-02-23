package com.cloudshot.toufik.cloudshot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudshot.toufik.classes.Outils;
import com.cloudshot.toufik.classes.Photo;
import com.cloudshot.toufik.classes.ServiceRest.DeletePicture;
import com.cloudshot.toufik.classes.ServiceRest.GetAllPictures;
import com.cloudshot.toufik.classes.ServiceRest.GetPicture;
import com.cloudshot.toufik.classes.ServiceRest.UpdatePicture;
import com.cloudshot.toufik.classes.User;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutionException;


public class EditionPhoto extends Activity {

    private Photo photo = new Photo();
    private User user = new User();
    private ImageView imageVue;
    private EditText nomPhoto_edit,lieuPhoto_edit;
    private Context ceContext;
    private int code = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edition_photo);

        ceContext=this;

        //Recuperation des donnée venu de l'intent
        Long idPhoto = getIntent().getLongExtra("Photo_ID", Long.MIN_VALUE);
        String lieuPhoto = getIntent().getStringExtra("Photo_Lieu");
        String nomPhoto = getIntent().getStringExtra("Photo_Nom");

        Long idUser = getIntent().getLongExtra("User_ID", Long.MIN_VALUE);
        String identifiantUser = getIntent().getStringExtra("User_Identifiant");
        String containerUser = getIntent().getStringExtra("User_Container");

        imageVue=(ImageView)findViewById(R.id.image_modifier_supprimer);
        nomPhoto_edit=(EditText)findViewById(R.id.nomPhoto_modifier_supprimer);
        lieuPhoto_edit=(EditText)findViewById(R.id.lieuPhoto_modifier_supprimer);


        if(idUser!=Long.MIN_VALUE  && idPhoto!=Long.MIN_VALUE  && !nomPhoto.equals(""))
        {
            photo.setId(idPhoto);
            photo.setNom(nomPhoto);
            photo.setLieu(lieuPhoto);
            user.setId(idUser);
            user.setContainer(containerUser);
            user.setIdentifiant(identifiantUser);
            photo.setUser(user);

            nomPhoto_edit.setHint(nomPhoto.replace(".jpg",""));
            lieuPhoto_edit.setHint(lieuPhoto);

            Log.w("EditionPhoto : ", "Id user "+idUser+" id photo "+idPhoto);

            recupererPhoto();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edition_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.Aaccueil:
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

            GetPicture getPic = new GetPicture(EditionPhoto.this);
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
                    Toast.makeText(EditionPhoto.this, "Contenu image vide...", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(EditionPhoto.this, "Erreur connexion "+code, Toast.LENGTH_LONG).show();

        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
    }

    public void confirmModifPhotoOnClick(View v)
    {
        int code = 0;
        String nomModifiee="";
        String lieuModifie="";

        nomModifiee=nomPhoto_edit.getText().toString();
        lieuModifie=lieuPhoto_edit.getText().toString();

        if(!nomModifiee.equals("") || !lieuModifie.equals(""))
        {
            //Je recupere le nom et lieu de la photo que l'utilisateur va introduire ou pas // je traite tous les cas
            if(!nomModifiee.equals(""))
                photo.setNom(nomModifiee+".jpg");
            else
                photo.setNom(nomPhoto_edit.getHint().toString()+".jpg");

            if(!lieuModifie.equals(""))
                photo.setLieu(lieuModifie);
            else
                photo.setLieu(lieuPhoto_edit.getHint().toString());

            try {
                UpdatePicture upadte = new UpdatePicture(EditionPhoto.this);
                upadte.execute(photo);

                code = upadte.get();
            }
            catch (Exception e)
            {
                Log.w("EditionPhoto ", "Erreur : "+e.getMessage());
                Log.w("EditionPhoto ", " Code : "+code);
            }

            if(code==200)
            {
                Log.w("EditionPhoto", "Dans 200 : ");
                Toast.makeText(EditionPhoto.this, "Modification effectuée", Toast.LENGTH_LONG).show();

                lancerAlbumPhoto();
            }
            else {
                Log.w("EditionPhoto id et idphoto sont vide! ", " Code : "+code);
                Toast.makeText(EditionPhoto.this, "CloudShot indisponible...", Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            lancerAlbumPhoto();
        }
    }

    public void confirmSuppPhotoOnClick(View v)
    {
        AlertDialog SupprimerDialogBox =new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Voulez-vous vraiment supprimer " + photo.getNom())


                .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        supprimerPhoto();
                        }}).

                    setNegativeButton("Annuler",new DialogInterface.OnClickListener() {
                        public void onClick (DialogInterface dialog,int which){
                            dialog.dismiss();
                            //lancerAlbumPhoto();

                        }}).show();

                }


    private void supprimerPhoto()
    {
        try {
            DeletePicture delete = new DeletePicture(EditionPhoto.this);
            delete.execute(photo);
            code = delete.get();

            if (code == 200) {
                Log.w("EditionPhoto", "Dans 200 : ");
                Toast.makeText(EditionPhoto.this, "Photo supprimée", Toast.LENGTH_LONG).show();

                lancerAlbumPhoto();

            } else {
                Log.w("EditionPhoto", " Code : " + code);
                Toast.makeText(EditionPhoto.this, "CloudShot indisponible...", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.w("EditionPhoto", " Code : " + code +" Exception "+e.getMessage());
            e.getStackTrace();
        }
    }

    private void lancerAlbumPhoto()
    {
        Intent intent = new Intent(this, AlbumPhoto.class);

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
