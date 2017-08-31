package com.bizonesoft.ace.tables;

/**
 * Created by sagar on 4/3/16.
 */
public class IMPLE_LONG_TBL {

    private IMPLE_LONG_TBL(){

    };
    public static String TABLE_NAME="IMPLE_LONG_TBL";
    public static String LS_REC_ID="LS_REC_ID";
    public static String LS_IMG="LS_IMG";
    public static String LS_IMG_SER="LS_IMG_SER";
    public static String LS_CAP_TYM="LS_CAP_TYM";
    public static String LS_STATUS="LS_STATUS";

    private static final String IMPLE_LONG_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    LS_REC_ID ).append( " integer , " ).append(LS_IMG_SER)
                    .append(" text, ").append(LS_CAP_TYM).append(" long, ").append(LS_STATUS).append(" text, ")
                    .append(LS_IMG).append( " text);").toString();

    private static final String IMPLE_LONG_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return IMPLE_LONG_TBL_CREATE;
    }

    public static String dropTable(){
        return IMPLE_LONG_TBL_DROP;
    }


}
