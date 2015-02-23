package com.cloudshot.toufik.cloudshot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cloudshot.toufik.classes.User;


public class Applications extends Activity {

    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);


        String identUser = getIntent().getStringExtra("User_Identifiant");
        String containerUser = getIntent().getStringExtra("User_Container");
        Long idUser = getIntent().getLongExtra("User_ID", Long.MIN_VALUE);

        if(identUser!=null && containerUser!=null && idUser!=Long.MIN_VALUE) {
            user.setIdentifiant(identUser);
            user.setId(idUser);
            user.setContainer(containerUser);
        }
        else
            Toast.makeText(this, "Erreur au chargement... ", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_applications, menu);
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


    public void albumPhotoOnClick(View v)
    {
        Intent intent = new Intent(this, AlbumPhoto.class);

        intent.putExtra("User_ID", user.getId());
        intent.putExtra("User_Identifiant", user.getIdentifiant());
        intent.putExtra("User_Container", user.getContainer());

        startActivity(intent);
    }

    public void prendrePhotoOnClick(View v)
    {
        Intent intent = new Intent(this, PictureParam_Activity.class);

        intent.putExtra("User_ID", user.getId());
        intent.putExtra("User_Identifiant", user.getIdentifiant());
        intent.putExtra("User_Container", user.getContainer());

        startActivity(intent);
    }


    public void exitOnClick(View v)
    {
        AlertDialog Quitter =new AlertDialog.Builder(this)
                .setTitle("Quitter")
                .setMessage("Voulez-vous vraiment quitter l'application ?")


                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                        //Mettre l'application en arriere plan, car on ne peut pas l'arreter...
                        finish();
                        redirectionToMain();
                        moveTaskToBack(true);
                    }}).

                        setNegativeButton("Non",new DialogInterface.OnClickListener() {
                            public void onClick (DialogInterface dialog,int which){
                                dialog.dismiss();
                            }}).show();

    }

    private void redirectionToMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

