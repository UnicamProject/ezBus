package com.ezbus.authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ezbus.R;
import com.ezbus.client.Pocket;
import com.ezbus.main.SharedPref;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.ezbus.main.MainActivity.navigationView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    public static GoogleSignInClient mGoogleSignInClient;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText editTextEmail;
    private EditText editTextPassword;
    private User newUser;
    SharedPref sharedpref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);
        if (sharedpref.loadNightModeState())
            setTheme(R.style.App_Dark);
        else setTheme(R.style.App_Green);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        editTextEmail = findViewById(R.id.emailCompany);
        editTextPassword = findViewById(R.id.passwordCompany);

        if (sharedpref.isClient()) {
            findViewById(R.id.loginCompany).setVisibility(View.GONE);
            findViewById(R.id.signInUser).setVisibility(View.VISIBLE);
            findViewById(R.id.signInUser).setOnClickListener(this);
        } else {
            findViewById(R.id.signInUser).setVisibility(View.GONE);
            findViewById(R.id.loginCompany).setVisibility(View.VISIBLE);
            findViewById(R.id.signInCompany).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginCompany();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        final GoogleSignInAccount account = acct;
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    final String uid = user.getUid();
                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("clients").child(uid);
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newUser = new Client();
                            if (dataSnapshot.exists()) {
                                newUser = dataSnapshot.getValue(Client.class);
                                ProfileActivity.setUser(mAuth.getCurrentUser(), sharedpref.getQuery(),LoginActivity.this);
                            } else {
                                newUser = new Client(account.getGivenName(), account.getFamilyName(), account.getEmail(), new Pocket());
                                newUser.setUid(uid);
                                rootRef.setValue(newUser);
                                ProfileActivity.setUser(mAuth.getCurrentUser(), sharedpref.getQuery(),LoginActivity.this);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                }
                updateUI(user);
                }
            });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginCompany() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!email.equals("") && !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (task.isSuccessful()) {
                        ProfileActivity.setUser(currentUser, sharedpref.getQuery(), LoginActivity.this);
                    } else {
                        Toast.makeText(LoginActivity.this, "Credenziali errate", Toast.LENGTH_SHORT).show();
                    }
                    updateUI(currentUser);
                    }
                });
        }

    }

    /* Utile perchè forse rimuove proprio dal database l'utente
    private void revokeAccess() {
        mAuth.signOut();
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }*/

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //Se l'user è loggato
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_register).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_profilo).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            View headerLayout = navigationView.getHeaderView(0);
            TextView navUsername =  headerLayout.findViewById(R.id.textView);
            navUsername.setText(user.getEmail());
            Intent resultIntent = new Intent();
            resultIntent.putExtra("User", user.getUid());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            //Se non è loggato
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signInUser)
            signIn();
    }

}