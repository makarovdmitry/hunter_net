package com.example.gron.hunternet;

import android.graphics.Bitmap;
import android.os.Environment;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@InjectViewState
public class ProfilePresenter extends MvpPresenter<ProfileView> implements Callback {
    public static final String TAG="ProfilePresenter";

    private User user;

    ProfilePresenter() {
        user = User.getInstance(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }
    protected void signOut() {
        MainPresenter.mAuth.signOut();
    }

    @Override
    public void applyDataToActivity() {
        getViewState().loadProfile(User.email, User.name, User.telephoneNumber, User.imageProfile);
        getViewState().hideProgressLoad();
    }


    public void saveDataToCache(String name, String telephone, String email) {
        User.name=name;
        User.telephoneNumber=telephone;
        User.email=email;
    }

    public void saveProfileToFirebase() {
        user.saveDataProfile();
        user.saveImageProfile();
    }

    @Override
    public void finishSaveImageProfile() {
        getViewState().startNextActivity();
    }

    public void showProgressLoad() {
        getViewState().showProgressLoad();
    }

    public void hideProgressLoad() {
        getViewState().hideProgressLoad();
    }

    public void setImageProfile(Bitmap bitmap) {
        User.imageProfile=bitmap;
    }

    public boolean validateUrl(String adress){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(adress).matches();
    }
    public boolean validateName(String name){
        return name.length()>0;
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}
