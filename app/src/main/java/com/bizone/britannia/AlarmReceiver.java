package com.bizone.britannia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bizone.britannia.logreports.Logger;

/**
 * Created by sagar on 26/7/16.
 */
public class AlarmReceiver extends BroadcastReceiver{

    private static String TAG = AlarmReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context ctx, Intent intent) {
        Logger.d(TAG, "inside onReceive");
        ctx.startService(new Intent(ctx,SyncService.class));
    }

}
