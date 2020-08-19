package com.aryotech.sohib.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryotech.sohib.R;
import com.aryotech.sohib.adapter.NotifAdapter;
import com.aryotech.sohib.model.Notifikasi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotifFragment extends Fragment {

    private static final String TAG = "NotifFragment";
    private RecyclerView recyclerView;
    private NotifAdapter notifAdapter;
    private List<Notifikasi> notifikasiList;

    public FirebaseUser fbUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notif, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_notif);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        notifikasiList = new ArrayList<>();
        notifAdapter = new NotifAdapter(getContext(), notifikasiList);
        recyclerView.setAdapter(notifAdapter);

        readNotifikasi();

        return view;

    }

    private void readNotifikasi() {

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection("notifikasi")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        assert value != null;
                        notifikasiList.clear();
                        for (QueryDocumentSnapshot snapshot : value){

                            Notifikasi notifikasi = snapshot.toObject(Notifikasi.class);
                            notifikasiList.add(notifikasi);

                        }

                        Collections.reverse(notifikasiList);
                        notifAdapter.notifyDataSetChanged();

                    }
                });
    }
}
