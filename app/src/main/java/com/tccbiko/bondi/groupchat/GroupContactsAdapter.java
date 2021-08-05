package com.tccbiko.bondi.groupchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.user.Users;

import java.util.List;

public class GroupContactsAdapter extends RecyclerView.Adapter<GroupContactsAdapter.ViewHolder> {
    private List<Users> list;
    private Context context;

    public GroupContactsAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_contact,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users user = list.get(position);

       holder.username.setText(user.getUserName());
        holder.desc.setText(user.getUserPhone());
        holder.add.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               list.get(holder.getAdapterPosition()).setSelected(isChecked);



            }
        });

        Glide.with(context).load(user.getProfilePhoto()).into(holder.imageProfile);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageProfile;
        private TextView username,desc;
        private CheckBox add;
        private Button create;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.tv_username);
            desc = itemView.findViewById(R.id.tv_desc);
            add= itemView.findViewById(R.id.add);
            create=itemView.findViewById(R.id.create);
        }
    }
}
