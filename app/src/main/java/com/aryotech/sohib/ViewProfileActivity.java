package com.aryotech.sohib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aryotech.sohib.fragment.ProfileFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ViewProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imgViewPp, close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Intent intent = getIntent();
        String image = intent.getStringExtra(ProfileFragment.KEY_IMAGE);
        String userName = intent.getStringExtra(ProfileFragment.USER_NAME);

        close = findViewById(R.id.iv_close_viewprofile);
        imgViewPp = findViewById(R.id.iv_view_detailprofil);
        toolbar = findViewById(R.id.toolbar_viewprofile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(userName);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorwhite));

        Toast.makeText(this,userName, Toast.LENGTH_SHORT).show();

        Glide.with(getApplicationContext()).load(image).apply(new RequestOptions().placeholder(R.drawable.status1)).into(imgViewPp);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }
}
