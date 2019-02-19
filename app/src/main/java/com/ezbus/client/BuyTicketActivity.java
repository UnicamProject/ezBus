package com.ezbus.client;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ezbus.R;
import com.ezbus.authentication.ProfileActivity;
import com.ezbus.main.SharedPref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BuyTicketActivity extends AppCompatActivity {

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedpref = new SharedPref(this);
        if (sharedpref.loadNightModeState())
            setTheme(R.style.App_Dark);
        else setTheme(R.style.App_Green);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);

        String idStart = getIntent().getSerializableExtra("Start").toString();
        String idDest = getIntent().getSerializableExtra("Dest").toString();
        TextView start = findViewById(R.id.partenza);
        TextView dest = findViewById(R.id.destinazione);
        start.setText(idStart);
        dest.setText(idDest);
        Button buyTicket = findViewById(R.id.buyTicket);
        buyTicket.setOnClickListener(v -> database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.child("stops").getChildren()) {
                    if (child.child("id").getValue().toString().equals(idStart)){
                        String idCompany = child.child("companyId").getValue().toString();
                        Ticket newTicket = new Ticket(idCompany, 5, 30, idStart, idDest);
                        if (ProfileActivity.getClient().getMyPocket().getCredit()>=newTicket.getPrice()) {
                            ProfileActivity.getClient().getMyPocket().addTicket(newTicket);
                            Toast.makeText(getApplicationContext(), "Biglietto acquistato", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(getApplicationContext(), "Credito insufficiente", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}