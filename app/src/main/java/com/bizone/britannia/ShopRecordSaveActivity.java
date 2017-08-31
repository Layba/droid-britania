package com.bizone.britannia;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.SaleMetadataEntity;
import com.bizone.britannia.entities.ShopEntity;
import com.bizone.britannia.entities.SubOrderEntity;
import com.bizone.britannia.internetTask.SendSalesDataTask;
import com.bizone.britannia.internetTask.SendSalesMetadataTask;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.DeleteQueries;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.queries.UpdateQueries;

import java.util.ArrayList;

/**
 * Created by siddhesh on 7/27/16.
 */
public class ShopRecordSaveActivity extends AppCompatActivity {
    private static final String TAG = ShopRecordSaveActivity.class.getSimpleName();
    private LinearLayout container;
    private LinearLayout mainLl;
    private TextView productTxt, mrpTxt, avaiQty;
    private ArrayList<EditText> qtyEditArray;
    private ArrayList<TextView> qtyAvailArray;
    private EditText qtyEdit;
    private ShopEntity shopEntity;
    private TextView totalSum;
    private ArrayList<Double> sellingPrices;
    private ArrayList<ProductSkuEntity> productSkuEntities;
    private ArrayList<SaleMetadataEntity> saleMetadataEntities;
    private ProgressDialog dialog;
    private int saleId, route_id;
    private Button noSale;
    private LocationEntity locationEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "inside onCreate");
        setContentView(R.layout.activity_record_save);
        noSale = (Button) findViewById(R.id.no_sale);
        mainLl = (LinearLayout) findViewById(R.id.main);

        totalSum = (TextView) findViewById(R.id.total);
        route_id = getIntent().getIntExtra("route_id", 0);
        locationEntity = (LocationEntity) getIntent().getSerializableExtra("LocationEntity");

        shopEntity = (ShopEntity) getIntent().getSerializableExtra("ShopEntity");
        saleMetadataEntities = (ArrayList<SaleMetadataEntity>) getIntent().getSerializableExtra("SaleMetadata");
        saleId = getIntent().getIntExtra("saleId", -1);

        noSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ShopRecordSaveActivity.this)
                        .setMessage("Are you sure there is No Sale for this shop?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                                EditText qty;
                                for (int i = 0; i < qtyEditArray.size(); i++) {
                                    qty = qtyEditArray.get(i);
                                    qty.setText("0");
                                }
                                totalSum.setText("0");
                                dialog.dismiss();
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
        });

        productSkuEntities = SelectQueries.getProductSkuElements(this, SelectQueries.getSetting(this, Settings.STATE));
        qtyEditArray = new ArrayList<EditText>();
        qtyAvailArray = new ArrayList<TextView>();
        sellingPrices = new ArrayList<Double>();

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
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.66f));
            qtyEdit.setSingleLine(true);
            qtyEdit.setInputType(InputType.TYPE_CLASS_PHONE);
            qtyEdit.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            if (i == 0) {
                qtyEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(qtyEdit, InputMethodManager.SHOW_IMPLICIT);
            }


            LinearLayout.LayoutParams params = new LinearLayout.
                    LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                            Toast.makeText(ShopRecordSaveActivity.this, "Enter proper number", Toast.LENGTH_SHORT).show();
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

        Logger.d(TAG, "setActionBar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
    }


    @Override
    protected void onResume() {
        super.onResume();

        for (int i = 0; i < productSkuEntities.size(); i++) {
            qtyAvailArray.get(i).setText(Commons.getAvailByProductName(this, productSkuEntities.get(i).name)+"");
        }

    }

    public void calculate() {

        double qtyNumber, sum = 0, sp;
        int scratchCoupon = 0;
        String qtyString;
        for (int i = 0; i < qtyEditArray.size(); i++) {
            qtyString = qtyEditArray.get(i).getText().toString();
            if (!"".equals(qtyString)) {
                sp = sellingPrices.get(i);
                qtyNumber = Integer.parseInt(qtyString);
                sum = (qtyNumber * sp) + sum;
                if (qtyNumber >= 24) {
                    scratchCoupon++;
                }
            }
        }
        //sum = Commons.round(sum,2);
        totalSum.setText(Math.round(sum) + "");

    }

    public void onSaveUpdate(View v) {
        boolean save = attemptSave();
        Logger.d(TAG, "Save= " + save);
        if (save) {
            saveSubstractedQtyInSettings();
            int saleId = saveToDatabase();
            UpdateQueries.updateSaleStatus(this, saleId, Constants.INPROGRESS);
            if (!ContextCommons.isOnline(this)) {
                Intent i = new Intent(ShopRecordSaveActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                Toast.makeText(this, "Internet is unavailable. Please turn ON mobile data", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading Data....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            new SendSalesDataTask(this, saleId) {
                @Override
                protected void afterExecution(boolean result, String msg, int sale_Id) {
                    if (result) {
                        sendMetaImages(sale_Id);
                    } else if ("403".equals(msg)) {
                        // expire
                        if (ShopRecordSaveActivity.this.isFinishing()) {
                            return;
                        }
                        closeDialog();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShopRecordSaveActivity.this);
                        builder.setMessage("Session expired. Please login again.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(ShopRecordSaveActivity.this, LoginActivity.class);
                                        InsertQueries.setSetting(ShopRecordSaveActivity.this, Settings.PASSWORD, "");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        if (ShopRecordSaveActivity.this.isFinishing()) {
                            return;
                        }
                        closeDialog();
                        Toast.makeText(ShopRecordSaveActivity.this, msg, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(ShopRecordSaveActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                }
            }.execute();
        }
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

    private void sendMetaImages(final int saleId) {

        dialog.setMessage("Uploading Images....");

        new SendSalesMetadataTask(this, saleId) {
            @Override
            protected void afterExecution() {
                if (ShopRecordSaveActivity.this.isFinishing()) {
                    return;
                }
                closeDialog();
                UpdateQueries.updateSaleStatus(ShopRecordSaveActivity.this, saleId, Constants.CLOSED);
                SaleEntity saleEntity = SelectQueries.getSaleElements(ShopRecordSaveActivity.this, saleId);
                UpdateQueries.updateShopDate(ShopRecordSaveActivity.this, saleEntity.created, saleId);
                Intent i = new Intent(ShopRecordSaveActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                Toast.makeText(ShopRecordSaveActivity.this, "Data uploaded successfully.", Toast.LENGTH_LONG).show();
            }

            @Override
            protected void notUploaded(String code, String msg) {
                if ("403".equals(code)) {
                    // expire
                    if (ShopRecordSaveActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopRecordSaveActivity.this);
                    builder.setMessage("Session expired. Please login again.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(ShopRecordSaveActivity.this, LoginActivity.class);
                                    InsertQueries.setSetting(ShopRecordSaveActivity.this, Settings.PASSWORD, "");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (ShopRecordSaveActivity.this.isFinishing()) {
                        return;
                    }
                    closeDialog();
                    Toast.makeText(ShopRecordSaveActivity.this, msg, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ShopRecordSaveActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        }.execute();
    }

    private int saveToDatabase() {

        SaleEntity saleEntity;
        SubOrderEntity subEntity;

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
        saleEntity.sale_type = "1";

        Logger.d(TAG, "Save insideSaleEntity=" + saleEntity.toString());
        shopEntity.sale_id = saleId;


        if ("closed".equals(getIntent().getStringExtra("status"))) {

            int saleId = (int) InsertQueries.insertSale(this, saleEntity);

            for (int i = 0; i < productSkuEntities.size(); i++) {
                subEntity = new SubOrderEntity();
                subEntity.product_id = productSkuEntities.get(i).product_id;
                subEntity.sale_id = saleId;
                subEntity.qty = Integer.parseInt(qtyEditArray.get(i).getText().toString());
                subEntity.total = (Double.parseDouble(productSkuEntities.get(i).selling_price) * subEntity.qty) + "";
                Logger.d(TAG, "Save Closed suborder=" + subEntity.toString());
                InsertQueries.insertSubOrder(this, subEntity);
            }

            for (SaleMetadataEntity saleMetaEntity : saleMetadataEntities) {
                saleMetaEntity.sale_id = saleId;
                Logger.d(TAG, "Save Closed saleMeta=" + saleMetaEntity.toString());
                InsertQueries.insertSaleMetadata(this, saleMetaEntity);
            }

            shopEntity.sale_id = saleId;
            Logger.d(TAG, "Save Closed saleid=" + saleId);
            UpdateQueries.updateShopRecord(this, shopEntity);
            this.saleId = saleId;

        } else if (saleId != -1) {

            UpdateQueries.updateSaleRecord(this, saleEntity);

            DeleteQueries.deleteSuborderRecord(this, saleId);
            for (int i = 0; i < productSkuEntities.size(); i++) {
                subEntity = new SubOrderEntity();
                subEntity.product_id = productSkuEntities.get(i).product_id;
                subEntity.sale_id = saleId;
                subEntity.qty = Integer.parseInt(qtyEditArray.get(i).getText().toString());
                subEntity.total = (Math.round(Double.parseDouble(productSkuEntities.get(i).selling_price)) * subEntity.qty) + "";
                Logger.d(TAG, "Save saleid Suborder=" + subEntity.toString());
                InsertQueries.insertSubOrder(this, subEntity);
            }

            DeleteQueries.deleteSaleMetadataRecord(this, saleId);
            for (SaleMetadataEntity saleMetaEntity : saleMetadataEntities) {
                saleMetaEntity.sale_id = saleId;
                Logger.d(TAG, "Save saleid saleMeta=" + saleMetaEntity.toString());
                InsertQueries.insertSaleMetadata(this, saleMetaEntity);
            }

            UpdateQueries.updateShopRecord(this, shopEntity);
        } else {

            int saleId = (int) InsertQueries.insertSale(this, saleEntity);

            for (int i = 0; i < productSkuEntities.size(); i++) {
                subEntity = new SubOrderEntity();
                subEntity.product_id = productSkuEntities.get(i).product_id;
                subEntity.sale_id = saleId;
                subEntity.qty = Integer.parseInt(qtyEditArray.get(i).getText().toString());
                subEntity.total = (Double.parseDouble(productSkuEntities.get(i).selling_price) * subEntity.qty) + "";
                Logger.d(TAG, "Save subentity=" + subEntity.toString());
                InsertQueries.insertSubOrder(this, subEntity);
            }

            for (SaleMetadataEntity saleMetaEntity : saleMetadataEntities) {
                saleMetaEntity.sale_id = saleId;
                InsertQueries.insertSaleMetadata(this, saleMetaEntity);
                Logger.d(TAG, "Save saleMeta=" + saleMetaEntity.toString());
            }

            shopEntity.sale_id = saleId;
            int shopId = (int) InsertQueries.insertShop(this, shopEntity);
            Logger.d(TAG, "Save shopid=" + shopId);

            this.saleId = saleId;

        }
        return saleId;
    }

    private String humanReadableByteCount(long bytes) {
        Logger.d(TAG, "inside humanreadable");
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
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

        for (int i = 0; i < qtyEditArray.size(); i++) {
            String qtyString = qtyEditArray.get(i).getText().toString().trim();
            String avlString = qtyAvailArray.get(i).getText().toString().trim();
            if (Integer.parseInt(avlString) - Integer.parseInt(qtyString) < 0) {
                new android.app.AlertDialog.Builder(ShopRecordSaveActivity.this)
                        .setMessage("You do not have enough available quantity for 1 or more products .Please purchase before proceeding.")
                        .setPositiveButton("Purchase", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(ShopRecordSaveActivity.this, PurchaseActivity.class);
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

    @Override
    public void onBackPressed() {
        if (!totalSum.getText().toString().equals("")) {
            new android.app.AlertDialog.Builder(ShopRecordSaveActivity.this)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
