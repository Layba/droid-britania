package com.bizone.britannia.tables;

/**
 * Created by sagar on 22/7/16.
 */
public class DEVICE_TBL {

    private DEVICE_TBL(){

    };
    public static String TABLE_NAME="DEVICE_TBL";
    public static String P_ID="P_ID";
    public static String DEVICE_ID="DEVICE_ID";
    public static String IMEI="IMEI";
    public static String MODEL="MODEL";
    public static String UID="UID";
    public static String CREATED="CREATED";

    private static final String DEVICE_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    P_ID ).append( " integer primary key autoincrement, " ).append(DEVICE_ID)
                    .append( " text,").append(IMEI).append(" text,").append(MODEL).append(" text, ")
                    .append(UID).append(" integer, ").append(CREATED).append( " integer);").toString();

    private static final String DEVICE_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return DEVICE_TBL_CREATE;
    }

    public static String dropTable(){
        return DEVICE_TBL_DROP;
    }

}
