package com.aryotech.sohib.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aryotech.sohib.Model.Users;
import com.aryotech.sohib.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter /*extends RecyclerView.Adapter<UserAdapter.ViewHolder> */{

    /*private Context context;
    private ArrayList<User> mUsers;
    private FirebaseUser fbUser;

    public UserAdapter(Context context, List<User> mUsers) {
        this.context = context;
        this.mUsers = (ArrayList<User>) mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        User users = mUsers.get(position);

        holder.username.setText(users.);

        holder.btnFollow.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, fullname;
        public CircleImageView imgUserItem;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_item);
            fullname = itemView.findViewById(R.id.fullname_item);
            imgUserItem = itemView.findViewById(R.id.img_user_item);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }*/
}
