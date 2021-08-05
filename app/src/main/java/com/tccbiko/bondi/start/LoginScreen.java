package com.tccbiko.bondi.start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.databinding.ActivityLoginScreenBinding;

import java.util.concurrent.TimeUnit;

public class LoginScreen extends AppCompatActivity {

    private ActivityLoginScreenBinding binding;
    private static final String TAG = "PhoneLoginActivity";

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String phone;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_screen);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //üye girişi varsa
        if (firebaseUser!=null){
            startActivity(new Intent(this, ProfileScreen.class));
        }

        progressDialog = new ProgressDialog(this);
binding.nocode.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(LoginScreen.this, LoginScreen.class));
    }
});

        binding.devamEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(binding.edCodeCountry.getText().toString().isEmpty()){
                    binding.edCodeCountry.setError("Ülke Kodunu Doldurunuz");

                }
                else if(binding.edPhone.getText().toString().length()!=10) {
                    binding.edPhone.setError("Numarayı kontrol edin");
                }
                else if (binding.devamEt.getText().toString().equals("Devam Et")) {

                    phone = "+" + binding.edCodeCountry.getText().toString() + binding.edPhone.getText().toString();
                    showConfDialog("Şu numaraya kod göndereceğiz:"+phone);

                }
                else if (binding.devamEt.getText().toString().equals("Onayla")){
                    if(binding.edCode.getText().toString().isEmpty()){
                        binding.edCode.setError("Kodu Giriniz");
                    }
                    else {
                        progressDialog.setMessage("Onaylanıyor...");
                        progressDialog.show();
                        verifyPhoneNumberWithCode(mVerificationId, binding.edCode.getText().toString());
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: Complete");
                signInWithPhoneAuthCredential(phoneAuthCredential);
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: " + e.getMessage());
            }
            //buraya bak
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Log.d(TAG, "onCodeSent:" + verificationId);


                mVerificationId = verificationId;
                mResendToken = token;
                binding.toolbar.setVisibility(View.INVISIBLE);
                binding.numberfield.setVisibility(View.INVISIBLE);
                binding.textfield.setVisibility(View.INVISIBLE);
                binding.devamEt.setText("Onayla");
                binding.loginphoto.setVisibility(View.INVISIBLE);
                binding.confirm.setVisibility(View.VISIBLE);
                binding.edCode.setVisibility(View.VISIBLE);
                binding.nocode.setVisibility(View.VISIBLE);
                binding.bondilogo.setVisibility(View.VISIBLE);
                binding.edCodeCountry.setEnabled(false);
                binding.edPhone.setEnabled(false);

                progressDialog.dismiss();

            }
        };
    }

    private void startPhoneNumberVerification(String phoneNumber) {

        progressDialog.setMessage("Onayladığınız numaraya kodu gönderiyoruz");
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void showConfDialog (String mesaj) {
        new AlertDialog.Builder(this)
                .setTitle("Numaranızı Onaylayınız")
                .setMessage(mesaj)
                .setPositiveButton("Onayla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startPhoneNumberVerification(phone);
                    }
                })
                .setNegativeButton("Düzenle",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void showErrorDialog (String mesaj) {
        new AlertDialog.Builder(this)
                .setTitle("Kodu Kontrol Ediniz")
                .setMessage(mesaj)
                .setPositiveButton("tamam", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(LoginScreen.this, LoginSuccess.class));
                            finish();

                        } else {

                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                showErrorDialog("Hatalı Kod Girdiniz");
                                Log.d(TAG, "onComplete: Error Code");



                            }
                        }
                    }
                });
    }
}