package com.example.splashactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(com.google.android.material.R.color.design_default_color_primary)));
        firebaseAuth = FirebaseAuth.getInstance();


    }
    protected void onStart()
    {
        super.onStart();

        FirebaseUser curretUser = firebaseAuth.getCurrentUser();
        if(curretUser  == null ){
            Intent registerIntent = new Intent(SplashActivity.this,RegisterActivity.class);
            startActivity(registerIntent);
            finish();
        }
        else{

            FirebaseFirestore.getInstance().collection("USERS").document(curretUser.getUid()).update("Last seen", FieldValue.serverTimestamp())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }
    }
}