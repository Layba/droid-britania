package com.bizone.britannia;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
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

import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.SaleMetadataEntity;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;


import java.io.File;
import java.util.ArrayList;

/**
 * Created by Sonam on 24/2/17.
 */
public class HaatPhotoActivity extends AppCompatActivity {


    private int saleId;
    private ProgressDialog dialog;
   // private LocationEntity locationEntity;
    private static final String TAG = HaatSummaryActivity.class.getSimpleName();
    private int route_id, villageId;
    private String villName;
 //   private boolean isLocationCaptured = false;
    private Uri outputFileUri = null;
    private ImageView photo1, photo2,photo3,photo4;
    private ArrayList<SaleMetadataEntity> saleMetadataEntities;
    private static final int PHOTO_CAMERA1 = 1;
    private static final int PHOTO_CAMERA2 = 2;
    private static final int PHOTO_CAMERA3 = 3;
    private static final int PHOTO_CAMERA4 = 4;
    private static final int MY_CAMERA_ONE = 5;
    private static final int MY_CAMERA_TWO = 6;
    private static final int MY_CAMERA_THREE = 7;
    private static final int MY_CAMERA_FOUR = 8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haat_photo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Haat Summary");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.haatsummary)));

        route_id = getIntent().getIntExtra("route_id", 0);
        villageId = getIntent().getIntExtra("vilId", 0);
        villName = getIntent().getStringExtra("vilName");
     //   saleId = getIntent().getIntExtra("saleId", -1);
        photo1 = (ImageView) findViewById(R.id.shop_img);
        photo2 = (ImageView) findViewById(R.id.slip_img);
        photo3 = (ImageView) findViewById(R.id.slip_img_3);
        photo4 = (ImageView) findViewById(R.id.slip_img_4);

