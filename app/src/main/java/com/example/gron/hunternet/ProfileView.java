package com.example.gron.hunternet;

import android.graphics.Bitmap;

import com.arellomobile.mvp.MvpView;

public interface ProfileView extends MvpView {
    void loadProfile(String email, String name, String telephoneNumber, Bitmap bmp);
    void startNextActivity();
    void hideProgressLoad();
    void showProgressLoad();
}
