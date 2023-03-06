package com.example.scalorie_v1;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.lang.Math;


public class user {
    public int age;
    public int height;
    public int gender;
    public int weight;
    public int bmr;
    public int points;
    public String urlProfile;
    public  int StartWeight;
    public String dayOfStart;
    public String userName;
    public String currentDay;
    public int dayCal;
    public String gmail;
    public user(int age, int height, int gender, int weight, int bmr, String gmail)
    {
        this.age = age;
        this.height = height;
        this.gender =gender;
        this.weight = weight;
        this.bmr = bmr;
        this.points = 0;
        this.urlProfile = "";
        this.StartWeight = weight;
        DateFormat df = new SimpleDateFormat("d MMM");
        this.currentDay = df.format(Calendar.getInstance().getTime());
        this.dayOfStart = df.format(Calendar.getInstance().getTime());
        int rand = (int) (Math.random() * (10000000+ 1));
        this.userName = "user"+ String.valueOf(rand);
        this.dayCal = bmr;
        this.gmail = gmail;
    }

}
