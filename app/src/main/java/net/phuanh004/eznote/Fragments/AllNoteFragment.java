package net.phuanh004.eznote.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.phuanh004.eznote.Adapter.NoteHolder;
import net.phuanh004.eznote.Helper.RecyclerItemClickListener;
import net.phuanh004.eznote.MainActivity;
import net.phuanh004.eznote.Models.Note;
import net.phuanh004.eznote.NoteManageActivity;
import net.phuanh004.eznote.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllNoteFragment extends Fragment {

    @BindView(R.id.allNoteProgressBar) ProgressBar allNoteProgressBar;
    @BindView(R.id.allNoteRecyclerView) RecyclerView recyclerView;
    public DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;

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
        ((MainActivity)getActivity()).currentFragment = 1;

        ButterKnife.bind(this, view);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentuser = ((MainActivity)getActivity()).currentuser;


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // Des Item
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int position) {
                DatabaseReference thisNote = adapter.getRef(position);
                Bundle bundle = new Bundle();
                bundle.putString("id", thisNote.getKey());
//                bundle.putString("title", thisNote.child("title").toString());
//                bundle.putString("content", thisNote.child("content").toString());

                Intent intent = new Intent(getActivity(), NoteManageActivity.class);
                intent.putExtra("note",bundle);
                getActivity().startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, final int position) {
//                showDeleteDialog(adapter.getRef(position).getKey());
//                Toast.makeText(getActivity(), adapter.getRef(position).getKey(), Toast.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar
                        .make(view, getString(R.string.dialog_message_delete), Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.RED)
                        .setAction("DELETE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDatabase.child("Users").child(currentuser).child("notes").child(adapter.getRef(position).getKey()).removeValue();
                            }
                        });

                snackbar.show();
            }
        }));

        setAdapter();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allNoteProgressBar.setVisibility(View.INVISIBLE);
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
                simpleDateFormat = SimpleDateFormat.getDateInstance();
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone(model.getTimeZone()));

                viewHolder.setTitle(model.getTitle());
                viewHolder.setTime(simpleDateFormat
                        .format(new Date(model.getSavedTime() * 1000L)));

                viewHolder.setContent(model.getContent());
                if (model.getImages() != null) {
                    viewHolder.setImage(model.getImages().entrySet().iterator().next().getValue());
                }


//                viewHolder.addEditNoteCardClick(getActivity(),  adapter.getRef(position), model);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void showDeleteDialog(final String id) {
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
