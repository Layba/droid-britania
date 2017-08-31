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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bizone.britannia.entities.LocationEntity;
import com.bizone.britannia.entities.ProductSkuEntity;
import com.bizone.britannia.entities.PurchaseEntity;
import com.bizone.britannia.entities.PurchaseSubOrderEntity;
import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.SubOrderEntity;
import com.bizone.britannia.internetTask.SendStockiestTask;
import com.bizone.britannia.location.LocationCaptureTask;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.queries.UpdateQueries;

import java.util.ArrayList;

/**
 * Created by Sonam on 9/12/16.
 */
public class PurchaseActivity extends AppCompatActivity {
    private static final String TAG=PurchaseActivity.class.getSimpleName();
    private LinearLayout container;
    private LinearLayout mainLl;
    private TextView productTxt ;
    private TextView mrpTxt,avaiQty;
    private Button purchaseUpload;
    private ArrayList<EditText> qtyEditArray;
    private ArrayList<TextView> qtyAvailArray;
    private EditText qtyEdit,stockName,stockNo;
    private TextView totalSum;
    private ArrayList<Double> sellingPrices;
    private ArrayList<ProductSkuEntity> productSkuEntities;
    private ArrayList<SubOrderEntity> subOrderEntities;
    private int saleId;
    private String stockistName, stockistNum;
    private ProgressDialog dialog;
    private boolean isLocationCaptured = false;
    private LocationEntity locationEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Purchase");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purchase)));

        mainLl = (LinearLayout) findViewById(R.id.main);
        totalSum = (TextView) findViewById(R.id.total);

        purchaseUpload = (Button) findViewById(R.id.purchaseUpload);
        stockName = (EditText) findViewById(R.id.stock_name);
        stockNo = (EditText) findViewById(R.id.stock_no);


        productSkuEntities = SelectQueries.getProductSkuElements(this, SelectQueries.getSetting(this, Settings.STATE));
        qtyEditArray = new ArrayList<EditText>();
        sellingPrices = new ArrayList<Double>();
        qtyAvailArray = new ArrayList<TextView>();
        subOrderEntities = new ArrayList<SubOrderEntity>();

        String status = Constants.CLOSED;
        if (saleId != -1) {
            SaleEntity entity = SelectQueries.getSaleElements(this, saleId);
            status = entity.status;
        }

        if (saleId != -1 && Constants.OPEN.equals(status)) {
            subOrderEntities = SelectQueries.getSubOrderElements(this, saleId);
        }

        SubOrderEntity subOrderEntity;

