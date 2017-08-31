package com.bizone.britannia;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bizone.britannia.logreports.Logger;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sagar on 26/7/16.
 */
public class ContextCommons {

    private static final String TAG = ContextCommons.class.getSimpleName();

    public static void setAlarm(Context context) {

        Logger.d(TAG, "inside set Alarm");
        try {
            Intent intent = new Intent();
            Logger.d(TAG,"Setting alarm");
            intent.setClass(context, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 123456, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            // int min = 0;
            // int hr = currentHour;
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.DATE, 1);
            calendar.add(Calendar.HOUR_OF_DAY, 7);

            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
            Logger.d(TAG, "next alarmtime=" + format.format(new Date(calendar.getTimeInMillis())));

            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, sender);

        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
    }

    public static boolean isOnline(Context context) {
        Logger.d(TAG,"inside isOnline");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    public static JSONObject getNetworkDetails(Context context) {

        JSONObject json = new JSONObject();
        try {
            Log.d(TAG, "inside network details");
            json.put("dateTime", System.currentTimeMillis());
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = telephonyManager.getNetworkOperator();
            json.put("serviceProviderName", networkOperator);

            String imei = "123456789012345";
            try {
                imei = telephonyManager.getDeviceId();
            } catch (Exception e) {
                Logger.e(TAG, e);
                e.printStackTrace();
            }
            json.put("imei", imei);
            json.put("deviceModel", Commons.getDeviceName());

            String androidOS = Build.VERSION.RELEASE;
            json.put("androidVersion", androidOS);

            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            json.put("appVersion", version);

            try {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();
                String ssid = info.getSSID();
                json.put("ssid", ssid);
            } catch (Exception e) {
                Logger.e(TAG, e);
                e.printStackTrace();
            }

            long rwBytes = TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes();
            json.put("wifiReceived", rwBytes);
            long twBytes = TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes();
            json.put("wifiSend", twBytes);
            long rxBytes = TrafficStats.getMobileRxBytes();
            json.put("mobileDataReceived", rxBytes);
            long txBytes = TrafficStats.getMobileTxBytes();
            json.put("mobileDataSend", txBytes);

            long bootTime = (System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime()) / 1000L;
            json.put("bootTime", bootTime);
            Logger.d(TAG, "bootTime added");

            boolean mobileDataEnabled = false; // Assume disabled
            try {
                Logger.d(TAG, "connectivitymanager on");
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                Class cmClass = Class.forName(cm.getClass().getName());
                Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                method.setAccessible(true); // Make the method callable
                // get the setting for "mobile data"
                mobileDataEnabled = (Boolean) method.invoke(cm);
            } catch (Exception e) {
                Logger.e(TAG, e);
                e.printStackTrace();
                // Some problem accessible private API
            }

            if (mobileDataEnabled) {
                Logger.d(TAG, "inside if mobile data enabled");
                json.put("mobileDataEnabled", 1);
            } else {
                json.put("mobileDataEnabled", 0);
            }
        } catch (Exception e) {
            Logger.e(TAG, e);
            e.printStackTrace();
        }

        return json;

    }


}
