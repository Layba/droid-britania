package com.bizonesoft.ace.tables;

/**
 * Created by sagar on 2/3/16.
 */
public class IMPLE_ELEM_DETAIL_TBL {

    private IMPLE_ELEM_DETAIL_TBL(){

    };
    public static String TABLE_NAME="IMPLE_ELEM_DETAIL_TBL";
    public static String IMPLE_ELEM_ID="IMPLE_ELEM_ID";
    public static String IMPLE_PR_BR_ID="IMPLE_PR_BR_ID";
    public static String IMPLE_PRO_ID="IMPLE_PRO_ID";
    public static String IMPLE_BR_ID="IMPLE_BR_ID";
    public static String IMPLE_ELE_TYP="IMPLE_ELE_TYP";
    public static String REC_ID="REC_ID";
    public static String REC_ELE_ID="REC_ELE_ID";
    public static String REC_HEIGHT="REC_HEIGHT";
    public static String REC_WIDTH="REC_WIDTH";
    public static String REC_IMAGE="REC_IMAGE";
    public static String REC_FULL_IMAGE="REC_FULL_IMAGE";
    public static String REC_IMAGE_LOCAL="REC_IMAGE_LOCAL";
    public static String REC_FULL_IMAGE_LOCAL="REC_FULL_IMAGE_LOCAL";
    public static String REC_STATUS="REC_STATUS";
    public static String REC_ELEM_STATUS="REC_ELEM_STATUS";
    public static String REC_DATE="REC_DATE";
    public static String REC_ELEM_IDENTIFIER="REC_ELEM_IDENTIFIER";
    public static String REC_COMMENTS="REC_COMMENTS";
    public static String CREA_IMAGE="CREA_IMAGE";
    public static String CREA_FULL_IMAGE="CREA_FULL_IMAGE";
    public static String CREA_DATE="CREA_DATE";
    public static String CREA_COMMENT="CREA_COMMENT";
    public static String ADAP_CREA_ID="ADAP_CREA_ID";
    public static String ADAP_CREA_IMAGE="ADAP_CREA_IMAGE";
    public static String ADAP_CREA_FULL_IMAGE="ADAP_CREA_FULL_IMAGE";
    public static String ADAP_CREA_IMAGE_LOCAL="ADAP_CREA_IMAGE_LOCAL";
    public static String ADAP_CREA_FULL_IMAGE_LOCAL="ADAP_CREA_FULL_IMAGE_LOCAL";
    public static String ADAP_CREA_DATE="ADAP_CREA_DATE";
    public static String ADAP_CREA_COMMENT="ADAP_CREA_COMMENT";
    public static String ADAP_CREA_ELEMENT_ID="ADAP_CREA_ELEMENT_ID";
    public static String ADAP_CREA_ELEMENT_STATUS="ADAP_CREA_ELEMENT_STATUS";
    public static String IMPLE_ID="IMPLE_ID";

    private static final String IMPLE_ELEM_DETAIL_TBL_CREATE =
            (new StringBuffer()).append("create table ").append( TABLE_NAME ).append(" ( ").append(
                    IMPLE_ELEM_ID ).append(" integer primary key autoincrement, " ).append(IMPLE_PR_BR_ID)
                    .append(" long, ").append(REC_ID).append(" long, ").append(REC_ELE_ID).append(" long, ").append(IMPLE_PRO_ID).append(" long, ")
                    .append(IMPLE_BR_ID).append(" long, ").append(IMPLE_ELE_TYP).append(" long, ")
                    .append(REC_HEIGHT).append(" text, ").append(REC_WIDTH).append(" text, ").append(REC_IMAGE).append(" text, ")
                    .append(REC_FULL_IMAGE).append(" text, ").append(REC_IMAGE_LOCAL).append(" text, ").append(REC_FULL_IMAGE_LOCAL).append(" text, ")
                    .append(REC_STATUS).append(" text, ").append(REC_ELEM_STATUS).append(" text, ").append(REC_DATE).append(" text, ")
                    .append(REC_ELEM_IDENTIFIER).append(" text, ").append(REC_COMMENTS).append(" text, ").append(CREA_IMAGE).append(" text, ")
                    .append(CREA_FULL_IMAGE).append(" text, ").append(CREA_DATE).append(" text, ").append(CREA_COMMENT).append(" text, ")
                    .append(ADAP_CREA_ID).append(" text, ").append(ADAP_CREA_IMAGE).append(" text, ").append(ADAP_CREA_FULL_IMAGE).append(" text, ")
                    .append(ADAP_CREA_IMAGE_LOCAL).append(" text, ").append(ADAP_CREA_FULL_IMAGE_LOCAL).append(" text, ")
                    .append(ADAP_CREA_DATE).append(" text, ").append(ADAP_CREA_COMMENT).append(" text, ").append(ADAP_CREA_ELEMENT_ID).append(" text, ")
                    .append(ADAP_CREA_ELEMENT_STATUS).append(" text, ").append(IMPLE_ID).append(" integer);").toString();

    private static final String IMPLE_ELEM_DETAIL_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static String createTable(){
        return IMPLE_ELEM_DETAIL_TBL_CREATE;
    }

    public static String dropTable(){
        return IMPLE_ELEM_DETAIL_TBL_DROP;
    }


}
