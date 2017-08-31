package com.bizone.britannia.entities;

/**
 * Created by Sonam on 8/12/16.
 */
public class PurchaseEntity {
    public int p_id;
    public String stockiestName;
    public String stockiestNo;
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
    public String purchase_field1;
    public String purchase_field2;
    public String purchase_field3;
    public String purchase_field4;
    public String purchase_field5;

    @Override
    public String toString() {
        String retVal="p_id ="+p_id+" stockiestName ="+stockiestName+" stockiestNo ="+stockiestNo+"total ="+total +
                "device_id ="+device_id+" created ="+created+" created_by ="+created_by+" updated ="+updated+
                "updated_by ="+updated_by+" status ="+status+" approved_flag ="+approved_flag+"latitude = "+latitude+"longitude ="+longitude
                +"accuracy = "+accuracy+" purchase_field1 ="+purchase_field1+
                "purchase_field2 ="+purchase_field2+" purchase_field3 ="+purchase_field3+" purchase_field4 ="+purchase_field4+" purchase_field5 ="+purchase_field5;

        return retVal;
    }
}
