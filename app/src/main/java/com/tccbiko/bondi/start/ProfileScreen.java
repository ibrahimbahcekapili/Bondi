package com.tccbiko.bondi.start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tccbiko.bondi.MainActivity;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.databinding.ActivityProfileScreenBinding;
import com.tccbiko.bondi.user.Users;

import org.jetbrains.annotations.NotNull;

public class ProfileScreen extends AppCompatActivity {
    private static final String TAG = "profilescreen";
    private ActivityProfileScreenBinding binding;
    private ProgressDialog progressDialog;
    String profilephoto;
    String bio;


    @Override
    public void onBackPressed() {
        return;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_screen);
        progressDialog = new ProgressDialog(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("Users").document(firebaseUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getString("userName") != null) {
                                binding.edName.setText(task.getResult().getString("userName"));
                            }
                            if (task.getResult().getString("profilePhoto") != null) {
                                profilephoto = task.getResult().getString("profilePhoto");

                            } else{
                                profilephoto="https://www.pasrc.org/sites/g/files/toruqf431/files/styles/freeform_750w/public/2021-03/blank-profile-picture-973460_1280.jpg?itok=QzRqRVu8";
                        }
                            if (task.getResult().getString("bio") != null) {
                                bio = task.getResult().getString("bio");

                            } else{
                                bio="";
                            }


                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        ButtonClick();

    }

    private void ButtonClick() {

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.edName.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Lütfen bir kullanıcı adı girin", Toast.LENGTH_LONG).show();
                } else {
                    doUpdate();
                }

            }
        });

    }

    private void doUpdate() {

        progressDialog.setMessage("Lütfen Bekleyin");
        progressDialog.show();
        FirebaseFirestore FirebaseFirestore = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        FirebaseUser FirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       if (FirebaseUser != null) {
            String UserID = FirebaseUser.getUid();
            Users users = new Users(UserID,
                    binding.edName.getText().toString(),
                    FirebaseUser.getPhoneNumber(), profilephoto
                    ,
                    "",
                    bio);
            FirebaseFirestore.collection("Users").document(FirebaseUser.getUid()).set(users)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "Kayıt Tamamlandı", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), ProfileScreen2.class));
                            finish();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Bir hata oluştu", Toast.LENGTH_LONG).show();
            // progressDialog.dismiss();

        }

    }
}
