package com.tccbiko.bondi.groupchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tccbiko.bondi.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {
    private List<GroupChatObject> list;
    private Context context;
    public static final int TYPE_LEFT=0;
    public static final int TYPE_RIGHT=1;
    public static final int TYPE_LEFT1=2;
    private FirebaseUser firebaseUser;



    public GroupChatAdapter(List<GroupChatObject> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public GroupChatAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false);
            return new ViewHolder(view);
        }
        else if(viewType==TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left1, parent, false);
            return new ViewHolder(view);
        }

    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupChatAdapter.ViewHolder holder, int position) {

      holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage;
        private TextView time;
        private TextView sender;
        private ImageView pp;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textMessage=itemView.findViewById(R.id.text_message);
            time=itemView.findViewById(R.id.time);
            sender=itemView.findViewById(R.id.sender);
            pp= itemView.findViewById(R.id.pp);

        }
        void bind(GroupChatObject group_chat_object ){
            //getsenderInformation();
            textMessage.setText(group_chat_object.getTextMessage());

            Date date = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            String today = format.format(date);

            Glide.with(context).load(group_chat_object.getPp()).into(pp);
            sender.setText(group_chat_object.getSendername());
            Calendar currentDateTime = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String currentTime = df.format(currentDateTime.getTime());
            if(group_chat_object.getDateDay().equals(today)){
                time.setText(group_chat_object.getDateTime());
            }else {
                time.setText(group_chat_object.getDateDay()+","+group_chat_object.getDateTime());
            }


        }
    }





    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())) {
            return TYPE_RIGHT;
        } else if (!list.get(position).getSender().equals(firebaseUser.getUid())&& position==0 || !list.get(position).getSender().equals(list.get(position-1).getSender())  ) {
            return TYPE_LEFT;
       }
        else
            {
            return TYPE_LEFT1;
        }



    }
    }

