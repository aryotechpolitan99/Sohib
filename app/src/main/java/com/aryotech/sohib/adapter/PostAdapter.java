package com.aryotech.sohib.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryotech.sohib.CommentActivity;
import com.aryotech.sohib.MainActivity;
import com.aryotech.sohib.fragment.ProfileFragment;
import com.aryotech.sohib.model.Post;
import com.aryotech.sohib.model.Users;
import com.aryotech.sohib.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.core.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context context;
    public List<Post> postList;
    private static final String TAG = "PostAdapter";
    public static final String ID_POST = "idPost";


    public FirebaseUser fbUser;

    public PostAdapter(Context context, List<Post> postList){
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = postList.get(position);

        Glide.with(context).load(post.getPostImage()).apply(new RequestOptions()
        .placeholder(R.drawable.status1)).into(holder.postImage);

        if (post.getDescription().equals("")){

            holder.desc.setVisibility(View.GONE);
        }
        else {

            holder.desc.setVisibility(View.VISIBLE);
            holder.desc.setText(post.getDescription());

        }

        publisherInfo(holder.imgProfile, holder.userName, holder.publisher, post.getPublisher());
        getComments(post.getPostId(), holder.comments);
        diSukai(post.getPostId(), holder.ivLike);
        penyuka(holder.totalLikes, post.getPostId());

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.ivLike.getTag().equals("sukai")){
                    final Map<String, Object> data = new HashMap<>();
                    data.put(fbUser.getUid(), true);
                    FirebaseFirestore.getInstance().collection("like")
                            .document(post.getPostId()).set(data, SetOptions.merge());

                    addNotifikasi(post.getPublisher(), post.getPostId());

                }
                else {

                    final Map<String, Object> data = new HashMap<>();
                    data.put(fbUser.getUid(), FieldValue.delete());
                    final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("like")
                            .document(post.getPostId());

                    documentReference.update(data);

                }
            }
        });

        holder.ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra(CommentActivity.ID_POST, post.getPostId());
                intent.putExtra(CommentAdapter.ID_PUBlISHER, post.getPublisher());
                context.startActivity(intent);

            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra(CommentActivity.ID_POST, post.getPostId());
                intent.putExtra(CommentAdapter.ID_PUBlISHER, post.getPublisher());
                context.startActivity(intent);

            }
        });

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();
                editor.putString(MainActivity.KEY, post.getPublisher());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frag_container, new ProfileFragment()).commit();

            }
        });

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();
                editor.putString(MainActivity.KEY, post.getPublisher());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frag_container, new ProfileFragment()).commit();

            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();
                editor.putString(MainActivity.KEY, post.getPublisher());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frag_container, new ProfileFragment()).commit();

            }
        });

    }

   private void penyuka(final TextView totalLikes, final String idPost){

        FirebaseFirestore.getInstance().collection("like").document(idPost)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        int jumlah = 0;
                        String template = "0 menyukai";
                        if (value != null){
                            if (value.exists()){
                                jumlah = value.getData().size();
                                template = jumlah + "menyukai";

                            }
                        }

                        totalLikes.setText(template);

                    }
                });

   }

    private void diSukai(final String postId, final ImageView postImage) {

        final DocumentReference reference = FirebaseFirestore.getInstance().collection("like").document(postId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                String id = fbUser.getUid();
                if (value != null){
                    if (value.exists()){
                        if (value.get(id) != null && value.getBoolean(id)){
                            postImage.setTag("disukai");
                            postImage.setImageResource(R.drawable.like_red);

                        }
                        else {

                            postImage.setTag("sukai");
                            postImage.setImageResource(R.drawable.like_black);

                        }
                    }
                    else {

                        postImage.setTag("sukai");

                    }
                }

            }
        });
    }

    private void getComments(final String postId, final TextView comments) {

        final  DocumentReference reference = FirebaseFirestore.getInstance().collection("comments").document(postId);
        reference.collection(postId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                int jumlah = 0;
                String template = "0 komentar";
                if (value != null){
                    for (DocumentSnapshot snapshot : value){

                        if (snapshot.exists()){
                            jumlah++;
                            template = jumlah + "komentar";

                        }
                    }
                }

                comments.setText(template);

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgProfile, postImage, ivLike, ivComment, ivSave;
        public TextView userName, totalLikes, publisher, desc, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.img_user_postitem);
            postImage = itemView.findViewById(R.id.img_photo_postitem);
            ivLike = itemView.findViewById(R.id.iv_like_postitem);
            ivComment = itemView.findViewById(R.id.iv_comment_postitem);
            ivSave = itemView.findViewById(R.id.iv_save_postitem);
            userName = itemView.findViewById(R.id.tv_username_postitem);
            totalLikes = itemView.findViewById(R.id.tv_like_postitem);
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

                if (value != null){
                    Users users = value.toObject(Users.class);
                    assert users != null;

                    Glide.with(context).load(users.getImageUrl()).into(imgProfile);
                    userName.setText(users.getUserName());
                    publisher.setText(users.getUserName());
                }

            }
        });
    }

    private void addNotifikasi(String idUser, String idPost){

        DocumentReference reference = FirebaseFirestore.getInstance().collection("notifikasi").document(idPost);
        Map<String, Object> dataNotif = new HashMap<>();
        dataNotif.put("idUser", fbUser.getUid());
        dataNotif.put("comments", "menyukai postingan");
        dataNotif.put("isPost", true);
        dataNotif.put("idPost", idPost);
        reference.set(dataNotif);

    }
}
