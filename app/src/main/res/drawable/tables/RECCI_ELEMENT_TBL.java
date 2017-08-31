package com.bizonesoft.ace.tables;

/**
 * Created by dipesh on 30/12/15.
 */
public class RECCI_ELEMENT_TBL {

    private RECCI_ELEMENT_TBL(){

    };
    public static String TABLE_NAME="RECCI_ELEMENT_TBL";
    public static String REL_ID="REL_ID";
    public static String REL_REC_ID="REL_REC_ID";
    public static String REL_IMG_ID="REL_IMG_ID";
    public static String REL_HGT="REL_HGT";
    public static String REL_WDT="REL_WDT";
    public static String REL_IMG="REL_IMG";
    public static String REL_CAP_TYM="REL_CAP_TYM";
    public static String REL_STATUS="REL_STATUS";
    public static String REL_DEV_ID="REL_DEV_ID";


    private static final String RECCI_ELEMENT_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    REL_ID ).append( " integer primary key autoincrement, " ).append(REL_REC_ID)
                    .append(" integer, ").append(REL_IMG_ID).append(" long, ").append(REL_HGT)
                    .append(" text, ").append(REL_WDT).append(" text, ").append(REL_IMG).append(" text, ")
                    .append(REL_CAP_TYM).append(" long, ").append(REL_STATUS).append(" text, ")
                    .append(REL_DEV_ID).append( " text);").toString();

    private static final String RECCI_ELEMENT_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return RECCI_ELEMENT_TBL_CREATE;
    }

    public static String dropTable(){
        return RECCI_ELEMENT_TBL_DROP;
    }

}
