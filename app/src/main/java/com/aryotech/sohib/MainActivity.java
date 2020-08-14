package com.aryotech.sohib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.aryotech.sohib.adapter.CommentAdapter;
import com.aryotech.sohib.fragment.HomeFragment;
import com.aryotech.sohib.fragment.NotifFragment;
import com.aryotech.sohib.fragment.ProfileFragment;
import com.aryotech.sohib.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bNavigation;
    private Fragment fragment;

    public static final String DATA_UID = "PREF_UID";
    public static final String KEY = "ID_PROFILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bNavigation = findViewById(R.id.bottom_nav);
        fragment = new HomeFragment();

        bNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null){

            String publisher = intent.getString(CommentAdapter.ID_PUBlISHER);
            SharedPreferences.Editor editor = getSharedPreferences(DATA_UID, MODE_PRIVATE).edit();
            editor.putString(KEY, publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFragment()).commit();

        }
        else {

            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new HomeFragment()).commit();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){

                case R.id.home_nav:
                    fragment = new HomeFragment();
                    break;

                case R.id.search_nav:
                    fragment = new SearchFragment();
                    break;

                case R.id.post_nav:
                    fragment = null;
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                    break;

                case R.id.like_nav:
                    fragment = new NotifFragment();
                    break;

                case R.id.akun_nav:
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = getSharedPreferences(DATA_UID, MODE_PRIVATE).edit();
                    editor.putString(KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    fragment = new ProfileFragment();
                    break;
            }

            if (fragment != null){

                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, fragment).commit();
            }

            return true;
        }
    };
}
