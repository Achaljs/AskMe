package com.example.askit;

import com.google.firebase.Timestamp;

public class modleClass {

    public static String sentByMe="me";
   public static   String sentByBot="bot";

    String message,sentBy;
     Timestamp timestamp;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public modleClass(){

}
    public modleClass(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    public modleClass(String message, String sentBy,Timestamp timestamp) {
        this.message = message;
        this.sentBy = sentBy;
        this.timestamp=timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
}
