package com.aryotech.sohib.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aryotech.sohib.MainActivity;
import com.aryotech.sohib.R;
import com.aryotech.sohib.model.Comments;
import com.aryotech.sohib.model.Users;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Comment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Comments> listcomments;
    public static final String ID_PUBlISHER = "idPublisher";

    private FirebaseUser fbUser;

    public CommentAdapter(Context context, List<Comments> listcomments) {
        this.context = context;
        this.listcomments = listcomments;

    }


    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comments comment = listcomments.get(position);

        holder.isComments.setText(comment.getIsComments());
        Log.d("ada",String.valueOf( comment.getIsComments()));
        getUserInfo(holder.circleImgUserItem, holder.tvCommenters, comment.getCommenters());

        holder.isComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(ID_PUBlISHER, comment.getCommenters());
                context.startActivity(intent);

            }
        });

        holder.circleImgUserItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(ID_PUBlISHER, comment.getCommenters());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return listcomments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView circleImgUserItem;
        public TextView tvCommenters, isComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImgUserItem = itemView.findViewById(R.id.circleimg_user_itemcomment);
            tvCommenters = itemView.findViewById(R.id.tv_username_itemcomment);
            isComments = itemView.findViewById(R.id.tv_comments_itemcomment);
        }
    }

    private void getUserInfo(final ImageView circleImgUserItem, final TextView tvCommenters, String publisher){

        FirebaseFirestore.getInstance().collection("users").document(publisher)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        Users users = value.toObject(Users.class);
                        Glide.with(context).load(users.getImageUrl()).into(circleImgUserItem);
                        tvCommenters.setText(users.getUserName());
                    }
                });
    }
}
