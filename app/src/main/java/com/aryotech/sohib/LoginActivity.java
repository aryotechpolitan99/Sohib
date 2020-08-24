package com.aryotech.sohib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aryotech.sohib.model.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, pass;
    private Button btnLogin;
    private TextView txt_signup;
    private ProgressBar progressBar;
    private ImageView ivLoginGoogle;

    private FirebaseUser fbUser;
    private FirebaseFirestore fbStore;
    private FirebaseAuth fbAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email_activitylogin);
        pass = findViewById(R.id.et_pass_activitylogin);
        btnLogin = findViewById(R.id.btn_login_activity);
        txt_signup = findViewById(R.id.txt_signup);
        ivLoginGoogle = findViewById(R.id.iv_google_signin);
        progressBar = findViewById(R.id.progbar);

        fbStore = FirebaseFirestore.getInstance();
        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();

       /* if (fbUser != null){

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }*/
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                String strEmail = etEmail.getText().toString().trim();
                String strPass = pass.getText().toString().trim();

                if (validasi()) {
                    if (RegisterActivity.isValidEmail(strEmail)){
                        loginEmail(strEmail, strPass);

                    }
                    else{

                        etEmail.setError("email tidak valid");
                        pass.requestFocus();
                    }
                }
            }
         });
        
        ivLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                loginGoogle();
                
            }
        });

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, signInOptions);
        
}

    private void loginGoogle() {

        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private boolean validasi() {

        boolean valid = true;
        if (pass.getText().toString().matches("")){

            pass.setError("please fill in the password");
            pass.requestFocus();
            valid = false;

        }
        if (pass.getText().toString().matches("")){

            pass.setError("email cannot be empty");
            pass.requestFocus();
            valid = false;

        }
        progressBar.setVisibility(View.GONE);
        return valid;

    }

    private void loginEmail(final String email, String password){

        fbAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            fbStore = FirebaseFirestore.getInstance();
                            DocumentReference reference = fbStore.collection("users")
                                    .document(fbAuth.getCurrentUser().getUid());
                            reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                }
                            });
                        }
                        else {

                            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                            switch (errorCode){

                                case "ERROR_WRONG_PASSWORD":
                                    progressBar.setVisibility(View.GONE);
                                    pass.setError("your password is wrong");
                                    pass.requestFocus();
                                    break;

                                case "ERROR_USERS_NOT_FOUND":
                                    progressBar.setVisibility(View.GONE);
                                    etEmail.setError("email has not been registered");
                                    etEmail.requestFocus();
                                    break;
                            }
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                assert account!= null;
                fbAuthWithGoogle(account);

            }
            catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void fbAuthWithGoogle(GoogleSignInAccount account){

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fbAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    fbStore = FirebaseFirestore.getInstance();
                    DocumentReference reference = fbStore.collection("users")
                            .document(fbAuth.getCurrentUser().getUid());
                    reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                    });

                    String idUsers = fbAuth.getCurrentUser().getUid();
                    String fullName = fbAuth.getCurrentUser().getDisplayName();
                    String userName = fbAuth.getCurrentUser().getDisplayName();
                    String imageUrl = "https://firebasestorage.googleapis.com/v0/b/sohib-42589.appspot.com/o/photos%2F1598004533668.jpg?alt=media&token=d1b24fdc-bfe2-4296-9543-157ddfbeec28";
                    Users users = new Users(idUsers, userName, fullName, imageUrl, "");

                    fbStore.collection("users").document(idUsers).set(users);
                }
            }
        });
    }

}
