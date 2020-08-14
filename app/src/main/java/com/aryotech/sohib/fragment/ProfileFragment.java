package com.aryotech.sohib.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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


public class ProfileFragment extends Fragment {

    Button editProfile, btnLogout;
    ImageView imgProfile, option;
    TextView posts, followers, following, fullname, bio, userName;

    FirebaseUser fbUser;
    String profileId;
    ImageButton myPhotos, savePhotos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container,false);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences pref = getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE);
        profileId = pref.getString("profileid","none");

        userName = view.findViewById(R.id.tv_usernm_profile);
        fullname = view.findViewById(R.id.tv_fullname_profile);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        imgProfile = view.findViewById(R.id.img_user_profile);
        option = view.findViewById(R.id.img_option);
        editProfile = view.findViewById(R.id.btn_editprofile);
        myPhotos = view.findViewById(R.id.ib_my_photos);
        savePhotos = view.findViewById(R.id.ib_save_photos);

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

    private void userInfo(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getContext() == null){
                    return;
                }

                Users user = dataSnapshot.getValue(Users.class);

                Glide.with(getContext()).load(user.getImageUrl()).into(imgProfile);
                userName.setText(user.getUserName());
                fullname.setText(user.getFullName());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(fbUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(profileId).exists()){
                    editProfile.setTag("following");
                }
                else {
                    editProfile.setTag("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(){

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("followers");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followers.setText(""+ dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("following");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                following.setText(""+ dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
