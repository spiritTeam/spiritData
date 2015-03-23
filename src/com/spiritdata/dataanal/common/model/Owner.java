package com.spiritdata.dataanal.common.model;

import java.io.Serializable;

/**
 * 所有者对象，封装了ownerId和ownerType
 * @author wh
 */
public class Owner implements Serializable {
    private static final long serialVersionUID = -1970271589243412626L;

    private int ownerType; //模式所对应的所有者类型（1=注册用户;2=非注册用户(session)）
    private String ownerId; //所有者标识（可能是用户id，也可能是SessionID）

    public int getOwnerType() {
        return ownerType;
    }
    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}