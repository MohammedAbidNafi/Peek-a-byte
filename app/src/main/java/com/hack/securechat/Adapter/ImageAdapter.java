package com.hack.securechat.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hack.securechat.ImageViewActivity;
import com.hack.securechat.Model.Image;
import com.hack.securechat.R;
import com.hack.securechat.VideoViewActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.leibnik.chatimageview.ChatImageView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Image> mImage;


    FirebaseUser firebaseUser;

    public static final int IMAGE_TYPE_LEFT = 0;
    public static final int IMAGE_TYPE_RIGHT = 1;

    public static final int VIDEO_TYPE_LEFT = 2;

    public static final int VIDEO_TYPE_RIGHT = 3;


    public ImageAdapter(Context mContext, List<Image> mImage) {
        this.mContext = mContext;
        this.mImage = mImage;

    }


    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ViewGroup viewGroup;
        if (viewType == IMAGE_TYPE_LEFT) {
            viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.image_left, parent, false);
        }
        else if(viewType == IMAGE_TYPE_RIGHT)  {
            viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.image_right, parent, false);
        }
        else if(viewType == VIDEO_TYPE_LEFT){
            viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.video_left,parent,false);
        }else {
            viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.video_right,parent,false);
        }
        return new ViewHolder(viewGroup);


    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mImage.get(position).getSender().equals(firebaseUser.getUid()))
        {

            if(mImage.get(position).getType().equals("image")){
                return IMAGE_TYPE_RIGHT;

            }else {
                return VIDEO_TYPE_RIGHT;
            }



        }
        else {


            if(mImage.get(position).getType().equals("image")){
                return IMAGE_TYPE_LEFT;
            }else {
                return VIDEO_TYPE_LEFT;
            }



        }



    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Image image = mImage.get(position);

        holder.timestamp.setText(image.getTimestamp());



            String uri = image.getImageUrl();

            Picasso.get().load(uri).placeholder(R.drawable.loading).into(holder.chatimage);

            holder.chatimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(image.getType().equals("image")){
                        openImage(uri);

                    }else {
                        openVideo(uri);
                    }
                }
            });


    }

    private void openImage(String uri) {

        Intent intent = new Intent(mContext, ImageViewActivity.class);
        intent.putExtra("imageuri",uri);
        mContext.startActivity(intent);



    }


    private void openVideo(String url){
        Intent intent = new Intent(mContext, VideoViewActivity.class);
        intent.putExtra("url",url);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mImage.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{


        public TextView timestamp;

        public ChatImageView chatimage;

        public ViewHolder(View view){
            super(view);


            timestamp = itemView.findViewById(R.id.timestamp);

            chatimage = itemView.findViewById(R.id.chatimage);


        }



    }

}
