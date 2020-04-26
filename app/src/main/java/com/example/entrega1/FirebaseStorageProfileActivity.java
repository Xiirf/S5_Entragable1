package com.example.entrega1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

public class FirebaseStorageProfileActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 0x512;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 0x513;
    private static final int TAKE_PHOTO_CODE = 0x514;

    private Button takePictureButton;
    private ImageView takePictureImage;
    private TextView emailProfile;
    private String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_storage_profile);

        takePictureButton = findViewById(R.id.take_picture_button);
        takePictureImage = findViewById(R.id.take_picture_image);
        emailProfile = findViewById(R.id.email_profile);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        emailProfile.setText(emailProfile.getText().toString() + ' ' + email);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profilePicture.jpg");
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(FirebaseStorageProfileActivity.this)
                            .load(task.getResult())
                            .placeholder(R.drawable.ic_person_black)
                            .centerCrop()
                            .into(takePictureImage);
                }
            }
        });
        takePictureButton.setOnClickListener(l -> {
            takePicture();
        });
    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Snackbar.make(takePictureButton, R.string.take_picture_camera_rationale, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.take_picture_camera_rationale_ok, click -> {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                });
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Snackbar.make(takePictureButton, R.string.take_picture_camera_rationale, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.take_picture_camera_rationale_ok, click -> {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                    });
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                }
            } else {
                // Tenemos todos los permisos
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                String dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/masterits";
                File newFile = new File(dir);
                newFile.mkdirs();

                file = dir + "/profilePicture.jpg";
                File newFilePicture = new File(file);
                try {
                    newFilePicture.createNewFile();
                } catch (Exception e) {

                }

                Uri outputFileDir = Uri.fromFile(newFilePicture);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileDir);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i("TEST", "IN");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            File filePicture = new File(file);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(filePicture.getName());
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(filePicture));
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.i("MasterITS", "Firebase Storage: completed " + task.getResult().getTotalByteCount());

                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Glide.with(FirebaseStorageProfileActivity.this)
                                            .load(task.getResult())
                                            .placeholder(R.drawable.ic_person_black)
                                            .centerCrop()
                                            .into(takePictureImage);
                                }
                            }
                        });
                    }
                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ERROR FILE", "FireBase Storage: error " + e.getMessage());
                }
            });
        }
    }

    public void setImageProfile() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(this, R.string.camera_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;
            case WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(this, R.string.storage_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
