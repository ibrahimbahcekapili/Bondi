package com.tccbiko.bondi.groupchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.databinding.ActivityGroupContactsBinding;
import com.tccbiko.bondi.user.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupContactsActivity extends AppCompatActivity {

    private ActivityGroupContactsBinding binding;
    private List<Users> list = new ArrayList<>();
    private GroupContactsAdapter adapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private String key;
    public static final int REQUEST_READ_CONTACTS = 79;
    private ArrayList mobileArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_group_contacts);
        FloatingActionButton create=findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat();
                startActivity(new Intent(GroupContactsActivity.this, CreateGroup.class)
                .putExtra( "groupIDD",key));
                finish();
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();


          getContactFromPhone();



        if (mobileArray!=null) {
            getContactList();

        }
    }

    private void createChat() {

        key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        Boolean validChat = false;


        DatabaseReference chatInfoDb1 = FirebaseDatabase.getInstance().getReference().child("GroupIDs").child(key);
        HashMap newChatMap2 = new HashMap();
        newChatMap2.put("groupid" ,key);
        newChatMap2.put("groupname" ,"");
        newChatMap2.put("photo" ,"");



        chatInfoDb1.updateChildren(newChatMap2);

            DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("GroupList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("groups").child(key);


            HashMap newChatMap = new HashMap();
            newChatMap.put("groupid" ,key);
            newChatMap.put("role/" + FirebaseAuth.getInstance().getUid(),"manager");




            for(Users mUser : list){
                DatabaseReference chatInfoD = FirebaseDatabase.getInstance().getReference().child("GroupList").child(mUser.getUserID()).child("groups").child(key);

                if(mUser.getSelected()){

                    HashMap newChatMap1 = new HashMap();
                    validChat = true;
                    newChatMap1.put("groupid" ,key);
                    newChatMap1.put("role/" + mUser.getUserID(), "user");
                    if(validChat){
                    chatInfoD.updateChildren(newChatMap1);
                    chatInfoDb.updateChildren(newChatMap);
                }
            }
        }

    }

    private void getContactFromPhone() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArray = getAllPhoneContacts();
        } else {
            requestPermission();
        }

    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllPhoneContacts();
                } else {
                    finish();
                }
                return;
            }
        }
    }
    private ArrayList getAllPhoneContacts() {
        ArrayList<String> phoneList = new ArrayList<>();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null,null);
        while(phones.moveToNext()){
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneList.add(phone); }
        return phoneList;
    }

    private void getContactList() {

        firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots){
                    String userID = snapshots.getString("userID");
                    String userName = snapshots.getString("userName");
                    String imageUrl = snapshots.getString("profilePhoto");
                    String desc = snapshots.getString("bio");
                    String phone = snapshots.getString("userPhone");
                    String status= snapshots.getString("status");

                    Users user = new Users();
                    user.setUserID(userID);
                    user.setBio(desc);
                    user.setUserName(userName);
                    user.setProfilePhoto(imageUrl);
                    user.setUserPhone(phone);
                    user.setStatus(status);

                    if (userID != null && !userID.equals(firebaseUser.getUid())) {
                        if (mobileArray.contains(user.getUserPhone())){
                            list.add(user);
                        }
                    }
                }



                adapter = new GroupContactsAdapter(list, GroupContactsActivity.this);
                binding.recyclerView.setAdapter(adapter);
            }

        });



    }

}