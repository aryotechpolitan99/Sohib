package com.aryotech.sohib.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
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
import com.aryotech.sohib.R;
import com.aryotech.sohib.fragment.PostDetailFragment;
import com.aryotech.sohib.fragment.ProfileFragment;
import com.aryotech.sohib.model.Comments;
import com.aryotech.sohib.model.Notifikasi;
import com.aryotech.sohib.model.Post;
import com.aryotech.sohib.model.Users;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.ViewHolder> {

    private static final String TAG = "NotifAdapter";
    private Context context;
    private List<Notifikasi> notifikasiList;

    public NotifAdapter(Context context, List<Notifikasi> notifikasiList) {
        this.context = context;
        this.notifikasiList = notifikasiList;
    }

    @NonNull
    @Override
    public NotifAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.notifikasi_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifAdapter.ViewHolder holder, int position) {

        final Notifikasi notif = notifikasiList.get(position);
        Log.d(TAG,"onBindViewHOlder : notif publisher" + notif);

        holder.tvComments.setText(notif.getComments());
        getUserInfo(holder.circleImgUser, holder.tvUsername, notif.getIdUser());

        if (notif.getisPost()){

            holder.circleImgUser.setVisibility(View.VISIBLE);
            getPostImage(holder.photoPost, notif.getIdPost());

        }
        else {

            holder.circleImgUser.setVisibility(View.GONE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();

                if (notif.getisPost()){
                    editor.putString(CommentActivity.ID_POST, notif.getIdPost());
                    editor.apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, new PostDetailFragment()).commit();
                }
                else {
                    editor.putString(MainActivity.KEY, notif.getIdUser());
                    editor.apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, new ProfileFragment()).commit();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifikasiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView circleImgUser, photoPost;
        public TextView tvUsername, tvComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImgUser = itemView.findViewById(R.id.circleimg_user_itemnotif);
            photoPost = itemView.findViewById(R.id.iv_imgpost_itemnotif);
            tvUsername = itemView.findViewById(R.id.tv_username_itemnotif);
            tvComments = itemView.findViewById(R.id.tv_commenttar_itemnotif);
        }
    }

    private void getUserInfo(final ImageView circleImgUser, final TextView tvUsername, String publisher){

        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(publisher);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                assert value != null;
                Users users = value.toObject(Users.class);
                Log.d(TAG, "getUserInfo: "+ users);

                assert  users != null;
                Glide.with(context).load(users.getImageUrl()).into(circleImgUser);
                tvUsername.setText(users.getUserName());

            }
        });
    }

    private void getPostImage(final ImageView photoPost, String idUpload){

        DocumentReference reference = FirebaseFirestore.getInstance().collection("photos").document(idUpload);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                assert value != null;
                Post post = value.toObject(Post.class);
                assert post != null;
                Glide.with(context).load(post.getPostImage()).into(photoPost);

            }
        });

    }
}
