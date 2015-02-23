package com.cloudshot.toufik.cloudshot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;


public class MainActivity extends Activity {

    TextView txvErrorNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvErrorNetwork=(TextView)findViewById(R.id.txv_msgErrorNetwork);

        if(!isNetworkEnable())
            Toast.makeText(this, "Verifiez votre connexion\n" + "CloudShot est indisponible!", Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void connexionOnClick(View v) {
        if(isNetworkEnable()) {
            Intent intent = new Intent(this, ConnexionActivity.class);
            startActivity(intent);
        }
        else
            txvErrorNetwork.setText("Verifiez votre connexion");
    }

    public void inscriptionOnClick(View v) {
        if(isNetworkEnable()) {
            Intent intent = new Intent(this, Inscription.class);
            startActivity(intent);
        }
        else
            txvErrorNetwork.setText("Verifiez votre connexion");
    }

    //Je verifie d'abord si l'appereil est connectée sinon rien ne marchera!!!
    public boolean isNetworkEnable()
    {

        ConnectivityManager connec = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // Check if wifi or mobile network is available or not. If any of them is
        // available or connected then it will return true, otherwise false;
        return wifi.isConnected() || mobile.isConnected();

    }
}
