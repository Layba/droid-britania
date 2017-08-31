package com.bizone.britannia.queries;

import android.content.ContentValues;
import android.content.Context;

import com.bizone.britannia.Constants;
import com.bizone.britannia.Settings;
import com.bizone.britannia.db.DBAdapter;
import com.bizone.britannia.entities.RoutePlanEntity;
import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.ShopEntity;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.tables.PRODUCT_SKU_TBL;
import com.bizone.britannia.tables.PURCHASE_TBL;
import com.bizone.britannia.tables.ROUTE_PLAN_TBL;
import com.bizone.britannia.tables.SALE_METADATA_TBL;
import com.bizone.britannia.tables.SALE_TBL;
import com.bizone.britannia.tables.SETTINGS_TBL;
import com.bizone.britannia.tables.SHOP_TBL;
import com.bizone.britannia.tables.STARTDAY_TBL;

/**
 * Created by siddhesh on 7/22/16.
 */
public class UpdateQueries {

    private static final String TAG = UpdateQueries.class.getSimpleName();
    private UpdateQueries(){
        super();
    }

    public synchronized static long updateSetting(Context context, Settings name, String value){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(SETTINGS_TBL.STB_STNGS_VAL, value);
        long retVal = adapter.getDB().update(
                SETTINGS_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(SETTINGS_TBL.STB_STNGS_NM).append(" = '").append(name).append("'").toString(), null);

        Logger.d(TAG, "retVal=" + retVal);

        //adapter.close();
        return retVal;
    }

    public synchronized static long updateSaleTransId(Context context, int saleId, int transId){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(SALE_TBL.TRANSACTION_ID, transId);
        long retVal = adapter.getDB().update(
                SALE_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(SALE_TBL.SALE_ID).append(" = ").append(saleId).toString(), null);

        Logger.d(TAG, "retVal=" + retVal);
        return retVal;
    }

    public synchronized static long updateSaleEntity(Context context, SaleEntity entity){
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();

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

        long retVal = adapter.getDB().update(
                SALE_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(SALE_TBL.SALE_ID).append(" = ").append(entity.sale_id).toString(), null);

        Logger.d(TAG, "retVal=" + retVal);
        return retVal;
    }

    public synchronized static long updateSaleMetaTransId(Context context, int saleId, int transId){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(SALE_METADATA_TBL.TRANSACTION_ID, transId);
        long retVal = adapter.getDB().update(
                SALE_METADATA_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(SALE_METADATA_TBL.SALE_ID).append(" = ").append(saleId).toString(), null);

        Logger.d(TAG, "retVal=" + retVal);

        //adapter.close();
        return retVal;
    }


    public static synchronized long changeProductSkuStatus(Context context,long projSkuId,String status) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(PRODUCT_SKU_TBL.STATUS, status);
        long retVal = adapter.getDB().update(
                PRODUCT_SKU_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(PRODUCT_SKU_TBL.PRODUCT_ID).append(" = ").append(projSkuId)
                        .toString(), null);

        Logger.d(TAG, "retVal=" + retVal);
        //adapter.close();

