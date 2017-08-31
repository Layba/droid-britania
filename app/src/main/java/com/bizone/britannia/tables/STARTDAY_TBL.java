package com.bizone.britannia.tables;

/**
 * Created by Sonam on 23/2/17.
 */
public class STARTDAY_TBL {

    private STARTDAY_TBL(){

    };
    public static String TABLE_NAME="STARTDAY_TBL";
    public static String START_ID="START_ID";
    public static String IMAGE_PATH="IMAGE_PATH";
    public static String START_DATE="START_DATE";
    public static String REMARKS="REMARKS";
    public static String LATITUDE="LATITUDE";
    public static String LONGITUDE="LONGITUDE";
    public static String ACCURACY="ACCURACY";
    public static String STATUS="STATUS";


    private static final String STARTDAY_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    START_ID ).append( " integer primary key autoincrement, " ).append(IMAGE_PATH)
                    .append( " text,").append(START_DATE).append(" integer,").append(REMARKS)
                    .append( " text,").append(LATITUDE)
                    .append( " text,").append(LONGITUDE)
                    .append( " text,").append(ACCURACY)
                    .append( " text,").append(STATUS)
                    .append(" text);").toString();

    private static final String STARTDAY_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return STARTDAY_TBL_CREATE;
    }

    public static String dropTable(){
        return STARTDAY_TBL_DROP;
    }
}

