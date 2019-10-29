package com.example.sept1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


import android.os.Bundle;

public class upload extends AppCompatActivity {

    CardView c1,c2;
    ImageView i1;
    int a = 0;
    Uri uri;
    Bitmap bitmap;
    String str = "jpg";

    Boolean camera_called = false;
    Boolean aBoolean = false;
    private StorageReference mStorageRef;
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        c1 = findViewById(R.id.card1);
        floatingActionButton = findViewById(R.id.fab);
        i1 = findViewById(R.id.image1);
        c2 = findViewById(R.id.upload);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera,500);
            }
        });

        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });

        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri== null)
                {
                    if(a == 0)
                    {
                        Toast.makeText(getApplicationContext(),"Select an Image to upload!!",Toast.LENGTH_LONG).show();

                    }
                    else{
                        if(!aBoolean)
                        {
                            File2();
                        }

                    }

                }
                else{
                    if(aBoolean)
                        FileUploader();
                    else {
                        File2();
                    }

                }

            }
        });
    }

    private void FileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData() != null)
        {
            a = 20;
            uri = data.getData();
            i1.setImageURI(uri);
            aBoolean = true;
        }
        else if (requestCode == 500 && resultCode == Activity.RESULT_OK )
        {
            a = -20;
            aBoolean = false;
            camera_called = true;
            assert data != null;
            bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            i1.setImageBitmap(bitmap);

            assert bitmap != null;
        }
        else{
            a = 0;
        }
    }
    private String getExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));

    }
    void FileUploader()
    {
        StorageReference Ref = mStorageRef.child(System.currentTimeMillis()+"."+getExtension(uri));
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading from storage...");
        progressDialog.show();

        Ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.cancel();
                        Toast.makeText(getApplicationContext(),"Upload Success :)",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),ABC.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.cancel();
                        Toast.makeText(getApplicationContext(),"Check Internet Connection :(",Toast.LENGTH_SHORT).show();
                    }
                });

    }
    void File2()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://sept1-67505.appspot.com/");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading image from camera...");
        progressDialog.show();

        StorageReference mountainsRef = storageRef.child(System.currentTimeMillis()+"."+str);


        i1.setDrawingCacheEnabled(true);
        i1.buildDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data2 = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data2);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(),"Check Internet Connection :(",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                progressDialog.cancel();
                Toast.makeText(getApplicationContext(),"Upload Success :)",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),ABC.class));

            }
        });


    }
}
