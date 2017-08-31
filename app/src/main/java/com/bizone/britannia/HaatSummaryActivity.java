package com.bizone.britannia;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bizone.britannia.entities.LocationEntity;
import com.bizone.britannia.entities.ProductSkuEntity;
import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.SubOrderEntity;
import com.bizone.britannia.internetTask.SendHaatSummaryTask;
import com.bizone.britannia.internetTask.SendSalesMetadataTask;
import com.bizone.britannia.location.LocationCaptureTask;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.queries.UpdateQueries;

import java.util.ArrayList;

/**
 * Created by Sonam on 9/12/16.
 */
public class HaatSummaryActivity extends AppCompatActivity {

    private LinearLayout mainLl;
    private TextView totalSum;
    private int saleId;
    private ArrayList<Double> sellingPrices;
    private ArrayList<ProductSkuEntity> productSkuEntities;
    private ArrayList<SubOrderEntity> subOrderEntities;
    private ArrayList<EditText> qtyEditArray;
    private LinearLayout container;
    private TextView productTxt;
    private TextView mrpTxt,avaiQty;
    private ArrayList<TextView> qtyAvailArray;
    private boolean isLocationCaptured = false;
    private EditText qtyEdit;
    private ProgressDialog dialog;
    private LocationEntity locationEntity;
    private static final String TAG = HaatSummaryActivity.class.getSimpleName();
    private int route_id, villageId;
    private String villName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haat_summary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Haat Summary");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.haatsummary)));

        mainLl = (LinearLayout) findViewById(R.id.main);
        totalSum = (TextView) findViewById(R.id.total);
        route_id = getIntent().getIntExtra("route_id",0);
        villageId = getIntent().getIntExtra("vilId",0);
        villName = getIntent().getStringExtra("vilName");
        saleId = getIntent().getIntExtra("saleId", -1);
        productSkuEntities = SelectQueries.getProductSkuElements(this, SelectQueries.getSetting(this, Settings.STATE));
        qtyEditArray = new ArrayList<EditText>();
        qtyAvailArray = new ArrayList<TextView>();
        sellingPrices = new ArrayList<Double>();
        subOrderEntities = new ArrayList<SubOrderEntity>();

        for (int i = 0; i < productSkuEntities.size(); i++) {
            Logger.d(TAG, "ProductSkuEntity=" + productSkuEntities.get(i).toString());
            container = new LinearLayout(this);
            productTxt = new TextView(this);
            mrpTxt = new TextView(this);
            avaiQty = new TextView(this);
            qtyEdit = new EditText(this);
            qtyEdit.setInputType(InputType.TYPE_CLASS_PHONE);
            qtyEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            qtyEdit.setGravity(Gravity.CENTER_HORIZONTAL);
            qtyEdit.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
            qtyEdit.setSingleLine(true);
            qtyEdit.setInputType(InputType.TYPE_CLASS_PHONE);
            qtyEdit.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            if (i == 0) {
                qtyEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(qtyEdit, InputMethodManager.SHOW_IMPLICIT);
            }


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);

            container.setLayoutParams(params);
            container.setOrientation(LinearLayout.HORIZONTAL);
            container.setWeightSum(11f);

