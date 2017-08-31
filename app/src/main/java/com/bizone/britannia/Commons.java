package com.bizone.britannia;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.utilities.ScalingUtilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by siddhesh on 7/25/16.
 */
public class Commons {
    private static final String TAG = Commons.class.getSimpleName();

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static String getBase64Encoded(String key) {
        String retVal = "";
        if ("".equals(key) || null == key) {
            return "";
        }
        try {
            retVal = Base64.encodeToString(key.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static final String getWSErrors(int htmlErrCode) {
        String retval = "Error";

        switch (htmlErrCode) {
            case 200:
                retval = "Success!";
                break;
            case 408:
                retval = "Connection timed out";
                break;
            case 413:
                retval = "Data size too large";
                break;
            case 403:
                retval = "Session expired. Please login again.";
                break;
            case 500:
                retval = "Server error. Kindly contact admin";
                break;
            case 404:
                retval = "Endpoint not available. Kindly contact admin";
                break;
            case 501:
                retval = "Not available. Kindly contact admin";
                break;
            case 204:
                retval = "No content. Kindly ccontact admin if error persists.";
                break;
            case 407:
                retval = "Proxy authentication failed";
                break;
            case 414:
                retval = "Request too long. Kindly contact admin";
                break;
            case 401:
                retval = "Unauthorised access. Please relogin.";
                break;
            case 406:
                retval = "Unauthorised access. Please relogin.";
                break;
            case 503:
                retval = "Service unavailable. Please try after a while";
                break;
        }

        Logger.d(TAG, retval);
        return retval;
    }

    public static final File getOutputMediaFile() {
        Logger.d(TAG, "inside getOutputMediaFile");
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Constants.fldr);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + ".BRITANNIABB" + timeStamp + ".jpg");
        Logger.d(TAG, mediaFile.getAbsolutePath());
        return mediaFile;

    }


    public static void copyTextFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Logger.e(TAG, fnfe1);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public static Date milliToDateObj(String milliString) {

        Long milli = Long.parseLong(milliString);
        milli = milli * 1000L;
        Date currentDate = new Date(milli);
        Logger.d(TAG, "current Date: " + currentDate);

        return currentDate;
    }

    public static String milliToDate(String milliString) {

        Long milli = Long.parseLong(milliString);
        milli = milli * 1000L;
        Date currentDate = new Date(milli);
        Logger.d(TAG, "current Date: " + currentDate);

        DateFormat df = new SimpleDateFormat("dd MMM");
        Logger.d(TAG, "Milliseconds to Date: " + df.format(currentDate));

        return df.format(currentDate);
    }

    public static String milliToDateWithYear(Long milli) {

        milli = milli * 1000L;
        Date currentDate = new Date(milli);
        Logger.d(TAG, "current Date: " + currentDate);

        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        Logger.d(TAG, "Milliseconds to Date: " + df.format(currentDate));

        return df.format(currentDate);
    }

    public static final String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            File mFolder = new File(Constants.fldr);
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.jpg";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();

                File origFile = new File(path);
                if (origFile.exists()) {
                    origFile.delete();
                    origFile = null;
                    f.renameTo(new File(path));
                    Log.d(TAG, "image=" + f.getName() + "; size=" + f.length() / 1024 + "KB");
                }
            } catch (FileNotFoundException e) {
                Logger.e(TAG, e);
                e.printStackTrace();
            } catch (Exception e) {
                Logger.e(TAG, e);
                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (strMyImagePath == null) {
            return path;
        }
//	        return strMyImagePath;
        return path;
    }

    public static final Calendar normalizeCalendarTime(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.AM_PM, 0);
        return calendar;
    }

    public static int getAvailByProductName(Context context, String productName) {
        int retVal = -1;
        String productQty = SelectQueries.getSetting(context, Settings.PRODUCT_AVAIL_QUANTITY);
        if (!"".equals(productQty)) {
            ArrayList<String> myList = new ArrayList<String>(Arrays.asList(productQty.split(",")));
            for (String singleProduct : myList) {
                String[] prodKeyVal = singleProduct.split("=");
                if (prodKeyVal[0].equals(productName)) {
                    retVal = Integer.parseInt(prodKeyVal[1]);
                    break;
                }
            }
            if (retVal == -1) {
                productQty = productQty + "," + productName + "=100";
                retVal = 100;
                InsertQueries.setSetting(context, Settings.PRODUCT_AVAIL_QUANTITY, productQty);
            }
        } else {
            productQty = productName + "=100";
            retVal = 100;
            InsertQueries.setSetting(context, Settings.PRODUCT_AVAIL_QUANTITY, productQty);
        }
        return retVal;
    }

    public static int substractAvailByProductName(Context context, String productName, int quantity) {
        int retVal = -1;
        String productQty = SelectQueries.getSetting(context, Settings.PRODUCT_AVAIL_QUANTITY);
        if (!"".equals(productQty)) {
            ArrayList<String> myList = new ArrayList<String>(Arrays.asList(productQty.split(",")));
            for (String singleProduct : myList) {
                String[] prodKeyVal = singleProduct.split("=");
                if (prodKeyVal[0].equals(productName)) {
                    retVal = Integer.parseInt(prodKeyVal[1]);
                    retVal = retVal - quantity;
                    myList.remove(singleProduct);
                    String joined = TextUtils.join(",", myList);
                    joined = joined + "," + productName + "=" + retVal;
                    InsertQueries.setSetting(context, Settings.PRODUCT_AVAIL_QUANTITY, joined);
                    break;
                }
            }
        }
        return retVal;
    }

    public static int addAvailByProductName(Context context, String productName, int quantity) {
        int retVal = -1;
        String productQty = SelectQueries.getSetting(context, Settings.PRODUCT_AVAIL_QUANTITY);
        if (!"".equals(productQty)) {
            ArrayList<String> myList = new ArrayList<String>(Arrays.asList(productQty.split(",")));
            for (String singleProduct : myList) {
                String[] prodKeyVal = singleProduct.split("=");
                if (prodKeyVal[0].equals(productName)) {
                    retVal = Integer.parseInt(prodKeyVal[1]);
                    retVal = retVal + quantity;
                    myList.remove(singleProduct);
                    String joined = TextUtils.join(",", myList);
                    joined = joined + "," + productName + "=" + retVal;
                    InsertQueries.setSetting(context, Settings.PRODUCT_AVAIL_QUANTITY, joined);
                    break;
                }
            }
        }
        return retVal;
    }

    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    public static void removeLines(String f, String TAG, String msg) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(f, "rw");

        // Shift remaining lines upwards.
        long writePos = raf.getFilePointer();
        for (int i = 0; i < 25000; i++)
            raf.readLine();
        long readPos = raf.getFilePointer();// here is 202

        byte[] buf = new byte[1024];
        int n;
        while (-1 != (n = raf.read(buf))) {
            raf.seek(writePos);
            raf.write(buf, 0, n);
            readPos += n;
            writePos += n;
            raf.seek(readPos);
        }

        raf.setLength(writePos);
        raf.close();

        Logger.writeLog(TAG,msg);
    }

    public static String millisecToDate(String time){
        String time1 = time+"000";
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        long milliSeconds= Long.parseLong(time1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String millisecToTime(String time){
        String time1 = time+"000";
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        long milliSeconds= Long.parseLong(time1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String millitoDate(String millisec){
        long millis = (long) Long.parseLong(millisec);
       String date= String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                return date;
    }


    public static String millisecToDateAndTime(String time){
     //   String timeInMilli = time +"000";
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        long milliSeconds= Long.parseLong(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
