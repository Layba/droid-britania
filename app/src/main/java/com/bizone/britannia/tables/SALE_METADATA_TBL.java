package com.bizone.britannia.tables;

/**
 * Created by sagar on 21/7/16.
 */
public class SALE_METADATA_TBL {
    private SALE_METADATA_TBL(){

    };
    public static String TABLE_NAME="SALE_METADATA_TBL";
    public static String SALE_M_ID="SALE_M_ID";
    public static String TRANSACTION_ID="TRANSACTION_ID";
    public static String SALE_ID="SALE_ID";
    public static String FID="FID";
    public static String FILE_PATH="FILE_PATH";
    public static String CREATED="CREATED";
    public static String STATUS="STATUS";
    public static String TAG="TAG";

    private static final String SALE_METADATA_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    SALE_M_ID ).append( " integer primary key autoincrement, " ).append(TRANSACTION_ID)
                    .append( " integer,").append(SALE_ID).append(" integer,").append(FID).append(" integer, ")
                    .append(FILE_PATH).append(" text, ").append(CREATED).append( " integer, ")
                    .append(STATUS).append( " text, ")
                    .append(TAG).append(" text);").toString();

    private static final String SALE_METADATA_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return SALE_METADATA_TBL_CREATE;
    }

    public static String dropTable(){
        return SALE_METADATA_TBL_DROP;
    }
}
