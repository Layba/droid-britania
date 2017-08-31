package com.bizone.britannia.queries;

import android.content.ContentValues;
import android.content.Context;

import com.bizone.britannia.Constants;
import com.bizone.britannia.Settings;
import com.bizone.britannia.db.DBAdapter;
import com.bizone.britannia.entities.DeviceEntity;
import com.bizone.britannia.entities.ProductSkuEntity;
import com.bizone.britannia.entities.PurchaseEntity;
import com.bizone.britannia.entities.PurchaseSubOrderEntity;
import com.bizone.britannia.entities.RoutePlanEntity;
import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.SaleMetadataEntity;
import com.bizone.britannia.entities.ShopEntity;
import com.bizone.britannia.entities.StartDayEntity;
import com.bizone.britannia.entities.SubOrderEntity;
import com.bizone.britannia.entities.VillageEntity;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.tables.DEVICE_TBL;
import com.bizone.britannia.tables.PRODUCT_SKU_TBL;
import com.bizone.britannia.tables.PURCHASE_SUB_ORDER_TBL;
import com.bizone.britannia.tables.PURCHASE_TBL;
import com.bizone.britannia.tables.ROUTE_PLAN_TBL;
import com.bizone.britannia.tables.SALE_METADATA_TBL;
import com.bizone.britannia.tables.SALE_TBL;
import com.bizone.britannia.tables.SHOP_TBL;
import com.bizone.britannia.tables.STARTDAY_TBL;
import com.bizone.britannia.tables.SUB_ORDER_TBL;
import com.bizone.britannia.tables.VILLAGE_SUMMARY_TBL;
import com.bizone.britannia.tables.VILLAGE_TBL;

/**
 * Created by siddhesh on 7/22/16.
 */
public class InsertQueries {

    private InsertQueries(){
        super();
    }

    private static String TAG = InsertQueries.class.getSimpleName();
    public synchronized static long setSetting(Context context, Settings name, String value){
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal = -1;
        //adapter.close();

        SelectQueries.getSetting(context, name);
        retVal = UpdateQueries.updateSetting(context, name, value);
        Logger.d(TAG, "retval=" + retVal);
        return retVal;
    }

