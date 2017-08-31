package com.bizone.britannia.entities;

import com.bizone.britannia.Commons;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by siddhesh on 7/22/16.
 */
public class RoutePlanEntity implements Serializable {

    public int r_id;
    public int vid;
    public int uid;
    public String date;
    public int villageid_1;
    public int villageid_2;
    public int villageid_3;
    public int villageid_4;
    public int villageid_5;
    public String villagename_1;
    public String villagename_2;
    public String villagename_3;
    public String villagename_4;
    public String villagename_5;
    public int created;
    public int created_by;
    public String rp_field1;
    public String rp_field2;
    public String rp_field3;
    public String rp_field4;
    public String rp_field5;
    public String rp_field6;
    public String rp_field7;
    public String rp_field8;
    public String rp_field9;
    public String rp_field10;

    @Override
    public String toString() {
        String retVal="r_id ="+r_id+" vid ="+vid+" uid ="+uid+" date ="+date
                +" villageid_1 = "+villageid_1+" villageid_2 ="+villageid_2 +" villageid_3 ="+villageid_3 +
                " villageid_4 = "+villageid_4+" villageid_5 ="+villageid_5 +" villagename_1 ="+villagename_1 +
                " villagename_2 ="+villagename_2+"villagename_3 ="+villagename_3+" villagename_4 ="+villagename_4+
                " villagename_5 ="+villagename_5+" created ="+created+
                " created_by ="+created_by+" rp_field1 ="+rp_field1+ "rp_field2 ="+rp_field2+" rp_field3 ="+rp_field3+
                " rp_field4 ="+rp_field4+" rp_field5 ="+rp_field5+" rp_field6 ="+rp_field6+ "rp_field7 ="+rp_field7+
                " rp_field8 ="+rp_field8+ " rp_field9 ="+rp_field9+" rp_field10 ="+rp_field10;
        return retVal;
    }

    public static Comparator<RoutePlanEntity> DATE_COMPARATOR = new Comparator<RoutePlanEntity>() {
        @Override
        public int compare(RoutePlanEntity lhs, RoutePlanEntity rhs) {
            int retval=0;
            if(lhs==null && rhs==null){
                retval=0;
            }else if(lhs==null){
                retval=1;
            }else if(rhs==null){
                retval=-1;
            }else if(lhs.date==null || rhs.date==null || lhs.date.equals("") || rhs.date.equals("")){
                retval=0;
            }else{
                Date dateLhs = Commons.milliToDateObj(lhs.date);
                Date dateRhs = Commons.milliToDateObj(rhs.date);

                retval = dateLhs.compareTo(dateRhs);
            }

            return retval;
        }
    };
}

