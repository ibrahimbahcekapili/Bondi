package com.tccbiko.bondi.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tccbiko.bondi.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatObject> list;
    private Context context;
    public static final int TYPE_LEFT=0;
    public static final int TYPE_RIGHT=1;
    private FirebaseUser firebaseUser;



    public ChatAdapter(List<ChatObject> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left1, parent, false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);
            return new ViewHolder(view);
        }

    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatAdapter.ViewHolder holder, int position) {

      holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage;
        private TextView time;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textMessage=itemView.findViewById(R.id.text_message);
            time=itemView.findViewById(R.id.time);

        }
        void bind(ChatObject chat_object ){
            textMessage.setText(chat_object.getTextMessage());
            Date date = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            String today = format.format(date);


            Calendar currentDateTime = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String currentTime = df.format(currentDateTime.getTime());
            if(chat_object.getDateDay().equals(today)){
                time.setText(chat_object.getDateTime());
            }else {
                time.setText(chat_object.getDateDay()+","+chat_object.getDateTime());
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).getSender().equals(firebaseUser.getUid())){
            return  TYPE_RIGHT;
        }else {
            return TYPE_LEFT;
        }

    }
}
