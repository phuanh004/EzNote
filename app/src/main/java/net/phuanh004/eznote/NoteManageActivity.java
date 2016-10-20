package net.phuanh004.eznote;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.phuanh004.eznote.Models.Note;

import java.util.Calendar;

public class NoteManageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private EditText noteHeaderEditText;
    private EditText noteContentEditText;
    private Note note;

    private String currentuser;
    private long currentTime;
    private String timeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        noteHeaderEditText = (EditText) findViewById(R.id.noteHeaderEditText);
        noteContentEditText = (EditText) findViewById(R.id.noteContentEditText);

        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser().getUid();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
//            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        note = new Note();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();
        timeZone = cal.getTimeZone().getID();
        currentTime = cal.getTimeInMillis() /1000L;

        note.setTitle("");
        note.setSavedTime(currentTime);
        note.setTimeZone(timeZone);

        if (!noteContentEditText.getText().toString().isEmpty()){
            Toast.makeText(NoteManageActivity.this, "Added",Toast.LENGTH_SHORT).show();
            note.setContent(noteContentEditText.getText().toString());

            if (!noteHeaderEditText.getText().toString().isEmpty()) { note.setTitle(noteHeaderEditText.getText().toString()); }
            else { note.setTitle(noteContentEditText.getText().toString());}
            mDatabase.child("Users").child(currentuser).child("notes").push().setValue(note);
        } else {
            Toast.makeText(NoteManageActivity.this, "Can't save empty note",Toast.LENGTH_SHORT).show();
        }
    }
}
