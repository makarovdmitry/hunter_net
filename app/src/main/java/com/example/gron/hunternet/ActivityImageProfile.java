package com.example.gron.hunternet;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

// TODO Класс нигде не используется и непонятно какую сущность вообще представляет
public class ActivityImageProfile {
    Activity activity;
    String email;
    ImageView imageProfile;
    ImageView imagePhotoBlur;
    ActivityImageProfile(Activity activity, ImageView imageProfile, ImageView imagePhotoBlur, String email) {
        this.activity=activity;
        this.email=email;
        this.imageProfile=imageProfile;
        this.imagePhotoBlur=imagePhotoBlur;
    }

    public void loadImageProfile() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();//getReferenceFromUrl("gs://hunternet-b8a7c.appspot.com/");
        StorageReference imageProfileRef = storageRef.child(email);

        final long ONE_MEGABYTE = 1024 * 1024;

        imageProfileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageProfile.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
        Bitmap resultBmp = BlurBuilder.blur(activity, ((BitmapDrawable)imageProfile.getDrawable()).getBitmap());
        imagePhotoBlur.setImageBitmap(resultBmp);
    }

    public void saveImageProfile() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();//FromUrl("gs://hunternet-b8a7c.appspot.com/");
        StorageReference imageProfileRef = storageRef.child(email);

        imageProfile.setDrawingCacheEnabled(true);
        imageProfile.buildDrawingCache();
        Bitmap bitmap = imageProfile.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageProfileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }
}
