package net.phuanh004.eznote;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


        mAuth = FirebaseAuth.getInstance();
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        layoutPass = (TextInputLayout) findViewById(R.id.layoutPass);
        layoutEmail = (TextInputLayout) findViewById(R.id.layoutEmail);

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User = etEmail.getText().toString();
                String Pass = etPass.getText().toString();
                if (User.equals("")) {
                    layoutEmail.setError("Email is required");
                }else if (!isValidEmailAddress(User)){
                    layoutEmail.setError("Email is vaild");
                }else{
                    layoutEmail.setError(null);
                }

                if (Pass.equals("")) {
                    layoutPass.setError("Password is required");
                }else if(Pass.length() < 6){
                    layoutPass.setError("Password must be of minimum 6 characters");
                }else {
                    layoutPass.setError(null);
                }
                if(isValidEmailAddress(User) && Pass.length() >=6 ){
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


    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void Login() {
        String User = etEmail.getText().toString();
        String Pass = etPass.getText().toString();
        mAuth.signInWithEmailAndPassword(User, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }else {
                            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                            NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                            if (mWifi.isConnected() || mMobile.isConnected()) {
                                Toast.makeText(LoginActivity.this, "Incorrect email or password",
                                Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(LoginActivity.this, "No internet access",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }


                    }
                });


    }
}

