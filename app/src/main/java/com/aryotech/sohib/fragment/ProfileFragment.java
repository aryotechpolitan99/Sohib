package com.aryotech.sohib.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.aryotech.sohib.EditProfileActivity;
import com.aryotech.sohib.LoginActivity;
import com.aryotech.sohib.MainActivity;
import com.aryotech.sohib.ViewProfileActivity;
import com.aryotech.sohib.adapter.PhotoAdapter;
import com.aryotech.sohib.model.Post;
import com.aryotech.sohib.model.Users;
import com.aryotech.sohib.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    private Button editProfile;
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
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

        getUserInfo();
        getFollowers();
        getNrPosts();
        getPhoto();


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String btnEdit = editProfile.getText().toString();

                if (btnEdit.equals("Edit Profile")){

                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
                else if (btnEdit.equals("follow")){

                    Map<String, Object> dataFollowing = new HashMap<>();
                    dataFollowing.put(profileId,true);
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("follow").document(fbUser.getUid())
                            .collection("following").document(fbUser.getUid()).set(dataFollowing);

                    Map<String, Object> dataFollowers = new HashMap<>();
                    dataFollowers.put(fbUser.getUid(), true);
                    firestore.collection("follow").document(profileId)
                            .collection("followers").document(fbUser.getUid()).set(dataFollowing);

                    addNotifikasi();

                }
                else if (btnEdit.equals("unfollow")){

                    Map<String,Object> dataFollowing = new HashMap<>();
                    dataFollowing.put(profileId, true);
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("follow").document(fbUser.getUid())
                            .collection("following").document(profileId).delete();

                    Map<String, Object> dataFollowers = new HashMap<>();
                    dataFollowers.put(fbUser.getUid(), true);
                    firestore.collection("follow").document(profileId)
                            .collection("followers").document(fbUser.getUid()).delete();

                }
            }
        });

        ivOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                alert.setTitle("yess..");
                alert.setCancelable(false);
                alert.setMessage("are sure you want to log out?");
                alert.setPositiveButton("logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        logout();
                        Toast.makeText(getContext(), "logout", Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton("canceled", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                });

                alert.create().show();

            }
        });

        ivImgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(profileId);
                reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (getContext() == null){
                            return;

                        }

                        Users users = value.toObject(Users.class);
                        Intent intent = new Intent(getContext(), ViewProfileActivity.class);
                        intent.putExtra(KEY_IMAGE, users.getImageUrl());
                        intent.putExtra(USER_NAME, users.getUserName());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                });
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


    private void cekFollow(){

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("follow").document(fbUser.getUid()).collection("following");
        collectionReference.document(profileId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                assert value != null;
                if (value.exists()){

                    editProfile.setText("unfollow");
                    editProfile.setBackground(getResources().getDrawable(R.drawable.bg_unfollow));

                }
                else {

                    editProfile.setText("follow");
                    editProfile.setBackground(getResources().getDrawable(R.drawable.button_bg));

                }
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

    private  void addNotifikasi(){

        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("notifikasi").document(profileId);
        Map<String, Object> dataNotif = new HashMap<>();
        dataNotif.put("idUser", fbUser.getUid());
        dataNotif.put("comments", "following you");
        dataNotif.put("idPost", "");
        dataNotif.put("isPost", false);
        reference.set(dataNotif);

    }

    private void logout(){

        FirebaseAuth.getInstance().signOut();
        FirebaseAuth client = null;
        client.signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Objects.requireNonNull(this.getActivity()).finish();

    }
}
