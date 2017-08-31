package com.bizone.britannia.entities;

import java.io.Serializable;

/**
 * Created by siddhesh on 7/22/16.
 */
public class ShopEntity implements Serializable{

    public int shop_id;
    public int vid;
    public int ser_shopId;
    public String shop_name;
    public String owner_name;
    public String mobile_no;
    public String alt_no;
    public String verification_status;
    public String alt_verification_status;
    public int base_village_id;
    public String village;
    public int sale_id;
    public String latitude;
    public String longitude;
    public String accuracy;
    public int created;
    public int created_by;
    public int updated;
    public int updated_by;
    public String shop_field1;
    public String shop_field2 ;
    public String shop_field3;
    public String shop_field4;
    public String shop_field5;

    @Override
    public String toString() {
        String retVal="shop_id ="+shop_id+" vid ="+vid+" ser_shopId ="+ser_shopId+" shop_name ="+shop_name
                +" owner_name = "+owner_name+" mobile_no ="+mobile_no +" alt_no ="+alt_no +
                " verification_status = "+verification_status+" alt_verification_status ="+alt_verification_status +" base_village_id ="+base_village_id +
                " village ="+village+"sale_id ="+sale_id+"latitude = "+latitude+"longitude ="+longitude
                +"accuracy = "+accuracy+" created ="+created+" created_by ="+created_by+" updated ="+updated+
                " updated_by ="+updated_by+" shop_field1 ="+shop_field1+ "shop_field2 ="+shop_field2+" shop_field3 ="+shop_field3+
                " shop_field4 ="+shop_field4+" shop_field5 ="+shop_field5;
        return retVal;
    }

}
