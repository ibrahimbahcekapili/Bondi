package com.tccbiko.bondi.fragements;

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
import com.tccbiko.bondi.chat.ChatList;
import com.tccbiko.bondi.chat.ChatListAdapter;
import com.tccbiko.bondi.databinding.FragmentChatsBinding;
import com.tccbiko.bondi.databinding.FragmentYakindaBinding;
import com.tccbiko.bondi.groupchat.GroupList;
import com.tccbiko.bondi.groupchat.GroupListAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    public GroupFragment() {
        // Required empty public constructor
    }

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
    private FragmentYakindaBinding binding;
    private List<GroupList> list;
    private RecyclerView recyclerView;
    private ArrayList<String> allGroupID;
    private Handler handler= new Handler();
    private GroupListAdapter adapter;
    private static final String TAG = "GroupFragment";
    private String groupname;
    private String photo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_yakinda,container,false);
        list= new ArrayList<>();
        allGroupID= new ArrayList<>();
        binding.recyclerView.setAdapter(new GroupListAdapter(list,getContext()));


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter= new GroupListAdapter(list,getContext());
        binding.recyclerView.setAdapter(adapter);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference();
        firestore=FirebaseFirestore.getInstance();

        getChatList();
        return binding.getRoot();
    }

    private void getChatList() {
        reference.child("GroupList").child(firebaseUser.getUid()).child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                list.clear();
                allGroupID.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren() ){
                    String groupid= snapshot.child("groupid").getValue().toString();
                    Log.d(TAG,"onDataChange: userID"+groupid);



                    allGroupID.add(groupid);




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
                for (String groupid:allGroupID){

reference.child("GroupIDs").child(groupid).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

        groupname= snapshot.child("groupname").getValue().toString();
        photo= snapshot.child("photo").getValue().toString();
        GroupList groupList=new GroupList(groupid,groupname,"","",photo);
if(!groupname.equals("")){
        list.add(groupList);}
        adapter.notifyItemChanged(0);
        if(list.size()!=0){
            binding.nogroup.setVisibility(View.INVISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull @NotNull DatabaseError error) {

    }
});



                }

                        }

        });


    }
}