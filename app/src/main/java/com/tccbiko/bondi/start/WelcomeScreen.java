package com.tccbiko.bondi.start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.google.firebase.auth.FirebaseUser;
import com.tccbiko.bondi.MainActivity;

import com.tccbiko.bondi.R;

public class WelcomeScreen extends AppCompatActivity  {
    public FirebaseUser firebaseUser;

    public void onBackPressed() {
        finishAffinity();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button btn_start = findViewById(R.id.btn_start);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser!=null){
                    startActivity(new Intent(WelcomeScreen.this, MainActivity.class));
                }
                else
                startActivity(new Intent(WelcomeScreen.this, LoginScreen.class));
                finish();
            }
        });
    }
}