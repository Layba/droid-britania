package com.bizone.britannia.entities;

/**
 * Created by siddhesh on 8/3/16.
 */
public class UploadSaleEntity {
    public String shopname;
    public Boolean isChecked;
    public int created;
    public int saleId;
    public String saleType;
    public String time;

    @Override
    public String toString() {
        String retVal="shopname ="+shopname+" isChecked ="+isChecked+" created date ="+created+" sale Id ="+saleId
                +" sale Type= "+saleType;
        return retVal;
    }
}
