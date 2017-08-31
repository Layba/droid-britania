package com.bizonesoft.ace.tables;

/**
 * Created by dipesh on 5/2/16.
 */
public class LONG_SHOT_TBL {

    private LONG_SHOT_TBL(){

    };
    public static String TABLE_NAME="LONG_SHOT_TBL";
    public static String LS_REC_ID="LS_REC_ID";
    public static String LS_IMG="LS_IMG";
    public static String LS_IMG_ID="LS_IMG_ID";
    public static String LS_CAP_TYM="LS_CAP_TYM";
    public static String LS_STATUS="LS_STATUS";
    public static String LS_DEV_ID="LS_DEV_ID";

    private static final String LONG_SHOT_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    LS_REC_ID ).append( " integer , " ).append(LS_DEV_ID)
                    .append(" text, ").append(LS_CAP_TYM).append(" long, ").append(LS_STATUS).append(" text, ")
                    .append(LS_IMG_ID).append(" long, ").append(LS_IMG)
                    .append( " text);").toString();

    private static final String LONG_SHOT_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return LONG_SHOT_TBL_CREATE;
    }

    public static String dropTable(){
        return LONG_SHOT_TBL_DROP;
    }
}
