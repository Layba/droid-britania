package com.bizone.britannia;

import android.os.Environment;

import com.bizone.britannia.logreports.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by sagar on 22/7/16.
 */
public class Constants {
    private static final String TAG=Constants.class.getSimpleName();

    public final static String BASE_URL="http://britanniabb.nebuladigital.in/";
   /* public final static String BASE_URL="http://37.187.19.42/";*/
    public final static String LOGIN_URL=BASE_URL+"device/app/login";
    public final static String PRODUCT_SKU_URL = BASE_URL+"app/products";
    public final static String HAAT_SUMMARY_URL = BASE_URL+"app/haatsummary";
    public final static String STOCKIEST_URL = BASE_URL+"app/stockist";
    public final static String APP_TRANSACTION_URL = BASE_URL+"app/transaction";
    public final static String SALES_METADATA_URL = BASE_URL+"sales/metadata";
    public final static String LOGIN_AUDIT_URL = BASE_URL+"sales/loginaudit";
    public final static String ROUTE_PLAN_URL = BASE_URL+"app/routeplan";
    public final static String SHOP_REQUEST_URL = BASE_URL+"app/shops";
    public final static String SETTINGS_URL = BASE_URL+"app/settings";
    public final static String LOGIN_HISTORY_URL = BASE_URL+"my-login-activity";
    public static final String OPEN = "OPEN";
    public static final String INPROGRESS = "INPROGRESS";
    public static final String CLOSED = "CLOSED";
    public final static String RETILER_SALE_LIST_URL=BASE_URL+"vendor-total-sale";
    public final static String HAAT_SALE_LIST_URL=BASE_URL+"haat-summary";
    public final static String PURCHASE_LIST_URL=BASE_URL+"field-user-purchase-list";
    public static final String fldr = Environment.getExternalStorageDirectory()+"/.BRITANNIABB/";
    public static final String logFileName = fldr+".logfile.txt";
    public static final long SIXHRSINMILLIS = 21600000;

    static {
        File dir = new File(fldr);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File logFile = new File(logFileName);
        if(!logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Logger.e(TAG,e);
                e.printStackTrace();
            }
        }
    }


    public static final int IMG_WIDTH=640;
    public static final int IMG_HEIGHT=480;

}
