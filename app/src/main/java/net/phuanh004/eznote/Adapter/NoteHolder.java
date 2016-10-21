package net.phuanh004.eznote.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.phuanh004.eznote.NotesActivity;
import net.phuanh004.eznote.R;

/**
 * Created by anhpham on 10/21/16.
 */

public class NoteHolder extends RecyclerView.ViewHolder {
    View mView;

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

    public void addRemoveCard(final Context context, final String id) {
        CardView cardView = (CardView) mView.findViewById(R.id.noteCardView);
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("^^^^", "onLongClick: " + id);
                if(context instanceof NotesActivity){
                    ((NotesActivity)context).showDeleteDialog(id);
                }
                return false;
            }
        });
    }

//    public void showDeleteDialog(final String id) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
////        builder.setTitle(getString(R.string.dialog_title));
//
//        builder.setMessage(getString(R.string.dialog_message_delete));
//
//        String positiveText = getString(R.string.delete);
//        builder.setPositiveButton(positiveText,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // positive button logic
//                        mDatabase.child("Users").child(currentuser).child("notes").child(id).removeValue();
//
//                        Log.d("^^^^", "onClick: "+id);
//                    }
//                });
//
//        String negativeText = getString(android.R.string.cancel);
//        builder.setNegativeButton(negativeText,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//        AlertDialog dialog = builder.create();
//        // display dialog
//        dialog.show();
//    }
}
