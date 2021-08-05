package com.tccbiko.bondi.start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tccbiko.bondi.MainActivity;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.databinding.ActivityStartScreenBinding;

import org.jetbrains.annotations.NotNull;

public class StartScreen extends AppCompatActivity {
    private static final String TAG = "StartScreen";
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityStartScreenBinding binding;
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_start_screen);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
binding.start.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(StartScreen.this, MainActivity.class));
        finish();
    }
});
        db.collection("Users").document(firebaseUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            username= task.getResult().getString("userName");

                            binding.infoname.setText(username+"!");

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
}