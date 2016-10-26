package net.phuanh004.eznote.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.phuanh004.eznote.Adapter.NoteHolder;
import net.phuanh004.eznote.Helper.RecyclerItemClickListener;
import net.phuanh004.eznote.Models.Note;
import net.phuanh004.eznote.MainActivity;
import net.phuanh004.eznote.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllNoteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    public DatabaseReference mDatabase;

    private List<String> listNoteKeys;
    private String currentuser;

    private DateFormat simpleDateFormat;

    public AllNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_note, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).navigationView.setCheckedItem(R.id.nav_note);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        currentuser = ((MainActivity)getActivity()).currentuser;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // Des Item
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.allNoteRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onLongItemClick(View view, int position) {
                showDeleteDialog(listNoteKeys.get(position));
            }
        }));

        listNoteKeys = new ArrayList<>();
        setAdapter();

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

//                viewHolder.addRemoveNoteCardClick(getActivity(), listNoteKeys.get(position));
                viewHolder.addEditNoteCardClick(getActivity(), listNoteKeys.get(position), model.getTitle(), model.getContent());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public void showDeleteDialog(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
}