    public static synchronized long insertProductSku(Context context, ProductSkuEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(PRODUCT_SKU_TBL.PRODUCT_ID, entity.product_id);
            initialValues.put(PRODUCT_SKU_TBL.NAME, entity.name);
            initialValues.put(PRODUCT_SKU_TBL.SKU, entity.sku);
            initialValues.put(PRODUCT_SKU_TBL.MRP, entity.mrp);
            initialValues.put(PRODUCT_SKU_TBL.SELLING_PRICE, entity.selling_price);
            initialValues.put(PRODUCT_SKU_TBL.STATE, entity.state);
            initialValues.put(PRODUCT_SKU_TBL.STATUS, entity.status);
            retVal = adapter.getDB().insert(PRODUCT_SKU_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }




    public static synchronized long insertShop(Context context, ShopEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(SHOP_TBL.SHOP_NAME, entity.shop_name);
            initialValues.put(SHOP_TBL.OWNER_NAME, entity.owner_name);
            initialValues.put(SHOP_TBL.MOBILE_NO, entity.mobile_no);
            initialValues.put(SHOP_TBL.VID, entity.vid);
            initialValues.put(SHOP_TBL.ALT_NO, entity.alt_no);
            initialValues.put(SHOP_TBL.VERIFICATION_STATUS, entity.verification_status);
            initialValues.put(SHOP_TBL.ALT_VERIFICATION_STATUS, entity.alt_verification_status);
            initialValues.put(SHOP_TBL.BASE_VILLAGE_ID, entity.base_village_id);
            initialValues.put(SHOP_TBL.VILLAGE, entity.village);
            initialValues.put(SHOP_TBL.SALE_ID, entity.sale_id);
            initialValues.put(SHOP_TBL.CREATED, entity.created);
            initialValues.put(SHOP_TBL.CREATED_BY, entity.created_by);
            initialValues.put(SHOP_TBL.UPDATED, entity.updated);
            initialValues.put(SHOP_TBL.UPDATED_BY, entity.updated_by);
            initialValues.put(SHOP_TBL.SHOP_FIELD1, entity.shop_field1);
            initialValues.put(SHOP_TBL.SHOP_FIELD2, entity.shop_field2);
            initialValues.put(SHOP_TBL.SHOP_FIELD3, entity.shop_field3);
            initialValues.put(SHOP_TBL.SHOP_FIELD4, entity.shop_field4);
            initialValues.put(SHOP_TBL.SHOP_FIELD5, entity.shop_field5);
            retVal = adapter.getDB().insert(SHOP_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }


    public static synchronized long insertSubOrder(Context context, SubOrderEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(SUB_ORDER_TBL.SALE_ID, entity.sale_id);
            initialValues.put(SUB_ORDER_TBL.PRODUCT_ID, entity.product_id);
            initialValues.put(SUB_ORDER_TBL.QTY, entity.qty);
            initialValues.put(SUB_ORDER_TBL.TOTAL, entity.total);
            initialValues.put(SUB_ORDER_TBL.SO_FIELD1, entity.so_field1);
            initialValues.put(SUB_ORDER_TBL.SO_FIELD2, entity.so_field2);
            initialValues.put(SUB_ORDER_TBL.SO_FIELD3, entity.so_field3);
            initialValues.put(SUB_ORDER_TBL.SO_FIELD4, entity.so_field4);
            initialValues.put(SUB_ORDER_TBL.SO_FIELD5, entity.so_field5);
            retVal = adapter.getDB().insert(SUB_ORDER_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }

    public static synchronized long insertPurchaseSubOrder(Context context, PurchaseSubOrderEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(PURCHASE_SUB_ORDER_TBL.P_ID, entity.p_id);
            initialValues.put(PURCHASE_SUB_ORDER_TBL.PRODUCT_ID, entity.product_id);
            initialValues.put(PURCHASE_SUB_ORDER_TBL.QTY, entity.qty);
            initialValues.put(PURCHASE_SUB_ORDER_TBL.TOTAL, entity.total);
            initialValues.put(PURCHASE_SUB_ORDER_TBL.SO_FIELD1, entity.so_field1);
            initialValues.put(PURCHASE_SUB_ORDER_TBL.SO_FIELD2, entity.so_field2);
            initialValues.put(PURCHASE_SUB_ORDER_TBL.SO_FIELD3, entity.so_field3);
            initialValues.put(PURCHASE_SUB_ORDER_TBL.SO_FIELD4, entity.so_field4);
            initialValues.put(PURCHASE_SUB_ORDER_TBL.SO_FIELD5, entity.so_field5);
            retVal = adapter.getDB().insert(PURCHASE_SUB_ORDER_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }

    public static synchronized long insertDevice(Context context, DeviceEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(DEVICE_TBL.P_ID, entity.p_id);
            initialValues.put(DEVICE_TBL.DEVICE_ID, entity.device_id);
            initialValues.put(DEVICE_TBL.IMEI, entity.imei);
            initialValues.put(DEVICE_TBL.MODEL, entity.model);
            initialValues.put(DEVICE_TBL.UID, entity.uid);
            initialValues.put(DEVICE_TBL.CREATED, entity.created);
            retVal = adapter.getDB().insert(DEVICE_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }



    public static synchronized long insertRoutePlan(Context context, RoutePlanEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(ROUTE_PLAN_TBL.R_ID, entity.r_id);
            initialValues.put(ROUTE_PLAN_TBL.VID, entity.vid);
            initialValues.put(ROUTE_PLAN_TBL.UID, entity.uid);
            initialValues.put(ROUTE_PLAN_TBL.DATE, entity.date);
            initialValues.put(ROUTE_PLAN_TBL.VILLAGEID_1, entity.villageid_1);
            initialValues.put(ROUTE_PLAN_TBL.VILLAGEID_2, entity.villageid_2);
            initialValues.put(ROUTE_PLAN_TBL.VILLAGEID_3, entity.villageid_3);
            initialValues.put(ROUTE_PLAN_TBL.VILLAGEID_4, entity.villageid_4);
            initialValues.put(ROUTE_PLAN_TBL.VILLAGEID_5, entity.villageid_5);
            initialValues.put(ROUTE_PLAN_TBL.VILLAGENAME_1, entity.villagename_1);
            initialValues.put(ROUTE_PLAN_TBL.VILLAGENAME_2, entity.villagename_2);
            initialValues.put(ROUTE_PLAN_TBL.VILLAGENAME_3, entity.villagename_3);
            initialValues.put(ROUTE_PLAN_TBL.VILLAGENAME_4, entity.villagename_4);
            initialValues.put(ROUTE_PLAN_TBL.VILLAGENAME_5, entity.villagename_5);
            initialValues.put(ROUTE_PLAN_TBL.CREATED, entity.created);
            initialValues.put(ROUTE_PLAN_TBL.CREATED_BY, entity.created_by);
            initialValues.put(ROUTE_PLAN_TBL.RP_FIELD1, entity.rp_field1);
            initialValues.put(ROUTE_PLAN_TBL.RP_FIELD2, entity.rp_field2);
            initialValues.put(ROUTE_PLAN_TBL.RP_FIELD3, entity.rp_field3);
            initialValues.put(ROUTE_PLAN_TBL.RP_FIELD4, entity.rp_field4);
            initialValues.put(ROUTE_PLAN_TBL.RP_FIELD5, entity.rp_field5);
            initialValues.put(ROUTE_PLAN_TBL.RP_FIELD6, entity.rp_field6);
            initialValues.put(ROUTE_PLAN_TBL.RP_FIELD7, entity.rp_field7);
            initialValues.put(ROUTE_PLAN_TBL.RP_FIELD8, entity.rp_field8);
            initialValues.put(ROUTE_PLAN_TBL.RP_FIELD9, entity.rp_field9);
            initialValues.put(ROUTE_PLAN_TBL.RP_FIELD10, entity.rp_field10);
            retVal = adapter.getDB().insert(ROUTE_PLAN_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }

    public static synchronized long insertSaleMetadata(Context context, SaleMetadataEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(SALE_METADATA_TBL.TRANSACTION_ID, entity.transaction_id);
            initialValues.put(SALE_METADATA_TBL.SALE_ID, entity.sale_id);
            initialValues.put(SALE_METADATA_TBL.FID, entity.fid);
            initialValues.put(SALE_METADATA_TBL.FILE_PATH, entity.filePath);
            initialValues.put(SALE_METADATA_TBL.CREATED, entity.created);
            initialValues.put(SALE_METADATA_TBL.STATUS, Constants.OPEN);
            initialValues.put(SALE_METADATA_TBL.TAG, entity.tag);

            retVal = adapter.getDB().insert(SALE_METADATA_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }

    public static synchronized long insertStartDayData(Context context, StartDayEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(STARTDAY_TBL.IMAGE_PATH, entity.imagePath);
            initialValues.put(STARTDAY_TBL.START_DATE, entity.startDate);
            initialValues.put(STARTDAY_TBL.REMARKS, entity.remarks);
            initialValues.put(STARTDAY_TBL.LATITUDE, entity.latitude);
            initialValues.put(STARTDAY_TBL.LONGITUDE, entity.longitude);
            initialValues.put(STARTDAY_TBL.ACCURACY, entity.accuracy);
            initialValues.put(STARTDAY_TBL.STATUS, Constants.INPROGRESS);
            retVal = adapter.getDB().insert(STARTDAY_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }


    public static synchronized long insertSale(Context context, SaleEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(SALE_TBL.TRANSACTION_ID, entity.transaction_id);
            initialValues.put(SALE_TBL.SUBTOTAL, entity.subtotal);
            initialValues.put(SALE_TBL.TAX, entity.tax);
            initialValues.put(SALE_TBL.DISCOUNT, entity.discount);
            initialValues.put(SALE_TBL.TOTAL, entity.total);
            initialValues.put(SALE_TBL.DEVICE_ID, entity.device_id);
            initialValues.put(SALE_TBL.CREATED, entity.created);
            initialValues.put(SALE_TBL.CREATED_BY, entity.created_by);
            initialValues.put(SALE_TBL.UPDATED, entity.updated);
            initialValues.put(SALE_TBL.UPDATED_BY, entity.updated_by);
            initialValues.put(SALE_TBL.LONGITUDE, entity.longitude);
            initialValues.put(SALE_TBL.LATITUDE, entity.latitude);
            initialValues.put(SALE_TBL.ACCURACY, entity.accuracy);
            initialValues.put(SALE_TBL.SALE_TYPE, entity.sale_type);
            initialValues.put(SALE_TBL.STATUS, Constants.OPEN);
            initialValues.put(SALE_TBL.SALE_FIELD1, entity.sale_field1);
            initialValues.put(SALE_TBL.SALE_FIELD2, entity.sale_field2);
            initialValues.put(SALE_TBL.SALE_FIELD3, entity.sale_field3);
            initialValues.put(SALE_TBL.SALE_FIELD4, entity.sale_field4);
            initialValues.put(SALE_TBL.SALE_FIELD5, entity.sale_field5);

            retVal = adapter.getDB().insert(SALE_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }

    public static synchronized long insertPurchase(Context context, PurchaseEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(PURCHASE_TBL.STOCKIEST_NAME, entity.stockiestName);
            initialValues.put(PURCHASE_TBL.STOCKIEST_NO, entity.stockiestNo);
            initialValues.put(PURCHASE_TBL.TOTAL, entity.total);
            initialValues.put(PURCHASE_TBL.DEVICE_ID, entity.device_id);
            initialValues.put(PURCHASE_TBL.LONGITUDE, entity.longitude);
            initialValues.put(PURCHASE_TBL.LATITUDE, entity.latitude);
            initialValues.put(PURCHASE_TBL.ACCURACY, entity.accuracy);
            initialValues.put(PURCHASE_TBL.CREATED, entity.created);
            initialValues.put(PURCHASE_TBL.CREATED_BY, entity.created_by);
            initialValues.put(PURCHASE_TBL.UPDATED, entity.updated);
            initialValues.put(PURCHASE_TBL.UPDATED_BY, entity.updated_by);
            initialValues.put(PURCHASE_TBL.STATUS, Constants.OPEN);
            initialValues.put(PURCHASE_TBL.PURCHASE_FIELD1, entity.purchase_field1);
            initialValues.put(PURCHASE_TBL.PURCHASE_FIELD2, entity.purchase_field2);
            initialValues.put(PURCHASE_TBL.PURCHASE_FIELD3, entity.purchase_field3);
            initialValues.put(PURCHASE_TBL.PURCHASE_FIELD4, entity.purchase_field4);
            initialValues.put(PURCHASE_TBL.PURCHASE_FIELD5, entity.purchase_field5);

            retVal = adapter.getDB().insert(PURCHASE_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }


    public static synchronized long insertVillage(Context context, VillageEntity entity){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(VILLAGE_TBL.VILLAGE_ID, entity.village_id);
            initialValues.put(VILLAGE_TBL.VILLAGE, entity.village);
            initialValues.put(VILLAGE_TBL.STATE, entity.state);
            initialValues.put(VILLAGE_TBL.CREATED, entity.created);
            initialValues.put(VILLAGE_TBL.CREATED_BY, entity.created_by);
            retVal = adapter.getDB().insert(VILLAGE_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
       
    }

    public static long insertVillageSummary(Context context, int saleId, int villageId, String villName,int routeId) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal =-1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(VILLAGE_SUMMARY_TBL.VILLAGE_ID, villageId);
            initialValues.put(VILLAGE_SUMMARY_TBL.SALE_ID, saleId);
            initialValues.put(VILLAGE_SUMMARY_TBL.VILLAGE_NAME, villName);
            initialValues.put(VILLAGE_SUMMARY_TBL.ROUTE_ID, routeId);
            retVal = adapter.getDB().insert(VILLAGE_SUMMARY_TBL.TABLE_NAME, null, initialValues);
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }
}
