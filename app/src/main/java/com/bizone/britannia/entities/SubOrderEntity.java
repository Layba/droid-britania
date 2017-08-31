package com.bizone.britannia.entities;

/**
 * Created by siddhesh on 7/22/16.
 */
public class SubOrderEntity {

    public int so_id;
    public int sale_id;
    public int product_id;
    public int qty;
    public String total;
    public String so_field1;
    public String so_field2;
    public String so_field3;
    public String so_field4;
    public String so_field5;

    @Override
    public String toString() {
        String retVal="so_id ="+so_id+" sale_id ="+sale_id+" product_id ="+product_id+" qty ="+qty
                +" total = "+total+" so_field1 ="+so_field1 +" so_field2 ="+so_field2
                +" so_field3 = "+so_field3+" so_field4 ="+so_field4 +" so_field5 ="+so_field5;
        return retVal;
    }

}
