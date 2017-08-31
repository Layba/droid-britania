package com.bizone.britannia.tables;

/**
 * Created by sagar on 22/7/16.
 */
public class VILLAGE_TBL {

    private VILLAGE_TBL(){

    };
    public static String TABLE_NAME="VILLAGE_TBL";
    public static String VILLAGE_ID="VILLAGE_ID";
    public static String VILLAGE="VILLAGE";
    public static String STATE="STATE";
    public static String CREATED="CREATED";
    public static String CREATED_BY="CREATED_BY";

    private static final String VILLAGE_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(
                    VILLAGE_ID ).append( " integer primary key autoincrement, " ).append(VILLAGE)
                    .append( " text,").append(STATE).append(" text,").append(CREATED).append(" integer, ")
                    .append(CREATED_BY).append( " integer);").toString();

    private static final String VILLAGE_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return VILLAGE_TBL_CREATE;
    }

    public static String dropTable(){
        return VILLAGE_TBL_DROP;
    }
}
