package com.hack.securechat.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hack.securechat.MessageActivity;
import com.hack.securechat.Model.Chatlist;
import com.hack.securechat.Model.User;
import com.hack.securechat.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context mContext;
    private final List<User> mUsers;

    Activity activity;



    public UserAdapter(Context mContext, List<User> mUsers,Activity activity) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.activity = activity;

    }



    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);


        holder.UsernameText.setText(user.getUsername());




        if (user.getImageURL().equals("default")) {
            Glide.with(mContext).load(R.drawable.user).into(holder.profile);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile);
        }

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)  {
                    holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.onAdapClick));

                }else {
                    holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background));

                }
                return false;
            }
        });

        holder.itemView.setOnClickListener(v -> {

            String userid = user.getId();
            OnMessage(userid);

        });


    }







    @Override
    public int getItemCount() {
        return mUsers.size();

    }
    public static class ViewHolder extends RecyclerView.ViewHolder {



        public TextView UsernameText = itemView.findViewById(R.id.username);


        public ImageView profile;




        public ViewHolder(View view) {
            super(view);
            profile = itemView.findViewById(R.id.profile_image);


        }
    }








    private void OnMessage(String userid){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chatlist chatlist = snapshot.getValue(Chatlist.class);





                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("userid", userid);
                    mContext.startActivity(intent);





            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });



    }

}
