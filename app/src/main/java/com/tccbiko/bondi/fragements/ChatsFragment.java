package com.tccbiko.bondi.fragements;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.tccbiko.bondi.MainActivity;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.chat.ChatList;
import com.tccbiko.bondi.chat.ChatListAdapter;
import com.tccbiko.bondi.databinding.FragmentChatsBinding;
import com.tccbiko.bondi.notification.MyFirebaseIdeService;
import com.tccbiko.bondi.notification.Token;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {


    private static final String TAG = "ChatsFragment";

    public ChatsFragment() {
        // Required empty public constructor
    }
private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
private FragmentChatsBinding binding;
    private List<ChatList> list;
    private RecyclerView recyclerView;
    private ArrayList<String> allUserID;
    private Handler handler= new Handler();
    private ChatListAdapter adapter;
    private String token;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
    @Override
    public void onComplete(@NonNull @NotNull Task<String> task) {
        if (!task.isSuccessful()) {
            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
            return;
        }

        // Get new FCM registration token
         token = task.getResult();



    }
});
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_chats,container,false);
        list= new ArrayList<>();
        allUserID= new ArrayList<>();
        binding.recyclerView.setAdapter(new ChatListAdapter(list,getContext()));


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
adapter= new ChatListAdapter(list,getContext());
binding.recyclerView.setAdapter(adapter);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference();
        firestore=FirebaseFirestore.getInstance();

       getChatList();
        return binding.getRoot();
    }

    private void getChatList() {


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference.child("ChatIDs").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                list.clear();
               allUserID.clear();

                for (DataSnapshot snapshot1: dataSnapshot.getChildren() ){
                    String userID= snapshot1.child("chatid").getValue().toString();
                    Log.d(TAG,"onDataChange: userID"+userID);
                   allUserID.add(userID);
                }

                getUserInfo();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }



private void getUserInfo(){

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (String userID:allUserID){

                    firestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ChatList chat=new ChatList(documentSnapshot.getString("userID"), documentSnapshot.getString("userName"),
                                    "Yakında",
                                    "Yakında", documentSnapshot.getString("profilePhoto"));

                            list.add(chat);
if(list.size()!=0){
    binding.nochat.setVisibility(View.INVISIBLE);
}
adapter.notifyItemChanged(0);
adapter.notifyDataSetChanged();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.d(TAG,"onFailure:Erro"+e.getMessage());
                        }
                    });

                }

            }
        });

updateToken(token);


}

    private void updateToken(String s) {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(s);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }
}