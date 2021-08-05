package com.tccbiko.bondi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tccbiko.bondi.start.WelcomeScreen;

public class SplashScreen extends AppCompatActivity {

    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    if(firebaseUser!=null) {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }, 1000);
    }else {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, WelcomeScreen.class));
            finish();
        }, 1000);
    }
    }
}