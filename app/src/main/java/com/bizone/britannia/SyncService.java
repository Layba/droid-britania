package com.bizone.britannia;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.internetTask.GetRoutePlanTask;
import com.bizone.britannia.internetTask.GetSettingTask;
import com.bizone.britannia.internetTask.SendSalesDataTask;
import com.bizone.britannia.internetTask.SendSalesMetadataTask;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.queries.UpdateQueries;

import java.util.ArrayList;

/**
 * Created by sagar on 26/7/16.
 */
public class SyncService extends Service {

    private static final String TAG = SyncService.class.getSimpleName();
    private int metaData = 0,count = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Logger.removeLines(TAG,"removing lines from async");
            }
        });

        Logger.d(TAG, "inside start command");

        if(ContextCommons.isOnline(this)) {
            new GetSettingTask(this) {

                @Override
                protected void afterExecution(boolean result, String msg) {
                    if (result) {
                        Logger.d(TAG, "inside Route Plan Task");
                        startRoutePlan();
                    } else {
                        stopSelf();
                    }
                }
            }.execute();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startRoutePlan() {

        int vid = SelectQueries.getRoutePlanVid(this);
        new GetRoutePlanTask(this,vid){
            @Override
            protected void afterExecution(boolean result, String msg) {
                if(result) {
                    Logger.d(TAG, "inside Brand Task");
                    startIncompleteUpload();
                }else {
                    stopSelf();
                }
            }
        }.execute();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startIncompleteUpload() {

        ArrayList<SaleEntity> saleEntities = SelectQueries.getAllSaleElements(this);
        metaData = saleEntities.size();
        count = 0;
        for(int i=0; i<saleEntities.size(); i++) {
            final int saleId = saleEntities.get(i).sale_id;
            SaleEntity entity = SelectQueries.getSaleElements(this,saleId);
            if (entity.transaction_id == 0) {
                UpdateQueries.updateSaleStatus(this, saleId, Constants.INPROGRESS);
                new SendSalesDataTask(this, saleId) {
                    @Override
                    protected void afterExecution(boolean result, String msg, int sale_Id) {
                        if (result) {
                            Logger.d(TAG, "inside Sale metadata Task");
                            sendMetaImages(sale_Id);
                        } else {
                            stopSelf();
                        }
                    }
                }.execute();
            }else {
                sendMetaImages(saleId);
            }
        }
    }

    private void sendMetaImages(final int saleId) {

        new SendSalesMetadataTask(this,saleId){
            @Override
            protected void afterExecution() {
                count++;
                if(metaData == count) {
                    stopSelf();
                }
                UpdateQueries.updateSaleStatus(SyncService.this, saleId, Constants.CLOSED);
                SaleEntity saleEntity = SelectQueries.getSaleElements(SyncService.this, saleId);
                UpdateQueries.updateShopDate(SyncService.this, saleEntity.created, saleEntity.sale_id);
            }

            @Override
            protected void notUploaded(String code, String msg) {
                stopSelf();
            }
        }.execute();
    }

}
