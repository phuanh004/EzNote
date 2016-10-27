package net.phuanh004.eznote;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        etEmails = (EditText)findViewById(R.id.etEmails);
        etPasss = (EditText)findViewById(R.id.etPasss);
        etNames = (EditText)findViewById(R.id.etNames);
        etPhones = (EditText)findViewById(R.id.etPhones);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        layoutPasss = (TextInputLayout) findViewById(R.id.layoutPasss);
        layoutEmails = (TextInputLayout) findViewById(R.id.layoutEmails);
        layoutNames = (TextInputLayout) findViewById(R.id.layoutNames);
        layoutPhones = (TextInputLayout) findViewById(R.id.layoutPhones);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Emails = etEmails.getText().toString();
                String Passs = etPasss.getText().toString();
                String Names = etNames.getText().toString();
                String Phones = etPhones.getText().toString();
                if(Emails.equals("") && Passs.equals("") && Names.equals("") && Phones.equals("")){
                    layoutEmails.setError("Email is required");
                    layoutPasss.setError("Password is required");
                    layoutNames.setError("Name is required");
                    layoutPhones.setError("Phone is required");
                }else {
                    SignUp();
                }
            }
        });





    }

    private void SignUp(){
        final String User = etEmails.getText().toString();
        final String Pass = etPasss.getText().toString();
        final String Name = etNames.getText().toString();
        final String Phone = etPhones.getText().toString();
        mAuth.createUserWithEmailAndPassword(User, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            User mUser = new User(Name,User,Pass,Phone,"null");
                            mDatabase.child("Users").setValue(mUser);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(Name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(SignUpActivity.this, "Failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
