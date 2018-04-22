package com.example.gron.hunternet;

import com.arellomobile.mvp.MvpView;

public interface MainView extends MvpView {
    void signIn(String email, String password);
    //void signOut();
    void createAccount(String email, String password);
    void showToast(int rstring);
    //void updateUI();
    void startNewActivity();

}
