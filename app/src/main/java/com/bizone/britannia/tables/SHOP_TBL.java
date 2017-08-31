package com.bizone.britannia.tables;

/**
 * Created by siddhesh on 7/22/16.
 */
public class SHOP_TBL {

    private SHOP_TBL(){

    };
    public static String TABLE_NAME="SHOP_TBL";
    public static String SHOP_ID="SHOP_ID";
    public static String SER_SHOP_ID="SER_SHOP_ID";
    public static String VID="VID";
    public static String SHOP_NAME="SHOP_NAME";
    public static String OWNER_NAME="OWNER_NAME";
    public static String MOBILE_NO="MOBILE_NO";
    public static String ALT_NO="ALT_NO";
    public static String VERIFICATION_STATUS="VERIFICATION_STATUS";
    public static String ALT_VERIFICATION_STATUS="ALT_VERIFICATION_STATUS";
    public static String BASE_VILLAGE_ID="BASE_VILLAGE_ID";
    public static String VILLAGE="VILLAGE";
    public static String SALE_ID="SALE_ID";
    public static String LATITUDE="LATITUDE";
    public static String LONGITUDE="LONGITUDE";
    public static String ACCURACY="ACCURACY";
    public static String CREATED="CREATED";
    public static String CREATED_BY="CREATED_BY";
    public static String UPDATED="UPDATED";
    public static String UPDATED_BY="UPDATED_BY";
    public static String SHOP_FIELD1="SHOP_FIELD1";
    public static String SHOP_FIELD2="SHOP_FIELD2";
    public static String SHOP_FIELD3="SHOP_FIELD3";
    public static String SHOP_FIELD4="SHOP_FIELD4";
    public static String SHOP_FIELD5="SHOP_FIELD5";


    private static final String SHOP_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(SHOP_ID )
                    .append( " integer primary key autoincrement , " ).append(VID).append(" integer, ")
                    .append(SER_SHOP_ID).append(" integer default 0, ").append(SHOP_NAME).append( " text , " ).append(OWNER_NAME)
                    .append( " text , " ).append(MOBILE_NO).append( " text , " ).append(ALT_NO)
                    .append( " text , " ).append(VERIFICATION_STATUS).append( " text , " ).append(ALT_VERIFICATION_STATUS)
                    .append( " text , " ).append(BASE_VILLAGE_ID).append( " integer , " ).append(VILLAGE)
                    .append( " text , " ).append(SALE_ID).append( " integer , " ).append(CREATED)
                    .append( " integer , " ).append(LATITUDE).append(" text , ").append(LONGITUDE).append(" text , ")
                    .append(ACCURACY).append(" text , ").append(CREATED_BY).append( " integer , " ).append(UPDATED)
                    .append( " integer , " ).append(UPDATED_BY).append( " integer , " ).append(SHOP_FIELD1)
                    .append( " text , " ).append(SHOP_FIELD2).append( " text , " ).append(SHOP_FIELD3)
                    .append( " text , " ).append(SHOP_FIELD4).append( " text , " ).append(SHOP_FIELD5)
                    .append( " text);").toString();

    private static final String SHOP_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return SHOP_TBL_CREATE;
    }

    public static String dropTable(){
        return SHOP_TBL_DROP;
    }
}
