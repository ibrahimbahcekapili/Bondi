package com.tccbiko.bondi.groupchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.databinding.ActivityGroupChatScreenBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GroupChatScreen extends AppCompatActivity {
    private ActivityGroupChatScreenBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    public String groupid;
    private GroupChatAdapter adapter;
    private List<GroupChatObject> list;
    private String userName;
    private String pp;





    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat_screen);
        firestore=FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        String userName = intent.getStringExtra("groupname");
        groupid = intent.getStringExtra("groupID");
       String userProfile = intent.getStringExtra("photo");
        binding.tvUsername.setText(userName);
        getmyInfo();

        Glide.with(this).load(userProfile).into(binding.imageProfile);
binding.btnBack.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        onBackPressed();
    }
});

        BtnClick();

        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);


        readChats();

    }

    private void readChats() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("GroupChats").child(groupid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupChatObject group_chat_object = snapshot.getValue(GroupChatObject.class);
                    if (group_chat_object.getGroupid().equals(groupid))

                    {

                        list.add(group_chat_object);
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    binding.recyclerView.scrollToPosition(list.size()-1);
                } else {
                    adapter = new GroupChatAdapter(list, GroupChatScreen.this);
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

        GroupChatObject group_chat_object = new GroupChatObject(currentTime,today,
                text, "TEXT", firebaseUser.getUid(), groupid,userName,pp);

        reference.child("GroupChats").child(groupid).push().setValue(group_chat_object).addOnSuccessListener(new OnSuccessListener<Void>() {
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


    }

    private void getmyInfo() {
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
              userName = Objects.requireNonNull(documentSnapshot.get("userName")).toString();
              pp = Objects.requireNonNull(documentSnapshot.get("profilePhoto")).toString();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("username Data", "onFailure" + e.getMessage());

            }
        });
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
    }

    protected void onPause() {

        status("");
        super.onPause();
    }



}