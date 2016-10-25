package net.phuanh004.eznote;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private Bundle noteBundle;
    private String noteid;
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

        noteBundle = getIntent().getBundleExtra("note");

        noteHeaderEditText = (EditText) findViewById(R.id.noteHeaderEditText);
        noteContentEditText = (EditText) findViewById(R.id.noteContentEditText);

        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (noteBundle != null) {
            noteid = noteBundle.getString("id");
            String noteTitle = noteBundle.getString("title");
            String noteContent = noteBundle.getString("content");

            noteHeaderEditText.setText(noteTitle);
            noteContentEditText.setText(noteContent);

            mDatabase.child("Users").child(currentuser).child("notes").child(noteid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                    noteHeaderEditText.setText( dataSnapshot.child("title").getValue().toString() );
//                    noteContentEditText.setText( dataSnapshot.child("content").getValue().toString() );
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Toast.makeText(NoteManageActivity.this, "This note was removed", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
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

        Calendar cal = Calendar.getInstance();
        timeZone = cal.getTimeZone().getID();
        currentTime = cal.getTimeInMillis() /1000L;

        note.setTitle("");
        note.setSavedTime(currentTime);
        note.setTimeZone(timeZone);

        if (!noteContentEditText.getText().toString().isEmpty()){
            note.setContent(noteContentEditText.getText().toString());
            note.setTitle(noteHeaderEditText.getText().toString());

            if(noteid != null){
                mDatabase.child("Users").child(currentuser).child("notes").child(noteid).child("title").setValue(note.getTitle());
                mDatabase.child("Users").child(currentuser).child("notes").child(noteid).child("content").setValue(note.getContent());
            }else {
                mDatabase.child("Users").child(currentuser).child("notes").push().setValue(note);
            }
        } else {
            Toast.makeText(NoteManageActivity.this, "Can't save empty note",Toast.LENGTH_SHORT).show();
        }
    }
}
