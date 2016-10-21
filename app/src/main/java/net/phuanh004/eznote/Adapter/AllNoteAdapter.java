package net.phuanh004.eznote.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.phuanh004.eznote.Models.Note;
import net.phuanh004.eznote.NoteManageActivity;
import net.phuanh004.eznote.NotesActivity;
import net.phuanh004.eznote.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by anhpham on 10/13/16.
 */

public class AllNoteAdapter extends RecyclerView.Adapter<AllNoteAdapter.MyViewHolder> {
    private Context mContext;
    private List<Note> noteList;
    private DateFormat simpleDateFormat;

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView noteHeaderTv;
        private TextView noteTimeTv;
        private TextView noteContentTv;
        private CardView noteCardView;

        MyViewHolder(View view) {
            super(view);

            noteHeaderTv = (TextView) view.findViewById(R.id.noteHeaderTv);
            noteTimeTv = (TextView) view.findViewById(R.id.noteTimeTv);
            noteContentTv = (TextView) view.findViewById(R.id.noteContentTv);
            noteCardView = (CardView) view.findViewById(R.id.noteCardView);

            simpleDateFormat = SimpleDateFormat.getTimeInstance();
        }
    }


    public AllNoteAdapter(Context mContext, List<Note> noteList) {
        this.mContext = mContext;
        this.noteList = noteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(noteList.get(position).getTimeZone()));

        holder.noteHeaderTv.setText(noteList.get(position).getTitle());
        holder.noteTimeTv.setText(simpleDateFormat
                .format( new Date(noteList.get(position).getSavedTime()*1000L) ));

        holder.noteContentTv.setText(noteList.get(position).getContent());

        holder.noteCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NoteManageActivity.class);
                mContext.startActivity(intent);
            }
        });

        holder.noteCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("^^^^", "onLongClick: " + noteList.get(position).getNoteId());

                if(mContext instanceof NotesActivity){
                    ((NotesActivity)mContext).showDeleteDialog(noteList.get(position).getNoteId());
                }
                return false;
            }
        });


//        holder.title.setText(album.getName());

//        holder.tvName.setText(TimeUnit.MILLISECONDS.toMinutes(pokemon.getTime())+" : " + (TimeUnit.MILLISECONDS.toSeconds(pokemon.getTime()) % 60));
//        if (Objects.equals(pokemon.getStreet(), "") || Objects.equals(pokemon.getStreet(), null)) {
//            holder.tvDrection.setText(pokemon.getLat() + ", " + pokemon.getLgt());
//        }else {
//            holder.tvDrection.setText(pokemon.getStreet());
//        }
//        Glide.with(mContext).load(pokemon.getImage()).into(holder.imgPoke);
//
//        holder.btnViewOnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+pokemon.getLat()+","+pokemon.getLgt()+"(Pokemon)");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//
//                if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
//                    mContext.startActivity(mapIntent);
//                }else {
//                    Snackbar.make(view, "Please install a maps application !", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//            }
//        });
//
//        holder.overflow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showPopupMenu(holder.overflow);
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
