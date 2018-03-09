package com.example.gron.hunternet;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import android.text.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @OnClick(R.id.buttonForgotPassword)
    public void onClickButtonForgotPassword(View v) {
        Toast.makeText(MainActivity.this, R.string.forgot,
                Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.buttonShowPassword)
    public void onClickButtonShowPassword(View v) {
        textPassword.setTransformationMethod(null);
        buttonShowPassword.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.buttonEnter)
    public void onClickButtonEnter(View v) {
        signIn(textEmail.getText().toString(), textPassword.getText().toString());
    }

    @OnClick(R.id.buttonRegistration)
    public void onClickButtonRegistration(View v) {
        createAccount(textEmail.getText().toString(), textPassword.getText().toString());
    }

    @OnClick({R.id.imageButtonBackArrow, R.id.buttonAutarisation})
    public void onClickLogOut(View v) {
        signOut();
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

         /*buttonShowPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textPassword.setTransformationMethod(null);
                buttonShowPassword.setVisibility(View.INVISIBLE);
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.forgot,
                        Toast.LENGTH_SHORT).show();
            }
        });


        buttonEnter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signIn(textEmail.getText().toString(), textPassword.getText().toString());
            }
        });

        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createAccount(textEmail.getText().toString(), textPassword.getText().toString());
            }
        });

        buttonAutarisation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signOut();
            }
        });

        imageButtonBackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signOut();
            }
        });

        buttonUseVkOkFb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonUseVkOkFb.setVisibility(View.INVISIBLE);
                imageUseVkOkFb.setVisibility(View.VISIBLE);
                imageShadow.setVisibility(View.VISIBLE);
                imageButtonVk.setVisibility(View.VISIBLE);
                imageButtonOk.setVisibility(View.VISIBLE);
                imageButtonFb.setVisibility(View.VISIBLE);

                imageShadow.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        buttonUseVkOkFb.setVisibility(View.VISIBLE);
                        imageUseVkOkFb.setVisibility(View.INVISIBLE);
                        imageShadow.setVisibility(View.INVISIBLE);
                        imageButtonVk.setVisibility(View.INVISIBLE);
                        imageButtonOk.setVisibility(View.INVISIBLE);
                        imageButtonFb.setVisibility(View.INVISIBLE);

                    }
                });
            }
        });*/

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(null);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, R.string.auth_successfull,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, R.string.sign_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.sign_successfull,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(mAuth.getCurrentUser());
                        }
                    }
                });
    }

    private void signOut() {
        if (mAuth.getCurrentUser()!=null) {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, R.string.signout,
                    Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
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

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            buttonEnter.setVisibility(View.GONE);
            //findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            buttonEnter.setVisibility(View.VISIBLE);
        }
    }
}
