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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

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

public class EncryptActivity extends AppCompatActivity {
    AppCompatEditText text,password;
    AppCompatButton next,TypeNext,TextNext,go_to_input,select_image,send;
    ImageView imageView,sec_image;
    Intent intent;
    Service service;
    Bitmap bmp,img;
    FirebaseUser firebaseUser;
    String userid;
    RadioButton text_btn,img_btn;
    //CardView img_card;
    TextInputLayout inputLayout;
    TextView sec_image_txt;
    Dialog dialog;
    static final int GALLERY_PICK = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int OGCAMERA_REQUEST = 2;
    boolean text_or_not = false;
    boolean imagethere= false;
    ProgressDialog progressDialog;
    RelativeLayout SourceLayout,type_layout,SelectTextLayout,SelectImageLayout,PassInpLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrpyt);

        imageView = findViewById(R.id.imageView);
        intent = getIntent();
        userid = intent.getStringExtra("sender");
        TypeNext = findViewById(R.id.TypeNext);
        TextNext = findViewById(R.id.TextNext);
        next = findViewById(R.id.next);
        SourceLayout = findViewById(R.id.SourceLayout);
        type_layout = findViewById(R.id.type_layout);
        SelectTextLayout = findViewById(R.id.SelectTextLayout);
        SelectImageLayout = findViewById(R.id.SelectImageLayout);
        PassInpLayout = findViewById(R.id.text_inp_layout);
        select_image = findViewById(R.id.select_image);
        go_to_input = findViewById(R.id.go_to_input);

        SourceLayout.setVisibility(View.VISIBLE);



        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Hiding....");

        byte[] byteArray = intent.getByteArrayExtra("Image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //Bitmap bitmap = (Bitmap) intent.getParcelableExtra("Image");
        imageView.setImageBitmap(bmp);
        send = findViewById(R.id.send);

        sec_image_txt = findViewById(R.id.sec_image_txt);

        text = findViewById(R.id.text_txt);

        inputLayout = findViewById(R.id.text);
        password = findViewById(R.id.password_txt);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //img_card = findViewById(R.id.img_card);

        text_btn = findViewById(R.id.txt);
        img_btn = findViewById(R.id.img);
        sec_image = findViewById(R.id.sec_image);

        dialog = new Dialog(this);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_or_not){
                    sendImageWTxt(text.getText().toString(),password.getText().toString());
                }else {
                    sendImageWimg(img,password.getText().toString());
                }

            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SourceLayout.setVisibility(View.GONE);
                type_layout.setVisibility(View.VISIBLE);

            }
        });

        TypeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_layout.setVisibility(View.GONE);


                if(!text_or_not){
                    SelectImageLayout.setVisibility(View.VISIBLE);
                }else {
                    SelectTextLayout.setVisibility(View.VISIBLE);
                }
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

        go_to_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImageLayout.setVisibility(View.GONE);
                PassInpLayout.setVisibility(View.VISIBLE);
            }
        });



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



    private void sendImageWTxt(String message_, String password_) {

        progressDialog.show();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Uri image = getImageUri(bmp, Bitmap.CompressFormat.PNG,100);
        service = new Retrofit.Builder().baseUrl("http://116.202.48.236:8989").client(client).build().create(Service.class);
        android.database.Cursor cursor = getContentResolver().query(image, filePathColumn, null, null, null);
        if (cursor == null)
            return;

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        File file = new File(filePath);


        Date date = new Date();

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
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


                        uploadImage(firebaseUser.getUid(),url,userid);
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


    private void sendImageWimg(Bitmap img,String password_){

        progressDialog.show();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        String[] sec_filePathColumn = {MediaStore.Images.Media.DATA};

        Uri image = getImageUri(bmp, Bitmap.CompressFormat.PNG,100);
        Uri secImage = getImageUri(img, Bitmap.CompressFormat.PNG,100);
        service = new Retrofit.Builder().baseUrl("http://116.202.48.221:8989").client(client).build().create(Service.class);
        android.database.Cursor cursor = getContentResolver().query(image, filePathColumn, null, null, null);
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

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
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


                        uploadImage(firebaseUser.getUid(),url,userid);
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
                imagethere = true;
                sec_image_txt.setVisibility(View.VISIBLE);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void uploadImage(String sender,String url, String receiver) {

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("type","image");
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
}
