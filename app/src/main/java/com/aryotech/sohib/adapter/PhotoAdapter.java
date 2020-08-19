package com.aryotech.sohib.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryotech.sohib.CommentActivity;
import com.aryotech.sohib.MainActivity;
import com.aryotech.sohib.R;
import com.aryotech.sohib.fragment.PostDetailFragment;
import com.aryotech.sohib.model.Post;
import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context context;
    private List<Post> postList;

    public PhotoAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;

    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position) {

        final Post post = postList.get(position);
        Glide.with(context).load(post.getPostImage()).into(holder.ivPostPhoto);

        holder.ivPostPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();
                editor.putString(CommentActivity.ID_POST, post.getPostId());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frag_container, new PostDetailFragment()).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPostPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPostPhoto = itemView.findViewById(R.id.iv_photo_post);
        }
    }
}
