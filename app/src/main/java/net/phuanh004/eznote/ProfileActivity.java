package net.phuanh004.eznote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
    TextView tvName,tvEmail,tvPhone;
    ImageView iveName,iveEmail,ivePhone,ivavatar,ivePass;
    final Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser().getUid();
        tvName = (TextView)findViewById(R.id.tvName);
        tvEmail = (TextView)findViewById(R.id.tvEmail);
        tvPhone = (TextView)findViewById(R.id.tvPhone);
        iveName = (ImageView)findViewById(R.id.iveName);
        iveEmail = (ImageView)findViewById(R.id.iveEmail);
        ivePhone = (ImageView)findViewById(R.id.ivePhone);
        ivePass = (ImageView)findViewById(R.id.ivePass);
        if(mAuth.getCurrentUser() != null){
            mDatabase.child("Users").child(currentuser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String,String> map = (Map)dataSnapshot.getValue();
                    String name = map.get("name");
                    String email = map.get("email");
                    String phone = map.get("phone");
                    tvName.setText(name);
                    tvEmail.setText(email);
                    tvPhone.setText(phone);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        iveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogName();
            }
        });

        iveEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEmail();
            }
        });
        ivePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDialogPhone();
            }
        });
        ivePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPass();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Update Avatar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    public void showDialogName(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_name, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText updateName = (EditText) mView.findViewById(R.id.updateName);
        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (updateName.getText().toString().equals("")) {

                        }else{
                            mDatabase.child("Users").child(currentuser).child("name").setValue(updateName.getText().toString());
                        }
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public void showDialogEmail(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_email, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText updateEmail = (EditText) mView.findViewById(R.id.updateEmail);
        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (updateEmail.getText().toString().equals("")) {

                        }else if (!isValidEmailAddress(updateEmail.getText().toString())){

                        }else{
                            mDatabase.child("Users").child(currentuser).child("email").setValue(updateEmail.getText().toString());
                        }


                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    public void showDialogPhone(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_phone, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText updatePhone = (EditText) mView.findViewById(R.id.updatePhone);
        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (updatePhone.getText().toString().equals("")) {

                        }else{
                            mDatabase.child("Users").child(currentuser).child("phone").setValue(updatePhone.getText().toString());
                        }


                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    public void showDialogPass(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_pass, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText updatePass = (EditText) mView.findViewById(R.id.updatePass);
        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (updatePass.getText().toString().equals("")) {

                        }else if (updatePass.getText().toString().length()<6){

                        }else{
                            Toast.makeText(ProfileActivity.this,"OK",Toast.LENGTH_SHORT).show();
                        }


                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
