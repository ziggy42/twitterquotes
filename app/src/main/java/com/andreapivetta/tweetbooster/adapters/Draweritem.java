package com.andreapivetta.tweetbooster.adapters;


public class Draweritem {
    private String title;
    private int icon;

    public Draweritem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return this.title;
    }

    public int getIcon() {
        return this.icon;
    }

}
