package com.example.gron.hunternet;

import android.content.ActivityNotFoundException;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class EditProfileActivity extends MvpAppCompatActivity implements ProfileView, EditProfileView {

    @InjectPresenter
    ProfilePresenter presenter;

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_CAPTURE = 2;
    private static final int PIC_CROP = 3;
    private static final int PERMISSION_REQUEST = 1;

    @BindView(R.id.buttonChangePhoto)  Button buttonChangePhoto;
    @BindView(R.id.imageButtonBackArrow) ImageButton imageButtonBackArrow;
    @BindView(R.id.buttonSave)  Button buttonSave;
    @BindView(R.id.imageProfile) ImageView imageProfile;
    @BindView(R.id.imagePhotoBlur) ImageView imagePhotoBlur;
    @BindView(R.id.textEmail) EditText textEmail;
    @BindView(R.id.textTelephoneNumber) EditText textTelephoneNumber;
    @BindView(R.id.textProfile) TextView textProfile;
    @BindView(R.id.textNameLastNameProfile) TextView textNameLastNameProfile;
    @BindView(R.id.progressLoad) ProgressBar progressLoad;

    @OnClick(R.id.imageButtonBackArrow)
    public void onClickImageButtonBackArrow(View v) {
        finish();
    }

    @OnClick(R.id.buttonChangePhoto)
    public void onClickButtonChangePhoto(View v) {
        showPopupMenu(v);
    }

    @OnClick(R.id.buttonSave)
    public void onClickButtonSave(View v) {
        if (!presenter.validateName(textNameLastNameProfile.getText().toString())) {
            textNameLastNameProfile.setError("Короткое имя");
            return;
        }
        if (!presenter.validateUrl(textEmail.getText().toString())) {
            textEmail.setError("Возможно опечатка в почте");
            return;
        }
        imageProfile.setDrawingCacheEnabled(true);
        imageProfile.buildDrawingCache();
        presenter.saveDataToCache(textNameLastNameProfile.getText().toString(), textTelephoneNumber.getText().toString(), textEmail.getText().toString());

        presenter.saveProfileToFirebase();

    }
    @Override
    public void startNextActivity() {
        finish();
    }

    @Override
    public void hideProgressLoad() {
        progressLoad.setVisibility(ProgressBar.INVISIBLE);
    }
    @Override
    public void showProgressLoad() {
        progressLoad.setVisibility(ProgressBar.VISIBLE);
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
        textNameLastNameProfile.setText(name);

        if (bmp!=null) {
            imageProfile.setImageBitmap(bmp);
            imagePhotoBlur.setImageBitmap(BlurBuilder.blur(this, bmp));
        } else {
            imagePhotoBlur.setImageBitmap(imageProfile.getDrawingCache());
        }

        FormatWatcher formatWatcher = new MaskFormatWatcher(MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER));
        formatWatcher.installOn(textTelephoneNumber);
        formatWatcher.getMask().setPlaceholder('*');
        formatWatcher.getMask().setShowingEmptySlots(true);
        textTelephoneNumber.setText(telephoneNumber);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }



    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popupmenu);
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.galary:
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(intent, GALLERY_REQUEST);
                                return true;

                            case R.id.camera:
                                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (ContextCompat.checkSelfPermission(EditProfileActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST);//выводит диалог, где пользователю предоставляется выбор

                                } else {
                                    try {
                                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(),
                                                "Something Wrong while taking photos", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                return true;

                            default:
                                return false;
                        }
                    }
                });

        popupMenu.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Something Wrong while taking photos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Разрешения не получены", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        Uri selectedImage;
        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        imageProfile.setImageBitmap(scaleBitmapToImageViewSize(bitmap));

                        presenter.setImageProfile(bitmap);
                        Bitmap resultBmp = BlurBuilder.blur(this, bitmap);
                        imagePhotoBlur.setImageBitmap(resultBmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CAMERA_CAPTURE:
                if(resultCode == RESULT_OK){
                    bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    imageProfile.setImageBitmap(scaleBitmapToImageViewSize(bitmap));
                    presenter.setImageProfile(bitmap);
                    Bitmap resultBmp = BlurBuilder.blur(this, bitmap);
                    imagePhotoBlur.setImageBitmap(resultBmp);
                }
                break;
            case PIC_CROP:
                break;
        }


    }

    public Bitmap scaleBitmapToImageViewSize(Bitmap bitmap) {

        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        outWidth = (inWidth * imageProfile.getHeight()) / inHeight;
        outHeight = outWidth;

        return Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
    }

    private void performCrop(Uri picUri){
        try {
            // Намерение для кадрирования. Не все устройства поддерживают его
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch(ActivityNotFoundException anfe) {
            String errorMessage = "Извините, но ваше устройство не поддерживает кадрирование";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