        return retVal;

    }

    public static synchronized long updateMetadataStatus(Context context, int saleId, String status) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(SALE_METADATA_TBL.STATUS, status);
        long retVal = adapter.getDB().update(
                SALE_METADATA_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(SALE_METADATA_TBL.SALE_ID).append(" = ").append(saleId)
                        .toString(), null);
        Logger.d(TAG, "retVal=" + retVal);
        //adapter.close();

        return retVal;
    }

    public static synchronized long updateStartDayStatus(Context context, int startId, String status) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(STARTDAY_TBL.STATUS, status);
        long retVal = adapter.getDB().update(
                STARTDAY_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(STARTDAY_TBL.START_ID).append(" = ").append(startId)
                        .toString(), null);
        Logger.d(TAG, "retVal=" + retVal);
        //adapter.close();

        return retVal;
    }

    public static synchronized long updateShopRecord(Context context, ShopEntity entity) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
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
        long retVal = adapter.getDB().update(
                SHOP_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(SHOP_TBL.SHOP_ID).append(" = ").append(entity.shop_id)
                        .toString(), null);

        Logger.d(TAG, "retVal=" + retVal);
        //adapter.close();

        return retVal;
    }

    public static synchronized long updateShopDate(Context context, int date , int saleId) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(SHOP_TBL.UPDATED, date);

        long retVal = adapter.getDB().update(
                SHOP_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(SHOP_TBL.SALE_ID).append(" = ").append(saleId)
                        .toString(), null);

        Logger.d(TAG, "retVal=" + retVal);

        return retVal;
    }

    public static synchronized long updateSaleStatus(Context context, int saleId, String status) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(SALE_TBL.STATUS, status);
        long retVal = adapter.getDB().update(
                SALE_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(SALE_TBL.SALE_ID).append(" = ").append(saleId)
                        .toString(), null);

        Logger.d(TAG, "retVal=" + retVal);
        //adapter.close();

        return retVal;
    }
    public static synchronized long updatePurchaseStatus(Context context, int pId, String status) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(PURCHASE_TBL.STATUS, status);
        long retVal = adapter.getDB().update(
                PURCHASE_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(PURCHASE_TBL.P_ID).append(" = ").append(pId)
                        .toString(), null);

        Logger.d(TAG, "retVal=" + retVal);
        //adapter.close();

        return retVal;
    }



    public static synchronized long updateSaleRecord(Context context, SaleEntity entity) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
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
        initialValues.put(SALE_TBL.STATUS, Constants.OPEN);
        initialValues.put(SALE_TBL.SALE_FIELD1, entity.sale_field1);
        initialValues.put(SALE_TBL.SALE_FIELD2, entity.sale_field2);
        initialValues.put(SALE_TBL.SALE_FIELD3, entity.sale_field3);
        initialValues.put(SALE_TBL.SALE_FIELD4, entity.sale_field4);
        initialValues.put(SALE_TBL.SALE_FIELD5, entity.sale_field5);
        long retVal = adapter.getDB().update(
                SALE_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(SALE_TBL.SALE_ID).append(" = ").append(entity.sale_id)
                        .toString(), null);
        //adapter.close();

        Logger.d(TAG, "retVal=" + retVal);

        return retVal;
    }

    public static synchronized long updateShopSerId(Context context, int saleId, int shop_id) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(SHOP_TBL.SER_SHOP_ID, shop_id);
        long retVal = adapter.getDB().update(
                SHOP_TBL.TABLE_NAME,
                initialValues,
                new StringBuffer().append(SHOP_TBL.SALE_ID).append(" = ").append(saleId)
                        .toString(), null);
        Logger.d(TAG, "retVal=" + retVal);
        //adapter.close();

        return retVal;
    }

    public static synchronized int updateRoutePlanRecord(Context context, RoutePlanEntity entity, boolean byId) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(ROUTE_PLAN_TBL.VID, entity.vid);
        initialValues.put(ROUTE_PLAN_TBL.UID, entity.uid);
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

        String whereClause;
        if(byId){
            initialValues.put(ROUTE_PLAN_TBL.DATE, entity.date);
            whereClause = new StringBuffer().append(ROUTE_PLAN_TBL.R_ID).append(" = ").append(entity.r_id).toString();
        }else {
            initialValues.put(ROUTE_PLAN_TBL.R_ID, entity.r_id);
            whereClause = new StringBuffer().append(ROUTE_PLAN_TBL.DATE).append(" = '").append(entity.date).append("'")
                    .toString();
        }
        long retVal = adapter.getDB().update(
                ROUTE_PLAN_TBL.TABLE_NAME,
                initialValues, whereClause, null);
        Logger.d(TAG, "retVal=" + retVal);
        //adapter.close();

        return (int)retVal;
    }
}
