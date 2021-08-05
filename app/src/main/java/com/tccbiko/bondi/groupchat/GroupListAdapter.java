package com.tccbiko.bondi.groupchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tccbiko.bondi.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.Holder> {
private List<GroupList> list;
private Context context;
String theLastMessage;
String groupid;
FirebaseUser firebaseUser;
    String time;
    String day;

    private FirebaseFirestore firestore;

    public GroupListAdapter(List<GroupList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public GroupListAdapter.Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
    View view= LayoutInflater.from(context).inflate(R.layout.chat_list_item,parent,false);
    return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupListAdapter.Holder holder, int position) {
GroupList groupList =list.get(position);
holder.tvName.setText(groupList.getGroupName());
        firestore=FirebaseFirestore.getInstance();


LastMessage(groupList.getGroupid(), holder.tvDesc, holder.tvDate);

        Glide.with(context).load(groupList.getProfilePhoto()).into(holder.profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Groups groups = new Groups();
                groupid=groupList.getGroupid();
                context.startActivity(new Intent(context, GroupChatScreen.class)
                .putExtra("groupID",groupList.getGroupid())
                .putExtra("groupname",groupList.getGroupName())
                .putExtra("photo",groupList.getProfilePhoto())
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvName,tvDesc,tvDate;
        private CircleImageView profile;
        public Holder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvName=itemView.findViewById(R.id.user_name);
            tvDesc=itemView.findViewById(R.id.desc);
            tvDate=itemView.findViewById(R.id.chattime);
            profile=itemView.findViewById(R.id.picture);
        }
    }
    private void LastMessage(String groupid, TextView tvDesc,TextView tvDate){
        theLastMessage="default";
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("GroupChats").child(groupid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupChatObject group_chat_object = snapshot.getValue(GroupChatObject.class);
                    if (group_chat_object.getGroupid().equals(groupid))
                             {

                        theLastMessage = group_chat_object.getTextMessage();
                                 time=group_chat_object.getDateTime();
                                 day=group_chat_object.getDateDay();


                    }

                    tvDesc.setText(theLastMessage);
                    Date date = Calendar.getInstance().getTime();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                    String today = format.format(date);

                    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                    if(group_chat_object.getDateDay().equals(today)){
                        tvDate.setText(time);
                    }else {
                        tvDate.setText(day);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}