        SaleMetadataEntity sale1 = new SaleMetadataEntity();
        sale1.sale_id = 0;
        sale1.filePath = null;
        SaleMetadataEntity sale2 = new SaleMetadataEntity();
        sale2.sale_id = 0;
        sale2.filePath = null;
        SaleMetadataEntity sale3 = new SaleMetadataEntity();
        sale3.sale_id = 0;
        sale3.filePath = null;
        SaleMetadataEntity sale4 = new SaleMetadataEntity();
        sale4.sale_id = 0;
        sale4.filePath = null;
        saleMetadataEntities = new ArrayList<SaleMetadataEntity>();
        saleMetadataEntities.add(sale1);
        saleMetadataEntities.add(sale2);
        saleMetadataEntities.add(sale3);
        saleMetadataEntities.add(sale4);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == PHOTO_CAMERA1) {
                String imgPath = "", tag = "";
                imgPath = outputFileUri.getPath();
                Logger.d(TAG, "imgPath=" + imgPath);
                Commons.decodeFile(imgPath, Constants.IMG_WIDTH, Constants.IMG_HEIGHT);
                saleMetadataEntities.get(0).filePath = imgPath;
                saleMetadataEntities.get(0).created = (int) (System.currentTimeMillis() / 1000L);
                saleMetadataEntities.get(0).tag = "haat1";
                Bitmap b = BitmapFactory.decodeFile(imgPath);
                photo1.setImageBitmap(Bitmap.createScaledBitmap(b, 150, 150, false));
            } else if (resultCode == Activity.RESULT_OK && requestCode == PHOTO_CAMERA2) {
                String imgPath = "", tag = "";
                imgPath = outputFileUri.getPath();
                Logger.d(TAG, "imgPath=" + imgPath);
                Commons.decodeFile(imgPath, Constants.IMG_WIDTH, Constants.IMG_HEIGHT);
                saleMetadataEntities.get(1).filePath = imgPath;
                saleMetadataEntities.get(1).created = (int) (System.currentTimeMillis() / 1000L);
                saleMetadataEntities.get(1).tag = "haat2";
                Bitmap b = BitmapFactory.decodeFile(imgPath);
                photo2.setImageBitmap(Bitmap.createScaledBitmap(b, 150, 150, false));
            } else if (resultCode == Activity.RESULT_OK && requestCode == PHOTO_CAMERA3) {
                String imgPath = "", tag = "";
                imgPath = outputFileUri.getPath();
                Logger.d(TAG, "imgPath=" + imgPath);
                Commons.decodeFile(imgPath, Constants.IMG_WIDTH, Constants.IMG_HEIGHT);
                saleMetadataEntities.get(2).filePath = imgPath;
                saleMetadataEntities.get(2).created = (int) (System.currentTimeMillis() / 1000L);
                saleMetadataEntities.get(2).tag = "haat3";
                Bitmap b = BitmapFactory.decodeFile(imgPath);
                photo3.setImageBitmap(Bitmap.createScaledBitmap(b, 150, 150, false));
            } else if (resultCode == Activity.RESULT_OK && requestCode == PHOTO_CAMERA4) {
                String imgPath = "", tag = "";
                imgPath = outputFileUri.getPath();
                Logger.d(TAG, "imgPath=" + imgPath);
                Commons.decodeFile(imgPath, Constants.IMG_WIDTH, Constants.IMG_HEIGHT);
                saleMetadataEntities.get(3).filePath = imgPath;
                saleMetadataEntities.get(3).created = (int) (System.currentTimeMillis() / 1000L);
                saleMetadataEntities.get(3).tag = "haat4";
                Bitmap b = BitmapFactory.decodeFile(imgPath);
                photo4.setImageBitmap(Bitmap.createScaledBitmap(b, 150, 150, false));
            }
        } catch (Exception e) {
            Logger.e(TAG, e);
            e.printStackTrace();
        }
    }

    public void onSaveUpdate() {

        final int saleId = saveToDatabase();
        Toast.makeText(HaatPhotoActivity.this, "Photos saved successfully", Toast.LENGTH_LONG).show();
        Intent i = new Intent(HaatPhotoActivity.this, SelectRetailerHaatActivity.class);
        i.putExtra("saleId",saleId);
        i.putExtra("vilId",villageId);
        i.putExtra("route_id",route_id);
        i.putExtra("vilName",villName);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
   }


    private int saveToDatabase() {
        saleId = SelectQueries.getSaleIdByVilIdAndRId(this,villageId,route_id);
        if (saleId == 0) {
            SaleEntity saleEntity;
            saleEntity = new SaleEntity();
            saleEntity.subtotal = "0";// empty
            saleEntity.total = "0";// empty
            saleEntity.device_id = SelectQueries.getSetting(this, Settings.DEVICE_ID);
            saleEntity.created = (int) (System.currentTimeMillis() / 1000L);
            saleEntity.sale_field1 = ContextCommons.getNetworkDetails(this).toString();
            saleEntity.sale_field2 = route_id + "";
            saleEntity.latitude = "-";
            saleEntity.longitude = "-";
            saleEntity.accuracy = "-";
            saleEntity.sale_type = "3"; // type 3 = haat photos

            this.saleId = (int) InsertQueries.insertSale(this, saleEntity);

            InsertQueries.insertVillageSummary(this, saleId, villageId, villName,route_id);
        }

        for (SaleMetadataEntity saleMetaEntity : saleMetadataEntities) {
            saleMetaEntity.sale_id = saleId;
            Logger.d(TAG, "Save Closed saleMeta=" + saleMetaEntity.toString());
            InsertQueries.insertSaleMetadata(this, saleMetaEntity);
        }
        return this.saleId;
    }

    public void validate(){

        if(null==(saleMetadataEntities.get(0).filePath) && null==(saleMetadataEntities.get(1).filePath)
                && null==(saleMetadataEntities.get(2).filePath) && null==(saleMetadataEntities.get(3).filePath)){
            Toast.makeText(this,"Capture Haat photos to proceed",Toast.LENGTH_SHORT).show();
        } else if(null==(saleMetadataEntities.get(0).filePath)){
            Toast.makeText(this,"Capture Haat photo 1 to proceed",Toast.LENGTH_SHORT).show();
        } else if(null==(saleMetadataEntities.get(1).filePath)){
            Toast.makeText(this,"Capture Haat photo 2 to proceed",Toast.LENGTH_SHORT).show();
        } else if(null==(saleMetadataEntities.get(2).filePath)){
            Toast.makeText(this,"Capture Haat photo 3 to proceed",Toast.LENGTH_SHORT).show();
        } else if(null==(saleMetadataEntities.get(3).filePath)){
            Toast.makeText(this,"Capture Haat photo 4 to proceed",Toast.LENGTH_SHORT).show();
        } else {
           onSaveUpdate();
        }
    }


    private void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        closeDialog();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (null != (saleMetadataEntities.get(0).filePath) || null != (saleMetadataEntities.get(1).filePath)
                || null != (saleMetadataEntities.get(2).filePath) || null != (saleMetadataEntities.get(3).filePath)) {
            new android.app.AlertDialog.Builder(HaatPhotoActivity.this)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onClickImg(View v) {
        Logger.d(TAG, "inside onClick");
        if (v.getId() == R.id.add_shop) {
            if (ContextCompat.checkSelfPermission(HaatPhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HaatPhotoActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_ONE);
            } else {
                if (ContextCompat.checkSelfPermission(HaatPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    takePictureShop();
                } else {
                    Toast.makeText(HaatPhotoActivity.this, "Please give storage permission to proceed further.", Toast.LENGTH_LONG).show();
                }
            }

        } else if (v.getId() == R.id.shop_img) {
            if (!(null == (saleMetadataEntities.get(0).filePath))) {
                Intent i = new Intent(HaatPhotoActivity.this, ImageActivity.class);
                i.putExtra("path", saleMetadataEntities.get(0).filePath);
                startActivity(i);
            }
        } else if (v.getId() == R.id.add_slip) {//to capture images
            if (ContextCompat.checkSelfPermission(HaatPhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HaatPhotoActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_TWO);
            } else {
                if (ContextCompat.checkSelfPermission(HaatPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    takePictureSlip();
                } else {
                    Toast.makeText(HaatPhotoActivity.this, "Please give storage permission to proceed further.", Toast.LENGTH_LONG).show();
                }
            }

        } else if (v.getId() == R.id.slip_img) {
            if (!(null == (saleMetadataEntities.get(1).filePath))) {
                Intent i = new Intent(HaatPhotoActivity.this, ImageActivity.class);
                i.putExtra("path", saleMetadataEntities.get(1).filePath);
                startActivity(i);
            }
        }else if (v.getId() == R.id.add_slip_3) {//to capture images
            if (ContextCompat.checkSelfPermission(HaatPhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HaatPhotoActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_THREE);
            } else {
                if (ContextCompat.checkSelfPermission(HaatPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    takePictureThree();
                } else {
                    Toast.makeText(HaatPhotoActivity.this, "Please give storage permission to proceed further.", Toast.LENGTH_LONG).show();
                }
            }

        } else if (v.getId() == R.id.add_slip_4) {//to capture images
            if (ContextCompat.checkSelfPermission(HaatPhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HaatPhotoActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_FOUR);
            } else {
                if (ContextCompat.checkSelfPermission(HaatPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    takePictureFour();
                } else {
                    Toast.makeText(HaatPhotoActivity.this, "Please give storage permission to proceed further.", Toast.LENGTH_LONG).show();
                }
            }

        }else if (v.getId() == R.id.slip_img_3) {
            if (!(null == (saleMetadataEntities.get(2).filePath))) {
                Intent i = new Intent(HaatPhotoActivity.this, ImageActivity.class);
                i.putExtra("path", saleMetadataEntities.get(2).filePath);
                startActivity(i);
            }
        }else if (v.getId() == R.id.slip_img_4) {
            if (!(null == (saleMetadataEntities.get(3).filePath))) {
                Intent i = new Intent(HaatPhotoActivity.this, ImageActivity.class);
                i.putExtra("path", saleMetadataEntities.get(3).filePath);
                startActivity(i);
            }
        } else if(v.getId() == R.id.upload_photo){
            validate();
        }
    }

    public void takePictureShop() {
        File file = Commons.getOutputMediaFile();
        outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, PHOTO_CAMERA1);
    }

    public void takePictureSlip() {
        File file = Commons.getOutputMediaFile();
        outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, PHOTO_CAMERA2);
    }

    public void takePictureThree() {
        File file = Commons.getOutputMediaFile();
        outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, PHOTO_CAMERA3);
    }

    public void takePictureFour() {
        File file = Commons.getOutputMediaFile();
        outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, PHOTO_CAMERA4);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_CAMERA_ONE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                takePictureShop();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(HaatPhotoActivity.this).create();
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
        } else if (requestCode == MY_CAMERA_TWO) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                takePictureSlip();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(HaatPhotoActivity.this).create();
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
        } else if (requestCode == MY_CAMERA_THREE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                takePictureThree();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(HaatPhotoActivity.this).create();
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
        } else if (requestCode == MY_CAMERA_FOUR) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                takePictureFour();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(HaatPhotoActivity.this).create();
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

