package com.bizone.britannia.tables;

/**
 * Created by dipesh on 30/12/15.
 */
public class SALE_TBL {

    private SALE_TBL(){

    };
    public static String TABLE_NAME="SALE_TBL";
    public static String SALE_ID="SALE_ID";
    public static String V_ID="V_ID";
    public static String TRANSACTION_ID="TRANSACTION_ID";
    public static String SUBTOTAL="SUBTOTAL";
    public static String TAX="TAX";
    public static String DISCOUNT="DISCOUNT";
    public static String TOTAL="TOTAL";
    public static String DEVICE_ID="DEVICE_ID";
    public static String LATITUDE = "LATITUDE";
    public static String LONGITUDE = "LONGITUDE";
    public static String ACCURACY = "ACCURACY";
    public static String CREATED="CREATED";
    public static String CREATED_BY="CREATED_BY";
    public static String UPDATED="UPDATED";
    public static String UPDATED_BY="UPDATED_BY";
    public static String APPROVED_FLAG="APPROVED_FLAG";
    public static String STATUS="STATUS";
    public static String SALE_FIELD1="SALE_FIELD1";
    public static String SALE_FIELD2="SALE_FIELD2";
    public static String SALE_FIELD3="SALE_FIELD3";
    public static String SALE_FIELD4="SALE_FIELD4";
    public static String SALE_FIELD5="SALE_FIELD5";
    public static String SALE_TYPE="SALE_TYPE";

    private static final String SALE_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    SALE_ID ).append( " integer primary key autoincrement, " ).append(V_ID).append(" integer, ")
                    .append(TRANSACTION_ID).append(" integer,").append(SUBTOTAL).append(" text,").append(TAX).append(" text, ")
                    .append(DISCOUNT).append(" text, ").append(TOTAL).append(" text, ").append(DEVICE_ID)
                    .append(" text, ").append(LATITUDE).append(" text, ").append(LONGITUDE).append(" text, ")
                    .append(ACCURACY).append(" text, ").append(CREATED).append(" integer, ").append(CREATED_BY).append(" integer, ")
                    .append(UPDATED).append(" integer, ").append(UPDATED_BY).append(" integer, ").append(APPROVED_FLAG)
                    .append(" text, ").append(STATUS).append(" text , ").append(SALE_FIELD1).append(" text, ")
                    .append(SALE_FIELD2).append(" text, ").append(SALE_FIELD3).append(" text, ").append(SALE_FIELD4)
                    .append(" text, ").append(SALE_FIELD5).append(" text, ").append(SALE_TYPE).append( " text DEFAULT '1');").toString();

    private static final String SALE_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return SALE_TBL_CREATE;
    }

    public static String dropTable(){
        return SALE_TBL_DROP;
    }

}
