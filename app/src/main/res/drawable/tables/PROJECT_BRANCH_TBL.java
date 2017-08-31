package com.bizonesoft.ace.tables;

/**
 * Created by dipesh on 30/12/15.
 */
public class PROJECT_BRANCH_TBL {

    private PROJECT_BRANCH_TBL(){

    };
    public static String TABLE_NAME="PROJECT_BRANCH_TBL";
    public static String PB_ID="PB_ID";
    public static String PB_PR_ID="PB_PR_ID";
    public static String PB_BR_ID="PB_BR_ID";
    public static String PB_ELEM_TYP="PB_ELEM_TYP";

    private static final String PROJECT_BRANCH_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    PB_ID ).append( " integer primary key , " ).append(PB_PR_ID)
                    .append(" long, ").append(PB_BR_ID).append(" long, ").append(PB_ELEM_TYP)
                    .append( " integer);").toString();

    private static final String PROJECT_BRANCH_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return PROJECT_BRANCH_TBL_CREATE;
    }

    public static String dropTable(){
        return PROJECT_BRANCH_TBL_DROP;
    }

}
