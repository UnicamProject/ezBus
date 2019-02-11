package com.ezbus.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ezbus.R;
import com.ezbus.main.MainActivity;

public class PrivacyActivity extends AppCompatActivity {

    public CheckBox check1;
    public Button button01;
    public String text;
    public TextView privacy;
    public TextView txtPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        this.button01 = findViewById(R.id.button01);
        this.check1 = findViewById(R.id.check1);
        this.txtPrivacy = findViewById(R.id.txtPrivacy);
        this.privacy = findViewById(R.id.privacy);

        //genera il viewText dato un file txt (INCOMPLETO)
            text = "";

            try {
                InputStream is = getAssets().open("app/java/res/raw/privacy.txt");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                text = new String(buffer);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            txtPrivacy.setText(text);

        }




    private void startNewActivity(Class act) {
        Intent intent = new Intent(this, act);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onClick(View view) {
        finish();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
    //Se viene premuto il pulsante Indietro di sistema
    @Override
    public void onBackPressed() {
        startNewActivity(RegisterActivity.class);
    }

}



