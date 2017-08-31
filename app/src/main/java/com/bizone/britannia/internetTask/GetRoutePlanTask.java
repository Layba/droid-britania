package com.bizone.britannia.internetTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.Settings;
import com.bizone.britannia.entities.RoutePlanEntity;
import com.bizone.britannia.queries.InsertQueries;
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

/**
 * Created by sagar on 29/7/16.
 */
public abstract class GetRoutePlanTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG=GetRoutePlanTask.class.getSimpleName();
    private Context context;
    private JSONObject json;
    private String errMsg = "Error fetching Route Plan";
    private int vid;

    public GetRoutePlanTask(Context context,int vid) {
        this.context = context;
        this.vid = vid;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Logger.d(TAG,"inside doInBackground: GetRoutePlanTask");
        boolean retVal=false;

        try {
            URL url=new URL(Constants.ROUTE_PLAN_URL);
            Logger.d(TAG,"url="+url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput (true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("X-CSRF-Token", SelectQueries.getSetting(context, Settings.TOKEN));
            urlConnection.setRequestProperty("Cookie", SelectQueries.getSetting(context, Settings.SESS_NAME_ID));

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("vid", Base64.encodeToString((vid+"").getBytes("UTF-8"), Base64.DEFAULT));

            DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream ());
            printout.writeBytes(jsonParam.toString());
            printout.flush();
            printout.close();

            int statusCode = urlConnection.getResponseCode();
            Logger.d(TAG,"inside doInBackground: GetRoutePlanTask: statusCode="+statusCode);

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
                    errMsg = json.optString("msg","Error fetching Route Plan");
                }
            }else if (statusCode == 403){
                errMsg ="403";
                retVal=false;
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
            errMsg = "Error fetching Route Plan";
            e.printStackTrace();
        }
        Logger.d(TAG,"inside doInBackground: GetRoutePlanTask: errMsg="+errMsg+" : retval="+retVal);

        return retVal;
    }

    private boolean addToDb() {
        boolean retVal=false;
        try {
            JSONArray branchArray=json.getJSONArray("route_plan");
            if(branchArray.length()==0){
                retVal=true;
            }
            for(int i=0;i<branchArray.length();i++){
                JSONObject routeJson=branchArray.getJSONObject(i);
                RoutePlanEntity entity = new RoutePlanEntity();
                entity.r_id = routeJson.getInt("r_id");
                entity.vid = routeJson.getInt("vid");
                entity.uid = routeJson.getInt("uid");
                entity.date = routeJson.getString("date");
                entity.villageid_1 = routeJson.optInt("villageid_1", -1);
                entity.villageid_2 = routeJson.optInt("villageid_2",-1);
                entity.villageid_3 = routeJson.optInt("villageid_3",-1);
                entity.villageid_4 = routeJson.optInt("villageid_4",-1);
                entity.villageid_5 = routeJson.optInt("villageid_5",-1);
                entity.villagename_1 = routeJson.optString("villagename_1", null);
                entity.villagename_2 = routeJson.optString("villagename_2",null);
                entity.villagename_3 = routeJson.optString("villagename_3",null);
                entity.villagename_4 = routeJson.optString("villagename_4",null);
                entity.villagename_5 = routeJson.optString("villagename_5",null);
                entity.created = routeJson.getInt("created");
                entity.created_by = routeJson.getInt("created_by");
                entity.rp_field1 = routeJson.getString("rp_field1");
                entity.rp_field2 = routeJson.getString("rp_field2");
                entity.rp_field3 = routeJson.getString("rp_field3");
                entity.rp_field4 = routeJson.getString("rp_field4");
                entity.rp_field5 = routeJson.getString("rp_field5");
                entity.rp_field6 = routeJson.getString("rp_field6");
                entity.rp_field7 = routeJson.getString("rp_field7");
                entity.rp_field8 = routeJson.getString("rp_field8");
                entity.rp_field9 = routeJson.getString("rp_field9");
                entity.rp_field10 = routeJson.getString("rp_field10");
                int id = UpdateQueries.updateRoutePlanRecord(context,entity,true);
                if(id==0) {
                    id = UpdateQueries.updateRoutePlanRecord(context, entity, false);
                }
                if(id==0) {
                    InsertQueries.insertRoutePlan(context, entity);
                }
                retVal=true;
            }
        }catch (JSONException e){

            Logger.e(TAG,e);
            e.printStackTrace();
            Logger.d(TAG,"error= "+e);
        }

        return retVal;
    }

    protected abstract void afterExecution(boolean result,String msg);

    @Override
    protected void onPostExecute(Boolean result) {
        Logger.d(TAG,"onPostExecute="+result);
        afterExecution(result, errMsg);
    }
}