package net.phuanh004.eznote.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.phuanh004.eznote.ChatActivity;
import net.phuanh004.eznote.Helper.CircleTransform;
import net.phuanh004.eznote.Models.User;
import net.phuanh004.eznote.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anhpham on 11/6/16.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {
    private Context mContext;
    private List<User> userList;

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


    public FriendAdapter(Context mContext, List<User> userList) {
        this.mContext = mContext;
        this.userList = userList;
    }

    @Override
    public FriendAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_chat, parent, false);

        return new FriendAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendAdapter.MyViewHolder holder, final int position) {
        holder.nameCardChatTextView.setText(userList.get(position).getName());
        holder.lastMessageCardChatTextView.setText(userList.get(position).getEmail());
        Glide.with(mContext).load(userList.get(position).getAvatar()).centerCrop().override(200,200).transform(new CircleTransform(mContext)).into(holder.img);
        holder.chatCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("id", userList.get(position).getUserId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
