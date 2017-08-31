package com.bizone.britannia.internetTask;

import android.content.Context;
import android.os.AsyncTask;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.Settings;
import com.bizone.britannia.entities.ProductSkuEntity;
import com.bizone.britannia.queries.DeleteQueries;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sagar on 28/7/16.
 */
public abstract class GetProductSkuTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG=GetProductSkuTask.class.getSimpleName();
    private Context context;
    JSONObject json;
    String errMsg = "Error fetching Product Sku";

    public GetProductSkuTask(Context context) {
        Logger.d(TAG,"inside GetProductSkuTask");
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        Logger.d(TAG,"inside onPreExecute");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Logger.d(TAG,"inside doInBackground");

        boolean retVal=false;

        try {
            URL url=new URL(Constants.PRODUCT_SKU_URL);
            Logger.d(TAG,"url="+url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput (true);
            urlConnection.setDoOutput (true);
            urlConnection.setUseCaches (false);
            urlConnection.setRequestMethod( "POST" );
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("X-CSRF-Token", SelectQueries.getSetting(context, Settings.TOKEN));
            urlConnection.setRequestProperty("Cookie", SelectQueries.getSetting(context, Settings.SESS_NAME_ID));
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
                    errMsg = json.optString("msg","Error fetching Product Sku");
                    Logger.d(TAG,"error ="+errMsg);
                }
            }else if(statusCode==403) {
                errMsg = "403";
                retVal = false;
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
        try {
            JSONArray branchArray=json.getJSONArray("products");
            if(branchArray.length()==0){
                retVal=true;
            }
            DeleteQueries.deleteProductSkuRecord(context);
            for(int i=0;i<branchArray.length();i++){
                JSONObject productJson=branchArray.getJSONObject(i);
                ProductSkuEntity entity = new ProductSkuEntity();
                entity.product_id = productJson.getInt("product_id");
                entity.name = productJson.getString("name");
                entity.sku = productJson.getString("sku");
                entity.mrp = productJson.getString("mrp");
                entity.selling_price = productJson.getString("selling_price");
                entity.state = productJson.getInt("state");
                entity.status = productJson.getString("status");
                InsertQueries.insertProductSku(context,entity);
                retVal=true;
            }
        }catch (JSONException e){
            Logger.e(TAG,e);
            e.printStackTrace();
        }

        return retVal;
    }

    protected abstract void afterExecution(boolean result,String msg);

    @Override
    protected void onPostExecute(Boolean result) {
        Logger.d(TAG,"inside onPostExecute="+result);
        afterExecution(result, errMsg);

    }
}
