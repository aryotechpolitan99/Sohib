package com.aryotech.sohib.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryotech.sohib.fragment.ProfileFragment;
import com.aryotech.sohib.model.Users;
import com.aryotech.sohib.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<Users> listUsers;

    private FirebaseUser fbUser;

    public UserAdapter(Context context, List<Users> mUsers) {
        this.context = context;
        this.listUsers = mUsers;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder holder, int position) {

        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        final Users users = listUsers.get(position);

        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.userName.setText(users.getUserName());
        holder.fullName.setText(users.getFullName());

        Glide.with(context).load(users.getImageUrl()).into(holder.imgUser);
        isFollowing(users.getIdUsers(), holder.btnFollow, users.getIdUsers());

        if (users.getIdUsers().equals(fbUser.getUid())){
            holder.btnFollow.setVisibility(View.GONE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileId", users.getIdUsers());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_container, new ProfileFragment()).commit();

            }
        });

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.btnFollow.getText().toString().equals("follow")){

                    Map<String, Object> dataFollowing = new HashMap<>();
                    dataFollowing.put(users.getIdUsers(), true);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("follow").document(fbUser.getUid())
                            .collection("following").document(users.getIdUsers()).set(dataFollowing);

                    Map<String, Object> dataFollower = new HashMap<>();
                    dataFollower.put(fbUser.getUid(), true);
                    db.collection("follow").document(users.getIdUsers())
                            .collection("followers").document(fbUser.getUid()).set(dataFollowing);

                }
                else {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("follow").document(fbUser.getUid())
                            .collection("following").document(users.getIdUsers()).delete();
                    db.collection("follow").document(users.getIdUsers())
                            .collection("followers").document(fbUser.getUid()).delete();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName, fullName;
        CircleImageView imgUser;
        Button btnFollow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.username_useritem);
            fullName = itemView.findViewById(R.id.fullname_useritem);
            imgUser = itemView.findViewById(R.id.img_useritem);
            btnFollow = itemView.findViewById(R.id.btn_follow_useritem);
        }
    }

   private void isFollowing(final String userId, final Button button, final String following){

       FirebaseFirestore db = FirebaseFirestore.getInstance();
       DocumentReference reference = db.collection("follow").document(following).collection("following").document(userId);
       reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

               if (value != null && value.exists()){

                   button.setText("Following");

               }
               else {

                   button.setText("Follow");
               }
           }
       });
   }
}
