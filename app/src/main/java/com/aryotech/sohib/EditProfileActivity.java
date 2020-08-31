package com.aryotech.sohib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryotech.sohib.model.Users;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivClose, ivSave, ivPp;
    private TextView tvUbahPp;
    private MaterialEditText etFullname, etUsername, etBio;

    private FirebaseUser fbUser;
    private StorageTask storageTask;
    private StorageReference storageReference;
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ivClose = findViewById(R.id.iv_close_edit_profile);
        ivSave = findViewById(R.id.iv_done_edit_profile);
        ivPp = findViewById(R.id.iv_pp_edit_profile);
        tvUbahPp = findViewById(R.id.tv_ubahpp_edit_profile);
        etFullname = findViewById(R.id.et_full_name_edit_profile);
        etUsername = findViewById(R.id.et_username_edit_profile);
        etBio = findViewById(R.id.et_bio_edit_profile);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("photos");

        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("users").document(fbUser.getUid());
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value!= null){
                    Users users = value.toObject(Users.class);
                    etFullname.setText(users.getFullName());
                    etUsername.setText(users.getUserName());
                    etBio.setText(users.getBio());
                    Glide.with(getApplicationContext()).load(users.getImageUrl()).into(ivPp);
                }


            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        tvUbahPp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity().setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);

            }
        });

        ivPp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity().setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);

            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fullname = etFullname.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String bio = etBio.getText().toString().trim();
                editProfile(fullname, username, bio);

            }
        });
    }

    private void editProfile(String fullname, String username, String bio){

        Map<String, Object> hasmap = new HashMap<>();
        hasmap.put("fullName", fullname);
        hasmap.put("userName", username);
        hasmap.put("bio", bio);

        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("users").document(fbUser.getUid());
        reference.update(hasmap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                uploadGambar();

            }
        });
    }

    private void uploadGambar(){

        /*final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setTitle("upload on process");
        progressDialog.show();*/

        if (imgUri != null){

            final StorageReference reference = storageReference.child(System.currentTimeMillis()+ "."+
                    PostActivity.getFileExtention(getApplicationContext(), imgUri));
            
            storageTask = reference.putFile(imgUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                   
                    if (!task.isSuccessful()){
                        throw task.getException();
                        
                    }
                    
                    return reference.getDownloadUrl();
                    
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    
                    if (task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        String uri = downloadUri.toString();
                        
                        Map<String, Object> hasmap = new HashMap<>();
                        hasmap.put("imageUrl", uri);
                        FirebaseFirestore.getInstance().collection("users")
                                .document(fbUser.getUid()).update(hasmap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        
                                        finish();
                                    }
                                });
                        
                        
                    }
                    else {
                        Toast.makeText(EditProfileActivity.this, "Missing upload image to server", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(EditProfileActivity.this, "on failure" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this, "you didn't select an image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgUri = result.getUri();
            ivPp.setImageURI(imgUri);
            
        }
        else{
            Toast.makeText(this, "There is something wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
