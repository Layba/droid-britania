package com.bizone.britannia;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bizone.britannia.entities.ShopEntity;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.logreports.SendLog;
import com.bizone.britannia.queries.SelectQueries;

/**
 * Created by siddhesh on 7/26/16.
 */
public class ShopActivity extends AppCompatActivity {

    private static final String TAG = ShopActivity.class.getSimpleName();
    private boolean isMobVerify = false, isAltVerify = false;
    private ImageView verifyMobImg;
    private EditText verMobEdit, shopNameEdit, ownerNameEdit, villNameEdit;
    private String shpName, ownName, vilName, veriMob, villageName;
    private int saleId, vilId, route_id;
    private ShopEntity shopEntity;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        Logger.d(TAG, "inside onCreate");

        villageName = getIntent().getStringExtra("vilName");
        vilId = getIntent().getIntExtra("vilId", -1);
        saleId = getIntent().getIntExtra("saleId", -1);

        verifyMobImg = (ImageView) findViewById(R.id.verify_img_mob);
        verMobEdit = (EditText) findViewById(R.id.mobEdit);
        shopNameEdit = (EditText) findViewById(R.id.shop_name);
        ownerNameEdit = (EditText) findViewById(R.id.owner_name);
        villNameEdit = (EditText) findViewById(R.id.village_name);


        route_id = getIntent().getIntExtra("route_id", 0);

        if (saleId != -1) {
            shopEntity = SelectQueries.getShopElements(this, saleId);
            Logger.d(TAG, "shopEntity=" + shopEntity.toString());
            populateViews();
        }

