package com.bizonesoft.ace.tables;

/**
 * Created by sagar on 7/3/16.
 */
public class LOCATN_TBL {

    private LOCATN_TBL(){

    };
    public static String TABLE_NAME="LOCATN_TBL";
    public static String LOC_ID="LOC_ID";
    public static String LOC_PR_BR="LOC_PR_BR";
    public static String LOC_JSON="LOC_JSON";
    public static String LOC_STATUS="LOC_STATUS";

    private static final String LOCATN_TBL_CREATE =
            (new StringBuffer()).append("create table ").append( TABLE_NAME ).append(" ( " ).append(
                    LOC_ID ).append( " integer primary key autoincrement, " ).append(LOC_PR_BR)
                    .append(" long,").append(LOC_JSON).append(" text,").append(LOC_STATUS)
                    .append( " text);").toString();

    private static final String LOCATN_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return LOCATN_TBL_CREATE;
    }

    public static String dropTable(){
        return LOCATN_TBL_DROP;
    }


}
