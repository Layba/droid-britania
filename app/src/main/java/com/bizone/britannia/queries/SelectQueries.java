package com.bizone.britannia.queries;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


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
import com.bizone.britannia.tables.SETTINGS_TBL;
import com.bizone.britannia.tables.SHOP_TBL;
import com.bizone.britannia.tables.STARTDAY_TBL;
import com.bizone.britannia.tables.SUB_ORDER_TBL;
import com.bizone.britannia.tables.VILLAGE_SUMMARY_TBL;
import com.bizone.britannia.tables.VILLAGE_TBL;

import java.util.ArrayList;

/**
 * Created by siddhesh on 7/22/16.
 */
public class SelectQueries {

    private SelectQueries(){};

    private static final String TAG=SelectQueries.class.getSimpleName();

    public static synchronized String getSetting(Context context, Settings type) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()
                .query(SETTINGS_TBL.TABLE_NAME,
                        new String[]{SETTINGS_TBL.STB_STNGS_VAL},
                        new StringBuffer().append(SETTINGS_TBL.STB_STNGS_NM)
                                .append(" = '").append(type).append("' ").toString(), null,
                        null, null, null);
        String retVal = null;

        if (cursor.getCount() == 0) {

            ContentValues initialValues = new ContentValues();
            initialValues.put(SETTINGS_TBL.STB_STNGS_NM, type.toString());
            initialValues.put(SETTINGS_TBL.STB_STNGS_VAL, "");
            adapter.getDB().insert(SETTINGS_TBL.TABLE_NAME, null, initialValues);

            cursor.close();
            cursor = adapter.getDB().query(
                    SETTINGS_TBL.TABLE_NAME,
                    new String[] { SETTINGS_TBL.STB_STNGS_VAL },
                    new StringBuffer().append(SETTINGS_TBL.STB_STNGS_NM).append(
                            " = '").append(type).append("' ").toString(), null, null, null,
                    null);
        }
        cursor.moveToFirst();
        retVal = cursor.getString(0);
        cursor.close();
        Logger.d(TAG, "retval=" + retVal+" cursor count="+cursor.getCount());
        //adapter.close();
        return retVal;
    }

    public static synchronized ArrayList<ShopEntity> getFilteredShops(Context context, int villageId, String filter) {
        Logger.d(TAG, " getScrShopImpl villageId = " + villageId);
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB().query(true,SHOP_TBL.TABLE_NAME, new String[]{SHOP_TBL.SHOP_NAME,SHOP_TBL.OWNER_NAME
                        , SHOP_TBL.MOBILE_NO, SHOP_TBL.VID, SHOP_TBL.ALT_NO, SHOP_TBL.VERIFICATION_STATUS, SHOP_TBL.ALT_VERIFICATION_STATUS
                        , SHOP_TBL.BASE_VILLAGE_ID, SHOP_TBL.VILLAGE, SHOP_TBL.SALE_ID, SHOP_TBL.CREATED, SHOP_TBL.CREATED_BY
                        , SHOP_TBL.UPDATED, SHOP_TBL.UPDATED_BY, SHOP_TBL.SHOP_FIELD1, SHOP_TBL.SHOP_FIELD2, SHOP_TBL.SHOP_FIELD3
                        , SHOP_TBL.SHOP_FIELD4, SHOP_TBL.SHOP_FIELD5},
                new StringBuffer().append(SHOP_TBL.BASE_VILLAGE_ID).append(" = ").append(villageId).append(" AND ")
                        .append(SHOP_TBL.SHOP_NAME).append(" LIKE ?").toString(),
                new String[] {"%"+ filter+ "%" }, null, null, null, null);


        int count = cursor.getCount();
        ArrayList<ShopEntity> entities = new ArrayList<ShopEntity>();
        if (count > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                ShopEntity entity = new ShopEntity();
                entity.shop_name = cursor.getString(0);
                entity.owner_name = cursor.getString(1);
                entity.mobile_no = cursor.getString(2);
                entity.vid = cursor.getInt(3);
                entity.alt_no = cursor.getString(4);
                entity.verification_status = cursor.getString(5);
                entity.alt_verification_status = cursor.getString(6);
                entity.base_village_id = cursor.getInt(7);
                entity.village = cursor.getString(8);
                entity.sale_id = cursor.getInt(9);
                entity.created = cursor.getInt(10);
                entity.created_by = cursor.getInt(11);
                entity.updated = cursor.getInt(12);
                entity.updated_by = cursor.getInt(13);
                entity.shop_field1 = cursor.getString(14);
                entity.shop_field2 = cursor.getString(15);
                entity.shop_field3 = cursor.getString(16);
                entity.shop_field4 = cursor.getString(17);
                entity.shop_field5 = cursor.getString(18);
                entities.add(entity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        Logger.d(TAG, " getScrShopImpl entities = " + entities);
        return entities;
    }

    public static synchronized ArrayList<ProductSkuEntity> getProductSkuElements(Context context,String state) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(PRODUCT_SKU_TBL.TABLE_NAME, new String[]{PRODUCT_SKU_TBL.PRODUCT_ID
                        , PRODUCT_SKU_TBL.NAME, PRODUCT_SKU_TBL.SKU, PRODUCT_SKU_TBL.MRP, PRODUCT_SKU_TBL.SELLING_PRICE,
                        PRODUCT_SKU_TBL.STATE, PRODUCT_SKU_TBL.STATUS},
                new StringBuffer().append(PRODUCT_SKU_TBL.STATE).append(" = '").append(state).append("'").toString(), null, null, null, PRODUCT_SKU_TBL.NAME);
        int count=cursor.getCount();
        ArrayList<ProductSkuEntity> entities=new ArrayList<ProductSkuEntity>();
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++){
                ProductSkuEntity entity=new ProductSkuEntity();
                entity.product_id=cursor.getInt(0);
                entity.name=cursor.getString(1);
                entity.sku=cursor.getString(2);
                entity.mrp=cursor.getString(3);
                entity.selling_price=cursor.getString(4);
                entity.state=cursor.getInt(5);
                entity.status=cursor.getString(6);
                entities.add(entity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entities;
    }

    public static synchronized ArrayList<DeviceEntity> getDeviceElements(Context context) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(DEVICE_TBL.TABLE_NAME, new String[]{DEVICE_TBL.P_ID
                        , DEVICE_TBL.DEVICE_ID, DEVICE_TBL.IMEI, DEVICE_TBL.MODEL, DEVICE_TBL.UID,
                        DEVICE_TBL.CREATED},
                null, null, null, null, null);
        int count=cursor.getCount();
        ArrayList<DeviceEntity> entities=new ArrayList<DeviceEntity>();
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++){
                DeviceEntity entity=new DeviceEntity();
                entity.p_id=cursor.getInt(0);
                entity.device_id=cursor.getString(1);
                entity.imei =cursor.getString(2);
                entity.model=cursor.getString(3);
                entity.uid=cursor.getInt(4);
                entity.created=cursor.getInt(5);
                entities.add(entity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entities;
    }



    public static synchronized int getRoutePlanVid(Context context){

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB().query(ROUTE_PLAN_TBL.TABLE_NAME, new String[]{"MAX(" + ROUTE_PLAN_TBL.VID + ")"}, null, null, null, null, null);
        int vid = 0;
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            vid = cursor.getInt(0);
        }
        Logger.d(TAG, "vid=" + vid);
        return vid;
    }

    public static synchronized ArrayList<RoutePlanEntity> getRoutePlanElements(Context context) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	ROUTE_PLAN_TBL.TABLE_NAME,new String[] {ROUTE_PLAN_TBL.R_ID,ROUTE_PLAN_TBL.VID
                        ,ROUTE_PLAN_TBL.UID,ROUTE_PLAN_TBL.DATE,ROUTE_PLAN_TBL.VILLAGEID_1,ROUTE_PLAN_TBL.VILLAGEID_2,
                        ROUTE_PLAN_TBL.VILLAGEID_3, ROUTE_PLAN_TBL.VILLAGEID_4,
                        ROUTE_PLAN_TBL.VILLAGEID_5,ROUTE_PLAN_TBL.VILLAGENAME_1,ROUTE_PLAN_TBL.VILLAGENAME_2,ROUTE_PLAN_TBL.VILLAGENAME_3,
                        ROUTE_PLAN_TBL.VILLAGENAME_4,ROUTE_PLAN_TBL.VILLAGENAME_5,
                        ROUTE_PLAN_TBL.CREATED,ROUTE_PLAN_TBL.CREATED_BY,ROUTE_PLAN_TBL.RP_FIELD1,ROUTE_PLAN_TBL.RP_FIELD2,
                        ROUTE_PLAN_TBL.RP_FIELD3,ROUTE_PLAN_TBL.RP_FIELD4,ROUTE_PLAN_TBL.RP_FIELD5,ROUTE_PLAN_TBL.RP_FIELD6,ROUTE_PLAN_TBL.RP_FIELD7
                        ,ROUTE_PLAN_TBL.RP_FIELD8,ROUTE_PLAN_TBL.RP_FIELD9,ROUTE_PLAN_TBL.RP_FIELD10},
                null, null,null, null, null);
        int count=cursor.getCount();
        ArrayList<RoutePlanEntity> entities=new ArrayList<RoutePlanEntity>();
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++){
                RoutePlanEntity entity=new RoutePlanEntity();
                entity.r_id=cursor.getInt(0);
                entity.vid=cursor.getInt(1);
                entity.uid=cursor.getInt(2);
                entity.date=cursor.getString(3);
                entity.villageid_1=cursor.getInt(4);
                entity.villageid_2=cursor.getInt(5);
                entity.villageid_3=cursor.getInt(6);
                entity.villageid_4=cursor.getInt(7);
                entity.villageid_5=cursor.getInt(8);
                entity.villagename_1=cursor.getString(9);
                entity.villagename_2=cursor.getString(10);
                entity.villagename_3=cursor.getString(11);
                entity.villagename_4=cursor.getString(12);
                entity.villagename_5=cursor.getString(13);
                entity.created=cursor.getInt(14);
                entity.created_by=cursor.getInt(15);
                entity.rp_field1=cursor.getString(16);
                entity.rp_field2=cursor.getString(17);
                entity.rp_field3=cursor.getString(18);
                entity.rp_field4=cursor.getString(19);
                entity.rp_field5=cursor.getString(10);
                entity.rp_field6=cursor.getString(21);
                entity.rp_field7=cursor.getString(22);
                entity.rp_field8=cursor.getString(23);
                entity.rp_field9=cursor.getString(24);
                entity.rp_field10=cursor.getString(25);
                entities.add(entity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entities;
    }


    public static synchronized SaleEntity getSaleElements(Context context, int saleId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	SALE_TBL.TABLE_NAME,new String[] {SALE_TBL.SALE_ID,SALE_TBL.TRANSACTION_ID
                        ,SALE_TBL.SUBTOTAL,SALE_TBL.TAX,SALE_TBL.DISCOUNT,SALE_TBL.TOTAL,
                        SALE_TBL.DEVICE_ID,SALE_TBL.CREATED,SALE_TBL.CREATED_BY,SALE_TBL.UPDATED,SALE_TBL.UPDATED_BY,SALE_TBL.STATUS,SALE_TBL.SALE_FIELD1
                        ,SALE_TBL.SALE_FIELD2,SALE_TBL.SALE_FIELD3,SALE_TBL.SALE_FIELD4,SALE_TBL.SALE_FIELD5,SALE_TBL.SALE_TYPE,SALE_TBL.LATITUDE,SALE_TBL.LONGITUDE
        ,SALE_TBL.ACCURACY},
                        new StringBuffer().append(SALE_TBL.SALE_ID).append(" = ").append(saleId).toString(), null,null, null, null);
        int count=cursor.getCount();
        SaleEntity entity = new SaleEntity();
        entity.status = Constants.CLOSED;
        if(count > 0){
            cursor.moveToFirst();
            entity=new SaleEntity();
            entity.sale_id=cursor.getInt(0);
            entity.transaction_id=cursor.getInt(1);
            entity.subtotal=cursor.getString(2);
            entity.tax=cursor.getString(3);
            entity.discount=cursor.getString(4);
            entity.total=cursor.getString(5);
            entity.device_id=cursor.getString(6);
            entity.created=cursor.getInt(7);
            entity.created_by=cursor.getInt(8);
            entity.updated=cursor.getInt(9);
            entity.updated_by=cursor.getInt(10);
            entity.status=cursor.getString(11);
            entity.sale_field1=cursor.getString(12);
            entity.sale_field2=cursor.getString(13);
            entity.sale_field3=cursor.getString(14);
            entity.sale_field4=cursor.getString(15);
            entity.sale_field5=cursor.getString(16);
            entity.sale_type=cursor.getString(17);
            entity.latitude=cursor.getString(18);
            entity.longitude=cursor.getString(19);
            entity.accuracy=cursor.getString(20);
            cursor.moveToNext();
        }
        cursor.close();
        //adapter.close();
        return entity;
    }

    public static synchronized PurchaseEntity getPurchaseElements(Context context, int pId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	PURCHASE_TBL.TABLE_NAME,new String[] {PURCHASE_TBL.P_ID,PURCHASE_TBL.STOCKIEST_NAME
                        ,PURCHASE_TBL.STOCKIEST_NO,PURCHASE_TBL.TOTAL,PURCHASE_TBL.DEVICE_ID,PURCHASE_TBL.LATITUDE,
                        PURCHASE_TBL.LONGITUDE,PURCHASE_TBL.ACCURACY,PURCHASE_TBL.CREATED,PURCHASE_TBL.CREATED_BY,PURCHASE_TBL.UPDATED,PURCHASE_TBL.UPDATED_BY,PURCHASE_TBL.STATUS
                        ,PURCHASE_TBL.PURCHASE_FIELD1,PURCHASE_TBL.PURCHASE_FIELD2,PURCHASE_TBL.PURCHASE_FIELD3,PURCHASE_TBL.PURCHASE_FIELD4,PURCHASE_TBL.PURCHASE_FIELD5},
                new StringBuffer().append(PURCHASE_TBL.P_ID).append(" = ").append(pId).toString(), null,null, null, null);
        int count=cursor.getCount();
        PurchaseEntity entity = new PurchaseEntity();
        entity.status = Constants.CLOSED;
        if(count > 0){
            cursor.moveToFirst();
            entity=new PurchaseEntity();
            entity.p_id=cursor.getInt(0);
            entity.stockiestName=cursor.getString(1);
            entity.stockiestNo=cursor.getString(2);
            entity.total=cursor.getString(3);
            entity.device_id=cursor.getString(4);
            entity.latitude=cursor.getString(5);
            entity.longitude=cursor.getString(6);
            entity.accuracy=cursor.getString(7);
            entity.created=cursor.getInt(8);
            entity.created_by=cursor.getInt(9);
            entity.updated=cursor.getInt(10);
            entity.updated_by=cursor.getInt(11);
            entity.status=cursor.getString(12);
            entity.purchase_field1=cursor.getString(13);
            entity.purchase_field2=cursor.getString(14);
            entity.purchase_field3=cursor.getString(15);
            entity.purchase_field4=cursor.getString(16);
            entity.purchase_field5=cursor.getString(17);
            cursor.moveToNext();
        }
        cursor.close();
        //adapter.close();
        return entity;
    }

    private static boolean SALE_ORDER_TOGGLE=true;
    public static synchronized ArrayList<SaleEntity> getAllSaleElements(Context context) {

        String order=null;
        if(SALE_ORDER_TOGGLE){
            order=SALE_TBL.SALE_ID + " desc";
        }else{
            order=SALE_TBL.SALE_ID + " asc";
        }
        SALE_ORDER_TOGGLE = !SALE_ORDER_TOGGLE;

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        String whereClause = new StringBuffer().append(SALE_TBL.SALE_TYPE).append(" != '3' and (").append(SALE_TBL.STATUS).append(" = '").append(Constants.INPROGRESS).append("' OR ")
                .append(SALE_TBL.STATUS).append(" = '").append(Constants.OPEN).append("')").toString();
        Logger.d(TAG,"whereClause="+whereClause);
        Cursor cursor = adapter.getDB()	.query(SALE_TBL.TABLE_NAME, new String[]{SALE_TBL.SALE_ID, SALE_TBL.TRANSACTION_ID
                        , SALE_TBL.SUBTOTAL, SALE_TBL.TAX, SALE_TBL.DISCOUNT, SALE_TBL.TOTAL,
                        SALE_TBL.DEVICE_ID, SALE_TBL.CREATED, SALE_TBL.CREATED_BY, SALE_TBL.UPDATED, SALE_TBL.UPDATED_BY,
                        SALE_TBL.STATUS, SALE_TBL.SALE_FIELD1, SALE_TBL.SALE_FIELD2, SALE_TBL.SALE_FIELD3, SALE_TBL.SALE_TYPE},
                whereClause, null, null, null, order);

        int count=cursor.getCount();
        ArrayList<SaleEntity> entities=new ArrayList<SaleEntity>();
        SaleEntity entity = null;
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++) {
                entity = new SaleEntity();
                entity.sale_id = cursor.getInt(0);
                entity.transaction_id = cursor.getInt(1);
                entity.subtotal = cursor.getString(2);
                entity.tax = cursor.getString(3);
                entity.discount = cursor.getString(4);
                entity.total = cursor.getString(5);
                entity.device_id = cursor.getString(6);
                entity.created = cursor.getInt(7);
                entity.created_by = cursor.getInt(8);
                entity.updated = cursor.getInt(9);
                entity.updated_by = cursor.getInt(10);
                entity.status = cursor.getString(11);
                entity.sale_field1 = cursor.getString(12);
                entity.sale_field2 = cursor.getString(13);
                entity.sale_field3 = cursor.getString(14);
                entity.sale_type = cursor.getString(15);
                entities.add(entity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entities;
    }


    public static synchronized ArrayList<SaleMetadataEntity> getSaleMetadataElements(Context context,int saleId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(SALE_METADATA_TBL.TABLE_NAME, new String[]{SALE_METADATA_TBL.SALE_M_ID
                        , SALE_METADATA_TBL.TRANSACTION_ID, SALE_METADATA_TBL.SALE_ID, SALE_METADATA_TBL.FID, SALE_METADATA_TBL.CREATED,
                SALE_METADATA_TBL.STATUS, SALE_METADATA_TBL.FILE_PATH,SALE_METADATA_TBL.TAG},
                new StringBuffer().append(SALE_METADATA_TBL.SALE_ID).append(" = ").append(saleId).toString(), null, null, null, null);
        int count=cursor.getCount();
        ArrayList<SaleMetadataEntity> entities=new ArrayList<SaleMetadataEntity>();
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++){
                SaleMetadataEntity entity=new SaleMetadataEntity();
                entity.m_id=cursor.getInt(0);
                entity.transaction_id=cursor.getInt(1);
                entity.sale_id=cursor.getInt(2);
                entity.fid=cursor.getInt(3);
                entity.created=cursor.getInt(4);
                entity.status=cursor.getString(5);
                entity.filePath=cursor.getString(6);
                entity.tag=cursor.getString(7);
                entities.add(entity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entities;
    }

    public static synchronized StartDayEntity getStartDayElements(Context context) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(STARTDAY_TBL.TABLE_NAME, new String[]{STARTDAY_TBL.START_ID
                        , STARTDAY_TBL.IMAGE_PATH, STARTDAY_TBL.START_DATE, STARTDAY_TBL.REMARKS
                        , STARTDAY_TBL.LATITUDE, STARTDAY_TBL.LONGITUDE, STARTDAY_TBL.ACCURACY, STARTDAY_TBL.STATUS},
                null, null, null, null, null);
        int count=cursor.getCount();
        StartDayEntity entity = null;
        if(count > 0){
            cursor.moveToLast();
            for(int i=0; i<count; i++){
                entity=new StartDayEntity();
                entity.startId=cursor.getInt(0);
                entity.imagePath=cursor.getString(1);
                entity.startDate=cursor.getInt(2);
                entity.remarks=cursor.getString(3);
                entity.latitude=cursor.getString(4);
                entity.longitude=cursor.getString(5);
                entity.accuracy=cursor.getString(6);
                entity.status=cursor.getString(7);
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entity;
    }

    public static synchronized StartDayEntity getStartDayElementsByStartId(Context context,int stardId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(STARTDAY_TBL.TABLE_NAME, new String[]{STARTDAY_TBL.START_ID
                        , STARTDAY_TBL.IMAGE_PATH, STARTDAY_TBL.START_DATE, STARTDAY_TBL.REMARKS
                        , STARTDAY_TBL.LATITUDE, STARTDAY_TBL.LONGITUDE, STARTDAY_TBL.ACCURACY, STARTDAY_TBL.STATUS},
                new StringBuffer().append(STARTDAY_TBL.START_ID).append(" = ").append(stardId).toString(), null, null, null, null);
        int count=cursor.getCount();
        StartDayEntity entity = null;
        if(count > 0){
            cursor.moveToLast();
            for(int i=0; i<count; i++){
                entity=new StartDayEntity();
                entity.startId=cursor.getInt(0);
                entity.imagePath=cursor.getString(1);
                entity.startDate=cursor.getInt(2);
                entity.remarks=cursor.getString(3);
                entity.latitude=cursor.getString(4);
                entity.longitude=cursor.getString(5);
                entity.accuracy=cursor.getString(6);
                entity.status=cursor.getString(7);
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entity;
    }


    public static synchronized ShopEntity getShopElements(Context context, int saleId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	SHOP_TBL.TABLE_NAME,new String[] {SHOP_TBL.SHOP_ID
                        ,SHOP_TBL.SHOP_NAME,SHOP_TBL.OWNER_NAME,SHOP_TBL.MOBILE_NO,SHOP_TBL.ALT_NO,
                        SHOP_TBL.VERIFICATION_STATUS,SHOP_TBL.ALT_VERIFICATION_STATUS,SHOP_TBL.BASE_VILLAGE_ID
                        ,SHOP_TBL.VILLAGE,SHOP_TBL.SALE_ID,SHOP_TBL.CREATED,SHOP_TBL.CREATED_BY,
                        SHOP_TBL.UPDATED,SHOP_TBL.UPDATED_BY,SHOP_TBL.SHOP_FIELD1,SHOP_TBL.SHOP_FIELD2,SHOP_TBL.SHOP_FIELD3,
                        SHOP_TBL.SHOP_FIELD4,SHOP_TBL.SHOP_FIELD5, SHOP_TBL.SER_SHOP_ID},
                        new StringBuffer().append(SHOP_TBL.SALE_ID).append(" = ").append(saleId).toString(), null,null, null, null);
        int count=cursor.getCount();
        ShopEntity entity= null;
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++){
                entity=new ShopEntity();
                entity.shop_id=cursor.getInt(0);
                entity.shop_name=cursor.getString(1);
                entity.owner_name=cursor.getString(2);
                entity.mobile_no=cursor.getString(3);
                entity.alt_no=cursor.getString(4);
                entity.verification_status=cursor.getString(5);
                entity.alt_verification_status=cursor.getString(6);
                entity.base_village_id=cursor.getInt(7);
                entity.village=cursor.getString(8);
                entity.sale_id=cursor.getInt(9);
                entity.created=cursor.getInt(10);
                entity.created_by=cursor.getInt(11);
                entity.updated=cursor.getInt(12);
                entity.updated_by=cursor.getInt(13);
                entity.shop_field1=cursor.getString(14);
                entity.shop_field2=cursor.getString(15);
                entity.shop_field3=cursor.getString(16);
                entity.shop_field4=cursor.getString(17);
                entity.shop_field5=cursor.getString(18);
                entity.ser_shopId=cursor.getInt(19);
                cursor.moveToNext();
            }
        }
        cursor.close();
        //adapter.close();
        return entity;
    }

    public static synchronized ShopEntity getShopElementsByShopId(Context context, int shopId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	SHOP_TBL.TABLE_NAME,new String[] {SHOP_TBL.SHOP_ID
                        ,SHOP_TBL.SHOP_NAME,SHOP_TBL.OWNER_NAME,SHOP_TBL.MOBILE_NO,SHOP_TBL.ALT_NO,
                        SHOP_TBL.VERIFICATION_STATUS,SHOP_TBL.ALT_VERIFICATION_STATUS,SHOP_TBL.BASE_VILLAGE_ID
                        ,SHOP_TBL.VILLAGE,SHOP_TBL.SALE_ID,SHOP_TBL.CREATED,SHOP_TBL.CREATED_BY,
                        SHOP_TBL.UPDATED,SHOP_TBL.UPDATED_BY,SHOP_TBL.SHOP_FIELD1,SHOP_TBL.SHOP_FIELD2,SHOP_TBL.SHOP_FIELD3,
                        SHOP_TBL.SHOP_FIELD4,SHOP_TBL.SHOP_FIELD5, SHOP_TBL.SER_SHOP_ID},
                new StringBuffer().append(SHOP_TBL.SHOP_ID).append(" = ").append(shopId).toString(), null,null, null, null);
        int count=cursor.getCount();
        ShopEntity entity= null;
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++){
                entity=new ShopEntity();
                entity.shop_id=cursor.getInt(0);
                entity.shop_name=cursor.getString(1);
                entity.owner_name=cursor.getString(2);
                entity.mobile_no=cursor.getString(3);
                entity.alt_no=cursor.getString(4);
                entity.verification_status=cursor.getString(5);
                entity.alt_verification_status=cursor.getString(6);
                entity.base_village_id=cursor.getInt(7);
                entity.village=cursor.getString(8);
                entity.sale_id=cursor.getInt(9);
                entity.created=cursor.getInt(10);
                entity.created_by=cursor.getInt(11);
                entity.updated=cursor.getInt(12);
                entity.updated_by=cursor.getInt(13);
                entity.shop_field1=cursor.getString(14);
                entity.shop_field2=cursor.getString(15);
                entity.shop_field3=cursor.getString(16);
                entity.shop_field4=cursor.getString(17);
                entity.shop_field5=cursor.getString(18);
                entity.ser_shopId=cursor.getInt(19);
                cursor.moveToNext();
            }
        }
        cursor.close();
        //adapter.close();
        return entity;
    }

    public static synchronized ArrayList<ShopEntity> getShopElementsByVid(Context context, int vilId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	SHOP_TBL.TABLE_NAME,new String[] {SHOP_TBL.SHOP_NAME,SHOP_TBL.SALE_ID
                ,SHOP_TBL.VILLAGE,SHOP_TBL.SER_SHOP_ID,SHOP_TBL.SHOP_ID, SHOP_TBL.CREATED, SHOP_TBL.UPDATED},
                new StringBuffer().append(SHOP_TBL.BASE_VILLAGE_ID).append(" = ").append(vilId).toString(), null,null, null, null);
        int count=cursor.getCount();
        ArrayList<ShopEntity> entities=new ArrayList<ShopEntity>();
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++){
                ShopEntity entity=new ShopEntity();
                entity.shop_name=cursor.getString(0);
                entity.sale_id = cursor.getInt(1);
                entity.village = cursor.getString(2);
                entity.ser_shopId = cursor.getInt(3);
                entity.shop_id = cursor.getInt(4);
                entity.created = cursor.getInt(5);
                entity.updated = cursor.getInt(6);
                entities.add(entity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entities;
    }

    public static synchronized ArrayList<SubOrderEntity> getSubOrderElements(Context context, int saleId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	SUB_ORDER_TBL.TABLE_NAME,new String[] {SUB_ORDER_TBL.SO_ID
                        ,SUB_ORDER_TBL.SALE_ID,SUB_ORDER_TBL.PRODUCT_ID,SUB_ORDER_TBL.QTY,SUB_ORDER_TBL.TOTAL,
                        SUB_ORDER_TBL.SO_FIELD1,SUB_ORDER_TBL.SO_FIELD2,SUB_ORDER_TBL.SO_FIELD3,SUB_ORDER_TBL.SO_FIELD4
                        ,SUB_ORDER_TBL.SO_FIELD5},
                        new StringBuffer().append(SUB_ORDER_TBL.SALE_ID).append(" = ").append(saleId).toString(), null,null, null, null);
        int count=cursor.getCount();
        ArrayList<SubOrderEntity> entities=new ArrayList<SubOrderEntity>();
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++){
                SubOrderEntity entity=new SubOrderEntity();
                entity.so_id=cursor.getInt(0);
                entity.sale_id=cursor.getInt(1);
                entity.product_id=cursor.getInt(2);
                entity.qty=cursor.getInt(3);
                entity.total=cursor.getString(4);
                entity.so_field1=cursor.getString(5);
                entity.so_field2=cursor.getString(6);
                entity.so_field3=cursor.getString(7);
                entity.so_field4=cursor.getString(8);
                entity.so_field5=cursor.getString(9);
                entities.add(entity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entities;
    }

    public static synchronized ArrayList<PurchaseSubOrderEntity> getPurchaseSubOrderElements(Context context, int pId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	PURCHASE_SUB_ORDER_TBL.TABLE_NAME,new String[] {PURCHASE_SUB_ORDER_TBL.PSO_ID
                        ,PURCHASE_SUB_ORDER_TBL.P_ID,PURCHASE_SUB_ORDER_TBL.PRODUCT_ID,PURCHASE_SUB_ORDER_TBL.QTY,PURCHASE_SUB_ORDER_TBL.TOTAL,
                        PURCHASE_SUB_ORDER_TBL.SO_FIELD1,PURCHASE_SUB_ORDER_TBL.SO_FIELD2,PURCHASE_SUB_ORDER_TBL.SO_FIELD3,PURCHASE_SUB_ORDER_TBL.SO_FIELD4
                        ,PURCHASE_SUB_ORDER_TBL.SO_FIELD5},
                new StringBuffer().append(PURCHASE_SUB_ORDER_TBL.P_ID).append(" = ").append(pId).toString(), null,null, null, null);
        int count=cursor.getCount();
        ArrayList<PurchaseSubOrderEntity> entities=new ArrayList<PurchaseSubOrderEntity>();
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++){
                PurchaseSubOrderEntity entity=new PurchaseSubOrderEntity();
                entity.pso_id=cursor.getInt(0);
                entity.p_id=cursor.getInt(1);
                entity.product_id=cursor.getInt(2);
                entity.qty=cursor.getInt(3);
                entity.total=cursor.getString(4);
                entity.so_field1=cursor.getString(5);
                entity.so_field2=cursor.getString(6);
                entity.so_field3=cursor.getString(7);
                entity.so_field4=cursor.getString(8);
                entity.so_field5=cursor.getString(9);
                entities.add(entity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entities;
    }


    public static synchronized ArrayList<VillageEntity> getVillageElements(Context context) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	VILLAGE_TBL.TABLE_NAME,new String[] {VILLAGE_TBL.VILLAGE_ID
                        ,VILLAGE_TBL.VILLAGE,VILLAGE_TBL.STATE,VILLAGE_TBL.CREATED,VILLAGE_TBL.CREATED_BY},
                null, null,null, null, null);
        int count=cursor.getCount();
        ArrayList<VillageEntity> entities=new ArrayList<VillageEntity>();
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0; i<count; i++){
                VillageEntity entity=new VillageEntity();
                entity.village_id=cursor.getInt(0);
                entity.village=cursor.getString(1);
                entity.state=cursor.getString(2);
                entity.created=cursor.getInt(3);
                entity.created_by=cursor.getInt(4);
                entities.add(entity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        //adapter.close();
        return entities;
    }
//as if add routeid
    public static synchronized int getVillIdBySaleId(Context context, int sale_id) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	VILLAGE_SUMMARY_TBL.TABLE_NAME,new String[] {VILLAGE_SUMMARY_TBL.VILLAGE_ID},
                new StringBuffer().append(VILLAGE_SUMMARY_TBL.SALE_ID).append(" = ").append(sale_id).toString(), null,null, null, null);
        int count=cursor.getCount();
        int villId = 0;
        if(count > 0){
            cursor.moveToFirst();
            villId = cursor.getInt(0);
        }
        cursor.close();
        //adapter.close();
        return villId;
    }


    public static synchronized String getVillNameByVilId(Context context, int vilId) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	VILLAGE_SUMMARY_TBL.TABLE_NAME,new String[] {VILLAGE_SUMMARY_TBL.VILLAGE_NAME},
                new StringBuffer().append(VILLAGE_SUMMARY_TBL.VILLAGE_ID).append(" = ").append(vilId).toString(), null,null, null, null);
        int count=cursor.getCount();
        String vilName = "";
        if(count > 0){
            cursor.moveToFirst();
            vilName = cursor.getString(0);
        }
        cursor.close();
        //adapter.close();
        return vilName;
    }
    public static synchronized int getSaleIdByVilIdAndRId(Context context, int vilId, int routeId) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	VILLAGE_SUMMARY_TBL.TABLE_NAME,new String[] {VILLAGE_SUMMARY_TBL.SALE_ID},
                new StringBuffer().append(VILLAGE_SUMMARY_TBL.VILLAGE_ID).append(" = '").append(vilId).append("' AND ")
                        .append(VILLAGE_SUMMARY_TBL.ROUTE_ID).append(" = '").append(routeId).append("'").toString(), null,null, null, null);
        int count=cursor.getCount();
        int saleId = 0;
        if(count > 0){
            cursor.moveToFirst();
            saleId = cursor.getInt(0);
        }
        cursor.close();
        //adapter.close();
        return saleId;
    }

    public static synchronized int checkHaatSummaryDone(Context context, int villId, int routeId) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB().query(VILLAGE_SUMMARY_TBL.TABLE_NAME, new String[]{VILLAGE_SUMMARY_TBL.SALE_ID},
                new StringBuffer().append(VILLAGE_SUMMARY_TBL.VILLAGE_ID).append(" = '").append(villId).append("' AND ")
                        .append(VILLAGE_SUMMARY_TBL.ROUTE_ID).append(" = '").append(routeId).append("'").toString(), null, null, null, null);
        int count = cursor.getCount();
        int saleId = -1;
        if (count > 0) {
            cursor.moveToFirst();
            saleId = cursor.getInt(0);
        }
        return saleId;
    }

    public static ArrayList<PurchaseEntity> getPurchaseByStatus(Context context, String status) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(	PURCHASE_TBL.TABLE_NAME,new String[] {PURCHASE_TBL.P_ID,PURCHASE_TBL.STOCKIEST_NAME
                        ,PURCHASE_TBL.STOCKIEST_NO,PURCHASE_TBL.TOTAL,PURCHASE_TBL.DEVICE_ID,PURCHASE_TBL.LATITUDE,
                        PURCHASE_TBL.LONGITUDE,PURCHASE_TBL.ACCURACY,PURCHASE_TBL.CREATED,PURCHASE_TBL.CREATED_BY,PURCHASE_TBL.UPDATED,PURCHASE_TBL.UPDATED_BY,PURCHASE_TBL.STATUS
                        ,PURCHASE_TBL.PURCHASE_FIELD1,PURCHASE_TBL.PURCHASE_FIELD2,PURCHASE_TBL.PURCHASE_FIELD3,PURCHASE_TBL.PURCHASE_FIELD4,PURCHASE_TBL.PURCHASE_FIELD5},
                new StringBuffer().append(PURCHASE_TBL.STATUS).append(" = '").append(status).append("'").toString(), null,null, null, null);
        int count=cursor.getCount();
        ArrayList<PurchaseEntity> entities = new ArrayList<>();
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0;i<count;i++) {
                PurchaseEntity entity=new PurchaseEntity();
                entity.p_id = cursor.getInt(0);
                entity.stockiestName = cursor.getString(1);
                entity.stockiestNo = cursor.getString(2);
                entity.total = cursor.getString(3);
                entity.device_id = cursor.getString(4);
                entity.latitude = cursor.getString(5);
                entity.longitude = cursor.getString(6);
                entity.accuracy = cursor.getString(7);
                entity.created = cursor.getInt(8);
                entity.created_by = cursor.getInt(9);
                entity.updated = cursor.getInt(10);
                entity.updated_by = cursor.getInt(11);
                entity.status = cursor.getString(12);
                entity.purchase_field1 = cursor.getString(13);
                entity.purchase_field2 = cursor.getString(14);
                entity.purchase_field3 = cursor.getString(15);
                entity.purchase_field4 = cursor.getString(16);
                entity.purchase_field5 = cursor.getString(17);
                cursor.moveToNext();
                entities.add(entity);
            }
        }
        cursor.close();
        //adapter.close();
        return entities;

    }


    public static synchronized ArrayList<StartDayEntity> getStartDayByStatus(Context context,String status) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        Cursor cursor = adapter.getDB()	.query(STARTDAY_TBL.TABLE_NAME, new String[]{STARTDAY_TBL.START_ID
                        , STARTDAY_TBL.IMAGE_PATH, STARTDAY_TBL.START_DATE, STARTDAY_TBL.REMARKS
                        , STARTDAY_TBL.LATITUDE, STARTDAY_TBL.LONGITUDE, STARTDAY_TBL.ACCURACY, STARTDAY_TBL.STATUS},
                new StringBuffer().append(STARTDAY_TBL.STATUS).append(" = '").append(status).append("'").toString(), null, null, null, null);
        int count=cursor.getCount();
        ArrayList<StartDayEntity> entities = new ArrayList<>();
        if(count > 0){
            cursor.moveToFirst();
            for(int i=0;i<count;i++) {
                StartDayEntity entity=new StartDayEntity();
                entity.startId=cursor.getInt(0);
                entity.imagePath=cursor.getString(1);
                entity.startDate=cursor.getInt(2);
                entity.remarks=cursor.getString(3);
                entity.latitude=cursor.getString(4);
                entity.longitude=cursor.getString(5);
                entity.accuracy=cursor.getString(6);
                entity.status=cursor.getString(7);
                cursor.moveToNext();
                entities.add(entity);
            }
        }
        cursor.close();
        Logger.d(TAG, "cursor count=" + cursor.getCount());
        //adapter.close();
        return entities;
    }
}
