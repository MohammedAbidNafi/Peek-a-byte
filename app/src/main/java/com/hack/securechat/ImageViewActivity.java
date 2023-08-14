package com.hack.securechat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

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
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageViewActivity extends AppCompatActivity {

    ImageView imageView;

    Intent intent;

    String ImageUrl;

    Service service;

    Dialog dialog;

    AppCompatButton unlock,share;

    int img_txt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView = findViewById(R.id.imageView);

        intent = getIntent();

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

        ImageUrl = intent.getStringExtra("imageuri");

        Log.d("LINK",ImageUrl);
        Picasso.get().load(ImageUrl).placeholder(R.drawable.loading).into(imageView);

        unlock = findViewById(R.id.unlock);
        share = findViewById(R.id.share);



        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        service = new Retrofit.Builder().baseUrl("http://116.202.48.236:8989").client(client).addConverterFactory(GsonConverterFactory.create()).build().create(Service.class);

        dialog = new Dialog(this);

        onPassword();

        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPassword();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    // Create a new thread to perform the network operation
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Create a URL object from the image URL
                                URL url = new URL(ImageUrl);

                                // Open a connection to the URL
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();

                                // Get the input stream from the connection
                                InputStream inputStream = connection.getInputStream();

                                // Create a bitmap from the input stream
                                String fileName = "image.png"; // Provide a filename for the downloaded image
                                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                                File file = new File(directory, fileName);
                                FileOutputStream fos = new FileOutputStream(file);
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    fos.write(buffer, 0, bytesRead);
                                }
                                fos.flush();
                                fos.close();

                                // Notify the MediaScanner to scan the saved image
                                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getPath()}, null, null);

                                // Share the downloaded image
                                shareImage(file);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
            }


        });

    }

    private void shareImage(File file) {
        Uri imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Image"));
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
                password_card.setVisibility(View.GONE);
                output_card.setVisibility(View.VISIBLE);
                SHOW_PROGRESS.setVisibility(View.VISIBLE);

                String password_ = password_edtxt.getText().toString();
                Log.d("url",ImageUrl);
                Log.d("pass",password_);

                RequestBody url = RequestBody.create(MediaType.parse("text/plain"), ImageUrl);
                RequestBody password = RequestBody.create(MediaType.parse("text/plain"), password_);


                JsonObject json = new JsonObject();
                json.addProperty("url", ImageUrl);
                json.addProperty("password",password_);

                Call<ResponseBody> call = service.DecodeImage(json);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // Handle success
                        try {

                            if (response.isSuccessful() && response.body() != null){
                                String contentType = response.headers().get("Content-Type");
                                Log.d("ContentType", contentType);
                                if (contentType != null && contentType.equals("image/png")) {
                                    // The response is an image
                                    InputStream inputStream = response.body().byteStream();
                                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                                    img_txt = 1;
                                    onOutput(bmp,"123");

                                } else if (contentType != null && contentType.equals("application/json; charset=utf-8")) {
                                    // The response is JSON

                                    Log.d("testing","Works");
                                    String responseBody = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    String message = jsonObject.getString("message");
                                    String error = jsonObject.getString("error");
                                    img_txt = 2;
                                    Toast.makeText(ImageViewActivity.this, error, Toast.LENGTH_SHORT).show();

                                    if (message.equals("")) {
                                        Toast.makeText(ImageViewActivity.this, error, Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(ImageViewActivity.this, error, Toast.LENGTH_SHORT).show();
                                        onOutput(null,message);


                                    }
                                    Log.d("message", message);


                                }else {
                                    Toast.makeText(ImageViewActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(ImageViewActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }





                        } catch (JSONException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Handle failure
                    }
                });

            }
        });

        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }


    private void onOutput(Bitmap bitmap,String message) {

        CardView password_card,output_card;

        AppCompatButton copy,share;

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


        if(message.equals("123")){
            output_image.setImageBitmap(bitmap);
            ouput_txt.setVisibility(View.GONE);
        }

        ouput_txt.setText(message);

        if(img_txt == 1){
            copy.setVisibility(View.GONE);
        }else {
            copy.setVisibility(View.VISIBLE);
        }

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (img_txt == 2){
                    setClipboard(getApplicationContext(),message);
                }

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(img_txt == 1){
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

                if(img_txt == 2){
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
}
