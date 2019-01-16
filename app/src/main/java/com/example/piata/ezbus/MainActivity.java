package com.example.piata.ezbus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private MapFragment mapFragment;
    private PocketFragment pocketFragment;
    private BuyFragment buyFragment;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    static NavigationView navigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tab1:
                    setFragment(1);
                    return true;
                case R.id.tab2:
                    if (LoginActivity.mAuth.getInstance().getCurrentUser()==null) {
                        Intent login = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(login);
                        overridePendingTransition(R.transition.fadein, R.transition.fadeout);
                        return false;
                    } else {
                        setFragment(2);
                        return true;
                    }
                case R.id.tab3:
                    if (LoginActivity.mAuth.getInstance().getCurrentUser()==null) {
                        Intent login = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(login);
                        return false;
                    } else {
                        setFragment(3);
                        return true;
                    }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainNav = findViewById(R.id.main_nav);
        mMainFrame = findViewById(R.id.main_frame);
        mapFragment = new MapFragment();
        pocketFragment = new PocketFragment();
        buyFragment = new BuyFragment();

        mDrawerLayout = findViewById(R.id.drag_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //Decommentare per barra menu personalizzata
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        TextView navUsername =  headerLayout.findViewById(R.id.textView);

        if (LoginActivity.mAuth.getInstance().getCurrentUser() == null) {
            navUsername.setText("Ospite");
            MainActivity.navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            MainActivity.navigationView.getMenu().findItem(R.id.nav_register).setVisible(true);
            MainActivity.navigationView.getMenu().findItem(R.id.nav_profilo).setVisible(false);
            MainActivity.navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        } else {
            navUsername.setText(LoginActivity.mAuth.getInstance().getCurrentUser().getEmail());
            MainActivity.navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            MainActivity.navigationView.getMenu().findItem(R.id.nav_register).setVisible(false);
            MainActivity.navigationView.getMenu().findItem(R.id.nav_profilo).setVisible(true);
            MainActivity.navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        }

        setFragment(1);
        BottomNavigationView navigation = findViewById(R.id.main_nav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void setFragment(int fragmentId) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(fragmentId) {
            case 1:
                if(fragmentManager.findFragmentByTag("one") != null) {
                    //Se il fragment esiste, viene mostrato
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("one")).commit();
                } else {
                    //Se il fragment non esiste, lo crea
                    fragmentManager.beginTransaction().add(R.id.main_frame, new MapFragment(), "one").commit();
                }
                //Se altri fragment sono visibili, vengono nascosti
                if(fragmentManager.findFragmentByTag("two") != null){
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("two")).commit();
                }
                if(fragmentManager.findFragmentByTag("three") != null){
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("three")).commit();
                }
                break;
            case 2:
                if(fragmentManager.findFragmentByTag("two") != null) {
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("two")).commit();
                } else {
                    fragmentManager.beginTransaction().add(R.id.main_frame, new PocketFragment(), "two").commit();
                }
                if(fragmentManager.findFragmentByTag("one") != null){
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("one")).commit();
                }
                if(fragmentManager.findFragmentByTag("three") != null){
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("three")).commit();
                }
                break;
            case 3:
                if(fragmentManager.findFragmentByTag("three") != null) {
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("three")).commit();
                } else {
                    fragmentManager.beginTransaction().add(R.id.main_frame, new BuyFragment(), "three").commit();
                }
                if(fragmentManager.findFragmentByTag("one") != null){
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("one")).commit();
                }
                if(fragmentManager.findFragmentByTag("two") != null){
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("two")).commit();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_profilo) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_logout) {
            AlertDialog.Builder logout = new AlertDialog.Builder(MainActivity.this);
            logout.setMessage("Vuoi davvero uscire?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Se utente accetta di uscire
                            setFragment(1);
                            mMainNav.setSelectedItemId(R.id.tab1);
                            LoginActivity.mAuth.getInstance().signOut();
                            LoginActivity.mGoogleSignInClient.signOut();
                            TextView navUsername =  navigationView.getHeaderView(0).findViewById(R.id.textView);
                            navUsername.setText("Ospite");
                            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
                            navigationView.getMenu().findItem(R.id.nav_register).setVisible(true);
                            navigationView.getMenu().findItem(R.id.nav_profilo).setVisible(false);
                            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Se annulla l'operazione
                        }
                    });
            logout.show();

        }
        if (id == R.id.nav_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drag_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}