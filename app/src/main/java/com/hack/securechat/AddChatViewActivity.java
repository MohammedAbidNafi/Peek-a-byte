package com.hack.securechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.hack.securechat.Fragments.AddChatFragment;

import java.util.ArrayList;
import java.util.Objects;

public class AddChatViewActivity extends AppCompatActivity {



    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat_view);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        viewPager = findViewById(R.id.viewPager);
        AddChatViewActivity.ViewPageAdapter viewPageAdapter = new AddChatViewActivity.ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.addFragment(new AddChatFragment(), "Users");
        viewPager.setAdapter(viewPageAdapter);

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
}