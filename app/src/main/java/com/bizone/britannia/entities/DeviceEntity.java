package com.bizone.britannia.entities;

/**
 * Created by siddhesh on 7/22/16.
 */
public class DeviceEntity {

    public int p_id;
    public String device_id;
    public String imei;
    public String model;
    public int uid;
    public int created;

    @Override
    public String toString() {
        String retVal="p_id ="+p_id+" device_id ="+device_id+" imei ="+imei+" model ="+model
                +" uid = "+uid+" created ="+created;
        return retVal;
    }
}
