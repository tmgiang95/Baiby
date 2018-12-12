package com.example.giangtm.baiby.chat.models;

import java.io.Serializable;

public class Message implements Serializable {

    private int type;
    private String message;
    private long time;

    public Message(int type, String message, long time) {
        this.type = type;
        this.message = message;
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
