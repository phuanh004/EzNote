package net.phuanh004.eznote;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    EditText etEmails,etPasss,etNames,etPhones;
    Button btnSignUp;
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


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });



    }

    private void SignUp(){
        String User = etEmails.getText().toString();
        String Pass = etPasss.getText().toString();
        mAuth.createUserWithEmailAndPassword(User, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String Name = etNames.getText().toString();
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("Users").child(mAuth.getCurrentUser().getUid().toString()).child("username").setValue(Name);
                            Intent intent = new Intent(SignUpActivity.this,NotesActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(SignUpActivity.this, "Failed!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }
}
