package com.bizone.britannia.internetTask;

import android.content.Context;
import android.os.AsyncTask;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;
import com.bizone.britannia.Settings;
import com.bizone.britannia.entities.ProductSkuEntity;
import com.bizone.britannia.entities.PurchaseEntity;
import com.bizone.britannia.entities.PurchaseSubOrderEntity;
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
public abstract class SendStockiestTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG=SendStockiestTask.class.getSimpleName();
    private Context context;
    JSONObject json;
    String errMsg = "Error sending Sales Data";
    int pId;

    public SendStockiestTask(Context context, int pId) {
        this.context = context;
        this.pId = pId;
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
            URL url=new URL(Constants.STOCKIEST_URL);
            Logger.d(TAG,"url="+url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput (true);
            urlConnection.setDoOutput (true);
            urlConnection.setUseCaches (false);
            urlConnection.setRequestMethod( "POST" );
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("X-CSRF-Token", SelectQueries.getSetting(context, Settings.TOKEN));
            urlConnection.setRequestProperty("Cookie", SelectQueries.getSetting(context, Settings.SESS_NAME_ID));
/*
            SaleEntity entity = SelectQueries.getSaleElements(context,saleId);

            if(entity.transaction_id != 0){
                return true; // If the sale is already uploaded, do not upload again
            }*/

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("purchase", getPurchaseJson());
            jsonParam.put("sub_order", getPurchaseSubOrderArray());
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
                   retVal = true;
                    UpdateQueries.updatePurchaseStatus(context,pId,Constants.CLOSED);
                }else {
                    errMsg = json.optString("msg","Error sending Purchase Data");
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
            errMsg = "Error Sending Purchase Order";
            e.printStackTrace();
        }

        return retVal;
    }


    protected abstract void afterExecution(boolean result,String msg);

    @Override
    protected void onPostExecute(Boolean result) {
        Logger.d(TAG,"inside onPostExecute="+result);
        ArrayList<PurchaseSubOrderEntity> entities = SelectQueries.getPurchaseSubOrderElements(context,pId);
        ArrayList<ProductSkuEntity> productSkuEntities = SelectQueries.getProductSkuElements(context, SelectQueries.getSetting(context, Settings.STATE));

        for(int i = 0;i < entities.size();i++){
            int qty = entities.get(i).qty;
            String productName = productSkuEntities.get(i).name;
            Commons.addAvailByProductName(context,productName,qty);
        }
        afterExecution(result, errMsg);

    }


    private JSONObject getPurchaseJson() {
        Logger.d(TAG,"inside getSalesJson");
        PurchaseEntity entity = SelectQueries.getPurchaseElements(context, pId);
        Logger.d(TAG,"PurchaseEntity="+entity);
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("p_id", Commons.getBase64Encoded(entity.p_id+""));
            jObj.put("stockiest_name", Commons.getBase64Encoded(entity.stockiestName));
            jObj.put("stockiest_no", Commons.getBase64Encoded(entity.stockiestNo));
            jObj.put("total", Commons.getBase64Encoded(entity.total));
            jObj.put("device_id", Commons.getBase64Encoded(entity.device_id));
            jObj.put("created", Commons.getBase64Encoded(entity.created+""));
            jObj.put("created_by", Commons.getBase64Encoded(entity.created_by+""));
            jObj.put("updated", Commons.getBase64Encoded(entity.updated+""));
            jObj.put("updated_by", Commons.getBase64Encoded(entity.updated_by+""));
            jObj.put("latitude",Commons.getBase64Encoded( entity.latitude));
            jObj.put("longitude", Commons.getBase64Encoded(entity.longitude));
            jObj.put("accuracy", Commons.getBase64Encoded(entity.accuracy));
            jObj.put("purchase_field1", Commons.getBase64Encoded(entity.purchase_field1));
            jObj.put("purchase_field2",Commons.getBase64Encoded( entity.purchase_field2));
            jObj.put("purchase_field3", Commons.getBase64Encoded(entity.purchase_field3));
            jObj.put("purchase_field4", Commons.getBase64Encoded(entity.purchase_field4));
            jObj.put("purchase_field5", Commons.getBase64Encoded(entity.purchase_field5));


        }catch (JSONException e){
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        return jObj;
    }

    private JSONArray getPurchaseSubOrderArray() {
        Logger.d(TAG,"inside getSubOrderArray");
        ArrayList<PurchaseSubOrderEntity> entities = SelectQueries.getPurchaseSubOrderElements(context,pId);
        JSONArray jArray = null;
        for (PurchaseSubOrderEntity entity:entities){
            Logger.d(TAG,"suborder="+entity);
            JSONObject jObj = new JSONObject();
            if(jArray == null) {
                jArray = new JSONArray();
            }
            try{
                jObj.put("pso_id", Commons.getBase64Encoded(entity.pso_id+""));
                jObj.put("p_id", Commons.getBase64Encoded(entity.p_id+""));
                jObj.put("product_id", Commons.getBase64Encoded(entity.product_id+""));
                jObj.put("qty",Commons.getBase64Encoded( entity.qty+""));
                jObj.put("total", Commons.getBase64Encoded(entity.total));
                jObj.put("so_field1", Commons.getBase64Encoded(entity.so_field1));
                jObj.put("so_field2", Commons.getBase64Encoded(entity.so_field2));
                jObj.put("so_field3", Commons.getBase64Encoded(entity.so_field3));
                jObj.put("so_field4",Commons.getBase64Encoded( entity.so_field4));
                jObj.put("so_field5",Commons.getBase64Encoded( entity.so_field5));
                jArray.put(jObj);
            }catch (JSONException e){
                Logger.e(TAG,e);
                e.printStackTrace();
            }
        }
        return jArray;
    }

}
