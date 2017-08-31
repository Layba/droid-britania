package com.bizonesoft.ace.tables;

/**
 * Created by sagar on 2/3/16.
 */
public class IMPLE_BRANCH_TBL {

    private IMPLE_BRANCH_TBL(){

    };
    public static String TABLE_NAME="IMPLE_BRANCH_TBL";
    public static String BR_ID="BR_ID";
    public static String BR_SOL_ID="BR_SOL_ID";
    public static String BR_NAME="BR_NAME";
    public static String BR_ZONE="BR_ZONE";
    public static String BR_ADDRESS="BR_ADDRESS";
    public static String BR_TYPE="BR_TYPE";
    public static String BR_STATUS="BR_STATUS";

    private static final String IMPLE_BRANCH_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    BR_ID ).append( " integer primary key, " ).append(BR_SOL_ID)
                    .append( " text,").append(BR_NAME).append(" text,").append(BR_ZONE).append(" text, ")
                    .append(BR_TYPE).append(" text, ").append(BR_ADDRESS).append(" text, ").append(BR_STATUS)
                    .append( " text);").toString();

    private static final String IMPLE_BRANCH_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return IMPLE_BRANCH_TBL_CREATE;
    }

    public static String dropTable(){
        return IMPLE_BRANCH_TBL_DROP;
    }

}
