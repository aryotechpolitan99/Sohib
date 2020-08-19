package com.aryotech.sohib.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryotech.sohib.LoginActivity;
import com.aryotech.sohib.MainActivity;
import com.aryotech.sohib.adapter.PhotoAdapter;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    private Button editProfile, btnLogout;
    private ImageView ivImgProfile, ivOption;
    private TextView tvPosts, tvfollowers, tvFollowing, tvFullname, tvBio, tvUserName;

    private RecyclerView recyclerView;
    private List<Post> postList;
    private PhotoAdapter photoAdapter;
    private FirebaseUser fbUser;
    private String profileId;
    private ImageButton myPhotos, savePhotos;

    public static final String KEY_IMAGE = "idImage";
    public static final String USER_NAME = "userName";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container,false);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences = Objects.requireNonNull(getContext())
                .getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE);
        profileId = preferences.getString(MainActivity.KEY, "none");

        recyclerView = view.findViewById(R.id.rv_myposts_profile);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), postList);
        recyclerView.setAdapter(photoAdapter);

        tvUserName = view.findViewById(R.id.tv_usernm_profile);
        tvFullname = view.findViewById(R.id.tv_fullname_profile);
        tvPosts = view.findViewById(R.id.posts);
        tvfollowers = view.findViewById(R.id.followers);
        tvFollowing = view.findViewById(R.id.following);
        ivImgProfile = view.findViewById(R.id.img_user_profile);
        ivOption = view.findViewById(R.id.img_option);
        editProfile = view.findViewById(R.id.btn_editprofile);
        myPhotos = view.findViewById(R.id.ib_my_photos);
        savePhotos = view.findViewById(R.id.ib_save_photos);
        tvBio = view.findViewById(R.id.tv_bio);
        btnLogout = view.findViewById(R.id.btn_logout);

        getUserInfo();
        getFollowers();
        getNrPosts();
        getPhoto();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String btnEdit = editProfile.getText().toString();

                if (btnEdit.equals("Edit Profile")){

                }
                else if (btnEdit.equals("follow")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fbUser.getUid())
                            .child("following").child(profileId).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(fbUser.getUid()).setValue(true);

                }
                else if (btnEdit.equals("following")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fbUser.getUid())
                            .child("following").child(profileId).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(fbUser.getUid()).removeValue();
                }
            }
        });

        return view;
    }

    private void getUserInfo() {

        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(profileId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (getContext() == null) {
                    return;

                }

                assert value != null;
                Users users = value.toObject(Users.class);
                assert users != null;
                Glide.with(getContext()).load(users.getImageUrl()).into(ivImgProfile);
                tvUserName.setText(users.getUserName());
                tvFullname.setText(users.getFullName());
                tvBio.setText(users.getBio());

            }
        });
    }


    private void getFollowers() {

        CollectionReference collection1 = FirebaseFirestore.getInstance().collection("follow")
                .document(profileId).collection("followers");
        collection1.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                List<String> jumlah = new ArrayList<>();
                assert value != null;
                for (DocumentSnapshot snapshot : value) {

                    jumlah.add(value.getDocumentChanges().toString());

                }

                tvfollowers.setText(String.valueOf(jumlah.size()));

            }
        });

        CollectionReference collection2 = FirebaseFirestore.getInstance().collection("follow")
                .document(profileId).collection("following");
        collection2.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                List<String> jumlah = new ArrayList<>();
                assert value != null;
                for (DocumentSnapshot snapshot : value) {

                    jumlah.add(value.getDocumentChanges().toString());

                }

                tvFollowing.setText(String.valueOf(jumlah.size()));

            }
        });
    }

    private void getNrPosts() {

    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("photos");
    collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

            int i = 0;
            assert value != null;
            for (DocumentSnapshot snapshot : value){

                Post post = snapshot.toObject(Post.class);
                assert  post != null;
                if (post.getPublisher().equals(profileId)){
                    i++;

                }
            }

            tvPosts.setText(String.valueOf(i));

        }
    });

    }

    private void getPhoto(){

        FirebaseFirestore.getInstance().collection("photos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for (DocumentSnapshot snapshot : value){

                            Post post = snapshot.toObject(Post.class);
                            if (post.getPublisher().equals(profileId)){

                                postList.add(post);

                            }
                        }

                        Collections.reverse(postList);
                        photoAdapter.notifyDataSetChanged();

                    }
                });
    }
}
