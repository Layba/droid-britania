package com.bizone.britannia.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

import com.bizone.britannia.tables.SETTINGS_TBL;


public class DBAdapter {
	private static final String TAG="DBAdapter";
	public static final String DB_NAME="BRANDBAZAAR";
	private static int dbVersion=2;
	private static DBAdapter adapter ;
	

//    private final Context context;    
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase dbObject;
    
    public SQLiteDatabase getDB(){
    	return dbObject;
    }

    private DBAdapter(final Context ctx) 
    {
//        this.context = ctx;
        dbHelper = new DatabaseHelper(ctx);
    }

	public static DBAdapter getInstance(final Context context){
		Log.d(TAG,"adapter : " + adapter + " : context : " + context);

		synchronized (context) {
			if(adapter == null){
				adapter=new DBAdapter(context);
			}
			
			return adapter;	
		}
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(final Context context) 
        {
            //super(context, Constants.fldr+ "/mydb.db", null, dbVersion);
            super(context, DB_NAME, null, dbVersion);
        }

		@Override
		public void onCreate(final SQLiteDatabase dbObj) {
			Log.d(TAG, "Inside onCreate");
            dbObj.execSQL(DEVICE_TBL.createTable());
            dbObj.execSQL(PRODUCT_SKU_TBL.createTable());
            dbObj.execSQL(PURCHASE_SUB_ORDER_TBL.createTable());
            dbObj.execSQL(ROUTE_PLAN_TBL.createTable());
            dbObj.execSQL(SALE_TBL.createTable());
            dbObj.execSQL(PURCHASE_TBL.createTable());
            dbObj.execSQL(SHOP_TBL.createTable());
            dbObj.execSQL(SALE_METADATA_TBL.createTable());
            dbObj.execSQL(SUB_ORDER_TBL.createTable());
            dbObj.execSQL(VILLAGE_TBL.createTable());
            dbObj.execSQL(SETTINGS_TBL.createTable());
            dbObj.execSQL(VILLAGE_SUMMARY_TBL.createTable());
            dbObj.execSQL(STARTDAY_TBL.createTable());
		}

		@Override
		public void onUpgrade(final SQLiteDatabase dbObj,final int oldVersion, final int newVersion) {
			dbVersion=newVersion;
            dbObj.execSQL(STARTDAY_TBL.createTable());
		}
    }
	
    //---opens the database---
    public DBAdapter open() throws SQLException 
    {
        dbObject = dbHelper.getWritableDatabase();        
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        dbHelper.close();
    }

	
}
