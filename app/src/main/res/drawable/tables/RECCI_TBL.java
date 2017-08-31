package com.bizonesoft.ace.tables;

/**
 * Created by dipesh on 30/12/15.
 */
public class RECCI_TBL {

    private RECCI_TBL(){

    };
    public static String TABLE_NAME="RECCI_TBL";
    public static String REC_ID="REC_ID";
    public static String REC_PB_ID="REC_PB_ID";
    public static String REC_IMG_CNT="REC_IMG_CNT";
    public static String REC_STAT="REC_STAT";
    public static String REC_DEV_ID="REC_DEV_ID";
    public static String REC_SER_ID="REC_SER_ID";

    private static final String RECCI_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    REC_ID ).append( " integer primary key autoincrement, " ).append(REC_PB_ID)
                    .append(" integer, ").append(REC_IMG_CNT).append(" integer, ").append(REC_STAT)
                    .append(" text, ").append(REC_DEV_ID).append(" text, ").append(REC_SER_ID).append( " long);").toString();

    private static final String RECCI_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return RECCI_TBL_CREATE;
    }

    public static String dropTable(){
        return RECCI_TBL_DROP;
    }

}
