package com.aryotech.sohib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.aryotech.sohib.Fragment.HomeFragment;
import com.aryotech.sohib.Fragment.NotifFragment;
import com.aryotech.sohib.Fragment.ProfileFragment;
import com.aryotech.sohib.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bNavigation;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bNavigation = findViewById(R.id.bottom_nav);
        fragment = new HomeFragment();

        bNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, fragment).commit();
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
                    SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
                    editor.apply();
                    fragment = new ProfileFragment();
                    break;
            }

            if (fragment != null){

                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, fragment).commit();
            }

            return false;
        }
    };
}
