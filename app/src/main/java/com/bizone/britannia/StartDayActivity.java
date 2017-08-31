package com.bizone.britannia;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.Toast;

import com.bizone.britannia.entities.StartDayEntity;
import com.bizone.britannia.internetTask.LoginAuditTask;
import com.bizone.britannia.location.LocationCaptureTask;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.queries.UpdateQueries;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sonam on 22/2/17.
 */
public class StartDayActivity extends AppCompatActivity {

        private static final String TAG = StartDayActivity.class.getSimpleName();
        private Uri outputFileUri = null;
        private ImageView shop,addImg;
      //  private ArrayList<StartDayEntity> startDayEntities ;
        private static final int SHOP_CAMERA = 1;
        private boolean isLocationCaptured = false;
        private static final int MY_CAMERA = 1;
        private TextClock clock;
        private ProgressDialog dialog;
       // private LocationEntity locationEntity;
        private EditText remarks;
        private Button startDayBtn;
       // private TextView started;
        private StartDayEntity startDayEntity;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start_day);
            Logger.d(TAG, "inside onCreate");
            shop=(ImageView)findViewById(R.id.photo);
            clock =(TextClock) findViewById(R.id.ct_time);
            remarks = (EditText) findViewById(R.id.remarks);
            startDayBtn = (Button) findViewById(R.id.start);
            addImg=(ImageView)findViewById(R.id.add_photo);
           // started=(TextView)findViewById(R.id.started);

                startDayEntity = new StartDayEntity();
                startDayEntity.imagePath=null;

            long currentTime = System.currentTimeMillis();
            Logger.d(TAG,"currentTime = "+currentTime);
            String lastDay = SelectQueries.getSetting(StartDayActivity.this,Settings.START_TIME);
            if(!"".equals(lastDay)){
                long lastStartDay = Long.parseLong(lastDay);
                Logger.d(TAG,"lastStartDay = "+lastStartDay);
                long dayOne= TimeUnit.DAYS.toMillis(1)-Constants.SIXHRSINMILLIS;
                Logger.d(TAG,"dayOne = "+dayOne);
                if(currentTime - lastStartDay < dayOne){
                    remarks.setEnabled(false);
                    addImg.setEnabled(false);
                    StartDayEntity previousEntity = SelectQueries.getStartDayElements(StartDayActivity.this);
                    Logger.d(TAG,"previousEntity = "+previousEntity);
                    //  started.setText("Your Day was started at " +SelectQueries.getSetting(StartDayActivity.this,Settings.START_TIME_DISPLAY));
                    if(!"null".equals(previousEntity)){
                        remarks.setText(previousEntity.remarks);
                        startDayBtn.setText("\"Start Your Day\" done at\n "+SelectQueries.getSetting(StartDayActivity.this,Settings.START_TIME_DISPLAY));
                        Bitmap b= BitmapFactory.decodeFile(previousEntity.imagePath);
                        shop.setImageBitmap(Bitmap.createScaledBitmap(b, 150, 150, false));
                    }

                }else {
                    remarks.setEnabled(true);
                    startDayBtn.setText("Start your day");
                    addImg.setEnabled(true);
                    remarks.setText("");
                }
            } else {
                remarks.setEnabled(true);
                startDayBtn.setText("Start your day");
                addImg.setEnabled(true);
                remarks.setText("");
            }

            startDayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textBtn = startDayBtn.getText().toString();
                    if(textBtn.startsWith("Your day was started at")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(StartDayActivity.this);
                        builder.setMessage(textBtn)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        validate();
                    }
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        }


        public void captureLocation() {
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
                            startDayEntity.latitude = lat;
                            startDayEntity.longitude = lon;
                            startDayEntity.accuracy = accuracy;
                            afterLocationCapture();
                        }
                    }.execute();
                }
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem menuItem) {
            if (menuItem.getItemId() == android.R.id.home) {
                Intent intent = new Intent();
                intent.putExtra("photoArray", startDayEntity);
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
                    startDayEntity.imagePath=imgPath;
                    startDayEntity.startDate=(int)(System.currentTimeMillis() / 1000L);
                 //   startDayEntity.created=System.currentTimeMillis();
                   // saleMetadataEntities.get(0).tag="shop";
                    Bitmap b= BitmapFactory.decodeFile(imgPath);
                    shop.setImageBitmap(Bitmap.createScaledBitmap(b, 150, 150, false));
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
                            //startActivity(elemAct);
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        public void onBackPressed() {
            if(null!=(startDayEntity.imagePath)){
                new android.app.AlertDialog.Builder(StartDayActivity.this)
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
            if(null==(startDayEntity.imagePath)){
                Toast.makeText(this,"Capture photo to proceed",Toast.LENGTH_SHORT).show();
            }else {
                captureLocation();
            }

        }

    public void afterLocationCapture(){
        startDayEntity.remarks=remarks.getText().toString();
        InsertQueries.insertStartDayData(this,startDayEntity);
        InsertQueries.setSetting(StartDayActivity.this,Settings.START_TIME,System.currentTimeMillis()+"");
        InsertQueries.setSetting(StartDayActivity.this,Settings.START_TIME_DISPLAY,clock.getText().toString());

        if (!ContextCommons.isOnline(this)) {
             Toast.makeText(this, "Internet is unavailable. Please turn ON mobile data", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Data....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        new LoginAuditTask(StartDayActivity.this,startDayEntity) {

            @Override
            protected void afterExecution() {
                closeDialog();
                UpdateQueries.updateStartDayStatus(StartDayActivity.this,startDayEntity.startId,Constants.CLOSED);
                Intent i = new Intent(StartDayActivity.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                Toast.makeText(StartDayActivity.this, "Data uploaded successfully.", Toast.LENGTH_LONG).show();
            }
            @Override
            protected void notUploaded(String code, String msg) {
                Toast.makeText(StartDayActivity.this,msg,Toast.LENGTH_LONG).show();
                closeDialog(); if ("403".equals(code)) {
                    // expire
                    if (StartDayActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(StartDayActivity.this);
                    builder.setMessage("Session expired. Please login again.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(StartDayActivity.this, LoginActivity.class);
                                    InsertQueries.setSetting(StartDayActivity.this, Settings.PASSWORD, "");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (StartDayActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    Toast.makeText(StartDayActivity.this, msg, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(StartDayActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }

        }.execute();
    }

    public void onClickImg(View v) {
            Logger.d(TAG, "inside onClick");
            if (v.getId() == R.id.add_photo) {
                if (ContextCompat.checkSelfPermission(StartDayActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(StartDayActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA);
                } else {
                    if (ContextCompat.checkSelfPermission(StartDayActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        takePictureShop();
                    } else {
                        Toast.makeText(StartDayActivity.this,"Please give storage permission to proceed further.",Toast.LENGTH_LONG).show();
                    }
                }

            } else if(v.getId() == R.id.photo) {
                if (!(null == (startDayEntity.imagePath))) {
                    Intent i = new Intent(StartDayActivity.this, ImageActivity.class);
                    i.putExtra("path", startDayEntity.imagePath);
                    startActivity(i);
                }
            }
        }

        public void takePictureShop() {
            File file = Commons.getOutputMediaFile();
            outputFileUri = Uri.fromFile(file);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, SHOP_CAMERA);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == MY_CAMERA) {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We can now safely use the API we requested access to
                    takePictureShop();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(StartDayActivity.this).create();
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
    }











