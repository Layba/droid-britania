package com.bizonesoft.ace.tables;

/**
 * Created by dipesh on 30/12/15.
 */
public class PROJECT_TBL {

    private PROJECT_TBL(){

    };
    public static String TABLE_NAME="PROJECT_TBL";
    public static String PR_ID="PR_ID";
    public static String PR_NAME="PR_NAME";

    private static final String PROJECT_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " )
                    .append(PR_ID ).append( " integer primary key autoincrement, " ).append(PR_NAME)
                    .append( " text);").toString();

    private static final String PROJECT_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return PROJECT_TBL_CREATE;
    }

    public static String dropTable(){
        return PROJECT_TBL_DROP;
    }

}
