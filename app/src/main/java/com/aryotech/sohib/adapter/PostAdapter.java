package com.aryotech.sohib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aryotech.sohib.model.Post;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.core.UserData;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context context;
    public List<Post> mPost;

    private FirebaseUser fbUser;

    public PostAdapter(Context context, List<Post> mPost){
        this.context = context;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);

        Glide.with(context).load(post.getPostImage()).into(holder.postImage);

        if (post.getDescription().equals("")){

            holder.desc.setVisibility(View.GONE);
        }
        else {

            holder.desc.setVisibility(View.VISIBLE);
            holder.desc.setText(post.getDescription());

        }

        publisherInfo(holder.imgProfile, holder.userName, holder.publisher, post.getPublisher());

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgProfile, postImage, like, comment, save;
        public TextView userName, likes, publisher, desc, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.img_user_postitem);
            postImage = itemView.findViewById(R.id.img_photo_postitem);
            like = itemView.findViewById(R.id.iv_like_postitem);
            comment = itemView.findViewById(R.id.iv_comment_postitem);
            save = itemView.findViewById(R.id.iv_save_postitem);
            userName = itemView.findViewById(R.id.tv_username_postitem);
            likes = itemView.findViewById(R.id.tv_like_postitem);
            publisher = itemView.findViewById(R.id.tv_publisher_postitem);
            desc = itemView.findViewById(R.id.tv_descript_postitem);
            comments = itemView.findViewById(R.id.tv_comment_postitem);

        }
    }

    private void publisherInfo(final ImageView imgProfile, final TextView userName, final TextView publisher, final String userId){

        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(userId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                assert  value != null;
                Users users = value.toObject(Users.class);
                assert users != null;

                Glide.with(context).load(users.getImageUrl()).into(imgProfile);
                userName.setText(users.getUserName());
                publisher.setText(users.getUserName());
            }
        });
    }
}
