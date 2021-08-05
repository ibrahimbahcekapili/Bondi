package com.tccbiko.bondi.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.databinding.ActivityChatScreenBinding;
import com.tccbiko.bondi.notification.APIService;
import com.tccbiko.bondi.notification.Client;
import com.tccbiko.bondi.notification.Data;
import com.tccbiko.bondi.notification.MyResponse;
import com.tccbiko.bondi.notification.Sender;
import com.tccbiko.bondi.notification.Token;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatScreen extends AppCompatActivity {
    private static final String TAG = "ChatScreen";
    private ActivityChatScreenBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    public String receiverID;
    private ChatAdapter adapter;
    private List<ChatObject> list;
    private APIService apiService;
    private String userName;
    private String myuserName;
    private String userProfile;
    private String myprofilePhoto;
    boolean notify=false;
    private String status;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    getStatus();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_screen);
        firestore=FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        receiverID = intent.getStringExtra("userID");
        userProfile = intent.getStringExtra("userProfile");
        binding.tvUsername.setText(userName);
        Glide.with(this).load(userProfile).into(binding.imageProfile);
binding.btnBack.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       onBackPressed();
    }
});
apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        BtnClick();
        readChats();
        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

getmyInformation();
        readChats();

    }
    private void getmyInformation() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 myuserName = Objects.requireNonNull(documentSnapshot.get("userName")).toString();
                 myprofilePhoto = Objects.requireNonNull(documentSnapshot.get("profilePhoto")).toString();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("username Data","onFailure" +e.getMessage());

            }
        });

    }
    private void readChats() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("ChatList").child(firebaseUser.getUid()).child(receiverID).child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatObject chat_object = snapshot.getValue(ChatObject.class);
                    if (chat_object.getSender().equals(firebaseUser.getUid()) && chat_object.getReceiver().equals(receiverID)
                            || chat_object.getReceiver().equals(firebaseUser.getUid()) && chat_object.getSender().equals(receiverID)) {

                        list.add(chat_object);

                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    binding.recyclerView.scrollToPosition(list.size()-1);


                } else {
                    adapter = new ChatAdapter(list, ChatScreen.this);
                    binding.recyclerView.setAdapter(adapter);


                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void BtnClick() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                if(!binding.edMessage.getText().toString().equals("")){
                    sendTextMessage(binding.edMessage.getText().toString());
                }

                binding.edMessage.setText("");
            }
        });

    }

    private void sendTextMessage(String text) {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String today = format.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String currentTime = df.format(currentDateTime.getTime());

        ChatObject chat_object = new ChatObject(currentTime,today,
                text, "TEXT", firebaseUser.getUid(), receiverID);

        reference.child("ChatList").child(firebaseUser.getUid()).child(receiverID).child("Chats").push().setValue(chat_object).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Send", "OnSuccess");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("send", "onFailure" + e.getMessage());
            }
        });


        reference.child("ChatList").child(receiverID).child(firebaseUser.getUid()).child("Chats").push().setValue(chat_object).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Send", "OnSuccess");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("send", "onFailure" + e.getMessage());
            }
        });

        final String msg = text;
        if(notify) {
            sendNotification(receiverID, userName, msg);
        }
        notify=false;
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        DatabaseReference chat1 = FirebaseDatabase.getInstance().getReference("ChatIDs").child(firebaseUser.getUid()).child(receiverID);
        chat1.child("chatid").setValue(receiverID);

        DatabaseReference chat2 = FirebaseDatabase.getInstance().getReference("ChatIDs").child(receiverID).child(firebaseUser.getUid());
        chat2.child("chatid").setValue(firebaseUser.getUid());


    }

    private void sendNotification(String receiverID, String userName, String text) {
        DatabaseReference tokens= FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiverID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Token token= snapshot1.getValue(Token.class);
                    Data data= new Data(firebaseUser.getUid(),R.drawable.bondi_icon_white,myuserName+" size yeni bir mesaj gönderdi","Yeni bir mesajınız var",receiverID,myuserName,myprofilePhoto);
                    Log.d(TAG, "onDataChange: success");
                    Sender sender= new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success!=1){
                                            Toast.makeText(ChatScreen.this,"failed",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.d(TAG, "onFailure: hata"+t);

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
private void currentUser(String receiverID){
    SharedPreferences.Editor editor= getSharedPreferences("PREFS",MODE_PRIVATE).edit();
    editor.putString("currentuser",receiverID);
    editor.apply();

}
    private void status(String status) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap);
    }

    protected void onResume() {

        status("Çevrim İçi");
        super.onResume();
        currentUser(receiverID);
    }

    protected void onPause() {

        status("");
        super.onPause();
        currentUser("none");
    }

    public String getStatus() {

        Intent intent = getIntent();
        receiverID = intent.getStringExtra("userID");
        FirebaseFirestore.getInstance().collection("Users").document(receiverID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                status = Objects.requireNonNull(value.get("status")).toString();
                binding.tvStatus.setText(status);
            }
        });
        return status;
    }

}