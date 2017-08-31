package com.bizone.britannia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bizone.britannia.logreports.Logger;

/**
 * Created by sagar on 26/7/16.
 */
public class BootReceiver extends BroadcastReceiver {

    private static String TAG = BootReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "inside onCreate");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Logger.d(TAG,"call setAlarm");
            ContextCommons.setAlarm(context);
        }
    }
}
