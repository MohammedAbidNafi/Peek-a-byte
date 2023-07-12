package com.hack.securechat.WelcomeScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.hack.securechat.R;
import com.stephentuso.welcome.FragmentWelcomePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

public class MyWelcomeActivity extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.background)
                .page(new FragmentWelcomePage() {
                          @Override
                          protected Fragment fragment() {
                              return new Fragment1();
                          }
                      }
                )
                .page(new FragmentWelcomePage() {
                          @Override
                          protected Fragment fragment() {
                              return new Fragment2();
                          }
                      }
                )
                .page(new FragmentWelcomePage() {
                          @Override
                          protected Fragment fragment() {
                              return new Fragment3();
                          }
                      }
                ).showNextButton(true)
                .showPrevButton(true)
                .swipeToDismiss(true)
                .build();
    }
}