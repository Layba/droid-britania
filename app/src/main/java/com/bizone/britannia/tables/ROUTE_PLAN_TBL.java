package com.bizone.britannia.tables;

/**
 * Created by sagar on 22/7/16.
 */
public class ROUTE_PLAN_TBL {
    private ROUTE_PLAN_TBL(){

};
public static String TABLE_NAME="ROUTE_PLAN_TBL";
public static String R_ID="R_ID";
public static String VID="VID";
public static String UID="UID";
public static String DATE="DATE";
public static String VILLAGEID_1="VILLAGEID_1";
public static String VILLAGEID_2="VILLAGEID_2";
public static String VILLAGEID_3="VILLAGEID_3";
public static String VILLAGEID_4="VILLAGEID_4";
public static String VILLAGEID_5="VILLAGEID_5";
public static String VILLAGENAME_1="VILLAGENAME_1";
public static String VILLAGENAME_2="VILLAGENAME_2";
public static String VILLAGENAME_3="VILLAGENAME_3";
public static String VILLAGENAME_4="VILLAGENAME_4";
public static String VILLAGENAME_5="VILLAGENAME_5";
public static String CREATED="CREATED";
public static String CREATED_BY="CREATED_BY";
public static String RP_FIELD1="RP_FIELD1";
public static String RP_FIELD2="RP_FIELD2";
public static String RP_FIELD3="RP_FIELD3";
public static String RP_FIELD4="RP_FIELD4";
public static String RP_FIELD5="RP_FIELD5";
public static String RP_FIELD6="RP_FIELD6";
public static String RP_FIELD7="RP_FIELD7";
public static String RP_FIELD8="RP_FIELD8";
public static String RP_FIELD9="RP_FIELD9";
public static String RP_FIELD10="RP_FIELD10";

private static final String ROUTE_PLAN_TBL_CREATE =
        (new StringBuffer()).append("create table ").append(TABLE_NAME).append(" ( ").append(
        R_ID ).append(" integer primary key , ").append(VID).append(" integer, ").append(UID)
        .append(" integer, ").append(DATE).append(" text, ").append(VILLAGEID_1).append(" integer, ").append(VILLAGEID_2).append(" integer, ")
        .append(VILLAGEID_3).append(" integer, ").append(VILLAGEID_4).append(" integer, ").append(VILLAGEID_5).append(" integer, ")
        .append(VILLAGENAME_1).append(" text, ").append(VILLAGENAME_2).append(" text, ").append(VILLAGENAME_3).append(" text, ")
        .append(VILLAGENAME_4).append(" text, ").append(VILLAGENAME_5).append(" text, ").append(CREATED).append(" integer, ")
        .append(CREATED_BY).append(" integer, ").append(RP_FIELD1).append(" text, ").append(RP_FIELD2).append(" text, ")
        .append(RP_FIELD3).append(" text, ").append(RP_FIELD4).append(" text, ").append(RP_FIELD5).append(" text, ").append(RP_FIELD6).append(" text, ")
        .append(RP_FIELD7).append(" text, ").append(RP_FIELD8).append(" text, ").append(RP_FIELD9).append(" text, ").append(RP_FIELD10).append(" text);").toString();

private static final String ROUTE_PLAN_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

public static String createTable(){
        return ROUTE_PLAN_TBL_CREATE;
        }

public static String dropTable(){
        return ROUTE_PLAN_TBL_DROP;
        }


        }

