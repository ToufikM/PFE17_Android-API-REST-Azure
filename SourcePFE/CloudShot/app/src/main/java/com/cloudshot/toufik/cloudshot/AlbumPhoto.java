package com.cloudshot.toufik.cloudshot;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudshot.toufik.classes.MydataBase;
import com.cloudshot.toufik.classes.Outils;
import com.cloudshot.toufik.classes.Photo;
import com.cloudshot.toufik.classes.ServiceRest.GetAllPictures;
import com.cloudshot.toufik.classes.User;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class AlbumPhoto extends Activity {

    private static String logtag = "Prendre photo";
    private static int PRENDRE_PHOTO=1;
    private Uri imageUrl;
    private List<Photo> maListePhoto;
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photo);

        Button cameraButton=(Button) findViewById(R.id.camera);
        maListePhoto=new ArrayList<Photo>();


        String identUser = getIntent().getStringExtra("User_Identifiant");
        String containerUser = getIntent().getStringExtra("User_Container");
        Long idUser = getIntent().getLongExtra("User_ID", Long.MIN_VALUE);

        if(identUser!=null && containerUser!=null && idUser!=Long.MIN_VALUE) {
            user.setIdentifiant(identUser);
            user.setId(idUser);
            user.setContainer(containerUser);
        }
        else
            Toast.makeText(this,"Erreur au chargement... ",Toast.LENGTH_LONG).show();

        if(lireListPhoto()) {

            remplireListeView();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_photo, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.camera:
                demarerAppareilPhoto();
                return true;
            case R.id.toAccueil:
                accueilApplication();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void demarerAppareilPhoto()
    {
        Intent intent=new Intent(this,PictureParam_Activity.class);

        intent.putExtra("User_ID", user.getId());
        intent.putExtra("User_Identifiant", user.getIdentifiant());
        intent.putExtra("User_Container", user.getContainer());

        this.startActivity(intent);

    }

    private boolean lireListPhoto()
    {
      try
      {
          GetAllPictures allPicFromCloud = new GetAllPictures(AlbumPhoto.this);
          Log.w("AlbumPhoto", "Avant execution getAllpic : " );
          allPicFromCloud.execute(user);
          Log.w("AlbumPhoto", "Apres execution getAllpic : " );
          if(allPicFromCloud.get()==200)
          {
              Log.w("AlbumPhoto", "Dans 200 : " );
              maListePhoto=GetAllPictures.listePhotoReçus;
              Log.w("AlbumPhoto", "200 Avant remplirelistView : " );

              return true;
          }
          else if(allPicFromCloud.get()==204)
              Toast.makeText(AlbumPhoto.this, "Erreur -> user : vide ...", Toast.LENGTH_LONG).show();
          else if(allPicFromCloud.get()==404)
              Toast.makeText(AlbumPhoto.this, "Votre Album est vide", Toast.LENGTH_LONG).show();
          else
              Toast.makeText(AlbumPhoto.this, "CloudShot indisponible...", Toast.LENGTH_LONG).show();

      }
      catch (Exception e)
      {
          Log.w("AlbumPhoto", "Exception : "+e.getMessage() );
      }

        return false;
    }

    private void remplireListeView()
    {
        final Context context=this;

        ArrayAdapter<Photo> adapter = new MaListeAdaptee(this);
        ListView liste = (ListView)findViewById(R.id.AlbumPhoto_listView);
        liste.setAdapter(adapter);

        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Photo picItem = (Photo) parent.getItemAtPosition(position);

                lancerVisionneurPhoto(picItem);
            }
        });
    }



    private class MaListeAdaptee extends ArrayAdapter<Photo> {

        boolean ScndPicExist=true;
        int imageViewTochange=0;

        public MaListeAdaptee(Context context)
        {
            super(context,R.layout.ligne_view,maListePhoto);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if(itemView == null)
            {
                itemView = getLayoutInflater().inflate(R.layout.ligne_view,parent,false);
            }


            if(maListePhoto.get(position)!=null) {
                //Trouvons la photo qui sera afficher
                Photo photoCourante = maListePhoto.get(position);

                //convert byte to bitmap take from Photo class
                byte[] outImage= Outils.decodePicture(photoCourante.getImageBase64());

                if(outImage!=null) {
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inSampleSize = 6;
                    Bitmap theImage = BitmapFactory.decodeStream(imageStream,null,options);


                    if(theImage!=null) {
                         ((ImageView) itemView.findViewById(R.id.imageView_gauche)).setImageBitmap(theImage);
                         ((TextView) itemView.findViewById(R.id.txt_nomPicAP)).setText(photoCourante.getNom().replace(".jpg",""));
                         ((TextView) itemView.findViewById(R.id.txv_PlaceAP)).setText(photoCourante.getLieu());
                         ((TextView) itemView.findViewById(R.id.txv_dateAP)).setText(photoCourante.getDateCreation());
                    }
                }
            }

            return itemView;
        }
    }

    private void lancerVisionneurPhoto(Photo picItem)
    {
        //Je passe le id et identifiant a l activity connexion
        Intent intent = new Intent(this, Visionneur.class);

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





/*
    private void prendrePhoto(View v)
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"photo.jpg");
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

            ImageView imageview = (ImageView) findViewById(R.id.image_cameraAP);
            ContentResolver cr = getContentResolver();

            Bitmap bitmap;

            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(cr,selectedImage);
                imageview.setImageBitmap(bitmap);
                Toast.makeText(AlbumPhoto.this,selectedImage.toString(),Toast.LENGTH_LONG).show();

            }catch (Exception e)
            {
                Log.e(logtag, e.toString());
            }

        }

    }*/