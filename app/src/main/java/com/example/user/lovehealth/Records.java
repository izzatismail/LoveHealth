package com.example.user.lovehealth;

public class Records {
    private String name, heartrate, time, glucose, notes, date, eaten, criticalrate, criticalglucose, dateNtime;

    public Records(){
        //Default Constructor
    }

    //Normal Constructor
    public Records(String na, String h, String t, String d, String g, String n, String e, String cr, String cg, String dt){
        this.name = na;
        this.heartrate = h;
        this.time = t;
        this.date= d;
        this.glucose = g;
        this.notes = n;
        this.eaten = e;
        this.criticalrate = cr;
        this.criticalglucose = cg;
        this.dateNtime = dt;
    }

    //Getter method
    public String getName(){
        return  name;
    }

    public String getHeartrate() {
        return heartrate;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getGlucose() {
        return glucose;
    }

    public String getNotes() {
        return notes;
    }

    public String getEaten() {
        return eaten;
    }

    public String getCriticalrate() {
        return criticalrate;
    }

    public String getCriticalglucose() {
        return criticalglucose;
    }

    public String getDateNtime() {return dateNtime;}//End of Getter method
}
