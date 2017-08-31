package com.bizonesoft.ace.tables;

/**
 * Created by sagar on 2/3/16.
 */
public class IMPLE_IMAGE_TBL {

    private IMPLE_IMAGE_TBL(){

    };
    public static String TABLE_NAME="IMPLE_IMAGE_TBL";
    public static String II_ID="II_ID";
    public static String II_IMAGE="II_IMAGE";
    public static String II_CAP_TIME="II_CAP_TIME";
    public static String II_DEVICE_ID="II_DEVICE_ID";
    public static String II_ADAP_CREA_ELEM_ID="II_ADAP_CREA_ELEM_ID";
    public static String II_STATUS="II_STATUS";

    private static final String IMPLE_IMAGE_TBL_CREATE =
            (new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( ")
                    .append(II_ID).append( " integer primary key autoincrement, " ).append(II_IMAGE).append(" text, ")
                    .append(II_CAP_TIME).append(" text, ").append(II_DEVICE_ID).append(" text, ").append(II_ADAP_CREA_ELEM_ID).append(" text, ")
                    .append(II_STATUS).append(" text);").toString();

    private static final String IMPLE_IMAGE_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return IMPLE_IMAGE_TBL_CREATE;
    }

    public static String dropTable(){
        return IMPLE_IMAGE_TBL_DROP;
    }

}
