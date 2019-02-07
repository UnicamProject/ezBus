package com.ezbus.authentication;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.widget.TextView;
import com.ezbus.R;
import com.ezbus.main.SharedPref;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView email;
    private TextView username;
    private static Client user;
    SharedPref sharedpref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpref = new SharedPref(this);

        if (sharedpref.loadNightModeState()==true)
            setTheme(R.style.App_Dark);
        else setTheme(R.style.App_Green);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.Email);
        username = findViewById(R.id.Username);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView(FirebaseUser user) {
        email.setText("Email: " + user.getEmail());
        username.setText("Username: " + user.getProviderId());
        username.setText("User: " + user.getDisplayName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public static Client getUser() {
        return user;
    }

    public static void setUser(Client newUser) {
        user = newUser;
    }

}