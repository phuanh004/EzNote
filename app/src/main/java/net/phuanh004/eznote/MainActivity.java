package net.phuanh004.eznote;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.phuanh004.eznote.Fragments.AllChatFragment;
import net.phuanh004.eznote.Fragments.AllNoteFragment;
import net.phuanh004.eznote.Helper.CircleTransform;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    @BindView(R.id.fab) public FloatingActionButton addFloatingActionButton;

    boolean doubleBackToExitPressedOnce = false;
    public NavigationView navigationView;

    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseAuth firebaseAuth;
    public DatabaseReference mDatabase;

    private TextView menuEmailTextView;
    private TextView menuDisplayNameTextView;
    private ImageView menuAvatarImg;

    public String currentuser;

    public int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        menuEmailTextView = (TextView) header.findViewById(R.id.menuEmailTextView);
        menuDisplayNameTextView = (TextView) header.findViewById(R.id.menuDisplayNameTextView);
        menuAvatarImg = (ImageView) header.findViewById(R.id.menuAvatarImg);

        Fragment fragment = new AllNoteFragment();
        openFragment(fragment);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentuser = firebaseAuth.getCurrentUser().getUid();
        setAuthListener();

        menuAvatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.fab)
    public void addButtonClick(FloatingActionButton fab){
        Intent intent = null;
        switch (currentFragment){
            case 1:
//                fab.setImageResource(R.drawable.add);
                intent = new Intent(MainActivity.this, NoteManageActivity.class);
                break;
            case 2:
//                fab.setImageResource(R.drawable.camera);
                intent = new Intent(MainActivity.this, ChatActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
    }

    private void setAuthListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mDatabase.child("Users").child(user.getUid().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Map<String,String> map = (Map)dataSnapshot.getValue();
//                            String name = map.get("name");
//                            menuDisplayNameTextView.setText(name);
//                            String avatar = map.get("avatar");
                            menuDisplayNameTextView.setText(dataSnapshot.child("name").getValue().toString());
//                            Glide.with(MainActivity.this)
//                                    .load(dataSnapshot.child("avatar").getValue().toString())
//                                    .transform(new CircleTransform(MainActivity.this))
//                                    .into(menuAvatarImg);
//                            menuDisplayNameTextView.setText(dataSnapshot.child("avatar").getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    menuEmailTextView.setText(user.getEmail());
                } else {
                    currentuser = null;
                    MainActivity.this.finish();
                }
            }
        };

        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    private void openFragment(final Fragment fragment)   {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        Fragment fragment;
        if (!item.isChecked()) {
            switch (id) {
                case R.id.nav_chat:
                    fragment = new AllChatFragment();
                    openFragment(fragment);
                    break;
                case R.id.nav_note:
                    fragment = new AllNoteFragment();
                    openFragment(fragment);
                    break;
            }
        }

        if (id == R.id.nav_log_out) {
            firebaseAuth.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}