package com.example.mark.activityplanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView image;
    private ImageButton ibtn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image);

        image = findViewById(R.id.iv_image_id);
        ibtn_close = findViewById(R.id.ib_close_id);

        ibtn_close.setOnClickListener(this);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");

        Glide.with(this)
                .load(imageUrl)
                .into(image);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.ib_close_id:{
                finish();
                break;
            }
        }
    }
}
