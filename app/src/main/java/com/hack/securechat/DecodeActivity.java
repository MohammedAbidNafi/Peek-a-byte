package com.hack.securechat;

import static com.hack.securechat.EncryptActivity.GALLERY_PICK;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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

public class DecodeActivity extends AppCompatActivity {


    AppCompatButton select, decode;
    ImageView imageView;

    Bitmap bitmap;

    Dialog dialog;

    Service service;

    Uri ImageURI;

    int img_txt = 0;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);


        imageView = findViewById(R.id.imageView);

        select = findViewById(R.id.Select);
        decode = findViewById(R.id.Decode);

        dialog = new Dialog(this);


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPassword();
            }
        });
    }

    private void onPassword() {

        CardView password_card,output_card;

        AppCompatButton unlock;
        AppCompatEditText password_edtxt;
        dialog.setContentView(R.layout.message_popup);

        ProgressBar SHOW_PROGRESS;

        password_card = dialog.findViewById(R.id.password_card);
        output_card = dialog.findViewById(R.id.output_card);
        unlock = dialog.findViewById(R.id.unlock);
        password_edtxt = dialog.findViewById(R.id.password);

        SHOW_PROGRESS = dialog.findViewById(R.id.SHOW_PROGRESS);



        password_card.setVisibility(View.VISIBLE);
        output_card.setVisibility(View.GONE);

        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

                service = new Retrofit.Builder().baseUrl("http://116.202.48.221:8989").client(client).build().create(Service.class);

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
                RequestBody password = RequestBody.create(MediaType.parse("text/plain"), password_edtxt.getText().toString());

                retrofit2.Call<okhttp3.ResponseBody> req = service.DecodeImageImage(body, password);
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            String contentType = response.headers().get("Content-Type");
                            if (contentType != null && contentType.equals("image/png")) {
                                // The response is an image
                                InputStream inputStream = response.body().byteStream();
                                Bitmap bmp = BitmapFactory.decodeStream(inputStream);

                                onOutput(bmp, "123");

                            } else if (contentType != null && contentType.equals("application/json; charset=utf-8")) {
                                // The response is JSON

                                Log.d("testing", "Works");
                                String responseBody = null;
                                try {
                                    responseBody = response.body().string();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(responseBody);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                String message = null;
                                try {
                                    message = jsonObject.getString("message");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                String error = null;
                                try {
                                    error = jsonObject.getString("error");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                try {
                                    Log.d("Test", jsonObject.getString("message"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }


                                if (message.equals("")) {
                                    Toast.makeText(DecodeActivity.this, error, Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(DecodeActivity.this, error, Toast.LENGTH_SHORT).show();
                                    onOutput(null, message);


                                }
                                Log.d("message", message);


                            } else {
                                // handle unsuccessful response
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

            }

        });

        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }



    private void onOutput(Bitmap bitmap, String message) {

        CardView password_card, output_card;

        AppCompatButton copy, share;

        TextView ouput_txt;

        ProgressBar SHOW_PROGRESS;

        ImageView output_image;

        dialog.setContentView(R.layout.message_popup);


        SHOW_PROGRESS = dialog.findViewById(R.id.SHOW_PROGRESS);
        SHOW_PROGRESS.setVisibility(View.GONE);

        output_card = dialog.findViewById(R.id.output_card);
        password_card = dialog.findViewById(R.id.password_card);
        output_image = dialog.findViewById(R.id.output_image);

        password_card.setVisibility(View.GONE);
        output_card.setVisibility(View.VISIBLE);
        copy = dialog.findViewById(R.id.copy);
        share = dialog.findViewById(R.id.share);
        ouput_txt = dialog.findViewById(R.id.output);


        if (message.equals("123")) {
            output_image.setImageBitmap(bitmap);
            ouput_txt.setVisibility(View.GONE);
        }


        ouput_txt.setText(message);


        if (img_txt == 1) {
            copy.setVisibility(View.GONE);
        } else {
            copy.setVisibility(View.VISIBLE);
        }

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (img_txt == 2) {
                    setClipboard(getApplicationContext(), message);
                }

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (img_txt == 1) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("image/jpeg");
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "title");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);


                    OutputStream outstream;
                    try {
                        outstream = getContentResolver().openOutputStream(uri);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
                        outstream.close();
                    } catch (Exception e) {
                        System.err.println(e.toString());
                    }

                    i.putExtra(Intent.EXTRA_STREAM, uri);

                    startActivity(Intent.createChooser(i, "Share Image"));
                }

                if (img_txt == 2) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    String sAux = message;

                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                }


            }
        });


        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void setClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        finish();
        Toast.makeText(context,"Text has been copied to clipboard",Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_PICK);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            ImageURI = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageURI);
                imageView.setImageBitmap(bitmap);


                file = new File(String.valueOf(ImageURI));



            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}