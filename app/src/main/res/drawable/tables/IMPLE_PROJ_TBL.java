package com.bizonesoft.ace.tables;

/**
 * Created by sagar on 2/3/16.
 */
public class IMPLE_PROJ_TBL {

    private IMPLE_PROJ_TBL(){

    };
    public static String TABLE_NAME="IMPLE_PROJ_TBL";
    public static String PR_ID="PR_ID";
    public static String PR_NAME="PR_NAME";
    public static String PR_STATUS="PR_STATUS";

    private static final String IMPLE_PROJ_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " )
                    .append(PR_ID ).append( " integer primary key, " ).append(PR_NAME).append(" text, ")
                    .append(PR_STATUS).append(" text);").toString();

    private static final String IMPLE_PROJ_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return IMPLE_PROJ_TBL_CREATE;
    }

    public static String dropTable(){
        return IMPLE_PROJ_TBL_DROP;
    }
}
