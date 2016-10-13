package net.phuanh004.eznote.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.phuanh004.eznote.Models.Note;
import net.phuanh004.eznote.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by anhpham on 10/13/16.
 */

public class AllNoteAdapter extends RecyclerView.Adapter<AllNoteAdapter.MyViewHolder> {
    private Context mContext;
    private List<Note> noteList;

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView noteHeaderTv;
        private TextView noteTimeTv;

        MyViewHolder(View view) {
            super(view);

            noteHeaderTv = (TextView) view.findViewById(R.id.noteHeaderTv);
            noteTimeTv = (TextView) view.findViewById(R.id.noteTimeTv);
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.noteHeaderTv.setText(noteList.get(position).getTitle());

        DateFormat simpleDateFormat = SimpleDateFormat.getTimeInstance();
        holder.noteTimeTv.setText(simpleDateFormat.format(noteList.get(position).getSavedTime()));

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