        for (int i = 0; i < productSkuEntities.size(); i++) {
            Logger.d(TAG, "ProductSkuEntity=" + productSkuEntities.get(i).toString());
            container = new LinearLayout(this);
            productTxt = new TextView(this);
            mrpTxt = new TextView(this);
            qtyEdit = new EditText(this);
            avaiQty = new TextView(this);
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
                            Toast.makeText(PurchaseActivity.this, "Enter proper number", Toast.LENGTH_SHORT).show();
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

    public void onSaveUpdate(View v) {
        Logger.d(TAG, "inside onClick");
        if (v.getId() == R.id.purchaseUpload) {
            boolean save = attemptSave();
            Logger.d(TAG, "Save= " + save);
            if (save) {
                int pId = saveToDatabase();
              //write for purchase tbl status
                UpdateQueries.updatePurchaseStatus(this, pId, Constants.INPROGRESS);
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
                new SendStockiestTask(this, pId) {
                    @Override
                    protected void afterExecution(boolean result, String msg) {
                        if (result) {
                            Toast.makeText(PurchaseActivity.this, "Stocklist added successfully", Toast.LENGTH_LONG).show();
                            finish();
                        } else if ("403".equals(msg)) {
                            // expire
                            if(PurchaseActivity.this.isFinishing()){
                                return;
                            }
                            closeDialog();
                            AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseActivity.this);
                            builder.setMessage("Session expired. Please login again.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(PurchaseActivity.this, LoginActivity.class);
                                            InsertQueries.setSetting(PurchaseActivity.this, Settings.PASSWORD, "");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            if(PurchaseActivity.this.isFinishing()){
                                return;
                            }
                            closeDialog();
                            Toast.makeText(PurchaseActivity.this, msg, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }.execute();
            }
        }
    }

    private boolean attemptSave() {

        if (totalSum.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_LONG).show();
            return false;
        }

        for (int i = 0; i < qtyEditArray.size(); i++) {

            Logger.d(TAG, "inside for i = " + i);
            EditText qty = qtyEditArray.get(i);

            if (qty.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Quantity should not be empty", Toast.LENGTH_LONG).show();
                qty.requestFocus();
                return false;
            }
        }
        boolean cancel = false;
        View focusView = null;

        stockName.setError(null);
        stockNo.setError(null);

        stockistName = stockName.getText().toString().trim();
        stockistNum = stockNo.getText().toString().trim();

            if (!stockistName.isEmpty() && isCodeValidate(stockistName) && !cancel) {
                stockName.setError(getString(R.string.error_more_3));
                focusView = stockName;
                cancel = true;
                return false;
            }

        if (!stockistNum.isEmpty() && !isContactValid(stockistNum) && !cancel) {
                stockNo.setError(getString(R.string.error_incorrect_contact));
                focusView = stockNo;
                cancel = true;
                return false;
            }

        return true;
    }

    private int saveToDatabase() {
        PurchaseEntity purchaseEntity;
        PurchaseSubOrderEntity purchaseSubOrderEntity;

        purchaseEntity = new PurchaseEntity();
        purchaseEntity.stockiestName = stockName.getText().toString().trim();
        purchaseEntity.stockiestNo = stockNo.getText().toString().trim();
        purchaseEntity.total = totalSum.getText().toString().trim();
        purchaseEntity.device_id = SelectQueries.getSetting(this,Settings.DEVICE_ID);
        purchaseEntity.latitude = locationEntity.latitude;
        purchaseEntity.longitude = locationEntity.longitude;
        purchaseEntity.accuracy = locationEntity.accuracy;
        purchaseEntity.created = (int) (System.currentTimeMillis()/1000L);

        Logger.d(TAG, "Save insidePurchaseEntity="+purchaseEntity.toString());


        int pId = (int) InsertQueries.insertPurchase(this, purchaseEntity);


            for (int i = 0; i < productSkuEntities.size(); i++) {
                purchaseSubOrderEntity = new PurchaseSubOrderEntity();
                purchaseSubOrderEntity.p_id = pId;
                purchaseSubOrderEntity.product_id = productSkuEntities.get(i).product_id;
                purchaseSubOrderEntity.qty = Integer.parseInt(qtyEditArray.get(i).getText().toString());
                purchaseSubOrderEntity.total = (Double.parseDouble(productSkuEntities.get(i).selling_price) * purchaseSubOrderEntity.qty) + "";
                Logger.d(TAG, "Save purchaseSubOrderEntity="+purchaseSubOrderEntity.toString());
                InsertQueries.insertPurchaseSubOrder(this, purchaseSubOrderEntity);
            }

        return pId;
    }

    private boolean isCodeValidate(String code) {
        return code.length() < 3;
    }

    private boolean isContactValid(String password) {
        return password.length() == 10;
    }


    public void calculate(){

        double qtyNumber,sum=0,sp;
        int scratchCoupon = 0;
        String qtyString;
        for(int i =0; i<qtyEditArray.size(); i++){
            qtyString = qtyEditArray.get(i).getText().toString();
            if(!"".equals(qtyString)) {
                sp = sellingPrices.get(i);
                qtyNumber = Integer.parseInt(qtyString);
                sum = (qtyNumber*sp)+sum;
                if(qtyNumber>=24){
                    scratchCoupon++;
                }
            }
        }
        //sum = Commons.round(sum,2);
        totalSum.setText(Math.round(sum) + "");

    }


    @Override
    public void onBackPressed() {
        if(!totalSum.getText().toString().equals("")){
            new android.app.AlertDialog.Builder(PurchaseActivity.this)
                    .setMessage("Your current data will be lost. Do you wish to continue?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else {
            finish();
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
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
