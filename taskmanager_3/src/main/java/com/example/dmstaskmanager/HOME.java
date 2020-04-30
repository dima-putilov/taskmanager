package com.example.dmstaskmanager;

import java.io.Serializable;

/**
 * Created by dima on 09.03.2018.
 */

public class HOME implements Serializable{

    public String _id;
    public String name;
    public String adres;
    public boolean isCounter;
    public int day_beg;
    public int day_end;

    public void HOME (String pName, String pAdres, boolean pIsCounter, int pDay_beg , int pDay_end){
        name        = pName;
        adres       = pAdres;
        isCounter   = pIsCounter;
        day_beg     = pDay_beg;
        day_end     = pDay_end;
    }

}
