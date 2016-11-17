package net.phuanh004.eznote;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.phuanh004.eznote.Models.User;


public class SignUpActivity extends AppCompatActivity {
    EditText etEmails,etPasss,etNames,etPhones;
    Button btnSignUp;
    TextInputLayout layoutNames,layoutEmails,layoutPhones,layoutPasss;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        etEmails = (EditText) findViewById(R.id.etEmails);
        etPasss = (EditText) findViewById(R.id.etPasss);
        etNames = (EditText) findViewById(R.id.etNames);
        etPhones = (EditText) findViewById(R.id.etPhones);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        layoutPasss = (TextInputLayout) findViewById(R.id.layoutPasss);
        layoutEmails = (TextInputLayout) findViewById(R.id.layoutEmails);
        layoutNames = (TextInputLayout) findViewById(R.id.layoutNames);
        layoutPhones = (TextInputLayout) findViewById(R.id.layoutPhones);
        progressBar.setVisibility(View.INVISIBLE);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Emails = etEmails.getText().toString();
                String Passs = etPasss.getText().toString();
                String Names = etNames.getText().toString();
                String Phones = etPhones.getText().toString();
                if (Passs.equals("")) {
                    layoutPasss.setError("Password is required");
                }else if(Passs.length() < 6){
                    layoutPasss.setError("Password must be of minimum 6 characters");
                }else {
                    layoutPasss.setError(null);
                }

                if (Phones.equals("")) {
                    layoutPhones.setError("Phone is required");
                }else {
                    layoutPhones.setError(null);
                }

                if (Names.equals("")) {
                    layoutNames.setError("Name is required");
                }else {
                    layoutNames.setError(null);
                }

                if (Emails.equals("")) {
                    layoutEmails.setError("Email is required");
                }else if (!isValidEmailAddress(Emails)){
                    layoutEmails.setError("Email is vaild");
                }else{
                    layoutEmails.setError(null);
                }

                if(isValidEmailAddress(Emails) && Passs.length() >= 6 && !Phones.equals("") && !Names.equals("")){
                    layoutNames.setError(null);
                    layoutEmails.setError(null);
                    layoutPasss.setError(null);
                    layoutPhones.setError(null);
                    SignUp();
                }
            }
        });

    }


    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void SignUp(){
        etEmails.setVisibility(View.INVISIBLE);
        etPasss.setVisibility(View.INVISIBLE);
        etNames.setVisibility(View.INVISIBLE);
        etPhones.setVisibility(View.INVISIBLE);
        btnSignUp.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        layoutPasss.setVisibility(View.INVISIBLE);
        layoutEmails.setVisibility(View.INVISIBLE);
        layoutNames.setVisibility(View.INVISIBLE);
        layoutPhones.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        final String Email = etEmails.getText().toString();
        String Pass = etPasss.getText().toString();
        final String Name = etNames.getText().toString();
        final String Phone = etPhones.getText().toString();
        mAuth.createUserWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            User mUser = new User(Name,Email,Phone,null);
                            mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(mUser);
                            Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                            startActivity(intent);
                        }else {
                            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                            NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                            if (mWifi.isConnected() || mMobile.isConnected()) {
                                etEmails.setVisibility(View.VISIBLE);
                                etPasss.setVisibility(View.VISIBLE);
                                etNames.setVisibility(View.VISIBLE);
                                etPhones.setVisibility(View.VISIBLE);
                                btnSignUp.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.VISIBLE);
                                layoutPasss.setVisibility(View.VISIBLE);
                                layoutEmails.setVisibility(View.VISIBLE);
                                layoutNames.setVisibility(View.VISIBLE);
                                layoutPhones.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(SignUpActivity.this, "Email is already registered",
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(SignUpActivity.this, "No internet access",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
