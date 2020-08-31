package com.aryotech.sohib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aryotech.sohib.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, fullname, etEmail, password;
    private Button btnRegister;
    private TextView txt_login;
    private ProgressBar progressBar;
    private static final String TAG = "REGISTER_ACTIVITY";

    private FirebaseAuth fbAuth;
    private FirebaseFirestore fbFirestore;
    //private FirebaseUser fbUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.et_username);
        fullname = findViewById(R.id.et_fullname);
        etEmail = findViewById(R.id.et_email);
        password = findViewById(R.id.et_pass);
        btnRegister = findViewById(R.id.btn_regist_activity);
        txt_login = findViewById(R.id.txt_login);
        progressBar = findViewById(R.id.progbar);

        fbFirestore = FirebaseFirestore.getInstance();
        //fbUser = FirebaseAuth.getInstance().getCurrentUser();
        fbAuth = FirebaseAuth.getInstance();


        /*if (fbAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }*/
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                final String strEmail = etEmail.getText().toString().trim();
                final String strPass = password.getText().toString().trim();
                final String strUsernm = username.getText().toString();
                final String strFullnm = fullname.getText().toString();

                if (isValidEmail(strEmail)) {
                    if (validasi()) {
                        register(strUsernm, strFullnm, strEmail, strPass);
                    }
                } else {
                    if (!etEmail.getText().toString().isEmpty()) {
                        etEmail.setError("invalid email");
                    } else {
                        etEmail.setError("email cannot be empty");

                    }
                    etEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);

                }
            }
        });
    }
        private boolean validasi(){

            boolean valid = true;
            if (password.getText().toString().matches("")){

                password.setError("password cannot be empty");
                password.requestFocus();
                valid = false;

            }
            else if (password.getText().toString().length() < 6){

                password.setError("password must contain at least 6 characters");
                password.requestFocus();
                valid = false;

            }

            if(fullname.getText().toString().matches("")){

                password.setError("please fill in this field");
                password.requestFocus();
                valid = false;

            }
            if (username.getText().toString().matches("")){

                password.setError("please fill in this field");
                password.requestFocus();
                valid = false;

            }

            return valid;

        }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void register(final String userName, final String fullName, final String email, final String password){

        fbAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            progressBar.setVisibility(View.GONE);
                            FirebaseUser firebaseUser = fbAuth.getCurrentUser();
                            String idUser = firebaseUser.getUid();
                            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/sohib-42589.appspot.com/o/photos%2F1598004533668.jpg?alt=media&token=d1b24fdc-bfe2-4296-9543-157ddfbeec28";
                            Users users = new Users(idUser, userName, fullName, password, imageUrl,"" );

                            fbFirestore = FirebaseFirestore.getInstance();
                            fbFirestore.collection("users").document(idUser)
                                    .set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        progressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                }
                            });
                        }
                        else {
                            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                            if ("ERROR_EMAIL_ALREADY_IN_USE".equals(errorCode)){

                                progressBar.setVisibility(View.GONE);
                                etEmail.setError("email is already registered, please login");
                                etEmail.requestFocus();

                            }
                        }
                    }
                });

    }
}