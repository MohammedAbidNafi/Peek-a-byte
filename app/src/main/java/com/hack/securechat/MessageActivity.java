package com.hack.securechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hack.securechat.Adapter.ImageAdapter;
import com.hack.securechat.Model.Image;
import com.hack.securechat.Model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 2;
    TextView Username;

    CircleImageView profile_image;

    ImageView AddImage,AddVideo;

    Dialog dialog;

    ArrayList<Image> mImages;

    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    static final int GALLERY_PICK = 1;

    FirebaseUser firebaseUser;

    String userid;

    Intent intent;

    private static final int CAMERA_REQUEST = 1888;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Username = findViewById(R.id.username);

        profile_image = findViewById(R.id.profile_image);

        AddImage = findViewById(R.id.addimage);
        AddVideo = findViewById(R.id.addVideo);

        intent = getIntent();

        userid = intent.getStringExtra("userid");

        recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog = new Dialog(MessageActivity.this);

        mImages = new ArrayList<>();

        AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptions();
            }
        });

        AddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectVideo();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Username.setText(user.getUsername());
                String imageUrl = user.getImageURL();
                Glide.with(getApplicationContext()).load(imageUrl).into(profile_image);
                readImages(firebaseUser.getUid(), userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        readImages(firebaseUser.getUid(),userid);
    }

    private void readImages(String myid,String userid) {

        mImages = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mImages.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Image image = snapshot1.getValue(Image.class);
                    assert image != null;
                    if(image.getReceiver().equals(myid)&& image.getSender().equals(userid) ||
                            image.getReceiver().equals(userid)&&image.getSender().equals(myid)) {
                        mImages.add(image);

                    }

                    ImageAdapter imageAdapter = new ImageAdapter(MessageActivity.this, mImages);
                    recyclerView.setAdapter(imageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onOptions() {

        CardView camera,cancel;
        AppCompatButton selectPicture;
        dialog.setContentView(R.layout.ask_medium_popup);

        cancel = dialog.findViewById(R.id.cancel);
        camera = dialog.findViewById(R.id.camera);

        selectPicture = dialog.findViewById(R.id.picture);
        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                dialog.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void SelectVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,"10");

        startActivityForResult(intent , REQUEST_TAKE_GALLERY_VIDEO);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_PICK);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Intent intent = new Intent(this, EncryptActivity.class);
            intent.putExtra("Image",byteArray);
            startActivity(intent);
        }

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Intent intent = new Intent(this, EncryptActivity.class);
                intent.putExtra("Image",byteArray);
                intent.putExtra("sender",userid);
                startActivity(intent);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
            Uri selectedImageUri = data.getData();
            // MEDIA GALLERY
            String selectedImagePath = getPath(selectedImageUri);
            Log.d("Video URI",selectedImagePath);
            if (selectedImagePath != null) {

                Intent intent = new Intent(MessageActivity.this,
                        EncryptVideo.class);
                intent.putExtra("path", selectedImagePath);
                intent.putExtra("userid",userid);
                startActivity(intent);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

}