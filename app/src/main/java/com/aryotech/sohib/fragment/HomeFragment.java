package com.aryotech.sohib.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aryotech.sohib.adapter.PostAdapter;
import com.aryotech.sohib.model.Post;
import com.aryotech.sohib.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followingList;
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView =view.findViewById(R.id.rv_home);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        checkFollowing();

        return view;
    }

    private void displayPost(){

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("photos");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                postList.clear();
                for (DocumentSnapshot snapshot : value) {

                    Post post = snapshot.toObject(Post.class);
                    Log.d(TAG, "onEvent : tampil" + snapshot);
                    Log.d(TAG, "onEvent : postingan" + post);

                    for (String id : followingList) {

                        if (post.getPublisher().equals(id)) {
                            postList.add(post);

                        }
                    }
                }

                postAdapter.notifyDataSetChanged();

            }

        });

    }

    private void checkFollowing(){

        followingList = new ArrayList<>();

        CollectionReference reference = FirebaseFirestore.getInstance().collection("follow")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("following");

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                followingList.clear();
                for (QueryDocumentSnapshot snapshot : value){

                    followingList.add(snapshot.getId());

                }

                displayPost();

            }
        });
    }

}
