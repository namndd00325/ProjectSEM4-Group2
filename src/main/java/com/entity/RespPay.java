package com.entity;

public class RespPay {
    private int status;
    private String message;
    private long money;

    public RespPay() {
        this.status=0;
        this.message = "";
        this.money=0;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }
}
