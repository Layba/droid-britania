package com.bizone.britannia.tables;

/**
 * Created by sagar on 13/12/16.
 */

public class VILLAGE_SUMMARY_TBL {

    private VILLAGE_SUMMARY_TBL(){

    };
    public static String TABLE_NAME="VILLAGE_SUMMARY_TBL";
    public static String VILLAGE_SUMMARY_ID="VILLAGE_SUMMARY_ID";
    public static String VILLAGE_ID="VILLAGE_ID";
    public static String SALE_ID="SALE_ID";
    public static String VILLAGE_NAME="VILLAGE_NAME";
    public static String ROUTE_ID="ROUTE_ID";

    private static final String VILLAGE_SUMMARY_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    VILLAGE_SUMMARY_ID ).append( " integer primary key autoincrement, " ).append(VILLAGE_ID)
                    .append(" integer, ").append(VILLAGE_NAME)
                    .append( " text,").append(SALE_ID)
                    .append( " integer,").append(ROUTE_ID).append( " integer);").toString();

    private static final String VILLAGE_SUMMARY_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return VILLAGE_SUMMARY_TBL_CREATE;
    }

    public static String dropTable(){
        return VILLAGE_SUMMARY_TBL_DROP;
    }

}
