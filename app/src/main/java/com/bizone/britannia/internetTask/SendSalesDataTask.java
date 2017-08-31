package com.bizone.britannia.internetTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.KeyEvent;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.Settings;
import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.ShopEntity;
import com.bizone.britannia.entities.SubOrderEntity;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.queries.UpdateQueries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sagar on 28/7/16.
 */
public abstract class SendSalesDataTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG=SendSalesDataTask.class.getSimpleName();
    private Context context;
    private ProgressDialog mProgressDialog;
    JSONObject json;
    String errMsg = "Error sending Sales Data";
    int saleId,shopId;

    public SendSalesDataTask(Context context,int saleId) {
        this.context = context;
        this.saleId = saleId;
    }

    @Override
    protected void onPreExecute() {
        Logger.d(TAG,"inside onPreExecute");
        //showProgressDialog();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Logger.d(TAG,"inside doInBackground");
        boolean retVal=false;

        try {
            URL url=new URL(Constants.APP_TRANSACTION_URL);
            Logger.d(TAG,"url="+url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput (true);
            urlConnection.setDoOutput (true);
            urlConnection.setUseCaches (false);
            urlConnection.setRequestMethod( "POST" );
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("X-CSRF-Token", SelectQueries.getSetting(context, Settings.TOKEN));
            urlConnection.setRequestProperty("Cookie", SelectQueries.getSetting(context, Settings.SESS_NAME_ID));

            SaleEntity entity = SelectQueries.getSaleElements(context,saleId);

            if(entity.transaction_id != 0){
                return true; // If the sale is already uploaded, do not upload again
            }

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("sales", getSalesJson());
            jsonParam.put("sub_order", getSubOrderArray());
            jsonParam.put("shop", getShopJson());
            jsonParam.put("saleMetaData", Commons.getBase64Encoded(getSaleMeta()));

            Logger.d(TAG,"jsonParams="+jsonParam.toString());
            DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());
            printout.writeBytes(jsonParam.toString());
            printout.flush ();
            printout.close ();
            int statusCode = urlConnection.getResponseCode();

            StringBuffer jsonString=new StringBuffer();
                     /* 200 represents HTTP OK */
            if (statusCode == 200) {
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
                String line;
                while ((line = inputStream.readLine()) != null) {
                    jsonString.append(line);
                }
                inputStream.close();
                Logger.d(TAG, "response =" + jsonString);
                json=new JSONObject(jsonString.toString());
                String status="error";
                status=json.optString("status");
                if("success".equals(status)){
                    retVal=addToDb();
                }else {
                    errMsg = json.optString("msg","Error sending Sales Data");
                    Logger.d(TAG,"error= "+errMsg);
                }
            }else {
                String resMsg;
                resMsg = urlConnection.getResponseMessage();
                Logger.d(TAG,"resMsg=" + resMsg);
                if("".equals(resMsg)) {
                    errMsg = Commons.getWSErrors(statusCode);
                }else {
                    errMsg = resMsg;
                }
                retVal = false;
            }
            urlConnection.disconnect();
        }catch (Exception e){
            Logger.e(TAG,e);
            retVal=false;
            errMsg = "Error fetching Product Sku";
            e.printStackTrace();
        }

        return retVal;
    }

    private boolean addToDb() {
        Logger.d(TAG,"inside addToDb");
        boolean retVal=false;
        int trans_id = json.optInt("transaction_id", 1);
        if(!"error".equals(trans_id)){
            try {
                UpdateQueries.updateSaleTransId(context, saleId, trans_id);
                ShopEntity shopEntity = SelectQueries.getShopElements(context, saleId);
                if(shopEntity.ser_shopId == 0) {
                    UpdateQueries.updateShopSerId(context, saleId, trans_id);
                }
                UpdateQueries.updateSaleMetaTransId(context, saleId, trans_id);
                retVal = true;
            }catch (Exception e) {
                Logger.e(TAG,e);
                e.printStackTrace();
            }
        }
        return retVal;
    }

    protected abstract void afterExecution(boolean result,String msg, int saleId);

    @Override
    protected void onPostExecute(Boolean result) {
        Logger.d(TAG,"inside onPostExecute="+result);
        if(mProgressDialog != null){
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
        afterExecution(result, errMsg, saleId);

    }

    private void showProgressDialog(){
        Logger.d(TAG,"inside showProgressDialog");
        String message;

        message = "Please Wait...";

        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    // Act as if all keys are processed
                    return true;
                }
            });
        }
        mProgressDialog.show();
    }

    private JSONObject getSalesJson() {
        Logger.d(TAG,"inside getSalesJson");
        SaleEntity entity = SelectQueries.getSaleElements(context, saleId);
        Logger.d(TAG,"saleEntity="+entity);
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("sale_id", Commons.getBase64Encoded(entity.sale_id+""));
            jObj.put("vid", Commons.getBase64Encoded(entity.vid+""));
            jObj.put("transaction_id", Commons.getBase64Encoded(entity.transaction_id+""));
            jObj.put("subtotal", Commons.getBase64Encoded(entity.subtotal));
            jObj.put("tax", Commons.getBase64Encoded(entity.tax));
            jObj.put("discount", Commons.getBase64Encoded(entity.discount));
            jObj.put("total", Commons.getBase64Encoded(entity.total));
            jObj.put("device_id", Commons.getBase64Encoded(entity.device_id));
            jObj.put("created", Commons.getBase64Encoded(entity.created+""));
            jObj.put("created_by", Commons.getBase64Encoded(entity.created_by+""));
            jObj.put("updated", Commons.getBase64Encoded(entity.updated+""));
            jObj.put("updated_by", Commons.getBase64Encoded(entity.updated_by+""));
            jObj.put("approved_flag", Commons.getBase64Encoded(entity.approved_flag));
            jObj.put("latitude", Commons.getBase64Encoded(entity.latitude));
            jObj.put("longitude", Commons.getBase64Encoded(entity.longitude));
            jObj.put("accuracy", Commons.getBase64Encoded(entity.accuracy));
            //jObj.put("sale_field1", Commons.getBase64Encoded(entity.sale_field1));
            jObj.put("route_plan_id", Commons.getBase64Encoded(entity.sale_field2));
            jObj.put("sale_field3", Commons.getBase64Encoded(entity.sale_field3));
            jObj.put("sale_field4", Commons.getBase64Encoded(entity.sale_field4));
            jObj.put("sale_field5", Commons.getBase64Encoded(entity.sale_field5));
            jObj.put("sale_type", Commons.getBase64Encoded(entity.sale_type));
        }catch (JSONException e){
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        return jObj;
    }

    private String getSaleMeta(){
        Logger.d(TAG,"inside getSaleMeta");
        SaleEntity entity = SelectQueries.getSaleElements(context, saleId);
        Logger.d(TAG,"saleEntity="+entity);
        return entity.sale_field1;
    }

    private JSONArray getSubOrderArray() {
        Logger.d(TAG,"inside getSubOrderArray");
        ArrayList<SubOrderEntity> entities = SelectQueries.getSubOrderElements(context,saleId);
        JSONArray jArray = null;
        for (SubOrderEntity entity:entities){
            Logger.d(TAG,"suborder="+entity);
            JSONObject jObj = new JSONObject();
            if(jArray == null) {
                jArray = new JSONArray();
            }
            try{
                jObj.put("so_id", Commons.getBase64Encoded(entity.so_id+""));
                jObj.put("sale_id", Commons.getBase64Encoded(entity.sale_id+""));
                jObj.put("product_id", Commons.getBase64Encoded(entity.product_id+""));
                jObj.put("qty", Commons.getBase64Encoded(entity.qty+""));
                jObj.put("total", Commons.getBase64Encoded(entity.total));
                jObj.put("so_field1", Commons.getBase64Encoded(entity.so_field1));
                jObj.put("so_field2", Commons.getBase64Encoded(entity.so_field2));
                jObj.put("so_field3", Commons.getBase64Encoded(entity.so_field3));
                jObj.put("so_field4", Commons.getBase64Encoded(entity.so_field4));
                jObj.put("so_field5", Commons.getBase64Encoded(entity.so_field5));
                jArray.put(jObj);
            }catch (JSONException e){
                Logger.e(TAG,e);
                e.printStackTrace();
            }
        }
        return jArray;
    }

    private JSONObject getShopJson() {
        Logger.d(TAG,"inside getShopJson");
        ShopEntity entity = SelectQueries.getShopElements(context, saleId);
        Logger.d(TAG,"ShopEntity="+entity.toString());
        JSONObject jObj = new JSONObject();
        try {
            shopId = entity.shop_id;
            jObj.put("shop_id", Commons.getBase64Encoded(entity.shop_id+""));
            jObj.put("ser_shop_id", Commons.getBase64Encoded(entity.ser_shopId+""));
            jObj.put("vid", Commons.getBase64Encoded(entity.vid+""));
            jObj.put("shop_name", Commons.getBase64Encoded(entity.shop_name));
            jObj.put("owner_name", Commons.getBase64Encoded(entity.owner_name));
            jObj.put("mobile_no", Commons.getBase64Encoded(entity.mobile_no));
            jObj.put("alt_no", Commons.getBase64Encoded(entity.alt_no));
            jObj.put("verification_status", Commons.getBase64Encoded(entity.verification_status));
            jObj.put("alt_verification_status", Commons.getBase64Encoded(entity.alt_verification_status));
            jObj.put("base_village_id", Commons.getBase64Encoded(entity.base_village_id+""));
            jObj.put("village", Commons.getBase64Encoded(entity.village));
            jObj.put("sale_id", Commons.getBase64Encoded(entity.sale_id+""));
            jObj.put("created", Commons.getBase64Encoded(entity.created+""));
            jObj.put("created_by", Commons.getBase64Encoded(entity.created_by+""));
            jObj.put("updated", Commons.getBase64Encoded(entity.updated+""));
            jObj.put("updated_by", Commons.getBase64Encoded(entity.updated_by+""));
            jObj.put("shop_field1", Commons.getBase64Encoded(entity.shop_field1));
            jObj.put("shop_field2", Commons.getBase64Encoded(entity.shop_field2));
            jObj.put("shop_field3", Commons.getBase64Encoded(entity.shop_field3));
            jObj.put("shop_field4", Commons.getBase64Encoded(entity.shop_field4));
            jObj.put("shop_field5", Commons.getBase64Encoded(entity.shop_field5));
        }catch (JSONException e){
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        return jObj;
    }
}