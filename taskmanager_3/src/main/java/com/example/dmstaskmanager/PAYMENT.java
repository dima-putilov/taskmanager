package com.example.dmstaskmanager;

import java.io.Serializable;

/**
 * Created by dima on 10.03.2018.
 */

public class PAYMENT implements Serializable {

    public int credit_id;
    public int _id;

    public long date;
    public double summa;
    public double summa_credit;
    public double summa_procent;
    public double summa_addon;
    public double summa_plus;
    public double summa_minus;

    public PAYMENT( int credit_id, long date, double summa, double summa_credit, double summa_procent, double summa_addon, double summa_plus, double summa_minus){
        this.credit_id = credit_id;
        this.date           = date;
        this.summa          = summa;
        this.summa_credit   = summa_credit;
        this.summa_procent  = summa_procent;
        this.summa_addon    = summa_addon;
        this.summa_plus     = summa_plus;
        this.summa_minus    = summa_minus;

    }

    public PAYMENT(int credit_id){
        this.credit_id = credit_id;
    }

    public PAYMENT(){

    }

}
