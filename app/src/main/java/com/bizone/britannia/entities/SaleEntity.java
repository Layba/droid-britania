package com.bizone.britannia.entities;

/**
 * Created by siddhesh on 7/22/16.
 */
public class SaleEntity {

    public int sale_id;
    public int vid;
    public int transaction_id;
    public String subtotal;
    public String tax;
    public String discount;
    public String total;
    public String device_id;
    public String latitude;
    public String longitude;
    public String accuracy;
    public int created;
    public int created_by;
    public int updated;
    public int updated_by;
    public String status;
    public String approved_flag;
    public String sale_field1;
    public String sale_field2;
    public String sale_field3;
    public String sale_field4;
    public String sale_field5;
    public String sale_type;

    @Override
    public String toString() {
        String retVal="sale_id ="+sale_id+" vid ="+vid+" transaction_id ="+transaction_id+" subtotal ="+subtotal
                +" tax = "+tax+" discount ="+discount +" total ="+total +
                "device_id ="+device_id+" created ="+created+" created_by ="+created_by+" updated ="+updated+
                "updated_by ="+updated_by+" status ="+status+" approved_flag ="+approved_flag+"latitude = "+latitude+"longitude ="+longitude
                +"accuracy = "+accuracy+" sale_field1 ="+sale_field1+
                "sale_field2 ="+sale_field2+" sale_field3 ="+sale_field3+" sale_field4 ="+sale_field4+" sale_field5 ="+sale_field5+" sale_type ="+sale_type;

        return retVal;
    }
}
