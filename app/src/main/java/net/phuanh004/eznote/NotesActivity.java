package net.phuanh004.eznote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.phuanh004.eznote.Adapter.NoteHolder;
import net.phuanh004.eznote.Models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NotesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;

    private RecyclerView recyclerView;
    private View header;
    private TextView menuEmailTextView;
    private TextView menuDisplayNameTextView;

    private String currentuser;
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

        FloatingActionButton addNoteBtn = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);

        navigationView.setCheckedItem(R.id.nav_note);
        menuEmailTextView = (TextView)header.findViewById(R.id.menuEmailTextView);
        menuDisplayNameTextView = (TextView)header.findViewById(R.id.menuDisplayNameTextView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(NotesActivity.this);
        // Des Item
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView = (RecyclerView) findViewById(R.id.allNoteRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentuser = firebaseAuth.getCurrentUser().getUid();
        setAuthListener();

        listNoteKeys = new ArrayList<>();
        setAdapter();

        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotesActivity.this, NoteManageActivity.class);
                startActivity(intent);
            }
        });

        mDatabase.child("Users").child(currentuser).child("notes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                listNoteKeys.add(dataSnapshot.getKey());
//                recyclerView.smoothScrollToPosition(listNoteKeys.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listNoteKeys.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setAuthListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    menuDisplayNameTextView.setText(user.getDisplayName());
                    menuEmailTextView.setText(user.getEmail());
                } else {
                    currentuser = null;
                    NotesActivity.this.finish();
                }
            }
        };

        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    private void setAdapter(){

        adapter = new FirebaseRecyclerAdapter<Note, NoteHolder>(Note.class, R.layout.note_card, NoteHolder.class, mDatabase.child("Users").child(currentuser).child("notes")) {
            @Override
            protected void populateViewHolder(NoteHolder viewHolder, Note model, int position) {
                simpleDateFormat = SimpleDateFormat.getTimeInstance();
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone(model.getTimeZone()));

                viewHolder.setTitle(model.getTitle());
                viewHolder.setTime(simpleDateFormat
                        .format( new Date(model.getSavedTime()*1000L) ));
                viewHolder.setContent(model.getContent());

                viewHolder.addRemoveNoteCardClick(NotesActivity.this, listNoteKeys.get(position));
                viewHolder.addEditNoteCardClick(NotesActivity.this, listNoteKeys.get(position), model.getTitle(), model.getContent());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public void showDeleteDialog(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);

        builder.setMessage(getString(R.string.dialog_message_delete));

        String positiveText = getString(R.string.delete);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child("Users").child(currentuser).child("notes").child(id).removeValue();
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
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                this.finishAffinity();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.press_back_to_exit, Toast.LENGTH_SHORT).show();

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

        if (id == R.id.nav_log_out) {
            firebaseAuth.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}