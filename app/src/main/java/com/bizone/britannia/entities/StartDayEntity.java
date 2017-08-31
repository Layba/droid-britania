package com.bizone.britannia.entities;

import java.io.Serializable;

/**
 * Created by Sonam on 22/2/17.
 */
public class StartDayEntity implements Serializable{

    public int startId;
    public String imagePath;
    public int startDate;
    public String remarks;
    public String latitude;
    public String longitude;
    public String accuracy;
    public String status;
    public long created;

    @Override
    public String toString() {
        String retVal=" imagePath = "+imagePath+" startDate ="+startDate +" remarks ="+remarks+
        " latitude = "+latitude+" longitude ="+longitude +" accuracy ="+accuracy;
        return retVal;
    }
}
