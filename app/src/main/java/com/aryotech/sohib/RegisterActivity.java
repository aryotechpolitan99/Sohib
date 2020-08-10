package com.aryotech.sohib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, password;
    Button btnRegister;
    TextView txt_login;
    String userId;

    FirebaseAuth fbAuth;
    DatabaseReference dbRef;
    ProgressDialog progressDialog;
    FirebaseFirestore fbFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.et_username);
        fullname = findViewById(R.id.et_fullname);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_pass);
        btnRegister = findViewById(R.id.btn_regist_activity);
        txt_login = findViewById(R.id.txt_login);

        fbFirestore = FirebaseFirestore.getInstance();

       /* if (fbAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }*/

        fbAuth = FirebaseAuth.getInstance();
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strEmail = email.getText().toString().trim();
                String strPass = password.getText().toString().trim();
                String strUsernm = username.getText().toString();
                String strFullnm = fullname.getText().toString();

                if (TextUtils.isEmpty(strEmail)){
                    email.setError("Email is Required !");
                    return;
                }

                if (TextUtils.isEmpty(strPass)){
                    password.setError("Password is Required !");
                    return;
                }

                if (strPass.length() < 6){
                    password.setError("Password must be >= 6 characters");
                    return;
                }


                fbAuth.createUserWithEmailAndPassword(strEmail, strPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            Toast.makeText(RegisterActivity.this, "User Created ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                        }
                        else {

                            Toast.makeText(RegisterActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });











    }
}