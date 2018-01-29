package com.example.yb.hstt.bean;

/**
 * Created by tfhr on 2018/1/17.
 */

public class Friend {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public String toString() {
        return getName();
    }
}
