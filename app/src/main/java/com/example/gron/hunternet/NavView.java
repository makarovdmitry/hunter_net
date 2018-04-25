package com.example.gron.hunternet;

import android.graphics.Bitmap;

import com.arellomobile.mvp.MvpView;

public interface NavView extends MvpView {
    void loadProfile(String email, String name,  Bitmap bmp );
}
