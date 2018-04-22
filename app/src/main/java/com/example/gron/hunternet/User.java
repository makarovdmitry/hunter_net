package com.example.gron.hunternet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

class User {
    private static Callback profilePresenterCall;
    private static final String TAG = "User";

    public  static String name;
    public static String email;
    public  static String telephoneNumber;
    public  static Bitmap imageProfile;

    private static User user;
    private User(Callback callback){
        this.profilePresenterCall = callback;
        loadDataProfile();
    }

    public static User getInstance(Callback callback)
    {
        if (user == null)
        {
            user = new User(callback);
        } else {
            if (user.email.equals(MainPresenter.mAuth.getCurrentUser().getEmail().toString())) {
                profilePresenterCall=callback;
                callback.applyDataToActivity();
            }
            else
                user = new User(callback);
        }
        return user;
    }

    public void loadDataProfile() {
        email = MainPresenter.mAuth.getCurrentUser().getEmail().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        db.collection("users").document(email)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        email=document.get("email").toString();
                        name=document.get("name").toString();
                        telephoneNumber=document.get("phone").toString();
                        loadImageProfile();
                    } else {
                        Log.d(TAG, "No such document");
                        name="";
                        telephoneNumber="";
                        imageProfile=null;
                        profilePresenterCall.applyDataToActivity();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void loadImageProfile() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageProfileRef = storage.getReference().child(email);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageProfileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageProfile= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePresenterCall.applyDataToActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    public void saveDataProfile() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        Map<String, Object> doc = new HashMap<>();
        doc.put("email", email);
        doc.put("name", name);
        doc.put("phone", telephoneNumber);

        db.collection("users").document(email).set(doc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }


    public void saveImageProfile() {
        Bitmap bitmap=imageProfile;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageProfileRef = storageRef.child(email);

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
                profilePresenterCall.finishSaveImageProfile();
            }
        });
    }
}
