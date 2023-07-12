package com.hack.securechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hack.securechat.Model.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    CircleImageView profile_image;

    AppCompatEditText GetUsername;

    AppCompatButton next;

    FirebaseUser firebaseUser;

    StorageReference storageReference;

    ProgressDialog loadingBar;

    private static final int GALLERY_PICK = 1;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profile_image = findViewById(R.id.DP);

        GetUsername = findViewById(R.id.username);

        next = findViewById(R.id.next);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;

                GetUsername.setText(user.getUsername(), TextView.BufferType.EDITABLE);

                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.drawable.clicky);
                } else {
                    Glide.with((getApplicationContext())).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages/"+firebaseUser.getUid());


        loadingBar = new ProgressDialog(this);
        profile_image.setOnClickListener(v -> openImage());

        next.setOnClickListener(v -> {

            String txt_username = Objects.requireNonNull(GetUsername.getText()).toString();


            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", firebaseUser.getUid());
            hashMap.put("username", txt_username);
            hashMap.put("typingto","");
            reference1.updateChildren(hashMap).addOnCompleteListener(task -> startActivity(new Intent(SetupActivity.this, MainActivity.class)));
        });

    }

    private void openImage() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_PICK);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setMultiTouchEnabled(true)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we update your profile picture...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);
                assert result != null;
                Uri resultUri = result.getUri();

                StorageReference filepath = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(resultUri));


                StorageTask uploadTask = filepath.putFile(resultUri);
                uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SetupActivity.this, "Profile Picture has updated.", Toast.LENGTH_SHORT).show();

                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            String mUri = downloadUri.toString();


                            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", mUri);
                            reference.updateChildren(map);

                            loadingBar.dismiss();

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Profile Picture updation is cancelled", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = SetupActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}