package com.cloudshot.toufik.cloudshot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudshot.toufik.classes.ServiceRest.CreateUser;
import com.cloudshot.toufik.classes.User;

public class Inscription extends Activity {

    User user= new User();
    EditText nom_et,prenom_et,password1_et,password2_et,identifiant_et;
    TextView message_error_txv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        //Recuperation des champs de saisi de formulaire
        nom_et=(EditText)findViewById(R.id.edt_a_inscriptionFormNom);
        prenom_et=(EditText)findViewById(R.id.edt_a_inscriptionFormPrenom);
        password1_et=(EditText)findViewById(R.id.edt_a_inscriptionFormPass);
        password2_et=(EditText)findViewById(R.id.edt_a_inscriptionFormPassV2);
        identifiant_et=(EditText)findViewById(R.id.edt_a_inscriptionFormIdent);
        message_error_txv=(TextView)findViewById(R.id.txv_FormMsgError);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inscription, menu);
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

    public void annulInscriptionOnClick(View v)
    {
        Toast.makeText(this,"Inscription annulée !",Toast.LENGTH_LONG).show();

        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void confirm_InscriptionOnClick(View v)
    {
        //Recuperation des champs de formulaire
        String ChampPWD1=password1_et.getText().toString();
        String ChampPWD2=password2_et.getText().toString();

        if(ChampPWD1.equals(ChampPWD2)) {

            if(!nom_et.getText().toString().equals(""))
                user.setNom(nom_et.getText().toString());
            else {
                nom_et.setBackgroundColor(Color.RED);
                nom_et.setHint("Ce champs est obligatoire");
                return;
            }

            if(!prenom_et.getText().toString().equals(""))
                user.setPrenom(prenom_et.getText().toString());
            else {
                prenom_et.setBackgroundColor(Color.RED);
                prenom_et.setHint("Ce champs est obligatoire");
                return;
            }

            if(!identifiant_et.getText().toString().equals(""))
                user.setIdentifiant(identifiant_et.getText().toString());
            else {
                identifiant_et.setBackgroundColor(Color.RED);
                identifiant_et.setHint("Ce champs est obligatoire");
                return;
            }

            if(!password1_et.getText().toString().equals(""))
                user.setPassword(password1_et.getText().toString());
            else {
                password1_et.setBackgroundColor(Color.RED);
                password1_et.setHint("Ce champs est obligatoire");
                return;
            }

            try
            {
                CreateUser rest = new CreateUser(Inscription.this);
                rest.execute(user);
                int code_reçu=rest.get();

                if(code_reçu==200)
                {
                    Toast.makeText(this,"Inscription réussi :-)",Toast.LENGTH_LONG).show();
                    User user_reçu = CreateUser.utilisateur;

                    //Je passe le id et identifiant a l activity connexion
                    Intent intent = new Intent(this.getBaseContext(), ConnexionActivity.class);
                    intent.putExtra("User_ID", user_reçu.getId());
                    intent.putExtra("User_Identifiant", user_reçu.getIdentifiant());
                    startActivity(intent);
                }
                else if(code_reçu==409)
                {
                    identifiant_et.setBackgroundColor(Color.RED);
                    identifiant_et.setText(null);
                    identifiant_et.setHint("Email déja utilisée!");
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
            password1_et.setBackgroundColor(Color.RED);
            password2_et.setBackgroundColor(Color.RED);
            message_error_txv.setText("Verifier votre mot de passe!");
        }
    }


}
