package net.phuanh004.eznote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.phuanh004.eznote.Adapter.AllNoteAdapter;
import net.phuanh004.eznote.Adapter.NoteHolder;
import net.phuanh004.eznote.Models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NotesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private List<Note> noteList;
    private Note note;
//    private AllNoteAdapter adapter;
    private FirebaseRecyclerAdapter adapter;
    private Button btnSignOut;

    private long currentTime;
    private String currentuser;
    private String timeZone;

    private DateFormat simpleDateFormat;
    private List<String> listNoteKeys;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(NotesActivity.this, NoteManageActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnSignOut = (Button)findViewById(R.id.btnSignOut);

        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser().getUid();

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("^^^", "onAuthStateChanged:signed_in:" + currentuser);

                } else {
                    // User is signed out
                    noteList = null;
                    currentuser = null;

                    NotesActivity.this.finish();
                    Log.d("^^^", "onAuthStateChanged:signed_out");
                }
            }
        };

        firebaseAuth.addAuthStateListener(mAuthListener);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        noteList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.allNoteRecyclerView);

//        adapter = new AllNoteAdapter(NotesActivity.this,noteList);
//
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(adapter);
        setData();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FirebaseRecyclerAdapter<Note, NoteHolder>(Note.class, R.layout.note_card, NoteHolder.class, mDatabase.child("Users").child(currentuser).child("notes")) {
            @Override
            protected void populateViewHolder(NoteHolder viewHolder, Note model, int position) {
                simpleDateFormat = SimpleDateFormat.getTimeInstance();
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone(model.getTimeZone()));

                viewHolder.setTitle(model.getTitle());
                viewHolder.setTime(simpleDateFormat
                        .format( new Date(model.getSavedTime()*1000L) ));
                viewHolder.setContent(model.getContent());

                viewHolder.addRemoveCard(NotesActivity.this, listNoteKeys.get(position));
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void setData(){

        listNoteKeys = new ArrayList<>();
//        Log.d("^^^^", "setData: "+mDatabase.child("Notes").orderByChild("savedTime"));

        mDatabase.child("Users").child(currentuser).child("notes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                note = new Note();

                listNoteKeys.add(dataSnapshot.getKey());
//                Log.d("^^^^", "onChildAdded: "+ dataSnapshot.getKey());
//                note = dataSnapshot.getValue(Note.class);
//                note.setNoteId(dataSnapshot.getKey());
//
//                noteList.add(note);
//                adapter.notifyItemInserted(noteList.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("^^^^", "onChildRemoved: " + dataSnapshot.getKey());
                listNoteKeys.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        note = new Note();
//
//        Calendar cal = Calendar.getInstance();
//        timeZone = cal.getTimeZone().getID();
//        currentTime = cal.getTimeInMillis() /1000L;

//        note.setTitle("Note header 1");
//        note.setSavedTime(currentTime);
//        note.setTimeZone(timeZone);

//        noteList.add(note);

//        Set value
//        DatabaseReference mDatabaseNotePush = mDatabase.child("Notes").push();
//        mDatabaseNotePush.setValue(note);

//       mDatabase.child("Users").child(currentuser).child("notes").push().setValue(note);


//        note = new Note();
//        note.setTitle("Note header 2");
//        note.setSavedTime(1456977906);
//        noteList.add(note);
//
//        note = new Note();
//        note.setTitle("Note header 3");
//        note.setSavedTime(1456985104);
//        noteList.add(note);

//        adapter.notifyDataSetChanged();
    }

    public void showDeleteDialog(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
//        builder.setTitle(getString(R.string.dialog_title));

        builder.setMessage(getString(R.string.dialog_message_delete));

        String positiveText = getString(R.string.delete);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        mDatabase.child("Users").child(currentuser).child("notes").child(id).removeValue();

//                        Log.d("^^^^", "onClick: "+id);
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();

            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
                this.finishAffinity();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}