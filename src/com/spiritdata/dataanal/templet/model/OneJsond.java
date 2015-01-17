package com.spiritdata.dataanal.templet.model;

import com.spiritdata.jsonD.model.AccessJsondOne;

public class OneJsond extends AccessJsondOne {
    private static final long serialVersionUID = 336480238247137657L;

    private int tdid; //此Id是在templet中进行标识用的

    public int getTdid() {
        return tdid;
    }

    public void setTdid(int did) {
        this.tdid = did;
    }
}