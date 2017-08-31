package com.bizone.britannia.internetTask;

import android.content.Context;
import android.os.AsyncTask;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;
import com.bizone.britannia.Settings;
import com.bizone.britannia.entities.ShopEntity;
import com.bizone.britannia.logreports.Logger;
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
 * Created by siddhesh on 8/9/16.
 */
public abstract class GetShopRequestTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG=GetShopRequestTask.class.getSimpleName();
    private Context context;
    JSONObject json;
    String errMsg = "Error fetching previous Shops";

    public GetShopRequestTask(Context context) {
        Logger.d(TAG,"inside GetShopRequestTask");
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
            URL url=new URL(Constants.SHOP_REQUEST_URL);
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
                    errMsg = json.optString("msg","Error fetching previous Shops");
                    Logger.d(TAG,"error ="+errMsg);
                }
            }else if(statusCode == 403){
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
            errMsg = "Error fetching previous Shops";
            e.printStackTrace();
        }

        return retVal;
    }

    private boolean addToDb() {
        Logger.d(TAG,"inside addToDb");
        boolean retVal=false;
        try {
            JSONArray shopArray=json.getJSONArray("shops");
            if(shopArray.length()==0){
                retVal=true;
            }
            for(int i=0;i<shopArray.length();i++){
                JSONObject shopJson=shopArray.getJSONObject(i);
                ShopEntity entity = new ShopEntity();
                entity.vid = shopJson.getInt("vid");
                entity.shop_name = shopJson.getString("shop_name");
                entity.owner_name = shopJson.getString("owner_name");
                entity.mobile_no = shopJson.getString("mobile_no");
                entity.alt_no = shopJson.getString("alt_no");
                entity.verification_status = shopJson.getString("verification_status");
                entity.alt_verification_status = shopJson.getString("alt_verification_status");
                entity.base_village_id = shopJson.getInt("base_village_id");
                entity.village = shopJson.getString("village");
                entity.sale_id = shopJson.getInt("sale_id");
                entity.created = shopJson.getInt("created");
                entity.created_by = shopJson.getInt("created_by");
                entity.updated = shopJson.getInt("updated");
                entity.updated_by = shopJson.getInt("updated_by");
                entity.shop_field1 = shopJson.optString("shop_field1","retailer");
                InsertQueries.insertShop(context, entity);
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
