package com.example.homeautomation.classes;

public class ChannelData {
    private String id;
    private String name;
    private boolean value;

    public ChannelData(String id, boolean value) {
        this.id = id;
        this.value = value;
        this.name = "Channel "+id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
