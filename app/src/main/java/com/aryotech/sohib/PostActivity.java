package com.aryotech.sohib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Uri imgUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageRef;

    private ImageView ivClose, ivImageAdd;
    private TextView post;
    private EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ivClose = findViewById(R.id.iv_close);
        ivImageAdd = findViewById(R.id.iv_added);
        post = findViewById(R.id.tv_post);
        description = findViewById(R.id.et_desc);

        storageRef = FirebaseStorage.getInstance().getReference("posts");

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();
            }
        });

        CropImage.activity().setAspectRatio(1, 1)
                .start(PostActivity.this);
    }

    public static String getFileExtention(Context context, Uri uri){

        String extention;

       if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)){

           final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
           extention = mimeTypeMap.getExtensionFromMimeType(context.getContentResolver().getType(uri));

       }
       else {

           extention = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
       }
        return extention;

    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Upload....");
        progressDialog.show();

        if (imgUri != null) {

            final StorageReference fileReff = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtention(getApplicationContext(), imgUri));
            uploadTask = fileReff.putFile(imgUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isComplete()) {
                        throw task.getException();
                    }
                    return fileReff.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        CollectionReference reference = FirebaseFirestore.getInstance().collection("photos");
                        String idPost = reference.document().getId();
                        Toast.makeText(PostActivity.this, "id document " + idPost, Toast.LENGTH_SHORT).show();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postId", idPost);
                        hashMap.put("postimage", myUrl);
                        hashMap.put("description", description.getText().toString().trim());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.document(idPost).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    progressDialog.dismiss();
                                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                                    Toast.makeText(PostActivity.this, "Success upload to database", Toast.LENGTH_SHORT).show();
                                    finish();

                                } else {

                                    Toast.makeText(PostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {

                        Toast.makeText(PostActivity.this, "Your dont choose image", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {

                    Toast.makeText(PostActivity.this, "On canceled listener", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(PostActivity.this, "On failure" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgUri = result.getUri();

            ivImageAdd.setImageURI(imgUri);
        }
        else {

            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
}
