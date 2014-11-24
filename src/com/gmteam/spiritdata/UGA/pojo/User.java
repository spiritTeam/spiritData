package com.gmteam.spiritdata.UGA.pojo;

import java.sql.Timestamp;

import com.gmteam.framework.UGA.UgaUser;

/**
 * 注册用户
 * @author wh
 */
public class User extends UgaUser {
    private static final long serialVersionUID = -6658966661407738276L;
    private String mailAdress; //用户邮箱
    private String nickName; //昵称
    private int userType; //用户分类：1=自然人用户;2=组织用户
    private String descn; //用户描述
    private Timestamp CTime; //记录创建时间
    private Timestamp lmTime; //最后修改时间:last modify time
    private int userState;//用户状态，0~2
    private String validataSequence;//存储邮箱验证码
    public String getValidataSequence() {
        return validataSequence;
    }
    public void setValidataSequence(String validataSequence) {
        this.validataSequence = validataSequence;
    }
    public int getUserState() {
        return userState;
    }
    public void setUserState(int userState) {
        this.userState = userState;
    }
    
    public String getMailAdress() {
        return mailAdress;
    }
    public void setMailAdress(String mailAdress) {
        this.mailAdress = mailAdress;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getDescn() {
        return descn;
    }
    public void setDescn(String descn) {
        this.descn = descn;
    }
    public int getUserType() {
        return userType;
    }
    public void setUserType(int userType) {
        this.userType = userType;
    }
    public Timestamp getCTime() {
        return CTime;
    }
    public void setCTime(Timestamp cTime) {
        CTime = cTime;
    }
    public Timestamp getLmTime() {
        return lmTime;
    }
    public void setLmTime(Timestamp lmTime) {
        this.lmTime = lmTime;
    }
}