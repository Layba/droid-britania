package com.bizonesoft.ace.tables;

/**
 * Created by sagar on 2/3/16.
 */
public class IMPLE_PROJ_BRANCH_TBL {

    private IMPLE_PROJ_BRANCH_TBL(){

    };
    public static String TABLE_NAME="IMPLE_PROJ_BRANCH_TBL";
    public static String PB_ID="PB_ID";
    public static String PB_PR_ID="PB_PR_ID";
    public static String PB_BR_ID="PB_BR_ID";
    public static String PB_ELEM_TYP="PB_ELEM_TYP";
    public static String PB_VENDOR_ID="PB_VENDOR_ID";
    public static String PB_VENDOR_DEVICE_ID="PB_VENDOR_DEVICE_ID";
    public static String PB_STATUS="PB_STATUS";

    private static final String IMPLE_PROJ_BRANCH_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    PB_ID ).append( " integer primary key , " ).append(PB_PR_ID)
                    .append(" long, ").append(PB_BR_ID).append(" long, ").append(PB_VENDOR_ID).append(" text, ")
                    .append(PB_VENDOR_DEVICE_ID).append(" text, ").append(PB_STATUS).append(" text, ")
                    .append(PB_ELEM_TYP).append( " integer);").toString();

    private static final String IMPLE_PROJ_BRANCH_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return IMPLE_PROJ_BRANCH_TBL_CREATE;
    }

    public static String dropTable(){
        return IMPLE_PROJ_BRANCH_TBL_DROP;
    }

}
