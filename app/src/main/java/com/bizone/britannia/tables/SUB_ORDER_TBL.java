package com.bizone.britannia.tables;

/**
 * Created by siddhesh on 7/22/16.
 */
public class SUB_ORDER_TBL {

    private SUB_ORDER_TBL(){

    };
    public static String TABLE_NAME="SUB_ORDER_TBL";
    public static String SO_ID="SO_ID";
    public static String SALE_ID="SALE_ID";
    public static String PRODUCT_ID="PRODUCT_ID";
    public static String QTY="QTY";
    public static String TOTAL="TOTAL";
    public static String SO_FIELD1="SO_FIELD1";
    public static String SO_FIELD2="SO_FIELD2";
    public static String SO_FIELD3="SO_FIELD3";
    public static String SO_FIELD4="SO_FIELD4";
    public static String SO_FIELD5="SO_FIELD5";


    private static final String SUB_ORDER_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append(SO_ID )
                    .append( " integer primary key autoincrement , " ).append(SALE_ID)
                    .append( " integer , " ).append(PRODUCT_ID)
                    .append( " integer , " ).append(QTY)
                    .append( " integer , " ).append(TOTAL).append( " text , " ).append(SO_FIELD1)
                    .append( " text , " ).append(SO_FIELD2).append( " text , " ).append(SO_FIELD3)
                    .append( " text , " ).append(SO_FIELD4).append( " text , " ).append(SO_FIELD5)
                    .append( " text);").toString();

    private static final String SUB_ORDER_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return SUB_ORDER_TBL_CREATE;
    }

    public static String dropTable(){
        return SUB_ORDER_TBL_DROP;
    }
}
