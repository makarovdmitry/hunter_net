package com.example.gron.hunternet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.EditText;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import android.util.Log;
import android.widget.Toast;
import android.text.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends MvpAppCompatActivity implements MainView {

    @InjectPresenter
    MainPresenter presenter;

    public static final String TAG = "MainActivity";


    @BindView(R.id.labelWrongPassword) TextView labelWrongPassword;
    @BindView(R.id.textPassword) EditText textPassword;
    @BindView(R.id.textEmail) EditText textEmail;
    @BindView(R.id.buttonUseVkOkFb) Button buttonUseVkOkFb;

    @BindView(R.id.buttonShowPassword) Button buttonShowPassword;
    @BindView(R.id.buttonForgotPassword) Button buttonForgotPassword;
    @BindView(R.id.buttonEnter) Button buttonEnter;
    @BindView(R.id.buttonRegistration) Button buttonRegistration;
    @BindView(R.id.buttonAutarisation) Button buttonAutarisation;
    @BindView(R.id.imageButtonVk) ImageButton imageButtonVk;
    @BindView(R.id.imageButtonOk) ImageButton imageButtonOk;
    @BindView(R.id.imageButtonFb) ImageButton imageButtonFb;
    @BindView(R.id.imageButtonBackArrow) ImageButton imageButtonBackArrow;
    @BindView(R.id.imageUseVkOkFb) ImageView imageUseVkOkFb;
    @BindView(R.id.imageShadow) ImageView imageShadow;
    @BindView(R.id.progressLoad) ProgressBar progressLoad;

    @OnClick(R.id.buttonForgotPassword)
    public void onClickButtonForgotPassword(View v) {
        showToast(R.string.forgot);
    }

    @OnClick(R.id.buttonShowPassword)
    public void onClickButtonShowPassword(View v) {
        textPassword.setTransformationMethod(null);
        buttonShowPassword.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.buttonEnter)
    public void onClickButtonEnter(View v) {
        progressLoad.setVisibility(ProgressBar.VISIBLE);
        signIn(textEmail.getText().toString(), textPassword.getText().toString());
    }

    @OnClick(R.id.buttonRegistration)
    public void onClickButtonRegistration(View v) {
        String email=textEmail.getText().toString();
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        presenter.createAccount(email, textPassword.getText().toString());
    }

    @OnClick({R.id.imageButtonBackArrow, R.id.buttonAutarisation})
    public void onClickLogOut(View v) {
        presenter.signOut();
    }

    @OnClick(R.id.buttonUseVkOkFb)
    public void onClickButtonUseVkOkFb(View v) {
        buttonUseVkOkFb.setVisibility(View.INVISIBLE);
        imageUseVkOkFb.setVisibility(View.VISIBLE);
        imageShadow.setVisibility(View.VISIBLE);
        imageButtonVk.setVisibility(View.VISIBLE);
        imageButtonOk.setVisibility(View.VISIBLE);
        imageButtonFb.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.imageShadow)
    public void onClickImageShadow(View v) {
        buttonUseVkOkFb.setVisibility(View.VISIBLE);
        imageUseVkOkFb.setVisibility(View.INVISIBLE);
        imageShadow.setVisibility(View.INVISIBLE);
        imageButtonVk.setVisibility(View.INVISIBLE);
        imageButtonOk.setVisibility(View.INVISIBLE);
        imageButtonFb.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.addAuthStateListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.removeAuthStateListener();
    }
    public void showToast(int rstring) {
        Toast.makeText(MainActivity.this, rstring,
                Toast.LENGTH_SHORT).show();
    }

    public void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        presenter.createAccount(email, password);
    }

    public void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        presenter.signIn(email, password);
    }

    public void startNewActivity() {
        progressLoad.setVisibility(ProgressBar.INVISIBLE);
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = textEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            textEmail.setError(getString(R.string.error));
            valid = false;
        } else {
            textEmail.setError(null);
        }

        String password = textPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            textPassword.setError(getString(R.string.error));
            valid = false;
        } else {
            textPassword.setError(null);
        }
        changeButtonEnter(valid);
        return valid;
    }

    private void changeButtonEnter(boolean valid) {
        if (!valid) {
            buttonEnter.setText(R.string.emptyfields);
            buttonEnter.setBackgroundColor(getResources().getColor(R.color.colorEmptyFields));
        } else {
            buttonEnter.setText(R.string.enter);
            buttonEnter.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        }
    }
}
