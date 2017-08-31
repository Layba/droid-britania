package com.bizone.britannia.internetTask;

import android.content.Context;
import android.os.AsyncTask;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;
import com.bizone.britannia.Settings;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by siddhesh on 8/9/16.
 */
public abstract class GetSettingTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG=GetSettingTask.class.getSimpleName();
    private Context context;
    JSONObject json;
    String errMsg = "Error fetching Settings";

    public GetSettingTask(Context context) {
        Logger.d(TAG,"inside GetSettingTask");
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
            URL url=new URL(Constants.SETTINGS_URL);
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
                    errMsg = json.optString("msg","Error fetching Settings");
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
            errMsg = "Error fetching Settings";
            e.printStackTrace();
        }

        return retVal;
    }

    private boolean addToDb() {
        Logger.d(TAG,"inside addToDb");
        boolean retVal=true;
        String pastDays = json.optString("edit_past_days","0");
        String futureDays = json.optString("edit_future_days","0");
        InsertQueries.setSetting(context,Settings.PAST_DAYS,pastDays);
        InsertQueries.setSetting(context,Settings.FUTURE_DAYS,futureDays);
        return retVal;
    }

    protected abstract void afterExecution(boolean result,String msg);

    @Override
    protected void onPostExecute(Boolean result) {
        Logger.d(TAG,"inside onPostExecute="+result);
        afterExecution(result, errMsg);

    }
}

