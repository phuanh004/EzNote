package net.phuanh004.eznote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.phuanh004.eznote.Models.User;

import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {
    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    public String currentuser;
    TextView textviewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser().getUid();
        textviewName = (TextView)findViewById(R.id.textviewName);
        if(mAuth.getCurrentUser() != null){
            mDatabase.child("Users").child(currentuser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String,String> map = (Map)dataSnapshot.getValue();
                    String name = map.get("name");
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle(name);
                    textviewName.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Update Avatar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


}
