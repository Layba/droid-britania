package com.bizone.britannia;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bizone.britannia.entities.SaleMetadataEntity;
import com.bizone.britannia.entities.SubOrderEntity;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.SelectQueries;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sonam on 9/12/16.
 */
public class SelectRetailerHaatActivity extends AppCompatActivity {

    private String villageName;
    private int vilId, route_id, sale_id;
    Button haatSummaryBtn, haatPhotoBtn;
    private boolean isHaatSummaryDone = false, isHaatPhotoDone = false;
    private static final String TAG = SelectRetailerHaatActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Logger.d(TAG, "inside onCreate");
        setContentView(R.layout.select_retailer_haat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        TextView vilName = (TextView) findViewById(R.id.vilName);
        villageName = getIntent().getStringExtra("vilName");
        vilId = getIntent().getIntExtra("vilId", -1);
        route_id = getIntent().getIntExtra("route_id", 0);
        sale_id = getIntent().getIntExtra("saleId", 0);
        haatSummaryBtn = (Button) findViewById(R.id.haatSummary);
        haatPhotoBtn = (Button) findViewById(R.id.haatPhotoSummary);
        vilName.setText("Haat Name : " + villageName);

    }

    @Override
    protected void onResume() {
        super.onResume();
        int saleId = SelectQueries.checkHaatSummaryDone(this, vilId,route_id);
        if (saleId != 0) {
            this.sale_id = saleId;
            ArrayList<SubOrderEntity> subOrderEntities = SelectQueries.getSubOrderElements(this, saleId);
            if (subOrderEntities.size() > 0) {
                haatSummaryBtn.setEnabled(false);
                haatSummaryBtn.setClickable(false);
                isHaatSummaryDone = true;
            }
            ArrayList<SaleMetadataEntity> metaEntities = SelectQueries.getSaleMetadataElements(this, saleId);

            if (metaEntities.size() > 0) {
                haatPhotoBtn.setEnabled(false);
                haatPhotoBtn.setClickable(false);
                isHaatPhotoDone = true;
            }

        }
    }

    public void onClickBtn(View v) {
        if (v.getId() == R.id.AddRetailer) {
            long currentTime = System.currentTimeMillis();
            Logger.d(TAG, "currentTime = " + currentTime);
            String lastDay = SelectQueries.getSetting(SelectRetailerHaatActivity.this, Settings.START_TIME);
            if (!"".equals(lastDay)) {
                long lastStartDay = Long.parseLong(lastDay);
                Logger.d(TAG, "lastStartDay = " + lastStartDay);
                long dayOne = TimeUnit.DAYS.toMillis(1) - Constants.SIXHRSINMILLIS;
                Logger.d(TAG, "dayOne = " + dayOne);
                if (currentTime - lastStartDay < dayOne) {
                    Intent i = new Intent(this, RetailerListActivity.class);
                    i.putExtra("vilName", villageName);
                    i.putExtra("vilId", vilId);
                    i.putExtra("route_id", route_id);
                    startActivity(i);
                } else {
                    startDayAlert();
                }
            } else {
                startDayAlert();
            }
        } else if (v.getId() == R.id.haatSummary) {
            ArrayList<SaleMetadataEntity> saleMetadataEntities = SelectQueries.getSaleMetadataElements(this,sale_id);
            if(saleMetadataEntities.size() == 4){
                long currentTime = System.currentTimeMillis();
                Logger.d(TAG, "currentTime = " + currentTime);
                String lastDay = SelectQueries.getSetting(SelectRetailerHaatActivity.this, Settings.START_TIME);
                if (!"".equals(lastDay)) {
                    long lastStartDay = Long.parseLong(lastDay);
                    Logger.d(TAG, "lastStartDay = " + lastStartDay);
                    long dayOne = TimeUnit.DAYS.toMillis(1) - Constants.SIXHRSINMILLIS;
                    Logger.d(TAG, "dayOne = " + dayOne);
                    if (currentTime - lastStartDay < dayOne) {
                        Intent i = new Intent(this, HaatSummaryActivity.class);
                        i.putExtra("vilName", villageName);
                        i.putExtra("vilId", vilId);
                        i.putExtra("route_id", route_id);
                        if (isHaatPhotoDone) {
                            i.putExtra("saleId", sale_id);
                        }
                        startActivity(i);
                    } else {
                        startDayAlert();
                    }
                } else {
                    startDayAlert();
                }
            } else {
                Toast.makeText(this,"Please capture Haat photos to proceed",Toast.LENGTH_SHORT).show();
            }

        } else if (v.getId() == R.id.haatPhotoSummary) {
            long currentTime = System.currentTimeMillis();
            Logger.d(TAG, "currentTime = " + currentTime);
            String lastDay = SelectQueries.getSetting(SelectRetailerHaatActivity.this, Settings.START_TIME);
            if (!"".equals(lastDay)) {
                long lastStartDay = Long.parseLong(lastDay);
                Logger.d(TAG, "lastStartDay = " + lastStartDay);
                long dayOne = TimeUnit.DAYS.toMillis(1) - Constants.SIXHRSINMILLIS;
                Logger.d(TAG, "dayOne = " + dayOne);
                if (currentTime - lastStartDay < dayOne) {
                    Intent i = new Intent(this, HaatPhotoActivity.class);
                    i.putExtra("vilName", villageName);
                    i.putExtra("vilId", vilId);
                    i.putExtra("route_id", route_id);
                    if (isHaatSummaryDone) {
                        i.putExtra("saleId", sale_id);
                    }
                    startActivity(i);
                } else {
                    startDayAlert();
                }
            } else {
                startDayAlert();
            }
        }
    }

    public void startDayAlert() {
        new android.app.AlertDialog.Builder(SelectRetailerHaatActivity.this)
                .setMessage("Start your day now?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(SelectRetailerHaatActivity.this, StartDayActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
