package net.phuanh004.eznote.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.phuanh004.eznote.Adapter.ChatHolder;
import net.phuanh004.eznote.MainActivity;
import net.phuanh004.eznote.Models.Conversation;
import net.phuanh004.eznote.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllChatFragment extends Fragment {
    
    public static final String TAG = "AllChat";
    
    @BindView(R.id.allChatProgressBar) ProgressBar allNoteProgressBar;
    @BindView(R.id.allChatRecyclerView) RecyclerView recyclerView;
    public DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;

    private String currentuser;

    public AllChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_all_chat, container, false);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).navigationView.setCheckedItem(R.id.nav_chat);
        ((MainActivity)getActivity()).currentFragment = 2;

        ButterKnife.bind(this, view);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentuser = ((MainActivity)getActivity()).currentuser;
        ((MainActivity)getActivity()).setActionBarTitle(getString(R.string.chat_activity));

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allNoteProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        setAdapter();
    }

    private void setAdapter(){
        adapter = new FirebaseRecyclerAdapter<Conversation, ChatHolder>(Conversation.class, R.layout.card_chat, ChatHolder.class, mDatabase.child("Users").child(currentuser).child("chats")) {
            @Override
            protected void populateViewHolder(ChatHolder viewHolder, Conversation model, int position) {
                showChatItemDetail(viewHolder, model);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void showChatItemDetail(ChatHolder viewHolder, Conversation model){
        if (model.getLastMessage() == null) {
            return;
        }

        viewHolder.setName(model.getSender());
        if (model.getAvatar() != null) {
            viewHolder.setAvatar(model.getAvatar());
        }
        viewHolder.setLastMessage(model.getLastMessage());
        viewHolder.setClickListener(model.getSenderId());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recyclerView != null) {
            recyclerView.getRecycledViewPool().clear();
            adapter.cleanup();
        }
    }
}
