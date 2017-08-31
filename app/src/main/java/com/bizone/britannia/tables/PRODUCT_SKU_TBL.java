package com.bizone.britannia.tables;

/**
 * Created by siddhesh on 7/22/16.
 */
public class PRODUCT_SKU_TBL {

    private PRODUCT_SKU_TBL(){

    };
    public static String TABLE_NAME="PRODUCT_SKU_TBL";
    public static String PRODUCT_ID="PRODUCT_ID";
    public static String NAME="NAME";
    public static String SKU="SKU";
    public static String MRP="MRP";
    public static String SELLING_PRICE="SELLING_PRICE";
    public static String COST_PRICE="COST_PRICE";
    public static String MIN_QTY="MIN_QTY";
    public static String STATE="STATE";
    public static String STATUS="STATUS";


    private static final String PRODUCT_SKU_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(PRODUCT_ID )
                    .append( " integer primary key , " ).append(NAME).append( " text , " ).append(SKU).append( " text , " )
                    .append(MRP).append( " text , " ).append(SELLING_PRICE).append( " text , " ).append(STATE)
                    .append( " integer , " ).append(STATUS).append(" text, ").append(COST_PRICE).append(" text, ").append(MIN_QTY)
                    .append( " text);").toString();

    private static final String PRODUCT_SKU_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return PRODUCT_SKU_TBL_CREATE;
    }

    public static String dropTable(){
        return PRODUCT_SKU_TBL_DROP;
    }
}
