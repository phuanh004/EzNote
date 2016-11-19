package net.phuanh004.eznote.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.phuanh004.eznote.ChatActivity;
import net.phuanh004.eznote.Helper.CircleTransform;
import net.phuanh004.eznote.Models.Note;
import net.phuanh004.eznote.Models.User;
import net.phuanh004.eznote.NoteManageActivity;
import net.phuanh004.eznote.R;
import net.phuanh004.eznote.ShareActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by huu on 19/11/16.
 */

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.MyViewHolder> {
    private Context mContext;
    private List<User> userList;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    public String currentuser;

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatarCardChatImageView) ImageView img;
        @BindView(R.id.nameCardChatTextView) TextView nameCardChatTextView;
        @BindView(R.id.lastMessageCardChatTextView) TextView lastMessageCardChatTextView;
        @BindView(R.id.chatCardView) CardView chatCardView;


        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }


    public ShareAdapter(Context mContext, List<User> userList) {
        this.mContext = mContext;
        this.userList = userList;
    }

    @Override
    public ShareAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_chat, parent, false);

        return new ShareAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShareAdapter.MyViewHolder holder, final int position) {
        holder.nameCardChatTextView.setText(userList.get(position).getName());
        holder.lastMessageCardChatTextView.setText(userList.get(position).getEmail());
        Glide.with(mContext).load(userList.get(position).getAvatar()).centerCrop().override(200,200).transform(new CircleTransform(mContext)).into(holder.img);
        holder.chatCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                currentuser = firebaseAuth.getCurrentUser().getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                Bundle noteBundle = ((Activity)(mContext)).getIntent().getBundleExtra("note1");
                final String noteid = noteBundle.getString("id");
                Log.d("chuot",noteid);
                mDatabase.child("Users").child(currentuser).child("notes").child(noteid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Note note = dataSnapshot.getValue(Note.class);
                        mDatabase.child("Users").child(userList.get(position).getUserId()).child("notes").child(noteid).setValue(note);
                        Log.d("chuot",userList.get(position).getUserId());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
