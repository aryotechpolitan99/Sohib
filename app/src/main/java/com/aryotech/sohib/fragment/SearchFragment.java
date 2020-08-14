package com.aryotech.sohib.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.aryotech.sohib.adapter.UserAdapter;
import com.aryotech.sohib.model.Users;
import com.aryotech.sohib.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Users> listUsers;
    private static final String TAG = "SearchFrgament";
    private EditText searchBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.rv_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBar = view.findViewById(R.id.et_search);

        listUsers = new ArrayList<>();
        userAdapter = new UserAdapter(this.getContext(), listUsers);
        recyclerView.setAdapter(userAdapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence search, int start, int before, int count) {

                searchUsers(search.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        readUsers();

        return view;
    }

    private void searchUsers(String key){

        CollectionReference reference = FirebaseFirestore.getInstance().collection("users");
        reference.whereEqualTo("username", key).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        listUsers.clear();
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())){
                            Log.d(TAG, "onEvent : "+ snapshot);
                            Users users = snapshot.toObject(Users.class);
                            listUsers.add(users);
                        }
                    }
                });

    }

    private void readUsers(){

        FirebaseFirestore.getInstance().collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (searchBar.getText().toString().equals("")){

                            assert value != null;
                            for (DocumentSnapshot snapshot : value){

                                Users users = snapshot.toObject(Users.class);
                                listUsers.add(users);
                                Toast.makeText(getContext(), users.toString(), Toast.LENGTH_SHORT).show();

                            }

                            userAdapter.notifyDataSetChanged();

                        }
                    }
                });
    }
}
