package com.aryotech.sohib.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter {

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgProfile, postImage, like, comment, save;
        public TextView userName, likes, publisher, desc, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
