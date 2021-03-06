package net.phuanh004.eznote;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.phuanh004.eznote.Adapter.ShareAdapter;
import net.phuanh004.eznote.Helper.RecyclerItemClickListener;
import net.phuanh004.eznote.Models.Note;
import net.phuanh004.eznote.Models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    @BindView(R.id.friendsRecyclerView) RecyclerView recyclerView;
    public static final String TAG = "ShareActivity";
    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    public String currentuser;
    private List<User> userList;
    private ShareAdapter adapter;
    private String noteid;
    private int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("chuot",currentuser);

        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ShareActivity.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        userList = new ArrayList<>();
        adapter = new ShareAdapter(this, userList);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        setAdapter();

//        recyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View view,int position) {
//                        int a = position;
//                        Bundle noteBundle = getIntent().getBundleExtra("note1");
//                        noteid = noteBundle.getString("id");
//                        Log.d("chuot",noteid);
//                        mDatabase.child("Users").child(currentuser).child("notes").child(noteid).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Note note = dataSnapshot.getValue(Note.class);
//                                //mDatabase.child("Users").child(userList.get(position).getUserId()).child("notes").setValue(note);
//                                Log.d("chuot",userList.get(a).getUserId());
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//
//                    }
//                })
//        );
    }

    private void setAdapter(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        ref.orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
//                    keysList.add(data.getKey());
                    userList.add(data.getValue(User.class));
                    userList.get(userList.size()-1).setUserId(data.getKey());
                    adapter.notifyItemInserted(userList.size() - 1);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setIconified(false);
        searchItem.expandActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
//        final List<User> filteredModelList = filter(userList, query);
//        adapter.edit
//                .edit()
//                .replaceAll(filteredModelList)
//                .commit();
//        mBinding.recyclerView.scrollToPosition(0);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() == 0){ recyclerView.setVisibility(View.INVISIBLE); }
        if (newText.length() == 1){ recyclerView.setVisibility(View.VISIBLE); }
        newText = newText.toString().toLowerCase();

        final List<User> filteredList = new ArrayList<>();

        for (int i = 0; i < userList.size(); i++) {

            final String text = userList.get(i).getName().toLowerCase();
            if (text.contains(newText)) {
                filteredList.add(userList.get(i));
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(ShareActivity.this));
        adapter = new ShareAdapter(ShareActivity.this, filteredList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    //    private static List<User> filter(List<User> models, String query) {
//        final String lowerCaseQuery = query.toLowerCase();
//
//        final List<User> filteredModelList = new ArrayList<>();
//        for (User model : models) {
//            final String text = model.getEmail().toLowerCase();
//            if (text.contains(lowerCaseQuery)) {
//                filteredModelList.add(model);
//            }
//        }
//        return filteredModelList;
//    }
}
