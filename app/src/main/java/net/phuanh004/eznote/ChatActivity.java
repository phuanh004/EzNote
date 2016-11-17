package net.phuanh004.eznote;

import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.phuanh004.eznote.Models.Chat;
import net.phuanh004.eznote.Models.Conversation;

import java.util.Calendar;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    public static final String TAG = "ChatActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private DatabaseReference mChatRef;
    private DatabaseReference mUserChatRef;
    private Button mSendButton;
    private EditText mMessageEdit;

    private RecyclerView mMessages;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Chat, ChatHolder> mRecyclerViewAdapter;

//    private String roomId;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final String receiverId = getIntent().getStringExtra("id");

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);

        mSendButton = (Button) findViewById(R.id.sendButton);
        mMessageEdit = (EditText) findViewById(R.id.messageEdit);

        mRef = FirebaseDatabase.getInstance().getReference();
        mUserChatRef = mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("chats").child(receiverId);

        mUserChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                dataSnapshot.getValue()
//                Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());
                if (dataSnapshot.getValue() == null){
                    DatabaseReference chatRef = mRef.child("Chat").push();
                    mUserChatRef.child("room").setValue(chatRef.getKey());
//                    roomId = chatRef.getKey();
                    mRef.child("Users").child(receiverId).child("chats").child(mAuth.getCurrentUser().getUid()).child("room").setValue(chatRef.getKey());
                    mChatRef = mRef.child("Chats").child(chatRef.getKey());

                }else {
                    mChatRef = mRef.child("Chats").child(dataSnapshot.child("room").getValue().toString());
                    attachRecyclerViewAdapter();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Objects.equals(mMessageEdit.getText().toString(), "")) {
                    final String uid = mAuth.getCurrentUser().getUid();

                    mRef.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Calendar cal = Calendar.getInstance();
                            String timeZone = cal.getTimeZone().getID();
                            long currentTime = cal.getTimeInMillis() / 1000L;

                            Chat chat = new Chat(dataSnapshot.child("name").getValue().toString(), uid, mMessageEdit.getText().toString());
                            final Conversation conversation = new Conversation();

                            conversation.setAvatar(dataSnapshot.child("avatar").getValue().toString());
                            conversation.setSender(dataSnapshot.child("name").getValue().toString());
                            conversation.setRoom(dataSnapshot.child("chats").child(receiverId).child("room").getValue().toString());
                            conversation.setSenderId(uid);
                            conversation.setLastMessage(mMessageEdit.getText().toString());
                            conversation.setTimeZone(timeZone);
                            conversation.setSendedTime(currentTime);

                            mRef.child("Users").child(receiverId).child("chats").child(uid).setValue(conversation);

                            mRef.child("Users").child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    conversation.setAvatar(dataSnapshot.child("avatar").getValue().toString());
                                    conversation.setSender(dataSnapshot.child("name").getValue().toString());
                                    conversation.setSenderId(receiverId);
                                    mRef.child("Users").child(uid).child("chats").child(receiverId).setValue(conversation);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

//                            Log.d(TAG, "onDataChange: "+dataSnapshot );
                            mChatRef.push().setValue(chat, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                                    if (databaseError != null) {
                                        Log.e(TAG, "Failed to write message", databaseError.toException());
                                    }
                                }
                            });

                            mMessageEdit.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        mMessages = (RecyclerView) findViewById(R.id.messagesList);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        mMessages.setHasFixedSize(false);
        mMessages.setLayoutManager(mManager);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecyclerViewAdapter != null) {
            mMessages.getRecycledViewPool().clear();
            mRecyclerViewAdapter.cleanup();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(this);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        updateUI();
    }

    private void attachRecyclerViewAdapter() {
        Query lastFifty = mChatRef.limitToLast(50);
        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(
                Chat.class, R.layout.message, ChatHolder.class, lastFifty) {

            @Override
            public void populateViewHolder(ChatHolder chatView, Chat chat, int position) {
                chatView.setName(chat.getName());
                chatView.setText(chat.getText());

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null && chat.getUid().equals(currentUser.getUid())) {
                    chatView.setIsSender(true);
                } else {
                    chatView.setIsSender(false);
                }
            }
        };

        // Scroll to bottom on new messages
        mRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mMessages, null, mRecyclerViewAdapter.getItemCount());
            }
        });

        mMessages.setAdapter(mRecyclerViewAdapter);
    }

    public boolean isSignedIn() {
        return (mAuth.getCurrentUser() != null);
    }

    public void updateUI() {
        // Sending only allowed when signed in
        mSendButton.setEnabled(isSignedIn());
        mMessageEdit.setEnabled(isSignedIn());
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {
        View mView;

        public ChatHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        @SuppressWarnings("ConstantConditions")
        private void setIsSender(Boolean isSender) {
            FrameLayout left_arrow = (FrameLayout) mView.findViewById(R.id.left_arrow);
            FrameLayout right_arrow = (FrameLayout) mView.findViewById(R.id.right_arrow);
            RelativeLayout messageContainer = (RelativeLayout) mView.findViewById(R.id.message_container);
            LinearLayout message = (LinearLayout) mView.findViewById(R.id.message);

            int color;
            if (isSender) {
                color = ContextCompat.getColor(mView.getContext(), R.color.material_green_300);

                left_arrow.setVisibility(View.GONE);
                right_arrow.setVisibility(View.VISIBLE);
                messageContainer.setGravity(Gravity.END);
            } else {
                color = ContextCompat.getColor(mView.getContext(), R.color.material_gray_300);

                left_arrow.setVisibility(View.VISIBLE);
                right_arrow.setVisibility(View.GONE);
                messageContainer.setGravity(Gravity.START);
            }

            ((GradientDrawable) message.getBackground()).setColor(color);
            ((RotateDrawable) left_arrow.getBackground()).getDrawable()
                    .setColorFilter(color, PorterDuff.Mode.SRC);
            ((RotateDrawable) right_arrow.getBackground()).getDrawable()
                    .setColorFilter(color, PorterDuff.Mode.SRC);
        }

        public void setName(String name) {
            TextView field = (TextView) mView.findViewById(R.id.name_text);
            field.setText(name);
        }

        public void setText(String text) {
            TextView field = (TextView) mView.findViewById(R.id.message_text);
            field.setText(text);
        }
    }
}
