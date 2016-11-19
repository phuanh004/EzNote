package net.phuanh004.eznote;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.phuanh004.eznote.Adapter.HorizontalImageAdapter;
import net.phuanh004.eznote.Models.Note;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteManageActivity extends AppCompatActivity {
    @BindView(R.id.noteHeaderEditText) EditText noteHeaderEditText;
    @BindView(R.id.noteContentEditText) EditText noteContentEditText;
    @BindView(R.id.noteImagesRecyclerView) RecyclerView recyclerView;
    @BindView(R.id.noteImagesUploadProgressBar) ProgressBar noteImagesUploadProgressBar;

    private DatabaseReference mDatabase;

    private String currentuser;
    private String noteid;

    private List<String> imageList = new ArrayList<>();
    private List<String> imageUploadedList = new ArrayList<>();
    private List<String> imageOnServerList = new ArrayList<>();
    private HorizontalImageAdapter adapter;
    private File cameraFile = null;
    private boolean isUploading = false;
    private boolean isStopApp = false;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check_white_24dp);
        }

        ButterKnife.bind(this);

        Bundle noteBundle = getIntent().getBundleExtra("note");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new HorizontalImageAdapter(this, imageUploadedList);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        getSupportActionBar().setTitle("");

        if (noteBundle != null) {
            noteid = noteBundle.getString("id");
//            String noteTitle = noteBundle.getString("title");
//            String noteContent = noteBundle.getString("content");
//
//            getSupportActionBar().setTitle(noteTitle);
//
//            noteHeaderEditText.setText(noteTitle);
//            noteContentEditText.setText(noteContent);

            mDatabase.child("Users").child(currentuser).child("notes").child(noteid).child("images").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (!isStopApp) {

                        imageUploadedList.add((String) dataSnapshot.getValue());
                        adapter.notifyItemChanged(imageUploadedList.size() - 1);
                        if (noteid != null) {
                            imageOnServerList.add((String) dataSnapshot.getValue());
                        }

                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDatabase.child("Users").child(currentuser).child("notes").child(noteid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    noteHeaderEditText.setText(dataSnapshot.child("title").getValue().toString());
                    noteContentEditText.setText(dataSnapshot.child("content").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    void showFileChooser(){
        final int REQUEST_CAMERA = 1;
        final int SELECT_FILE = 2;

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setIcon(R.drawable.ic_camera_black_36dp);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void onClick(DialogInterface dialog, int item) {
            if (items[item].equals("Take Photo")) {
                int permissionCheck = ContextCompat.checkSelfPermission(NoteManageActivity.this, Manifest.permission.CAMERA);
                if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    try {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File dir = new File(Environment.getExternalStorageDirectory().getPath() +"/Eznote/temp");
                        if (!dir.exists()){
                            dir.mkdirs();
                        }
                        cameraFile = File.createTempFile("img", ".png", new File(Environment.getExternalStorageDirectory().getPath() +"/Eznote/temp") );
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(NoteManageActivity.this, BuildConfig.APPLICATION_ID + ".provider", cameraFile));
                        startActivityForResult(intent, REQUEST_CAMERA);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(NoteManageActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }
            } else if (items[item].equals("Choose from Library")) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_FILE);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
            }
        });
        builder.show();
    }

    void uploadImage(){
//        if (noteid != null) {
//            imageUploadedList.removeAll(imageOnServerList);
//        }

        if (!isUploading) {
            String path = "noteImages/" + currentuser + "/" + UUID.randomUUID() + ".png";

            StorageReference noteImagesRef = storage.getReference(path);

            Uri file = Uri.fromFile(new File(imageList.get(0)));

            StorageMetadata metadata = new StorageMetadata.Builder().build();

            UploadTask uploadTask = noteImagesRef.putFile(file, metadata);
            noteImagesUploadProgressBar.setVisibility(View.VISIBLE);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    isUploading = true;
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    noteImagesUploadProgressBar.setProgress((int) progress);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NoteManageActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    isUploading = false;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    noteImagesUploadProgressBar.setVisibility(View.INVISIBLE);
                    imageList.remove(imageList.size() - 1);
                    imageUploadedList.add(taskSnapshot.getMetadata().getDownloadUrl().toString());
                    adapter.notifyItemChanged(imageUploadedList.size() - 1);
                    isUploading = false;
                }
            });
        }else {
//            Todo: add toast when uploading
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Note note = new Note();

        Calendar cal = Calendar.getInstance();
        String timeZone = cal.getTimeZone().getID();
        long currentTime = cal.getTimeInMillis() / 1000L;

        note.setTitle("");
        note.setSavedTime(currentTime);
        note.setTimeZone(timeZone);

        if (!noteContentEditText.getText().toString().isEmpty()){
            String title;
            title = !Objects.equals(noteHeaderEditText.getText().toString(), "") ? noteHeaderEditText.getText().toString() : getString(R.string.no_title);

            note.setTitle(title);
            note.setContent(noteContentEditText.getText().toString());

            if(noteid != null){
                mDatabase.child("Users").child(currentuser).child("notes").child(noteid).child("title").setValue(note.getTitle());
                mDatabase.child("Users").child(currentuser).child("notes").child(noteid).child("content").setValue(note.getContent());

//                todo: send note and edit

                imageUploadedList.removeAll(imageOnServerList);
                isStopApp = true;

                for (String img:imageUploadedList) {
                    mDatabase.child("Users").child(currentuser).child("notes").child(noteid).child("images").push().setValue(img);
                }
            }else {
                DatabaseReference notePush = mDatabase.child("Users").child(currentuser).child("notes").push();
                notePush.setValue(note);

                for (String img:imageUploadedList) {
                    mDatabase.child("Users").child(currentuser).child("notes").child(notePush.getKey()).child("images").push().setValue(img);
                }

            }
        } else {
            Toast.makeText(NoteManageActivity.this, "Can't save empty note",Toast.LENGTH_SHORT).show();
        }
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_manage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_add_image){
            showFileChooser();
        }
        else if (item.getItemId() == R.id.action_share_note){
            Bundle bundle = new Bundle();
            bundle.putString("id", noteid);
            Log.d("chuot",noteid);
            Intent intent = new Intent(NoteManageActivity.this,ShareActivity.class);
            intent.putExtra("note1",bundle);
            startActivityForResult(intent,3);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    imageList.add(cameraFile.getPath());
                    uploadImage();
                }

                break;
            case 2:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    imageList.add(getRealPathFromUri(NoteManageActivity.this, selectedImage));
                    uploadImage();
                }
                break;
            case 3:
                if(resultCode == RESULT_OK){
                    Toast.makeText(NoteManageActivity.this,"Shared",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
