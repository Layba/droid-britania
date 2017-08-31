package com.bizone.britannia;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bizone.britannia.entities.PurchaseEntity;
import com.bizone.britannia.entities.SaleEntity;

import android.widget.Toast;

import com.bizone.britannia.entities.ShopEntity;
import com.bizone.britannia.entities.StartDayEntity;
import com.bizone.britannia.entities.UploadSaleEntity;
import com.bizone.britannia.internetTask.LoginAuditTask;
import com.bizone.britannia.internetTask.SendHaatSummaryTask;
import com.bizone.britannia.internetTask.SendSalesDataTask;
import com.bizone.britannia.internetTask.SendSalesMetadataTask;
import com.bizone.britannia.internetTask.SendStockiestTask;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.queries.UpdateQueries;
import com.bizone.britannia.utilities.SelectAllCheckbox;

import java.util.ArrayList;

/**
 * Created by siddhesh on 8/3/16.
 */
public class UploadSalesActivity extends AppCompatActivity {
    private static final String TAG = UploadSalesActivity.class.getSimpleName();

    private ListView list;
    private ArrayList<UploadSaleEntity> uploadSaleEntities;
    private ProgressDialog dialog;
    private SelectAllCheckbox checkAll;
    private ArrayList<SaleEntity> saleEntities;
    private int checkCount = 0, currentCount = 0;
    private int count = 0, metaData = 0;
    private LinearLayout mainll;
    private Button upload;
    private TextView noRecord;
    private boolean ifAllCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "inside onCreate");
        setContentView(R.layout.upload_sales);

        saleEntities = SelectQueries.getAllSaleElements(this);
        checkAll = (SelectAllCheckbox) findViewById(R.id.checkall);
        mainll = (LinearLayout) findViewById(R.id.checkll);
        upload = (Button) findViewById(R.id.upload);
        noRecord = (TextView) findViewById(R.id.norecord);
        final SalesCustomList adapter = new SalesCustomList();
        uploadSaleEntities = new ArrayList<UploadSaleEntity>();

        for (SaleEntity entity : saleEntities) {
            checkCount++;
            UploadSaleEntity saleEntity = new UploadSaleEntity();
            ShopEntity shopEntity = SelectQueries.getShopElements(this, entity.sale_id);
            if (shopEntity != null && "1".equals(entity.sale_type)) {
                saleEntity.shopname = shopEntity.shop_name;
                saleEntity.isChecked = true;
                saleEntity.created = shopEntity.created;
                saleEntity.saleId = entity.sale_id;
                saleEntity.saleType = entity.sale_type;
                uploadSaleEntities.add(saleEntity);
            } else if ("2".equals(entity.sale_type)) {
                int vilId = SelectQueries.getVillIdBySaleId(this,entity.sale_id);
                saleEntity.shopname = SelectQueries.getVillNameByVilId(this,vilId);
                saleEntity.isChecked = true;
                saleEntity.created = entity.created;
                saleEntity.saleId = entity.sale_id;
                saleEntity.saleType = entity.sale_type;
                uploadSaleEntities.add(saleEntity);
            } else {
                Logger.d(TAG, "not found shop");
            }
        }

        ArrayList<PurchaseEntity> purchaseEntities = SelectQueries.getPurchaseByStatus(this, Constants.INPROGRESS);
        if (purchaseEntities.size() > 0) {
            for (int i = 0; i < purchaseEntities.size(); i++) {
                PurchaseEntity purchaseEntity = purchaseEntities.get(i);
                UploadSaleEntity saleEntity = new UploadSaleEntity();
                saleEntity.shopname = purchaseEntity.stockiestName;
                saleEntity.isChecked = true;
                saleEntity.created = purchaseEntity.created;
                saleEntity.saleId = purchaseEntity.p_id;
                saleEntity.saleType = "3";
                uploadSaleEntities.add(saleEntity);
            }
        }

        ArrayList<StartDayEntity> startDayActivities = SelectQueries.getStartDayByStatus(this, Constants.INPROGRESS);
        if (startDayActivities.size() > 0) {
            for (int i = 0; i < startDayActivities.size(); i++) {
                StartDayEntity startDayEntity = startDayActivities.get(i);
                UploadSaleEntity saleEntity = new UploadSaleEntity();
                saleEntity.shopname = Commons.millisecToDate(startDayEntity.startDate+"");
                saleEntity.isChecked = true;
                saleEntity.created = startDayEntity.startDate;
                saleEntity.saleId = startDayEntity.startId;
                saleEntity.saleType = "4";
                uploadSaleEntities.add(saleEntity);
            }
        }

        list = (ListView) findViewById(R.id.list_view_sales);
        Logger.d(TAG, "setting adapter");
        list.setAdapter(adapter);
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkAll.isSelfUncheckFlag()) {
                    checkAll.setSelfUncheckFlag(false);
                } else if (isChecked) {
                    for (int i = 0; i < uploadSaleEntities.size(); i++) {
                        uploadSaleEntities.get(i).isChecked = true;

                    }
                } else {
                    for (int i = 0; i < uploadSaleEntities.size(); i++) {
                        uploadSaleEntities.get(i).isChecked = false;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        if(uploadSaleEntities.size() > 0){
            mainll.setVisibility(View.VISIBLE);
            list.setVisibility(View.VISIBLE);
            upload.setVisibility(View.VISIBLE);
            noRecord.setVisibility(View.GONE);
        }

        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });*/
        Logger.d(TAG, "setActionBar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
    }

    @Override
    protected void onDestroy() {
        closeDialog();
        super.onDestroy();
    }

    public void onUpload(View v) {
        currentCount = 0;
        checkCount = 0;
        for (int i = 0; i < uploadSaleEntities.size(); i++) {
            if (uploadSaleEntities.get(i).isChecked) {
                checkCount++;
            }
        }

        if (checkCount == 0) {
            Toast.makeText(UploadSalesActivity.this, "Please select one or more record to upload", Toast.LENGTH_LONG).show();
            return;
        }

        if (!ContextCommons.isOnline(this)) {
            Toast.makeText(this, "Please enable internet connection.", Toast.LENGTH_LONG).show();
            return;
        }

        count = 0;
        metaData = 0;
        for (UploadSaleEntity uploadSaleEntity : uploadSaleEntities) {
            if (uploadSaleEntity.isChecked) {
                count++;
            }
        }
        if(count == uploadSaleEntities.size()){
            ifAllCheck = true;
        }

        if (count > 0) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait....");
            dialog.setCancelable(false);
            dialog.show();
        } else {
            return;
        }

        for (int i = 0; i < uploadSaleEntities.size(); i++) {
            if (uploadSaleEntities.get(i).isChecked) {
                if ("1".equals(uploadSaleEntities.get(i).saleType)) {
                    int saleId = uploadSaleEntities.get(i).saleId;
                    SaleEntity entity = SelectQueries.getSaleElements(this, saleId);
                    if (entity.transaction_id == 0) {
                        UpdateQueries.updateSaleStatus(this, saleId, Constants.INPROGRESS);
                        new SendSalesDataTask(this, saleId) {
                            @Override
                            protected void afterExecution(boolean result, String msg, int sale_Id) {
                                if (result) {
                                    sendMetaImages(sale_Id);
                                } else if ("403".equals(msg)) {
                                    // expire
                                    if (UploadSalesActivity.this.isFinishing()) {
                                        return;
                                    }
                                    closeDialog();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadSalesActivity.this);
                                    builder.setMessage("Session expired. Please login again.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(UploadSalesActivity.this, LoginActivity.class);
                                                    InsertQueries.setSetting(UploadSalesActivity.this, Settings.PASSWORD, "");
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                } else {
                                    if (UploadSalesActivity.this.isFinishing()) {
                                        return;
                                    }
                                    closeDialog();
                                    goToMainActivity();
                                    Toast.makeText(UploadSalesActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        }.execute();
                    } else {
                        sendMetaImages(saleId);
                    }
                } else if ("2".equals(uploadSaleEntities.get(i).saleType)) {
                    final int saleId = uploadSaleEntities.get(i).saleId;
                    new SendHaatSummaryTask(this, saleId) {
                        @Override
                        protected void afterExecution(boolean result, String msg, int sale_Id) {
                            if (result) {
                                sendMetaImagesHaat(sale_Id);
                               } else if ("403".equals(msg)) {
                                // expire
                                if (UploadSalesActivity.this.isFinishing()) {
                                    return;
                                }
                                closeDialog();
                                AlertDialog.Builder builder = new AlertDialog.Builder(UploadSalesActivity.this);
                                builder.setMessage("Session expired. Please login again.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(UploadSalesActivity.this, LoginActivity.class);
                                                InsertQueries.setSetting(UploadSalesActivity.this, Settings.PASSWORD, "");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                if (UploadSalesActivity.this.isFinishing()) {
                                    return;
                                }
                                closeDialog();
                                Toast.makeText(UploadSalesActivity.this, msg, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(UploadSalesActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        }
                    }.execute();
                } else if ("3".equals(uploadSaleEntities.get(i).saleType)) {
                    new SendStockiestTask(this, uploadSaleEntities.get(i).saleId) {
                        @Override
                        protected void afterExecution(boolean result, String msg) {
                            if (result) {
                                if (UploadSalesActivity.this.isFinishing()) {
                                    return;
                                }
                                metaData++;
                                if (metaData == count) {
                                    if (UploadSalesActivity.this.isFinishing()) {
                                        return;
                                    }
                                    closeDialog();
                                      goToMainActivity();
                                    currentCount++;
                                    if (currentCount == checkCount) {
                                        Toast.makeText(UploadSalesActivity.this, "Data uploaded successfully.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else if ("403".equals(msg)) {
                                // expire
                                if (UploadSalesActivity.this.isFinishing()) {
                                    return;
                                }
                                closeDialog();
                                AlertDialog.Builder builder = new AlertDialog.Builder(UploadSalesActivity.this);
                                builder.setMessage("Session expired. Please login again.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(UploadSalesActivity.this, LoginActivity.class);
                                                InsertQueries.setSetting(UploadSalesActivity.this, Settings.PASSWORD, "");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                if (UploadSalesActivity.this.isFinishing()) {
                                    return;
                                }
                                closeDialog();
                                Toast.makeText(UploadSalesActivity.this, msg, Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    }.execute();
                }else if ("4".equals(uploadSaleEntities.get(i).saleType)) {
                    final StartDayEntity startDayEntity = SelectQueries.getStartDayElementsByStartId
                            (UploadSalesActivity.this,uploadSaleEntities.get(i).saleId);
                    new LoginAuditTask(UploadSalesActivity.this,startDayEntity) {

                        @Override
                        protected void afterExecution() {
                            closeDialog();
                            UpdateQueries.updateStartDayStatus(UploadSalesActivity.this,startDayEntity.startId,Constants.CLOSED);
                            Intent in = new Intent(UploadSalesActivity.this,MainActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(in);
                            finish();
                            Toast.makeText(UploadSalesActivity.this, "Data uploaded successfully.", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        protected void notUploaded(String code, String msg) {
                            Toast.makeText(UploadSalesActivity.this,msg,Toast.LENGTH_LONG).show();
                            closeDialog(); if ("403".equals(code)) {
                                // expire
                                if (UploadSalesActivity.this.isFinishing()) {
                                    return;
                                }
                                closeDialog();
                                AlertDialog.Builder builder = new AlertDialog.Builder(UploadSalesActivity.this);
                                builder.setMessage("Session expired. Please login again.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(UploadSalesActivity.this, LoginActivity.class);
                                                InsertQueries.setSetting(UploadSalesActivity.this, Settings.PASSWORD, "");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                if (UploadSalesActivity.this.isFinishing()) {
                                    return;
                                }
                                closeDialog();
                                Toast.makeText(UploadSalesActivity.this, msg, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(UploadSalesActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                        }

                    }.execute();
                }

            }
        }
    }

    private void sendMetaImagesHaat(final int saleId) {

        dialog.setMessage("Uploading Images....");

        new SendSalesMetadataTask(this, saleId) {
            @Override
            protected void afterExecution() {
                metaData++;
                if (metaData == count) {
                    if (UploadSalesActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    goToMainActivity();
                    currentCount++;
                    if (currentCount == checkCount) {
                        Toast.makeText(UploadSalesActivity.this, "Data uploaded successfully.", Toast.LENGTH_LONG).show();
                    }
                }
                UpdateQueries.updateSaleStatus(UploadSalesActivity.this, saleId, Constants.CLOSED);
                }

            @Override
            protected void notUploaded(String code, String msg) {
                if ("403".equals(code)) {
                    // expire
                    if (UploadSalesActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadSalesActivity.this);
                    builder.setMessage("Session expired. Please login again.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(UploadSalesActivity.this, LoginActivity.class);
                                    InsertQueries.setSetting(UploadSalesActivity.this, Settings.PASSWORD, "");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (UploadSalesActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    goToMainActivity();
                    Toast.makeText(UploadSalesActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }


    private void sendMetaImages(final int saleId) {

        new SendSalesMetadataTask(this, saleId) {
            @Override
            protected void afterExecution() {
                metaData++;
                if (metaData == count) {
                    if (UploadSalesActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    goToMainActivity();
                    currentCount++;
                    if (currentCount == checkCount) {
                        Toast.makeText(UploadSalesActivity.this, "Data uploaded successfully.", Toast.LENGTH_LONG).show();
                    }
                }
                UpdateQueries.updateSaleStatus(UploadSalesActivity.this, saleId, Constants.CLOSED);
                SaleEntity saleEntity = SelectQueries.getSaleElements(UploadSalesActivity.this, saleId);
                UpdateQueries.updateShopDate(UploadSalesActivity.this, saleEntity.created, saleEntity.sale_id);
            }

            @Override
            protected void notUploaded(String code, String msg) {
                if ("403".equals(code)) {
                    // expire
                    if (UploadSalesActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadSalesActivity.this);
                    builder.setMessage("Session expired. Please login again.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(UploadSalesActivity.this, LoginActivity.class);
                                    InsertQueries.setSetting(UploadSalesActivity.this, Settings.PASSWORD, "");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (UploadSalesActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    goToMainActivity();
                    Toast.makeText(UploadSalesActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void goToMainActivity() {
        Intent intent;
        if(ifAllCheck){
            intent = new Intent(UploadSalesActivity.this, MainActivity.class);
        } else {
            intent = new Intent(UploadSalesActivity.this, UploadSalesActivity.class);
        }
        startActivity(intent);
        finish();

    }

    private void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


    public class SalesCustomList extends BaseAdapter {

        @Override
        public int getCount() {
            return uploadSaleEntities.size();
        }

        @Override
        public Object getItem(int position) {
            return uploadSaleEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView shop_name, date, shopHead, dateHead;
            CheckBox ischeck;

        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup arg2) {
            Logger.d(TAG, "inside getView");


            ViewHolder holder = null;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.upload_sales_lits_item, null);

                holder = new ViewHolder();
                holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.ischeck = (CheckBox) convertView.findViewById(R.id.check);
                holder.shopHead = (TextView) convertView.findViewById(R.id.shop_head);
                holder.dateHead = (TextView) convertView.findViewById(R.id.date_head);
                holder.ischeck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            uploadSaleEntities.get(pos).isChecked = true;
                            checkCount++;
                        } else {
                            uploadSaleEntities.get(pos).isChecked = false;
                            checkCount--;
                            if (checkAll.isChecked()) {
                                checkAll.setSelfUncheckFlag(true);
                                checkAll.setChecked(false);
                            }
                        }
                    }
                });
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            UploadSaleEntity entity = uploadSaleEntities.get(pos);
            Logger.d(TAG, "uploadSaleEntity=" + entity.toString());

            if ("2".equals(entity.saleType)) {
                holder.shopHead.setText("Haat : ");
                holder.dateHead.setText("Summary Date: ");
                convertView.setBackgroundResource(R.color.pending_haat);
            } else if ("3".equals(entity.saleType)) {
                holder.shopHead.setText("Stockist Name : ");
                holder.dateHead.setText("Purchase Date: ");
                convertView.setBackgroundResource(R.color.pending_stockist);
            } else if ("1".equals(entity.saleType)) {
                holder.shopHead.setText("Shop Name : ");
                holder.dateHead.setText("Sale Date: ");
                convertView.setBackgroundResource(R.color.pending_retailer);
            } else if ("4".equals(entity.saleType)) {
                holder.shopHead.setText("Start Date : ");
                holder.dateHead.setText("Start Time: ");
                convertView.setBackgroundResource(R.color.pending_start);
            }

            holder.shop_name.setText(entity.shopname);
            if("4".equals(entity.saleType)){
                holder.date.setText(Commons.millisecToTime(entity.created + ""));
            }else {
                holder.date.setText((Commons.milliToDateWithYear(Long.parseLong(entity.created + ""))));
            }
            holder.ischeck.setChecked(entity.isChecked);

            return convertView;
        }


    }


}
