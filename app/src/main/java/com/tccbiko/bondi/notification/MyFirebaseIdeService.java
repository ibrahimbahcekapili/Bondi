package com.tccbiko.bondi.notification;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.jetbrains.annotations.NotNull;

public class MyFirebaseIdeService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        Log.d("NEW_TOKEN","NewToken:"+s);
     if(firebaseUser!=null){

         updateToken(s);
     }
    }

    private void updateToken(String s) {
        FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(s);
        reference.child(firebaseUser.getUid()).setValue(token);


    }
}
