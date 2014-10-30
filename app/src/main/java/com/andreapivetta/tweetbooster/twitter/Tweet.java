package com.andreapivetta.tweetbooster.twitter;


public class Tweet implements Comparable<Tweet> {

    public String tweet;
    public String source;
    private int minute;
    private int hour;
    private int day;
    private int month;
    private int year;

    public Tweet(String tweet, int minute, int hour, int day, int month, int year) {
        this.tweet = tweet;
        this.minute = minute;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Tweet(String tweet, String source) {
        this.tweet = tweet;
        this.source = source;
    }

    public String getTweet() {
        return tweet;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public int compareTo(Tweet another) {

        if (year < another.getYear()) {
            return -1;
        } else {
            if (year > another.getYear()) return 1;
            if (month < another.getMonth()) {
                return -1;
            } else {
                if (month > another.getMonth()) return 1;
                if (day < another.getDay()) {
                    return -1;
                } else {
                    if (day > another.getDay()) return 1;
                    if (hour < another.getHour()) {
                        return -1;
                    } else {
                        if (hour > another.getHour()) return 1;
                        if (minute < another.getMinute()) {
                            return -1;
                        } else {
                            if (minute > another.getMinute()) return 1;
                            return 0;
                        }
                    }
                }
            }
        }
    }

}
