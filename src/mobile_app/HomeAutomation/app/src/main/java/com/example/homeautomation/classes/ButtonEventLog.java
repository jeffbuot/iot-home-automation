package com.example.homeautomation.classes;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ButtonEventLog {

    public String uid;
    public String userName;
    public Date date;
    public boolean status;
    public String buttonName;

    public ButtonEventLog(String uid, String userName, Date date, boolean status, String buttonName) {
        this.uid = uid;
        this.userName = userName;
        this.date = date;
        this.status = status;
        this.buttonName = buttonName;
    }

    public ButtonEventLog() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("userName", userName);
        result.put("date", date);
        result.put("status", status);
        result.put("buttonName", buttonName);
        return result;
    }

    @Exclude
    public String getLogInfo(){
        return String.format("[%s] turned [%s] -> [%s]",userName,buttonName,(status?"ON":"OFF"));
    }

    @Exclude
    public String getDateString(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy\nHH:mm:ss");
        return formatter.format(date);
    }
}
