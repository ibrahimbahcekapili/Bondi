package com.tccbiko.bondi.contacts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.chat.ChatScreen;
import com.tccbiko.bondi.user.Users;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<Users> list;
    private Context context;

    public ContactsAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users user = list.get(position);

        holder.username.setText(user.getUserName());
        holder.desc.setText(user.getUserPhone());

        Glide.with(context).load(user.getProfilePhoto()).into(holder.imageProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatScreen.class)
                        .putExtra("userID",user.getUserID())
                        .putExtra("userName",user.getUserName())
                        .putExtra("status",user.getStatus())
                        .putExtra("userProfile",user.getProfilePhoto()));

            }

        }
        );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageProfile;
        private TextView username,desc;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.tv_username);
            desc = itemView.findViewById(R.id.tv_desc);
        }
    }
}
