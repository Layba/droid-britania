package com.bizone.britannia.entities;

import java.io.Serializable;

/**
 * Created by siddhesh on 7/22/16.
 */
public class    SaleMetadataEntity implements Serializable {

    public int m_id;
    public int transaction_id;
    public int sale_id;
    public int fid;
    public String filePath;
    public int created;
    public String status;
    public String tag;

    @Override
    public String toString() {
        String retVal="m_id ="+m_id+" transaction_id ="+transaction_id+" sale_id ="+sale_id+" fid ="+fid
                +" filePath = "+filePath+" created ="+created +" status ="+status;
        return retVal;
    }
}
