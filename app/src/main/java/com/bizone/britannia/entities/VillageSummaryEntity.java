package com.bizone.britannia.entities;

/**
 * Created by sagar on 13/12/16.
 */

public class VillageSummaryEntity {

    public int village_summary_id;
    public int village_id;
    public int sale_id;

    @Override
    public String toString() {
        String retVal="village_id ="+village_id+" village_summary_id ="+village_summary_id+" sale_id ="
                +sale_id;
        return retVal;
    }

}
