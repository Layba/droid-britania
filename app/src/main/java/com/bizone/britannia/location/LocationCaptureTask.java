package com.bizone.britannia.location;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Calendar;
import java.util.List;

/**
 * Created by sagar on 7/3/16.
 */
public abstract class LocationCaptureTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Location newLocation, bestLocation;
    private ProgressDialog mProgressDialog;
    private LocationClient locationClient;
    private static final int GPS_WAIT_TIME = 30 * 1000 * 4;
    private static final int THREAD_SLEEP_TIME = 3 * 1000;// thread sleep time reduced from 8 to 3 sec
    private static final int MIN_ACCURACY = 60;
    private static final int MAX_TIME_DIFF = 30 * 60 * 1000; // 30 min
    private String lat = "No Location", lon = "No Location", accu = "N.A";

    private static final String TAG = "LocationCaptureTask";

    public LocationCaptureTask(Context context) {
        super();
        this.context = context;
        locationClient = new LocationClient(context) {

        @Override
        public void onLocationChanged(Location location) {
                newLocation = location;
            }

        };
    }

    @Override
    protected void onPreExecute() {
        locationClient.start();
        try {
            showProgressDialog();
        }catch (Exception e) {
            e.printStackTrace();
        }
        super.onPreExecute();
    }

    private void showProgressDialog(){
        String message;

        message = "Capturing location...";

        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    // Act as if all keys are processed
                    return true;
                }
            });
        }
        mProgressDialog.show();
    }

    private Location getLocationMethod1() {
        // Get the location manager
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Service.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = null;
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           location = locationManager.getLastKnownLocation(bestProvider);
        }
        return location;
    }

    private Location getLocationMethod2() {
        LocationManager lm = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(false);

        Location l = null;
        for (int i = providers.size() - 1; i >= 0; i--) {
            if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                l = lm.getLastKnownLocation(providers.get(i));
            }
            if (l != null)
                break;
        }
        return l;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            Log.d(TAG, "doInBackground");

            //int maxCount = GPS_WAIT_TIME / THREAD_SLEEP_TIME;
            int maxCount = 10;// maxcount changed from 15 to 10
            int count = 0;
            // boolean islocation = newLocation == null ? true :
            // newLocation.getAccuracy() > MIN_ACCURACY;
            // while (count<maxCount && islocation) {

            bestLocation = getLocationMethod1();

            if (bestLocation == null) {
                bestLocation = getLocationMethod2();
            }

            while (count <= maxCount) {
                count++;
                try {
                    Thread.sleep(THREAD_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (isLocationBetter(newLocation, bestLocation)) {
                    bestLocation = newLocation;
                    if (bestLocation != null && bestLocation.getAccuracy() < MIN_ACCURACY) {
                        break;
                    }
                }
            }


            if (bestLocation != null) {
                lat = bestLocation.getLatitude() + "";
                lon = bestLocation.getLongitude() + "";
                accu = bestLocation.getAccuracy() + "";
            }

           // InsertQueries.insertLocation(context,json,pbBrId);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    private boolean isLocationBetter(Location newLocation, Location currentLocation) {
        boolean retval = false;
        if (currentLocation == null) {
            if (newLocation == null) {
                retval = false;
            } else {
                retval = true;
            }
        } else if (newLocation == null) {
            retval = false;
        } else {
            boolean isCurrentLocWithinTime = Calendar.getInstance()
                    .getTimeInMillis() - currentLocation.getTime() <= MAX_TIME_DIFF ? true
                    : false;
            boolean isNewLocWithinTime = Calendar.getInstance()
                    .getTimeInMillis() - newLocation.getTime() <= MAX_TIME_DIFF ? true
                    : false;
            // Commons.writeLog("deltaTimeCurrent="+deltaTimeCurrent+"; deltaTimeNew="+deltaTimeNew);

            boolean isNewerLocation = newLocation.getTime()
                    - currentLocation.getTime() >= 0 ? true : false;
            boolean isMoreAccurate = newLocation.getAccuracy()
                    - currentLocation.getAccuracy() <= 0 ? true : false;
            // Commons.writeLog("deltaAccuracy="+deltaAccuracy);
            if (isMoreAccurate) {
                if (isNewerLocation) {
                    retval = true;
                } else {
                    if (isNewLocWithinTime) {
                        retval = true;
                    } else if (isCurrentLocWithinTime) {
                        retval = false;
                    } else {
                        retval = false;
                    }
                }
            } else {
                if (!isNewerLocation) {
                    retval = false;
                } else {
                    if (isCurrentLocWithinTime) {
                        retval = false;
                    } else if (isNewLocWithinTime) {
                        retval = true;
                    } else {
                        retval = true;
                    }
                }
            }
        }
        return retval;
    }

    public void stopDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
        }
    }

    protected abstract void afterExecution(String Lat,String Lon, String accu);

    @Override
    protected void onPostExecute(Void result) {
        locationClient.stop();
        if(((Activity)context).isFinishing()){
            return;
        }
        stopDialog();
        super.onPostExecute(result);
        afterExecution(lat,lon, accu);
    }

}
