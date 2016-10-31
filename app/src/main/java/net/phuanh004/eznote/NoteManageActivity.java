package net.phuanh004.eznote;

import android.*;
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
import android.support.design.widget.Snackbar;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.phuanh004.eznote.Adapter.HorizontalImageAdapter;
import net.phuanh004.eznote.Models.Note;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteManageActivity extends AppCompatActivity {
    @BindView(R.id.noteHeaderEditText) EditText noteHeaderEditText;
    @BindView(R.id.noteContentEditText) EditText noteContentEditText;

    @BindView(R.id.noteImagesRecyclerView) RecyclerView recyclerView;
//    @BindView(R.id.imageOnMobRecyclerView) RecyclerView imageOnMobRecyclerView;

    private DatabaseReference mDatabase;

    private String currentuser;
    private String noteid;

    private List<String> imageList;
    private List<String> imageListOnMob;
    private HorizontalImageAdapter adapter;
    private File cameraFile = null;

    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ButterKnife.bind(this);

        Bundle noteBundle = getIntent().getBundleExtra("note");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        imageList = new ArrayList<>();
        imageListOnMob = new ArrayList<>();

        adapter = new HorizontalImageAdapter(this, imageList);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        getSupportActionBar().setTitle(R.string.no_title);

        if (noteBundle != null) {
            noteid = noteBundle.getString("id");
            String noteTitle = noteBundle.getString("title");
            String noteContent = noteBundle.getString("content");

            getSupportActionBar().setTitle(noteTitle);

            noteHeaderEditText.setText(noteTitle);
            noteContentEditText.setText(noteContent);

            mDatabase.child("Users").child(currentuser).child("notes").child(noteid).child("images").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

//                    Log.d("^^^^^", "onChildAdded: " + dataSnapshot.getChildren().iterator().next());
//                    Log.d("^^^^^", "onChildAdded: " +"s: "+ s);
//                    Log.d("^^^^^", "onChildAdded: " +"d: "+ dataSnapshot.getValue());

                    imageList.add((String) dataSnapshot.getValue());
                    adapter.notifyItemChanged(imageList.size()-1);
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

            mDatabase.child("Users").child(currentuser).child("notes").child(noteid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                    noteHeaderEditText.setText( dataSnapshot.child("title").getValue().toString() );
//                    noteContentEditText.setText( dataSnapshot.child("content").getValue().toString() );
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Toast.makeText(NoteManageActivity.this, "This note was removed", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    void initData(){
        imageList.add("https://s-media-cache-ak0.pinimg.com/236x/7c/1d/29/7c1d299817abd3ca3b2fc48dee9ea88f.jpg");
        imageList.add("https://s-media-cache-ak0.pinimg.com/564x/f0/c4/13/f0c4139464c75eebbf8b7613f7e8aee3.jpg");
        imageList.add("http://i.amz.mshcdn.com/UD-KASEmF9tLG5TaqmEQQL_LJrc=/fit-in/1200x9600/https%3A%2F%2Fblueprint-api-production.s3.amazonaws.com%2Fuploads%2Fcard%2Fimage%2F122081%2FS-Skype-logo.png");
        imageList.add("https://cdn.colorlib.com/wp/wp-content/uploads/sites/2/2014/02/Olympic-logo.png");
    }

    void showFileChooser(){
        final int REQUEST_CAMERA = 1;
        final int SELECT_FILE = 2;

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
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
                            cameraFile = File.createTempFile("img", ".jpg", new File(Environment.getExternalStorageDirectory().getPath() +"/Eznote/temp") );
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

    void uploadImage(final String key){

        for (int i=0; i<imageListOnMob.size(); i++) {
            Uri file = Uri.fromFile(new File(imageListOnMob.get(i)));

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            UploadTask uploadTask = storage.child("noteImages/"+currentuser+"/"+noteid+"/"+file.getLastPathSegment()).putFile(file, metadata);

            final int finalI = i;
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NoteManageActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(NoteManageActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
                    imageListOnMob.remove(finalI);
                    mDatabase.child("Users").child(currentuser).child("notes").child(key).child("images").push().setValue(taskSnapshot.getMetadata().getDownloadUrl().toString());
//                    imageList.add(taskSnapshot.getMetadata().getDownloadUrl().toString());
//                    adapter.notifyItemChanged(imageList.size()-1);
                }
            });
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

//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DCIM), "Camera");
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//        return image;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
//                    data.setClipData(ClipData.newRawUri(null,  selectedImage));
//                    Log.d("~!@#$%", "onActivityResult: "+cameraFile.getPath());
//                    Log.d("~!@#$%", "onActivityResult: "+cameraFile.getAbsolutePath());
//                    Log.d("~!@#$%", "onActivityResult: "+cameraFile.getCanonicalPath());
                    imageListOnMob.add(cameraFile.getPath());
//                    adapter.notifyDataSetChanged();
                }

                break;
            case 2:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
//                    Log.d("~!@#$%", "onActivityResult: "+selectedImage);
                    imageListOnMob.add(getRealPathFromUri(NoteManageActivity.this, selectedImage));
//                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Note note = new Note();

        Calendar cal = Calendar.getInstance();
        String timeZone = cal.getTimeZone().getID();
        long currentTime = cal.getTimeInMillis() / 1000L;

        note.setTitle("");
        note.setSavedTime(currentTime);
        note.setTimeZone(timeZone);

        if (!noteContentEditText.getText().toString().isEmpty()){
            String title = "";
            title = !Objects.equals(noteHeaderEditText.getText().toString(), "") ? noteHeaderEditText.getText().toString() : getString(R.string.no_title);

            note.setTitle(title);
            note.setContent(noteContentEditText.getText().toString());

            if(noteid != null){
                mDatabase.child("Users").child(currentuser).child("notes").child(noteid).child("title").setValue(note.getTitle());
                mDatabase.child("Users").child(currentuser).child("notes").child(noteid).child("content").setValue(note.getContent());
            }else {
//                mDatabase.child("Users").child(currentuser).child("notes").push().setValue(note);

                DatabaseReference notePush = mDatabase.child("Users").child(currentuser).child("notes").push();
                notePush.setValue(note);

                for (String img:imageListOnMob) {
                    uploadImage(notePush.getKey());

//                    mDatabase.child("Users").child(currentuser).child("notes").child(notePush.getKey()).child("images").push().setValue(img);
                }

            }
        } else {
            Toast.makeText(NoteManageActivity.this, "Can't save empty note",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_manage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (imageListOnMob.size() == 0) {
                onBackPressed();
                this.finish();
            }else {
                return false;
            }
        } else if (item.getItemId() == R.id.action_add_image){
            showFileChooser();
        }

        return super.onOptionsItemSelected(item);
    }


}
