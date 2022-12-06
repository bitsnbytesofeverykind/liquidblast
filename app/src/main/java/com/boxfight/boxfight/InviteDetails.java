package com.boxfight.boxfight;

/**
 * Created by SHIVAM VYAS on 04-Oct-17.
 */

public class InviteDetails {
    private  String myUserId;
    private String oppoUserId;
    private  String oppoUserName;
    private  long counter;
    public String photoUrl;
    public long getCounter(){return  counter;}

    public String getMyUserId() {
        return myUserId;
    }

    public void setMyUserId(String myUserId) {
        this.myUserId = myUserId;
    }

    public String getOppoUserId() {
        return oppoUserId;
    }

    public void setOppoUserId(String oppoUserId) {
        this.oppoUserId = oppoUserId;
    }

    public void setCounter(long x){
        counter = x;
    }


    public String getOppoUserName() {
        return oppoUserName;
    }

    public void setOppoUserName(String oppoUserName) {
        this.oppoUserName = oppoUserName;
    }
}
