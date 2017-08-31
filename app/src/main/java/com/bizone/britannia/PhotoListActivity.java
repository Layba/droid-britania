package com.bizone.britannia;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bizone.britannia.entities.LocationEntity;
import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.SaleMetadataEntity;
import com.bizone.britannia.entities.ShopEntity;
import com.bizone.britannia.location.LocationCaptureTask;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.SelectQueries;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by siddhesh on 7/26/16.
 */
public class PhotoListActivity extends AppCompatActivity {

    private static final String TAG = PhotoListActivity.class.getSimpleName();
    private Uri outputFileUri = null;
    private ImageView shop,slip;
    private ArrayList<SaleMetadataEntity> saleMetadataEntities ;
    private ShopEntity shopEntity;
    private static final int SHOP_CAMERA = 1;
    private static final int SLIP_CAMERA = 2;
    private int saleId, route_id;
    private boolean isLocationCaptured = false;
    private static final int MY_CAMERA = 1;
    private static final int MY_CAMERA_SLIP = 1;
    private LocationEntity locationEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photolist);
        Logger.d(TAG, "inside onCreate");
        shop=(ImageView)findViewById(R.id.shop_img);
        slip=(ImageView)findViewById(R.id.slip_img);
        shopEntity = (ShopEntity) getIntent().getSerializableExtra("ShopEntity");
        Logger.d(TAG, "shopEntity="+shopEntity.toString());
        saleId = getIntent().getIntExtra("saleId", -1);
        String status = Constants.CLOSED;
        if(saleId != -1) {
            SaleEntity entity = SelectQueries.getSaleElements(this, saleId);
            status = entity.status;
        }

        route_id = getIntent().getIntExtra("route_id",0);

        if(saleId != -1 && Constants.OPEN.equals(status)) {
            saleMetadataEntities = SelectQueries.getSaleMetadataElements(this, saleId);
            Bitmap b= BitmapFactory.decodeFile(saleMetadataEntities.get(0).filePath);
            Bitmap b1= BitmapFactory.decodeFile(saleMetadataEntities.get(1).filePath);
            shop.setImageBitmap(Bitmap.createScaledBitmap(b, 150, 150, false));
            slip.setImageBitmap(Bitmap.createScaledBitmap(b1, 150, 150, false));
        }else{
            SaleMetadataEntity sale1=new SaleMetadataEntity();
            sale1.sale_id=0;
            sale1.filePath=null;
            sale1.tag="";
            SaleMetadataEntity sale2=new SaleMetadataEntity();
            sale2.sale_id=0;
            sale2.filePath=null;
            sale2.tag="";
            saleMetadataEntities=new ArrayList<SaleMetadataEntity>();
            saleMetadataEntities.add(sale1);
            saleMetadataEntities.add(sale2);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isLocationCaptured) {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            } else {
                new LocationCaptureTask(this) {
                    @Override
                    protected void afterExecution(String lat, String lon, String accuracy) {
                        Logger.d(TAG, "Lat =" + lat + " Lon =" + lon);
                        isLocationCaptured = true;
                        locationEntity = new LocationEntity();
                        locationEntity.latitude = lat;
                        locationEntity.longitude = lon;
                        locationEntity.accuracy = accuracy;
                    }
                }.execute();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("photoArray", saleMetadataEntities);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(resultCode == Activity.RESULT_OK && requestCode == SHOP_CAMERA) {
                String imgPath="",tag="";
                imgPath = outputFileUri.getPath();
                Logger.d(TAG, "imgPath=" + imgPath);
                Commons.decodeFile(imgPath, Constants.IMG_WIDTH, Constants.IMG_HEIGHT);
                saleMetadataEntities.get(0).filePath=imgPath;
                saleMetadataEntities.get(0).created=(int)(System.currentTimeMillis() / 1000L);
                saleMetadataEntities.get(0).tag="shop";
                Bitmap b= BitmapFactory.decodeFile(imgPath);
                shop.setImageBitmap(Bitmap.createScaledBitmap(b, 150, 150, false));
            } else if(resultCode == Activity.RESULT_OK && requestCode == SLIP_CAMERA) {
                String imgPath="",tag="";
                imgPath = outputFileUri.getPath();
                Logger.d(TAG, "imgPath=" + imgPath);
                Commons.decodeFile(imgPath, Constants.IMG_WIDTH, Constants.IMG_HEIGHT);
                saleMetadataEntities.get(1).filePath=imgPath;
                saleMetadataEntities.get(1).created=(int)(System.currentTimeMillis() / 1000L);
                saleMetadataEntities.get(1).tag = "sale_slip";
                Bitmap b= BitmapFactory.decodeFile(imgPath);
                slip.setImageBitmap(Bitmap.createScaledBitmap(b, 150, 150, false));
            }
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, please enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                        //startActivity(elemAct);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        if(null!=(saleMetadataEntities.get(0).filePath)||null!=(saleMetadataEntities.get(1).filePath)){
            new android.app.AlertDialog.Builder(PhotoListActivity.this)
                    .setMessage("Your current data will be lost. Do you wish to continue?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    }).show();
        } else {
            finish();
        }
    }

    public void validate(){
        if(null==(saleMetadataEntities.get(0).filePath) && null==(saleMetadataEntities.get(1).filePath)){
            Toast.makeText(this,"Capture shop and sales slip photo to proceed",Toast.LENGTH_SHORT).show();
        } else if(null==(saleMetadataEntities.get(0).filePath)){
            Toast.makeText(this,"Capture shop photo to proceed",Toast.LENGTH_SHORT).show();
        } else if(null==(saleMetadataEntities.get(1).filePath)){
            Toast.makeText(this,"Capture sales slip photo to proceed",Toast.LENGTH_SHORT).show();
        } else {
            Intent i =new Intent(this, ShopRecordSaveActivity.class);
            i.putExtra("ShopEntity",shopEntity);
            i.putExtra("SaleMetadata",saleMetadataEntities);
            i.putExtra("LocationEntity",locationEntity);
            if(saleId != -1){
                i.putExtra("shopId",shopEntity.shop_id);
                i.putExtra("saleId", saleId);
            }

            if("closed".equals(getIntent().getStringExtra("status")) || shopEntity.ser_shopId != 0 ){
                i =new Intent(this,ShopRecordSaveActivity.class);
                i.putExtra("ShopEntity",shopEntity);
                i.putExtra("SaleMetadata",saleMetadataEntities);
                i.putExtra("saleId", saleId);
                i.putExtra("LocationEntity",locationEntity);
                i.putExtra("status","closed");
            }
            i.putExtra("route_id",route_id);
            startActivity(i);
        }
    }

    public void onClickImg(View v) {
        Logger.d(TAG, "inside onClick");
        if (v.getId() == R.id.add_shop) {
            if (ContextCompat.checkSelfPermission(PhotoListActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(PhotoListActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA);
            } else {
                if (ContextCompat.checkSelfPermission(PhotoListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    takePictureShop();
                } else {
                    Toast.makeText(PhotoListActivity.this,"Please give storage permission to proceed further.",Toast.LENGTH_LONG).show();
                }
            }

        } else if(v.getId() == R.id.shop_img){
            if(!(null==(saleMetadataEntities.get(0).filePath))){
                Intent i = new Intent(PhotoListActivity.this, ImageActivity.class);
                i.putExtra("path",saleMetadataEntities.get(0).filePath);
                startActivity(i);
            }
        } else if (v.getId() == R.id.add_slip) {//to capture images
            if (ContextCompat.checkSelfPermission(PhotoListActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(PhotoListActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_SLIP);
            } else {
                if (ContextCompat.checkSelfPermission(PhotoListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    takePictureSlip();
                } else {
                    Toast.makeText(PhotoListActivity.this,"Please give storage permission to proceed further.",Toast.LENGTH_LONG).show();
                }
            }

        } else if(v.getId() == R.id.slip_img){
            if(!(null==(saleMetadataEntities.get(1).filePath))){
                Intent i = new Intent(PhotoListActivity.this, ImageActivity.class);
                i.putExtra("path",saleMetadataEntities.get(1).filePath);
                startActivity(i);
            }
        } else if(v.getId() == R.id.next){
            validate();
        }
    }

    public void takePictureShop() {
        File file = Commons.getOutputMediaFile();
        outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, SHOP_CAMERA);
    }

    public void takePictureSlip() {
        File file = Commons.getOutputMediaFile();
        outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, SLIP_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                takePictureShop();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(PhotoListActivity.this).create();
                alertDialog.setMessage("For Britannia BB to work properly you require Camera permission");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //ActivityCompat.requestPermissions(ActivityStart.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_READ_PHONE_STATE);
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }

                });

                alertDialog.show();
            }
        }else if (requestCode == MY_CAMERA_SLIP) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                takePictureSlip();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(PhotoListActivity.this).create();
                alertDialog.setMessage("For Britannia BB to work properly you require Camera permission");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //ActivityCompat.requestPermissions(ActivityStart.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_READ_PHONE_STATE);
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }

                });

                alertDialog.show();
            }
        }
        }
    }











