package com.example.gron.hunternet;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

@InjectViewState
public class NavigationPresenter extends MvpPresenter<NavView> implements Callback {
    NavigationPresenter() {
        User.getInstance(this);
    }
    @Override
    public void applyDataToActivity() {
        getViewState().loadProfile(User.email, User.name, User.imageProfile);
    }

    @Override
    public void finishSaveImageProfile() {

    }

    @Override
    public void showProgressLoad() {

    }
    @Override
    public void hideProgressLoad() {

    }
}
