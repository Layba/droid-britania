package com.bizone.britannia.internetTask;

import android.content.Context;
import android.os.AsyncTask;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;
import com.bizone.britannia.Settings;
import com.bizone.britannia.entities.StartDayEntity;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.utilities.MultipartUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Sonam on 22/2/17.
 */
public abstract class LoginAuditTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private StartDayEntity startDayEntity;
    private String response;
    private String errCode = "",errMsg = "Error sending Data";
    private static final String TAG=LoginAuditTask.class.getSimpleName();

    public LoginAuditTask(Context context, StartDayEntity startDayEntity) {
        this.context = context;
        this.startDayEntity = startDayEntity;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean retVal=false;

          String charset = "UTF-8";
        String requestURL = Constants.LOGIN_AUDIT_URL;
        String deviceId = SelectQueries.getSetting(context, Settings.DEVICE_ID);

       StartDayEntity entity = SelectQueries.getStartDayElements(context);
            Logger.d(TAG,"Data="+entity.toString());
                try {
                    File uploadFile = new File(entity.imagePath);
                    MultipartUtility multipart = new MultipartUtility(context, requestURL, charset);
                    if(uploadFile.exists()){
                        multipart.addFilePart("image1", uploadFile);
                    } else {
                        multipart.addFormField("image1", Commons.getBase64Encoded("File Not Found"));
                    }

                    multipart.addFormField("device_id", Commons.getBase64Encoded(deviceId));
                    multipart.addFormField("date", Commons.getBase64Encoded(entity.startDate + ""));
                    multipart.addFormField("remarks", Commons.getBase64Encoded(entity.remarks + ""));
                    multipart.addFormField("latitude", Commons.getBase64Encoded(entity.latitude + ""));
                    multipart.addFormField("longitude", Commons.getBase64Encoded(entity.longitude + ""));
                    multipart.addFormField("accuracy", Commons.getBase64Encoded(entity.accuracy + ""));
                    response = multipart.finish();
                    Logger.d(TAG,"response="+response.toString());

                    if (response.contains("Error from server")) {
                        retVal = false;
                    } else {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        String status = jsonObject.getString("status");
                        if ("success".equals(status)) {
                            //update
                            retVal = true;
                           } else {
                            int code = jsonObject.optInt("code",0);
                            if(code != 0){
                                errMsg = Commons.getWSErrors(code);
                                errCode = code+"";
                            }
                            retVal = false;
                        }
                    }

                } catch (IOException ex) {
                    Logger.e(TAG,ex);
                    ex.printStackTrace();
                    retVal = false;

                } catch (JSONException e) {
                    Logger.e(TAG,e);
                    e.printStackTrace();
                    retVal = false;

                }


        return retVal;
    }

    protected abstract void afterExecution();
    protected abstract void notUploaded(String code, String msg);

    @Override
    protected void onPostExecute(Boolean result) {
        Logger.d(TAG,"onpostResult="+response);
        if(result){
            afterExecution();
        }else {
            Logger.d(TAG,"errCode="+errCode+" errMsg="+errMsg);
            notUploaded(errCode,errMsg);
        }
    }

}
