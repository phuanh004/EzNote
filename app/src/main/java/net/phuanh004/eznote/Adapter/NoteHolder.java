package net.phuanh004.eznote.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.phuanh004.eznote.Models.Note;
import net.phuanh004.eznote.NoteManageActivity;
import net.phuanh004.eznote.R;

/**
 * Created by anhpham on 10/21/16.
 */

public class NoteHolder extends RecyclerView.ViewHolder {
    private View mView;

    public NoteHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setTitle(String title) {
        TextView field = (TextView) mView.findViewById(R.id.noteHeaderTv);
        field.setText(title);
    }

    public void setTime(String time) {
        TextView field = (TextView) mView.findViewById(R.id.noteTimeTv);
        field.setText(time);
    }

    public void setContent(String content) {
        TextView field = (TextView) mView.findViewById(R.id.noteContentTv);
        field.setText(content);
    }

    public void setImage(String image){
        ImageView img = (ImageView) mView.findViewById(R.id.noteImageImg) ;
        Glide.with(mView.getContext()).load(image).into(img);
    }

    public void addEditNoteCardClick(final Context context, final String id, final Note note){
        CardView cardView = (CardView) mView.findViewById(R.id.noteCardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("title", note.getTitle());
                bundle.putString("content", note.getContent());

                Intent intent = new Intent(context, NoteManageActivity.class);
                intent.putExtra("note",bundle);
                context.startActivity(intent);
            }
        });
    }
}
