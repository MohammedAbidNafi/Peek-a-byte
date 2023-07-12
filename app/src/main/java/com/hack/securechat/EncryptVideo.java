package com.hack.securechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EncryptVideo extends AppCompatActivity {

    AppCompatEditText text,password;
    AppCompatButton next,TypeNext,TextNext,select_image,send;

    VideoView videoView;
    ImageView sec_image;

    Service service;
    RadioButton text_btn,img_btn;
    boolean text_or_not = false;
    RelativeLayout selectedView;
    Dialog dialog;

    Bitmap bmp,img;
    String VideoURI,userid;
    Intent intent;

    private static final int GALLERY_PICK = 1;
    private static final int CAMERA_REQUEST = 1888;
    MediaController mediaController;

    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;
    RelativeLayout SourceLayout,TypeLayout,SelectTextLayout,SelectImageLayout,PassInpLayout;

    StorageReference storageReference;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_video);

        videoView = findViewById(R.id.videoView);

        SourceLayout = findViewById(R.id.SourceLayout);
        TypeNext = findViewById(R.id.TypeNext);
        TextNext = findViewById(R.id.TextNext);
        next = findViewById(R.id.next);
        send =findViewById(R.id.send);

        TypeLayout = findViewById(R.id.TypeLayout);
        SelectTextLayout = findViewById(R.id.SelectTextLayout);
        SelectImageLayout = findViewById(R.id.SelectImageLayout);
        PassInpLayout = findViewById(R.id.text_inp_layout);
        select_image = findViewById(R.id.select_image);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Hiding....");

        SourceLayout.setVisibility(View.VISIBLE);

        text = findViewById(R.id.text_txt);

        //inputLayout = findViewById(R.id.text);
        password = findViewById(R.id.password_txt);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //img_card = findViewById(R.id.img_card);

        text_btn = findViewById(R.id.txt);
        img_btn = findViewById(R.id.img);
        sec_image = findViewById(R.id.sec_image);

        storageReference = FirebaseStorage.getInstance().getReference("Chats").child(firebaseUser.getUid());


        intent = getIntent();
        dialog = new Dialog(this);

        userid = intent.getStringExtra("userid");

        VideoURI = intent.getStringExtra("path");

        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);


        videoView.setVideoURI(Uri.parse(VideoURI));
        videoView.start();
        videoView.setMediaController(mediaController);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SourceLayout.setVisibility(View.GONE);
                SelectTextLayout.setVisibility(View.VISIBLE);
            }
        });

        TypeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeLayout.setVisibility(View.GONE);


                /*
                if(!text_or_not){
                    SelectImageLayout.setVisibility(View.VISIBLE);
                }else {
                    SelectTextLayout.setVisibility(View.VISIBLE);
                }

                 */

                SelectTextLayout.setVisibility(View.VISIBLE);
            }
        });

        TextNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectTextLayout.setVisibility(View.GONE);
                PassInpLayout.setVisibility(View.VISIBLE);
            }
        });




        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptions();
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                if(text_or_not){
                    sendVideoWTxt(text.getText().toString(),password.getText().toString());
                }else {
                    sendVideoWimg(password.getText().toString());
                }

                 */

                uploadVideo_();
            }
        });




    }

    private void uploadVideo_() {

        progressDialog.show();

        StorageReference filepath = storageReference.child(System.currentTimeMillis()
                + "." + getFileExtension(Uri.parse(VideoURI)));



        StorageTask uploadTask = filepath.putFile(Uri.fromFile(new File(VideoURI)));
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Calendar calendar = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
                    String timestamp = simpleDateFormat.format(calendar.getTime());

                    String sender = firebaseUser.getUid();
                    String receiver = userid;

                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    String mUri = downloadUri.toString();

                    databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", sender);
                    hashMap.put("receiver", receiver);
                    hashMap.put("type","video");
                    hashMap.put("message",text.getText().toString());
                    hashMap.put("password",password.getText().toString());
                    hashMap.put("imageUrl", mUri);
                    hashMap.put("timestamp", timestamp);
                    databaseReference.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            finish();
                            progressDialog.dismiss();
                        }
                    });



                }

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



    @SuppressLint("NonConstantResourceId")
    public void onReminderSelect(View view){
        switch (view.getId()) {

            case R.id.txt:
                if (text_btn.isChecked()) {
                    text_or_not = true;
                }
                break;

            case R.id.img:
                if (img_btn.isChecked()) {
                    text_or_not = false;
                }
                break;

        }
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
            img = (Bitmap) data.getExtras().get("data");
            sec_image.setImageBitmap(img);

        }

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                sec_image.setImageBitmap(img);



            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }


    private void sendVideoWTxt(String message_,String password_){

        progressDialog.show();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        //Uri image = getImageUri(bmp, Bitmap.CompressFormat.PNG,100);
        service = new Retrofit.Builder().baseUrl("http://116.202.48.236:8989").client(client).build().create(Service.class);
        android.database.Cursor cursor = getContentResolver().query(Uri.parse(VideoURI), filePathColumn, null, null, null);
        if (cursor == null)
            return;

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        File file = new File(filePath);


        Date date = new Date();

        RequestBody reqFile = RequestBody.create(MediaType.parse("video/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("video", file.getName(), reqFile);
        RequestBody message = RequestBody.create(MediaType.parse("text/plain"), message_);
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), password_);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), firebaseUser.getUid()+ "/"+ String.valueOf(date.getTime()) + ".png");

        retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body, message,password,filename);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);

                        String url = jsonObject.getString("url");
                        String message = jsonObject.getString("message");


                        uploadVideo(firebaseUser.getUid(),url,userid);
                        Log.d("URL", url);
                        Log.d("Message", message);
                        // do something with url and message
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // handle unsuccessful response
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });




    }


    private void sendVideoWimg(String password_){

        progressDialog.show();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        String[] sec_filePathColumn = {MediaStore.Images.Media.DATA};

        //Uri image = getImageUri(bmp, Bitmap.CompressFormat.PNG,100);
        Uri secImage = getImageUri(img, Bitmap.CompressFormat.PNG,100);
        service = new Retrofit.Builder().baseUrl("http://116.202.48.236:8989").client(client).build().create(Service.class);
        android.database.Cursor cursor = getContentResolver().query(Uri.parse(VideoURI), filePathColumn, null, null, null);
        if (cursor == null)
            return;

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        File file = new File(filePath);

        android.database.Cursor sec_cursor = getContentResolver().query(secImage, sec_filePathColumn, null, null, null);
        if (sec_cursor == null)
            return;

        sec_cursor.moveToFirst();

        int sec_columnIndex = sec_cursor.getColumnIndex(sec_filePathColumn[0]);
        String sec_filePath = sec_cursor.getString(sec_columnIndex);
        sec_cursor.close();

        File sec_file = new File(sec_filePath);


        Date date = new Date();

        RequestBody reqFile = RequestBody.create(MediaType.parse("video/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("video", file.getName(), reqFile);
        RequestBody sec_reqFile = RequestBody.create(MediaType.parse("image/*"), sec_file);
        MultipartBody.Part sec_body = MultipartBody.Part.createFormData("imageToEncode", sec_file.getName(), sec_reqFile);

        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), password_);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), firebaseUser.getUid()+ "/"+ String.valueOf(date.getTime()) + ".png");

        retrofit2.Call<okhttp3.ResponseBody> req = service.postImageWithImage(body,sec_body,password,filename);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);

                        String url = jsonObject.getString("url");
                        String message = jsonObject.getString("message");


                        uploadVideo(firebaseUser.getUid(),url,userid);
                        Log.d("URL", url);
                        Log.d("Message", message);
                        // do something with url and message
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // handle unsuccessful response
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }




    private void uploadVideo(String senderId,String url,String userId){

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", senderId);
        hashMap.put("receiver", userId);
        hashMap.put("type","video");
        hashMap.put("imageUrl",url);
        hashMap.put("timestamp",timestamp);
        databaseReference.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                progressDialog.dismiss();
                finish();
            }

        });
    }

    public Uri getImageUri(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), src, String.valueOf(Calendar.getInstance().getTime()), null);
        return Uri.parse(path);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = EncryptVideo.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}