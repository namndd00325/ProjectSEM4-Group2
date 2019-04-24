package com.entity;

import com.util.Generate;

public class Session {
    private String jsession;
    private long createTime;
    private String phone;


    public Session() {
    }

    public Session(String phone) {
        this.jsession = Generate.randomAlphaNumeric(25);
        this.phone = phone;
        this.createTime = System.currentTimeMillis();
    }

    public String getJsession() {
        return jsession;
    }

    public void setJsession(String jsession) {
        this.jsession = jsession;
    }

    public long getCreateTime() {
        return createTime;
    }



    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