        if (getIntent().getIntExtra("shopId", -1) != -1) {
            shopEntity = SelectQueries.getShopElementsByShopId(this, getIntent().getIntExtra("shopId", -1));
            Logger.d(TAG, "shopEntity=" + shopEntity.toString());
            populateViews();
        }
        verMobEdit.addTextChangedListener(new TextWatcher() {

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
                verifyMobImg.setImageResource(R.mipmap.unverified);
                isMobVerify = false;
            }
        });

        villNameEdit.setText(villageName);



    }

    private void populateViews() {
        shopNameEdit.setText(shopEntity.shop_name);
        ownerNameEdit.setText(shopEntity.owner_name);
        verMobEdit.setText(shopEntity.mobile_no);
        villNameEdit.setText(shopEntity.village);

        if ("closed".equals(getIntent().getStringExtra("status")) || "true".equals(getIntent().getStringExtra("server_Id"))) {
            //shopNameEdit.setClickable(false);
            shopNameEdit.setEnabled(false);
            ownerNameEdit.setEnabled(false);
            villNameEdit.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onClickBtn(View v) {
        Logger.d(TAG, "inside onClick");
        if (v.getId() == R.id.next) {
            attemptRegister();
        } else if (v.getId() == R.id.send_log) {
            Intent i = new Intent(this, SendLog.class);
            startActivity(i);
        } else if (v.getId() == R.id.verify_mob || v.getId() == R.id.call_mob) {
            Logger.d(TAG, "click verify mobile");
            final String mobEdit = verMobEdit.getText().toString();
            Boolean isNumeric = true;
            try {
                Long num = Long.parseLong(mobEdit);
            } catch (Exception e) {
                Logger.d(TAG, "error");
                isNumeric = false;
            }
            if (mobEdit.length() == 10 && isNumeric) {
                Logger.d(TAG, "call getCallDetails");
                if (v.getId() == R.id.call_mob) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    String phNum = "tel:" + mobEdit;
                    callIntent.setData(Uri.parse(phNum));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Please grant call permission in Manage Apps." + mobEdit + ""
                                , Toast.LENGTH_LONG).show();
                        return;
                    }
                    startActivity(callIntent);
                } else {
                    if (ActivityCompat.checkSelfPermission(ShopActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Toast.makeText(this,"Please give Phone permission to proceed further.",Toast.LENGTH_LONG).show();
                        return;
                    }
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("Verifying number....");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    dialog.show();
                    new VerifyMobileTask(mobEdit) {
                        @Override
                        protected void afterExecution(final boolean result) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    if(ShopActivity.this.isFinishing()){
                                        return;
                                    }
                                    closeDialog();
                                    if (result) {
                                        verifyMobImg.setImageResource(R.mipmap.verified);
                                        isMobVerify = true;
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Sorry " + mobEdit + " could not be verified."
                                                , Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, 3000);
                        }
                    }.execute();
                }
            } else {
                verMobEdit.setError("Enter correct mobile no.");
                //Toast.makeText(this,"Please enter correct mobile no.",Toast.LENGTH_LONG).show();
            }

        }
    }

    private ShopEntity createShopEntity() {


        ShopEntity entity = new ShopEntity();
        entity.base_village_id = vilId;
        entity.village = villNameEdit.getText().toString().trim();
        entity.shop_name = shopNameEdit.getText().toString().trim();
        entity.owner_name = ownerNameEdit.getText().toString().trim();
        entity.mobile_no = verMobEdit.getText().toString().trim();

        if (isMobVerify) {
            entity.verification_status = "1";
        } else {
            entity.verification_status = "0";
        }
        if (isAltVerify) {
            entity.alt_verification_status = "1";
        } else {
            entity.alt_verification_status = "0";
        }
        entity.created = (int) (System.currentTimeMillis() / 1000L);

        if (saleId != -1 || "closed".equals(getIntent().getStringExtra("status"))) {
            entity.shop_id = shopEntity.shop_id;
            entity.ser_shopId = shopEntity.ser_shopId;
        }

        Logger.d(TAG, "on save shopEntity=" + entity.toString());

        return entity;
    }

    private void attemptRegister() {

        boolean cancel = false;
        View focusView = null;

        shopNameEdit.setError(null);
        ownerNameEdit.setError(null);
        villNameEdit.setError(null);
        verMobEdit.setError(null);

        shpName = shopNameEdit.getText().toString().trim();
        ownName = ownerNameEdit.getText().toString().trim();
        vilName = villNameEdit.getText().toString().trim();
        veriMob = verMobEdit.getText().toString().trim();


        if (TextUtils.isEmpty(shpName)) {
            shopNameEdit.setError("Shop Name is required");
            focusView = shopNameEdit;
            cancel = true;
        } else if (isCodeValidate(shpName)) {
            shopNameEdit.setError(getString(R.string.error_more_3));
            focusView = shopNameEdit;
            cancel = true;
        }


        if (TextUtils.isEmpty(ownName) && !cancel) {
            ownerNameEdit.setError("Owner Name is required");
            focusView = ownerNameEdit;
            cancel = true;
        } else if (isCodeValidate(ownName) && !cancel) {
            ownerNameEdit.setError(getString(R.string.error_more_3));
            focusView = ownerNameEdit;
            cancel = true;
        }


        if (TextUtils.isEmpty(veriMob) && !cancel) {
            verMobEdit.setError("Mobile no. is required");
            focusView = verMobEdit;
            cancel = true;
        } else if (!isContactValid(veriMob) && !cancel) {
            verMobEdit.setError(getString(R.string.error_incorrect_contact));
            focusView = verMobEdit;
            cancel = true;

        }


        if (TextUtils.isEmpty(vilName) && !cancel) {
            villNameEdit.setError("Village is required");
            focusView = villNameEdit;
            cancel = true;
        } else if (isCodeValidate(vilName) && !cancel) {
            villNameEdit.setError(getString(R.string.error_more_3));
            focusView = villNameEdit;
            cancel = true;
        }

        if (!isMobVerify && !cancel) {
            new android.app.AlertDialog.Builder(ShopActivity.this)
                    .setMessage("Mobile number is not verified. Do you wish to verify?")
                    .setPositiveButton("Verify", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startVerify(veriMob);
                        }
                    })
                    .setNegativeButton("Proceed", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                       startNextActivity();
                        }
                    }).show();
            cancel = true;
        }


        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            //further code to be executed
            startNextActivity();
        }

    }


    private void startVerify(final String veriMob) {

        dialog = new ProgressDialog(ShopActivity.this);
        dialog.setMessage("Verifying number....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        new VerifyMobileTask(veriMob) {
            @Override
            protected void afterExecution(final boolean result) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if(ShopActivity.this.isFinishing()){
                            return;
                        }
                        closeDialog();
                        if (result) {
                            verifyMobImg.setImageResource(R.mipmap.verified);
                            isMobVerify = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "Sorry " + veriMob + " could not be verified."
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                }, 3000);

            }
        }.execute();
    }

    private void startNextActivity() {
        if (ActivityCompat.checkSelfPermission(ShopActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ShopActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(ShopActivity.this,"Please give Location permission to proceed further.",Toast.LENGTH_LONG).show();
            return;
        }
        ShopEntity entity = createShopEntity();
        Intent i = new Intent(this, PhotoListActivity.class);
        i.putExtra("ShopEntity", entity);
        if (saleId != -1) {
            i.putExtra("shopId", shopEntity.shop_id);
            i.putExtra("saleId", saleId);
        }
        if ("closed".equals(getIntent().getStringExtra("status"))) {
            i = new Intent(this, PhotoListActivity.class);
            i.putExtra("ShopEntity", entity);
            i.putExtra("status", "closed");
        }
        i.putExtra("route_id", route_id);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if ((!TextUtils.isEmpty(shpName)) || (!TextUtils.isEmpty(ownName)) || (!TextUtils.isEmpty(villageName)) || (!TextUtils.isEmpty(veriMob))) {
            new android.app.AlertDialog.Builder(ShopActivity.this)
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

    public static boolean isValidateName(String lastName) {
        return lastName.matches("[a-zA-z]+([ '-][a-zA-Z]+)*");
    }

    private boolean isCodeValidate(String code) {
        return code.length() < 3;
    }

    private boolean isContactValid(String password) {
        return password.length() == 10;
    }


    @Override
    protected void onDestroy() {
        closeDialog();
        super.onDestroy();
    }

    private void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(TAG, "inside onPause");

    }

    public abstract class VerifyMobileTask extends AsyncTask<Void, Void, Boolean> {

        private String mobileNo;

        VerifyMobileTask(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        @Override
        protected void onPreExecute() {
            Logger.d(TAG, "inside onPreExecute");
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean retVal = false;

            Logger.d(TAG, "inside getCallDetails");
            StringBuffer sb = new StringBuffer();
            String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
  /* Query the CallLog Content Provider */
            if (ActivityCompat.checkSelfPermission(ShopActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return false;
            }
            Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    null, null, strOrder);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            while (managedCursor.moveToNext()) {
                Logger.d(TAG,"inside while loop");
                String phNum = managedCursor.getString(number);
                String callTypeCode = managedCursor.getString(type);
                String callDuration=managedCursor.getString(duration);
                String callType = "None";
                int callcode = Integer.parseInt(callTypeCode);
                switch (callcode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        callType = "Outgoing";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        callType = "Incoming";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callType = "Missed";
                        break;

                }

                if(callType.equals("Outgoing") && !callDuration.isEmpty() && !callDuration.contains("0")) {
                    Logger.d(TAG,"Outgoing call");
                    if (PhoneNumberUtils.compare(phNum, mobileNo)) {
                        retVal = true;
                        break;
                    }
                }

                if(callType.equals("Missed")||callType.equals("Incoming")) {
                    Logger.d(TAG,"incomming or missed");
                    if (PhoneNumberUtils.compare(phNum, mobileNo)) {
                        retVal = true;
                        break;
                    }
                }

            }
            // managedCursor.close();

            return retVal;
        }

        protected abstract void afterExecution(boolean result);

        @Override
        protected void onPostExecute(Boolean result) {
            Logger.d(TAG,"inside onPostExecute="+result);
            afterExecution(result);

        }
    }
}
