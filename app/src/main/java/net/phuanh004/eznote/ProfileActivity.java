package net.phuanh004.eznote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText tvsd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tvsd=(EditText) findViewById(R.id.tvsd);
        tvsd.setText(mAuth.getInstance().getCurrentUser().getDisplayName());
    }
}
