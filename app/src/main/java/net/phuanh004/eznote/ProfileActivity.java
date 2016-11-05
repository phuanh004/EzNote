package net.phuanh004.eznote;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

import net.phuanh004.eznote.Models.User;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ProfileActivity extends AppCompatActivity {
    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    public String currentuser;
    TextView tvName,tvEmail,tvPhone;
    ImageView iveName,iveEmail,ivePhone,ivavatar,ivePass;
    ProgressBar profileProgressBar;
    final Context c = this;
    private File cameraFile = null;
    private boolean isUploading = false;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private List<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser().getUid();
        tvName = (TextView)findViewById(R.id.tvName);
        tvEmail = (TextView)findViewById(R.id.tvEmail);
        tvPhone = (TextView)findViewById(R.id.tvPhone);
        iveName = (ImageView)findViewById(R.id.iveName);
        iveEmail = (ImageView)findViewById(R.id.iveEmail);
        ivePhone = (ImageView)findViewById(R.id.ivePhone);
        ivePass = (ImageView)findViewById(R.id.ivePass);
        ivavatar = (ImageView)findViewById(R.id.ivavatar);
        profileProgressBar = (ProgressBar)findViewById(R.id.profileProgressBar);
        if(mAuth.getCurrentUser() != null){
            mDatabase.child("Users").child(currentuser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String,String> map = (Map)dataSnapshot.getValue();
                    String name = map.get("name");
                    String email = map.get("email");
                    String phone = map.get("phone");
                    tvName.setText(name);
                    tvEmail.setText(email);
                    tvPhone.setText(phone);
                    String avatar = map.get("avatar");
                    new DownloadImageTask((ImageView) findViewById(R.id.ivavatar))
                            .execute(avatar);
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle("Profile");
                    profileProgressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        iveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogName();
            }
        });

        iveEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEmail();
            }
        });
        ivePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDialogPhone();
            }
        });
        ivePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPass();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }


    public void showDialogName(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_name, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText updateName = (EditText) mView.findViewById(R.id.updateName);
        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (updateName.getText().toString().equals("")) {
                            Toast.makeText(ProfileActivity.this,"Name can't be blank",Toast.LENGTH_SHORT).show();
                        }else{
                            mDatabase.child("Users").child(currentuser).child("name").setValue(updateName.getText().toString());
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(updateName.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this,"Name updated",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public void showDialogEmail(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_email, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText updateEmail = (EditText) mView.findViewById(R.id.updateEmail);
        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (updateEmail.getText().toString().equals("")) {
                            Toast.makeText(ProfileActivity.this,"Email can't be blank",Toast.LENGTH_SHORT).show();
                        }else if (!isValidEmailAddress(updateEmail.getText().toString())){
                            Toast.makeText(ProfileActivity.this,"Email is vaild",Toast.LENGTH_SHORT).show();
                        }else{
                            mDatabase.child("Users").child(currentuser).child("email").setValue(updateEmail.getText().toString());
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updateEmail(updateEmail.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this,"User email address updated",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }


                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    public void showDialogPhone(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_phone, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText updatePhone = (EditText) mView.findViewById(R.id.updatePhone);
        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (updatePhone.getText().toString().equals("")) {
                            Toast.makeText(ProfileActivity.this,"Phone can't be blank",Toast.LENGTH_SHORT).show();
                        }else{
                            mDatabase.child("Users").child(currentuser).child("phone").setValue(updatePhone.getText().toString());
                            Toast.makeText(ProfileActivity.this,"Phone updated",Toast.LENGTH_SHORT).show();
                        }


                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    public void showDialogPass(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_pass, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);

        final EditText updatePass = (EditText) mView.findViewById(R.id.updatePass);
        alertDialogBuilderUserInput
                .setCancelable(true)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (updatePass.getText().toString().equals("")) {
                            Toast.makeText(ProfileActivity.this,"Pass can't be blank",Toast.LENGTH_SHORT).show();
                        }else if (updatePass.getText().toString().length()<6){
                            Toast.makeText(ProfileActivity.this,"Password must be of minimum 6 characters",Toast.LENGTH_SHORT).show();
                        }else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(updatePass.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this,"User password updated",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }


                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }




    void showFileChooser(){
        final int REQUEST_CAMERA = 1;
        final int SELECT_FILE = 2;

        final CharSequence[] items = {"Take Photo", "Choose from Library","Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Update Avatar");
        builder.setIcon(R.drawable.ic_camera_black_36dp);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    int permissionCheck = ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.CAMERA);
                    if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        try {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File dir = new File(Environment.getExternalStorageDirectory().getPath() +"/Eznote/temp");
                            if (!dir.exists()){
                                dir.mkdirs();
                            }
                            cameraFile = File.createTempFile("img", ".png", new File(Environment.getExternalStorageDirectory().getPath() +"/Eznote/temp") );
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ProfileActivity.this, BuildConfig.APPLICATION_ID + ".provider", cameraFile));
                            startActivityForResult(intent, REQUEST_CAMERA);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
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
        if (!isUploading) {
            String path = "Avatars/" + currentuser + "/" +"avatar"+ ".png";

            StorageReference noteImagesRef = storage.getReference(path);

            Uri file = Uri.fromFile(new File(imageList.get(0)));

            StorageMetadata metadata = new StorageMetadata.Builder().build();

            UploadTask uploadTask = noteImagesRef.putFile(file, metadata);
            profileProgressBar.setVisibility(View.VISIBLE);


            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    isUploading = true;
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    profileProgressBar.setProgress((int) progress);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    isUploading = false;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileProgressBar.setVisibility(View.INVISIBLE);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mDatabase.child("Users").child(currentuser).child("avatar").setValue(downloadUrl.toString());
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(downloadUrl.toString()))
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDatabase.child("Users").child(currentuser).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String,String> map = (Map)dataSnapshot.getValue();
                                                String avatar = map.get("avatar");
                                                new DownloadImageTask((ImageView) findViewById(R.id.ivavatar))
                                                        .execute(avatar);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        Toast.makeText(ProfileActivity.this, "Avatar updated", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                    imageList.remove(imageList.size() - 1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    imageList.add(cameraFile.getPath());
                }

                break;
            case 2:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    imageList.add(getRealPathFromUri(ProfileActivity.this, selectedImage));
                }
                break;
        }

        uploadImage();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



}


