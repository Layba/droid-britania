package com.bizone.britannia.internetTask;

import android.content.Context;
import android.os.AsyncTask;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;
import com.bizone.britannia.Settings;
import com.bizone.britannia.entities.SaleMetadataEntity;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.queries.UpdateQueries;
import com.bizone.britannia.utilities.MultipartUtility;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sagar on 29/7/16.
 */
public abstract class SendSalesMetadataTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private int saleId;
    private String response;
    private String errCode = "",errMsg = "Error sending SalesMetaData";
    private static final String TAG=SendSalesMetadataTask.class.getSimpleName();

    public SendSalesMetadataTask(Context context, int saleId) {
        this.context = context;
        this.saleId = saleId;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean retVal=false;

        ArrayList<SaleMetadataEntity> entities= SelectQueries.getSaleMetadataElements(context,saleId);
        String charset = "UTF-8";
        String requestURL = Constants.SALES_METADATA_URL;
        String deviceId = SelectQueries.getSetting(context, Settings.DEVICE_ID);
        Logger.d(TAG, "deviceid="+deviceId + " requesturl=" + requestURL+" saleid="+saleId);

        for(SaleMetadataEntity entity:entities) {

            Logger.d(TAG,"Sale meta data="+entity.toString());
                if(entity != null && !(Constants.CLOSED).equals(entity.status)) {
                    try {
                        File uploadFile = new File(entity.filePath);
                        MultipartUtility multipart = new MultipartUtility(context, requestURL, charset);
                        if(uploadFile.exists()){
                            multipart.addFilePart("image", uploadFile);
                        } else {
                            multipart.addFormField("image",Commons.getBase64Encoded("File Not Found"));
                        }

                        multipart.addFormField("image_capture_time", Commons.getBase64Encoded(entity.created + ""));
                        multipart.addFormField("transaction_id", Commons.getBase64Encoded(entity.transaction_id+""));
                        multipart.addFormField("sale_id", Commons.getBase64Encoded(entity.sale_id+""));
                        multipart.addFormField("device_id", Commons.getBase64Encoded(deviceId));
                        multipart.addFormField("tag", Commons.getBase64Encoded(entity.tag+""));

                        response = multipart.finish();
                        Logger.d(TAG,"response="+response.toString());

                        if (response.contains("Error from server")) {
                            retVal = false;
                            break;
                        } else {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String status = jsonObject.getString("status");
                            if ("success".equals(status)) {
                                //update
                                retVal = true;
                                UpdateQueries.updateMetadataStatus(context, saleId, Constants.CLOSED);
                            } else {
                                int code = jsonObject.optInt("code",0);
                                if(code != 0){
                                    errMsg = Commons.getWSErrors(code);
                                    errCode = code+"";
                                }
                                retVal = false;
                                break;
                            }
                        }

                    } catch (IOException ex) {
                        Logger.e(TAG,ex);
                        ex.printStackTrace();
                        retVal = false;
                        break;
                    } catch (JSONException e) {
                        Logger.e(TAG,e);
                        e.printStackTrace();
                        retVal = false;
                        break;
                    }
                }else{
                    retVal = true;
                }
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
