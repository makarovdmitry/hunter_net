package com.example.gron.hunternet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;


import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;


public class ProfileActivity extends MvpAppCompatActivity implements ProfileView {

    @InjectPresenter
    ProfilePresenter presenter;

    @BindView(R.id.buttonEditProfile) Button buttonEditProfile;
    @BindView(R.id.buttonExitProfile) Button buttonExitProfile;
    @BindView(R.id.textEmail) TextView textEmail;
    @BindView(R.id.textTelephoneNumber) TextView textTelephoneNumber;
    @BindView(R.id.textProfile) TextView textProfile;
    @BindView(R.id.imageProfile) ImageView imageProfile;
    @BindView(R.id.imagePhotoBlur) ImageView imagePhotoBlur;
    @BindView(R.id.imageButtonBackArrow) ImageButton imageButtonBackArrow;
    @BindView(R.id.buttonProfile) Button buttonProfile;
    @BindView(R.id.switchHideShowDataProfile)   Switch switchHideShowDataProfile;

    @OnClick(R.id.buttonEditProfile)
    public void onClickButtonEditProfile(View v) {
        startNextActivity();
    }


    @OnClick(R.id.buttonExitProfile)
    public void onClickButtonExitProfile(View v) {
        presenter.signOut();
        finish();
    }

    @OnClick({R.id.imageButtonBackArrow, R.id.buttonProfile})
    public void onClickLogOut(View v) {
        finish();
    }

    @OnCheckedChanged (R.id.switchHideShowDataProfile)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            textEmail.setVisibility(View.INVISIBLE);
            textTelephoneNumber.setVisibility(View.INVISIBLE);
        } else {
            textEmail.setVisibility(View.VISIBLE);
            textTelephoneNumber.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile(User.email, User.name, User.telephoneNumber, User.imageProfile);
    }

    @Override
    public void loadProfile(String email, String name, String telephoneNumber, Bitmap bmp ) {
        textEmail.setText(email);
        textProfile.setText(name);

        textTelephoneNumber.setText(telephoneNumber);
        if (bmp!=null) {
            imageProfile.setImageBitmap(bmp);
            imagePhotoBlur.setImageBitmap(BlurBuilder.blur(this, bmp));
        } else {
            imagePhotoBlur.setImageBitmap(imageProfile.getDrawingCache());
        }

    }

    @Override
    public void startNextActivity() {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }
}
