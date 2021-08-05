package com.tccbiko.bondi.start;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tccbiko.bondi.R;
import com.tccbiko.bondi.databinding.ActivityLoginSuccessBinding;

public class LoginSuccess extends AppCompatActivity {
    ActivityLoginSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_login_success);

        binding.devamEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginSuccess.this, ProfileScreen.class));
                finish();
            }
        });

    }
}