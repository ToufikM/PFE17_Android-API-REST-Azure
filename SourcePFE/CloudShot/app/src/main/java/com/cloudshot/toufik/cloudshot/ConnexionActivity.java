package com.cloudshot.toufik.cloudshot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudshot.toufik.classes.MydataBase;
import com.cloudshot.toufik.classes.ServiceRest.LogUser;
import com.cloudshot.toufik.classes.User;

import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.List;


public class ConnexionActivity extends Activity {

    private EditText identifiant,password;
    private TextView msgErreurConnexion;
    private User user = new User();
    private String identApresInscription="";
    private MydataBase db ;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        identifiant=(EditText)findViewById(R.id.edt_identifiant);
        password=(EditText)findViewById(R.id.edt_password);
        msgErreurConnexion=(TextView)findViewById(R.id.txv_msgErrorConnexion);
        checkBox=(CheckBox)findViewById(R.id.checkBoxConnexion);

        Log.w("Connexion", "Erreur : Avant instantiation db");
        db = new MydataBase(this) ;

        identApresInscription = getIntent().getStringExtra("User_Identifiant");
        Long id_ApresInscription = getIntent().getLongExtra("User_ID", Long.MIN_VALUE);

        Log.w("Connexion", "Erreur : Avant db.listeUser");
        User users = null;
        users=db.listeUser();

        if(users!=null)
        {
            identifiant.setText(users.getIdentifiant());
            password.setText(users.getPassword());
            checkBox.setChecked(true);
        }

        if(identApresInscription!=null)
            identifiant.setText(identApresInscription);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connexion, menu);
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

    public void creerCompteOnClick(View v)
    {
        Intent intent=new Intent(this,Inscription.class);
        startActivity(intent);
    }

    public void connexionOnClick(View v)
    {
        if(!identifiant.getText().toString().equals("") && !password.getText().toString().equals(""))
        {
            user.setIdentifiant(identifiant.getText().toString());
            user.setPassword(password.getText().toString());

            try
            {
                LogUser restLogUser = new LogUser(ConnexionActivity.this);
                restLogUser.execute(user);
                int code_reçu=restLogUser.get();

                if(code_reçu==200)
                {
                    User user_reçu = restLogUser.utilisateur;
                    Toast.makeText(this, "Bienvenu "+user_reçu.getPrenom()+" :-)", Toast.LENGTH_LONG).show();

                    //Si la case de remember me est coché je sauvegarde le user
                    if(checkBox.isChecked()) {
                        if(!db.isPresent(user_reçu))
                              db.insertUser(user_reçu);
                    }
                    else
                        db.viderMaTable();

                    //Je passe le id et identifiant a l activity connexion
                    Intent intent=new Intent(this.getBaseContext(),Applications.class);
                    intent.putExtra("User_ID", user_reçu.getId());
                    intent.putExtra("User_Identifiant", user_reçu.getIdentifiant());
                    intent.putExtra("User_Container", user_reçu.getContainer());
                    startActivity(intent);
                }
                else if(code_reçu==404)
                {
                    msgErreurConnexion.setText("Verifier Identifiant/Mot de passe!");
                }
                else
                {
                    Toast.makeText(this,"Service Indisponible... "+code_reçu,Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e)
            {
                Toast.makeText(this,"Erreur : "+e.toString(),Toast.LENGTH_LONG).show();
                Log.w("Inscription", "Error : " + e.toString());
            }

        }
        else {
            identifiant.setBackgroundColor(Color.RED);
            password.setBackgroundColor(Color.RED);
            msgErreurConnexion.setText("Ces champs sont obligatoires");
        }
    }
}