            if (Build.VERSION.SDK_INT < 23) {
                productTxt.setTextAppearance(getApplicationContext(), R.style.details_txt_list);
                mrpTxt.setTextAppearance(getApplicationContext(), R.style.details_txt);
                avaiQty.setTextAppearance(getApplicationContext(), R.style.details_txt);
            } else {
                productTxt.setTextAppearance(R.style.details_txt_list);
                mrpTxt.setTextAppearance(R.style.details_txt);
                avaiQty.setTextAppearance(R.style.details_txt);
            }
            // productName.setBackgroundResource(R.color.normalTextViewColor);
            productTxt.setText(productSkuEntities.get(i).name);
            productTxt.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 6f));
            productTxt.setGravity(Gravity.BOTTOM);
            mrpTxt.setText(productSkuEntities.get(i).selling_price);
            mrpTxt.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.66f));
            mrpTxt.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

            int prodQty = Commons.getAvailByProductName(this, productSkuEntities.get(i).name);
            avaiQty.setText(prodQty + "");
            avaiQty.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.66f));
            avaiQty.setGravity(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);

            qtyEdit.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() != 0) {
                        try {
                            String str = s.toString();
                            Integer.parseInt(str);
                            calculate();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            totalSum.setText("");
                            Logger.e(TAG, e);
                            Toast.makeText(HaatSummaryActivity.this, "Enter proper number", Toast.LENGTH_SHORT).show();
                        }
                    } else if (s.length() == 0) {
                        totalSum.setText("");
                        double qtyNumber;

                        String qtyString;
                        for (int i = 0; i < qtyEditArray.size(); i++) {
                            qtyString = qtyEditArray.get(i).getText().toString();
                            if (!"".equals(qtyString)) {

                                qtyNumber = Integer.parseInt(qtyString);
                                if (qtyNumber >= 24) {

                                }
                            }
                        }

                    }
                }
            });

            container.addView(productTxt);
            container.addView(avaiQty);
            container.addView(mrpTxt);
            container.addView(qtyEdit);
            mainLl.addView(container);
            qtyEditArray.add(qtyEdit);
            qtyAvailArray.add(avaiQty);
            sellingPrices.add(Double.parseDouble(productSkuEntities.get(i).selling_price));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < productSkuEntities.size(); i++) {
            qtyAvailArray.get(i).setText(Commons.getAvailByProductName(this, productSkuEntities.get(i).name)+"");
        }
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

    private boolean attemptSave() {

        if(totalSum.getText().toString().equals("")){
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_LONG).show();
            return false;
        }

        for(int i=0;i<qtyEditArray.size();i++) {

            Logger.d(TAG,"inside for i = "+i);
            EditText qty=qtyEditArray.get(i);

            if (qty.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Quantity should not be empty", Toast.LENGTH_LONG).show();
                qty.requestFocus();
                return false;
            }
        }

        for (int i = 0; i < qtyEditArray.size(); i++) {
            String qtyString = qtyEditArray.get(i).getText().toString().trim();
            String avlString = qtyAvailArray.get(i).getText().toString().trim();
            if (Integer.parseInt(avlString) - Integer.parseInt(qtyString) < 0) {
                new android.app.AlertDialog.Builder(HaatSummaryActivity.this)
                        .setMessage("You do not have enough available quantity of 1 or more products .Please purchase before proceeding.")
                        .setPositiveButton("Purchase", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(HaatSummaryActivity.this, PurchaseActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        }).show();
                qtyEditArray.get(i).requestFocus();
                return false;
            }
        }

        return true;
    }
    
    public void onSaveUpdate(View v){
        boolean save = attemptSave();
        Logger.d(TAG, "Save= " + save);
        if (save) {
            saveSubstractedQtyInSettings();
            final int saleId = saveToDatabase();
            UpdateQueries.updateSaleStatus(this, saleId, Constants.INPROGRESS);
            if (!ContextCommons.isOnline(this)) {
                Intent i = new Intent(HaatSummaryActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              //  i.putExtra("vilName", shopEntity.village);
              //  i.putExtra("vilId", shopEntity.base_village_id);
                startActivity(i);
                Toast.makeText(this, "Internet is unavailable. Please turn ON mobile data", Toast.LENGTH_LONG).show();
                return;
            }
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading Data....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            new SendHaatSummaryTask(this, saleId) {
                @Override
                protected void afterExecution(boolean result, String msg, int sale_Id) {
                    if (result) {
                        sendMetaImages(sale_Id);
                     } else if ("403".equals(msg)) {
                        // expire
                        if (HaatSummaryActivity.this.isFinishing()) {
                            return;
                        }
                        closeDialog();
                        AlertDialog.Builder builder = new AlertDialog.Builder(HaatSummaryActivity.this);
                        builder.setMessage("Session expired. Please login again.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(HaatSummaryActivity.this, LoginActivity.class);
                                        InsertQueries.setSetting(HaatSummaryActivity.this, Settings.PASSWORD, "");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        if (HaatSummaryActivity.this.isFinishing()) {
                            return;
                        }
                        closeDialog();
                        Toast.makeText(HaatSummaryActivity.this, msg, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(HaatSummaryActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
            }.execute();
        }
    }

    private void sendMetaImages(final int saleId) {

        dialog.setMessage("Uploading Images....");

        new SendSalesMetadataTask(this, saleId) {
            @Override
            protected void afterExecution() {
                if (HaatSummaryActivity.this.isFinishing()) {
                    return;
                }
                        closeDialog();
                        UpdateQueries.updateSaleStatus(HaatSummaryActivity.this, saleId, Constants.CLOSED);
                        Intent i = new Intent(HaatSummaryActivity.this,MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                        Toast.makeText(HaatSummaryActivity.this, "Data uploaded successfully.", Toast.LENGTH_LONG).show();
                 }

            @Override
            protected void notUploaded(String code, String msg) {
                if ("403".equals(code)) {
                    // expire
                    if (HaatSummaryActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(HaatSummaryActivity.this);
                    builder.setMessage("Session expired. Please login again.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(HaatSummaryActivity.this, LoginActivity.class);
                                    InsertQueries.setSetting(HaatSummaryActivity.this, Settings.PASSWORD, "");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (HaatSummaryActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    Toast.makeText(HaatSummaryActivity.this, msg, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(HaatSummaryActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        }.execute();
    }


    private int saveToDatabase() {

        saleId = SelectQueries.getSaleIdByVilIdAndRId(this,villageId,route_id);

        SaleEntity saleEntity;
        saleEntity = new SaleEntity();
        saleEntity.subtotal = totalSum.getText().toString();
        saleEntity.total = saleEntity.subtotal;
        saleEntity.device_id = SelectQueries.getSetting(this, Settings.DEVICE_ID);
        saleEntity.created = (int) (System.currentTimeMillis() / 1000L);
        saleEntity.sale_field1 = ContextCommons.getNetworkDetails(this).toString();
        saleEntity.sale_field2 = route_id + "";
        saleEntity.latitude = locationEntity.latitude;
        saleEntity.longitude = locationEntity.longitude;
        saleEntity.accuracy = locationEntity.accuracy;
        saleEntity.sale_type = "2";

        if (saleId == 0) {
            this.saleId = (int) InsertQueries.insertSale(this, saleEntity);
            InsertQueries.insertVillageSummary(this, saleId, villageId, villName,route_id);
        }else {
            saleEntity.sale_id=saleId;
            UpdateQueries.updateSaleEntity(this,saleEntity);
        }

        SubOrderEntity subEntity;

        for (int i = 0; i < productSkuEntities.size(); i++) {
            subEntity = new SubOrderEntity();
            subEntity.product_id = productSkuEntities.get(i).product_id;
            subEntity.sale_id = saleId;
            subEntity.qty = Integer.parseInt(qtyEditArray.get(i).getText().toString());
            subEntity.total = (Double.parseDouble(productSkuEntities.get(i).selling_price) * subEntity.qty) + "";
            Logger.d(TAG, "Save subentity=" + subEntity.toString());
            InsertQueries.insertSubOrder(this, subEntity);
        }

        return this.saleId;
    }

    private void saveSubstractedQtyInSettings() {
        for (int i = 0; i < productSkuEntities.size(); i++) {
            String qtyString = qtyEditArray.get(i).getText().toString().trim();
            int qtyInt = Integer.parseInt(qtyString);
            Commons.substractAvailByProductName(this,productSkuEntities.get(i).name,qtyInt);
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

    public void calculate(){

        double qtyNumber,sum=0,sp;
        String qtyString;
        for(int i =0; i<qtyEditArray.size(); i++){
            qtyString = qtyEditArray.get(i).getText().toString();
            if(!"".equals(qtyString)) {
                sp = sellingPrices.get(i);
                qtyNumber = Integer.parseInt(qtyString);
                sum = (qtyNumber*sp)+sum;
            }
        }
        //sum = Commons.round(sum,2);
        totalSum.setText(Math.round(sum) + "");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
