package com.bizone.britannia.entities;

/**
 * Created by siddhesh on 7/22/16.
 */
public class VillageEntity {

    public int village_id;
    public String village;
    public String state;
    public int created;
    public int created_by;

    @Override
    public String toString() {
        String retVal="village_id ="+village_id+" village ="+village+" state ="+state+" created ="+created
                +" created_by = "+created_by;
        return retVal;
    }
}
