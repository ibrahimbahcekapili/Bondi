package com.tccbiko.bondi.chat;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tccbiko.bondi.MainActivity;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.user.Users;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {
private List<ChatList> list;
private Context context;
private String theLastMessage;
private String receiverID;
private FirebaseUser firebaseUser;
private String time;
private String day;

    private FirebaseFirestore firestore;

    public ChatListAdapter(List<ChatList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ChatListAdapter.Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
    View view= LayoutInflater.from(context).inflate(R.layout.chat_list_item,parent,false);
    return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatListAdapter.Holder holder, int position) {
ChatList chatList =list.get(position);
holder.tvName.setText(chatList.getUserName());
        firestore=FirebaseFirestore.getInstance();
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

               delete(chatList.getUserID());
               notifyDataSetChanged();
               notifyItemChanged(position);
           context.startActivity(new Intent(context,MainActivity.class));
                return true;

            }

        });

LastMessage(chatList.getUserID(), holder.tvDesc, holder.tvDate);


        Glide.with(context).load(chatList.getProfilePhoto()).into(holder.profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Users users= new Users();


                receiverID=chatList.getUserID();

                context.startActivity(new Intent(context, ChatScreen.class)
                .putExtra("userID",chatList.getUserID())
                .putExtra("userName",chatList.getUserName())
                .putExtra("userProfile",chatList.getProfilePhoto()));

            }

        });
    }

    private void delete(String position) {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        Query query= databaseReference.orderByKey().equalTo(position);
       query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    snapshot1.getRef().removeValue();
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("ChatIDs").child(firebaseUser.getUid());
        Query query1= databaseReference1.orderByKey().equalTo(position);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot2: snapshot.getChildren()){
                    snapshot2.getRef().removeValue();
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    /*public String getStatus() {

        FirebaseFirestore.getInstance().collection("Users").document(receiverID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                status = Objects.requireNonNull(value.get("status")).toString();
            }
        });
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }*/
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
    private void LastMessage(String receiverID, TextView tvDesc,TextView tvDate){
        ChatObject chat_object=new ChatObject();
        theLastMessage="default";
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ChatList").child(firebaseUser.getUid()).child(receiverID).child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatObject chat_object = snapshot.getValue(ChatObject.class);
                    if (chat_object.getSender().equals(firebaseUser.getUid()) && chat_object.getReceiver().equals(receiverID)
                            || chat_object.getReceiver().equals(firebaseUser.getUid()) && chat_object.getSender().equals(receiverID)) {

                        theLastMessage = chat_object.getTextMessage();
                        time=chat_object.getDateTime();
                        day=chat_object.getDateDay();


                    }

                    tvDesc.setText(theLastMessage);
                    Date date = Calendar.getInstance().getTime();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                    String today = format.format(date);


                    Calendar currentDateTime = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                    String currentTime = df.format(currentDateTime.getTime());
                    if(chat_object.getDateDay().equals(today)){
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


