package com.bizone.britannia.entities;

/**
 * Created by Sonam on 8/12/16.
 */
public class PurchaseSubOrderEntity {
    public int pso_id;
    public int p_id;
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
        String retVal="pso_id ="+pso_id+" p_id ="+p_id+" product_id ="+product_id+" qty ="+qty
                +" total = "+total+" so_field1 ="+so_field1 +" so_field2 ="+so_field2
                +" so_field3 = "+so_field3+" so_field4 ="+so_field4 +" so_field5 ="+so_field5;
        return retVal;
    }

}

