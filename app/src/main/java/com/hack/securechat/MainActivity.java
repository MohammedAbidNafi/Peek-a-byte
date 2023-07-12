package com.hack.securechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import com.hack.securechat.Fragments.ChatFragment;
import com.hack.securechat.Fragments.CompressFragment;
import com.hack.securechat.Fragments.SettingsFragment;
import com.hack.securechat.Fragments.UpComingFragment;
import com.hack.securechat.Model.User;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;

public class MainActivity extends AppCompatActivity implements PermissionListener {


    PageNavigationView bottomNavigationView;

    ViewPager viewPager;

    ProgressBar network_check;

    TextView network_txt,username;

    FirebaseUser firebaseUser;
    CircleImageView DP;

    DatabaseReference reference;

    String imageurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottom);

        viewPager = findViewById(R.id.viewPager);

        DP = findViewById(R.id.DP);
        username = findViewById(R.id.username);

        network_check = findViewById(R.id.network_check);
        network_txt = findViewById(R.id.network_txt);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");


        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                boolean connected = snapshot1.getValue(Boolean.class);
                if (connected) {
                    network_check.setVisibility(View.GONE);
                    network_txt.setVisibility(View.GONE);
                    DP.setVisibility(View.VISIBLE);
                    username.setVisibility(View.VISIBLE);

                } else {
                    network_check.setVisibility(View.VISIBLE);
                    network_txt.setVisibility(View.VISIBLE);
                    DP.setVisibility(View.GONE);
                    username.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();





        NavigationController navigationController = bottomNavigationView.material()
                .addItem(R.drawable.baseline_person_outline_24,"Chats" )
                .addItem(R.drawable.baseline_lock_reset_24,"Encode and Decode")
                .addItem(R.drawable.baseline_upcoming_24,"Upcoming")
                .addItem(R.drawable.baseline_settings_24, "Settings")
                .setDefaultColor(getResources().getColor(R.color.black))
                .build();


        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                imageurl = user.getImageURL();
                username.setText(user.getUsername());
                if (imageurl.equals("default")) {
                    Glide.with(getApplicationContext()).load(R.drawable.user).into(DP);
                } else {

                    Glide.with(getApplicationContext()).load(imageurl).into(DP);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        final ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.addFragment(new ChatFragment(), "Chat");
        viewPageAdapter.addFragment(new CodeAndDecodeFragment(), "Code and Decode");
        viewPageAdapter.addFragment(new UpComingFragment(), "Upcoming");
        viewPageAdapter.addFragment(new SettingsFragment(), "Settings");
        viewPager.setAdapter(viewPageAdapter);
        navigationController.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addchat:
                startActivity(new Intent(MainActivity.this, AddChatViewActivity.class));
                overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);

                return true;
        }

        return false;
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        Toast.makeText(getApplicationContext(),"All Permissions are granted. Enjoy this app!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        // check for permanent denial of permission
        if (response.isPermanentlyDenied()) {
            // navigate user to app settings
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("For this app to work as expected please allow some permissions.");
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher_round);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton(("No"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        alertDialogBuilder.setPositiveButton(("Allow"),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Dexter.withActivity(MainActivity.this)
                                .withPermissions(
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                ).withListener(new MultiplePermissionsListener() {
                                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
                                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                                }).check();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
        token.continuePermissionRequest();
    }



    static class ViewPageAdapter extends FragmentPagerAdapter {

        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;

        ViewPageAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public void onBackPressed(){
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

}