package net.phuanh004.eznote;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView tvSignUp;
    EditText etEmail,etPass;
    Button btnLogin;
    TextInputLayout layoutPass,layoutEmail;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getActionBar() != null){
            getActionBar().hide();
        }


        mAuth = FirebaseAuth.getInstance();
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        layoutPass = (TextInputLayout) findViewById(R.id.layoutPass);
        layoutEmail = (TextInputLayout) findViewById(R.id.layoutEmail);

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this,NotesActivity.class);
            startActivity(intent);
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User = etEmail.getText().toString();
                String Pass = etPass.getText().toString();
                if(Pass.equals("") && User.equals("")){
                    layoutEmail.setError("Email is required");
                    layoutPass.setError("Password is required");
                }
                else if(Pass.equals("")){
                    layoutEmail.setError(null);
                    layoutPass.setError("Password is required");

                }
                else if(User.equals("")){
                    layoutEmail.setError("Email is required");
                    layoutPass.setError(null);
                }
                else{
                    layoutEmail.setError(null);
                    layoutPass.setError(null);
                    Login();
                }
            }
        });



        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    private void Login() {
        String User = etEmail.getText().toString();
        String Pass = etPass.getText().toString();
        mAuth.signInWithEmailAndPassword(User, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this,NotesActivity.class);
                            startActivity(intent);
                        }else {
                            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                            if (mWifi.isConnected()) {
                                Toast.makeText(LoginActivity.this, "Incorrect email or password",
                                Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(LoginActivity.this, "Internet not access",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }


                    }
                });


    }
}

