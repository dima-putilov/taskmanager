package com.example.dmstaskmanager;

import java.io.Serializable;

// Class CREDIT, Screen #3
// Кредиты

public class CREDIT implements Serializable {

    public int _id;

    public String name;
    public long date;
    public double summa;
    public double summa_pay;
    public double procent;
    public int period;
    public int finish;

    public CREDIT( String name, long date, double summa){
        this.name    = name;
        this.date    = date;
        this.summa    = summa;
    }

    public CREDIT(){
        this._id    = -1;
        this.name    = "";
        this.date    = 0;
        this.summa    = 0;
        this.procent = 0;
        this.period = 0;
        this.summa_pay = 0;
        this.finish = 0;

    }

    public int GetID(){
        return _id;
    }

    public void SetParam(double procent, int period, double summa_pay){
        this.procent = procent;
        this.period = period;
        this.summa_pay = summa_pay;
    }

    public void SetFinish(boolean flag ){
        this.finish = flag?1:0;
    }
}
