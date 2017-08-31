package com.bizone.britannia.queries;

import android.content.Context;

import com.bizone.britannia.db.DBAdapter;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.tables.PRODUCT_SKU_TBL;
import com.bizone.britannia.tables.SALE_METADATA_TBL;
import com.bizone.britannia.tables.SUB_ORDER_TBL;

/**
 * Created by sagar on 2/8/16.
 */
public class DeleteQueries {

    private static final String TAG = DeleteQueries.class.getSimpleName();
    private DeleteQueries(){
        super();
    }

    public static synchronized long deleteSuborderRecord(Context context, int saleId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal = adapter.getDB().delete(
                SUB_ORDER_TBL.TABLE_NAME,
                new StringBuffer().append(SUB_ORDER_TBL.SALE_ID).append(" = ").append(saleId).toString(), null);

        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }

    public static synchronized long deleteSaleMetadataRecord(Context context, int saleId) {

        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal = adapter.getDB().delete(
                SALE_METADATA_TBL.TABLE_NAME,
                new StringBuffer().append(SALE_METADATA_TBL.SALE_ID).append(" = ").append(saleId).toString(), null);

        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }


    public static synchronized long deleteProductSkuRecord(Context context) {
        DBAdapter adapter = DBAdapter.getInstance(context);
        adapter.open();
        long retVal = adapter.getDB().delete(PRODUCT_SKU_TBL.TABLE_NAME, null, null);

        Logger.d(TAG, "retval="+retVal);
        //adapter.close();
        return retVal;
    }
}
