package net.phuanh004.eznote.Adapter;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.phuanh004.eznote.ChatActivity;
import net.phuanh004.eznote.Helper.CircleTransform;
import net.phuanh004.eznote.R;

/**
 * Created by anhpham on 11/7/16.
 */

public class ChatHolder extends RecyclerView.ViewHolder {
    private View mView;

    public ChatHolder(android.view.View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setName(String name) {
        TextView field = (TextView) mView.findViewById(R.id.nameCardChatTextView);
        field.setText(name);
    }

    public void setAvatar(String avatar) {
        ImageView field = (ImageView) mView.findViewById(R.id.avatarCardChatImageView);
        Glide.with(mView.getContext()).load(avatar).override(100, 100).centerCrop()
                .transform(new CircleTransform(mView.getContext()))
                .into(field);
    }

    public void setLastMessage(String lastMessage){
        TextView field = (TextView) mView.findViewById(R.id.lastMessageCardChatTextView);
        field.setText(lastMessage);
    }

    public void setClickListener(final String id){
        CardView cardView = (CardView) mView.findViewById(R.id.chatCardView) ;

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mView.getContext(), ChatActivity.class);
                intent.putExtra("id", id);
                mView.getContext().startActivity(intent);
            }
        });

    }
}
