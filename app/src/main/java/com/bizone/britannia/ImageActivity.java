package com.bizone.britannia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.utilities.TouchImageView;

/**
 * Created by siddhesh on 8/1/16.
 */
public class ImageActivity extends AppCompatActivity {

    private static final String TAG=ImageActivity.class.getSimpleName();

    TouchImageView img;
    Bitmap photo;
    Bitmap mBitmap;
    String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        img = (TouchImageView)findViewById(R.id.imageView1);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        Logger.d(TAG, "path="+path);
        Bitmap newBitmap = BitmapFactory.decodeFile(path);

        try {
            mBitmap = Bitmap.createBitmap(newBitmap);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        if(mBitmap != null){
            img.setImageBitmap(mBitmap);
        }
//        img.setImageBitmap(photo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


}

