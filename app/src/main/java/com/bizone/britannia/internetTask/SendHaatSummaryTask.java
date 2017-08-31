package com.bizone.britannia.internetTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;
import com.bizone.britannia.Settings;
import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.SubOrderEntity;
import com.bizone.britannia.logreports.Logger;
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
 * Created by Sonam on 9/12/16.
 */
public abstract class SendHaatSummaryTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG=SendHaatSummaryTask.class.getSimpleName();
    private Context context;
    private ProgressDialog mProgressDialog;
    JSONObject json;
    String errMsg = "Error sending Sales Data";
    int saleId,shopId;

    public SendHaatSummaryTask(Context context, int saleId) {
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
            URL url=new URL(Constants.HAAT_SUMMARY_URL);
            Logger.d(TAG,"url="+url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput (true);
            urlConnection.setDoOutput (true);
            urlConnection.setUseCaches (false);
            urlConnection.setRequestMethod( "POST" );
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("X-CSRF-Token", SelectQueries.getSetting(context, Settings.TOKEN));
            urlConnection.setRequestProperty("Cookie", SelectQueries.getSetting(context, Settings.SESS_NAME_ID));

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("haatsummary", getSalesJson());
            jsonParam.put("sub_order", getSubOrderArray());

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
                    retVal = addToDb();
                }else {
                    errMsg = json.optString("msg","Error sending Haat Summary");
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
            errMsg = "Error sending Haat summary";
            e.printStackTrace();
        }

        return retVal;
    }

    protected abstract void afterExecution(boolean result,String msg,int saleId);

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

    private boolean addToDb() {
        Logger.d(TAG,"inside addToDb");
        boolean retVal=false;
        int trans_id = json.optInt("transaction_id", 1);
        if(!"error".equals(trans_id)){
            try {
                UpdateQueries.updateSaleTransId(context, saleId, trans_id);

                UpdateQueries.updateSaleMetaTransId(context, saleId, trans_id);
                retVal = true;
            }catch (Exception e) {
                Logger.e(TAG,e);
                e.printStackTrace();
            }
        }
        return retVal;
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
            int vilId = SelectQueries.getVillIdBySaleId(context,entity.sale_id);
            jObj.put("village_id", Commons.getBase64Encoded(vilId+""));
        }catch (JSONException e){
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        return jObj;
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
}
