package org.app.mydukan.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by arpithadudi on 10/13/16.
 */

@SuppressWarnings("serial")
public class Record extends Object implements Serializable {
    private String recordId = "";
    private String productid = "";
    private String productname = "";
    private SupplierInfo supplierinfo;
    private ArrayList<RecordInfo> mRecordInfoList = new ArrayList<>();

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getProductname() {
        return productname;
    }

    public String getProductid() {
        return productid;
    }

    public SupplierInfo getSupplierInfo() {
        return supplierinfo;
    }

    public ArrayList<RecordInfo> getRecordList() {
        return mRecordInfoList;
    }

    public void setRecordList(ArrayList<RecordInfo> list) {
        this.mRecordInfoList = list;
    }
}
