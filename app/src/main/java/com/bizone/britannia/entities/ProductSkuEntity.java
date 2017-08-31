package com.bizone.britannia.entities;

/**
 * Created by siddhesh on 7/22/16.
 */
public class ProductSkuEntity {

    public int product_id;
    public String name;
    public String sku;
    public String mrp;
    public String selling_price;
    public int state;
    public String status;

    @Override
    public String toString() {
        String retVal="product_id ="+product_id+" name ="+name+" sku ="+sku+" mrp ="+mrp
                +" selling price = "+selling_price+" state ="+state +" status ="+status;
        return retVal;
    }
}
