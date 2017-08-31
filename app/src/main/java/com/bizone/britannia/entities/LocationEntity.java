package com.bizone.britannia.entities;

import java.io.Serializable;

/**
 * Created by sagar on 9/12/16.
 */

public class LocationEntity implements Serializable{

    public String latitude;
    public String longitude;
    public String accuracy;

    @Override
    public String toString() {
        String retVal = " latitude ="+latitude +" longitude ="+longitude
                +" accuracy = "+accuracy;
        return retVal;
    }
}
