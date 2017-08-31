package com.bizone.britannia.tables;

/**
 * Created by Sonam on 8/12/16.
 */
public class PURCHASE_TBL {
    private PURCHASE_TBL(){

    };
    public static String TABLE_NAME="PURCHASE_TBL";
    public static String P_ID="P_ID";
    public static String STOCKIEST_NAME="STOCKIEST_NAME";
    public static String STOCKIEST_NO="STOCKIEST_NO";
    public static String TOTAL="TOTAL";
    public static String DEVICE_ID="DEVICE_ID";
    public static String LATITUDE = "LATITUDE";
    public static String LONGITUDE = "LONGITUDE";
    public static String ACCURACY = "ACCURACY";
    public static String CREATED="CREATED";
    public static String CREATED_BY="CREATED_BY";
    public static String UPDATED="UPDATED";
    public static String UPDATED_BY="UPDATED_BY";
    public static String STATUS="STATUS";
    public static String PURCHASE_FIELD1="PURCHASE_FIELD1";
    public static String PURCHASE_FIELD2="PURCHASE_FIELD2";
    public static String PURCHASE_FIELD3="PURCHASE_FIELD3";
    public static String PURCHASE_FIELD4="PURCHASE_FIELD4";
    public static String PURCHASE_FIELD5="PURCHASE_FIELD5";

    private static final String PURCHASE_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    P_ID ).append( " integer primary key autoincrement, " )
                    .append(STOCKIEST_NAME).append(" text,").append(STOCKIEST_NO).append(" text, ")
                    .append(TOTAL).append(" text, ").append(DEVICE_ID)
                    .append(" text, ").append(LATITUDE).append(" text, ").append(LONGITUDE).append(" text, ")
                    .append(ACCURACY).append(" text, ").append(CREATED).append(" integer, ").append(CREATED_BY).append(" integer, ")
                    .append(UPDATED).append(" integer, ").append(UPDATED_BY).append(" integer, ").append(STATUS).append(" text, ").append(PURCHASE_FIELD1)
                    .append(" text, ").append(PURCHASE_FIELD2).append(" text , ").append(PURCHASE_FIELD3).append(" text, ")
                    .append(PURCHASE_FIELD4).append(" text, ").append(PURCHASE_FIELD5).append( " text);").toString();

    private static final String PURCHASE_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return PURCHASE_TBL_CREATE;
    }

    public static String dropTable(){
        return PURCHASE_TBL_DROP;
    }

}

