package net.phuanh004.eznote.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.phuanh004.eznote.R;

import java.util.List;

/**
 * Created by anhpham on 10/30/16.
 */

public class HorizontalImageAdapter extends RecyclerView.Adapter<HorizontalImageAdapter.MyViewHolder> {
    private Context mContext;
    private List<String> imageList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        MyViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.noteCardImg);
        }
    }


    public HorizontalImageAdapter(Context mContext, List<String> imageList) {
        this.mContext = mContext;
        this.imageList = imageList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Glide.with(mContext).load(imageList.get(position)).override(200,200).centerCrop().into(holder.img);
//        holder.title.setText(album.getName());

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
        return imageList.size();
    }
}

