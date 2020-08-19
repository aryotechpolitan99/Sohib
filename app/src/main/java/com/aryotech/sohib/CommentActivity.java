package com.aryotech.sohib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryotech.sohib.adapter.CommentAdapter;
import com.aryotech.sohib.model.Comments;
import com.aryotech.sohib.model.Users;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    public static final String ID_POST = "idPost" ;
    private static final String TAG = "CommentActivity";
    private EditText etCommentar;
    private ImageView circleImgUser;
    private TextView send;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comments> commentsList;
    private String idPost, idPublisher;
    private FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        etCommentar = findViewById(R.id.et_add_comments);
        circleImgUser = findViewById(R.id.circleimg_user_comment);
        send = findViewById(R.id.tv_send_comments);
        recyclerView = findViewById(R.id.rv_comment);

        Toolbar toolbar = findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentsList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentsList);
        recyclerView.setAdapter(commentAdapter);
        
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        idPost = intent.getStringExtra(ID_POST);
        idPublisher = intent.getStringExtra(CommentAdapter.ID_PUBlISHER);
        
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (etCommentar.getText().toString().matches("")){

                    Toast.makeText(CommentActivity.this, "comments cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    
                    addDataComment();
                }
            }
        });
        
        getImgUser();
        readComments();
    }

    private void readComments() {

        FirebaseFirestore.getInstance().collection("comments").document(idPost).collection(idPost)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        //commentsList.clear();
                        for (DocumentSnapshot snapshot : value){

                            Comments comments = snapshot.toObject(Comments.class);
                            commentsList.add(comments);

                        }

                        commentAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void getImgUser() {

        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(fbUser.getUid());
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                Users users = task.getResult().toObject(Users.class);
                Log.d(TAG, "onComplete : dataUser " + users);
                Glide.with(getApplicationContext()).load(users.getImageUrl()).into(circleImgUser);

            }
        });
    }

    private void addDataComment() {

        Map<String, Object> dataComments = new HashMap<>();
        dataComments.put("isComments", etCommentar.getText().toString().trim());
        dataComments.put("commenters", fbUser.getUid());
        FirebaseFirestore.getInstance().collection("comments").document(idPost).collection(idPost)
                .add(dataComments).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                Toast.makeText(CommentActivity.this, "comment sent successfully", Toast.LENGTH_SHORT).show();
                addNotifikasi();
                etCommentar.setText("");

            }
        });
    }

    private void addNotifikasi() {

        DocumentReference reference =  FirebaseFirestore.getInstance().collection("notifikasi")
                .document(idPublisher);
        Map<String,Object> dataNotif = new HashMap<>();
        dataNotif.put("idUser", fbUser.getUid());
        dataNotif.put("comments", "komentar: "+ etCommentar.getText().toString());
        dataNotif.put("idPost", idPost);
        dataNotif.put("isPost", true);
        reference.set(dataNotif);

    }
}
