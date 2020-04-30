package com.example.dmstaskmanager;

import java.io.Serializable;

/**
 * Created by dima on 09.03.2018.
 */

// Class TASK, Screen #1
// Задачи, Экран №1 - текущие задачи
// создаются автоматически
// Реквизит type может принимать значения:
// 1 - Квартплата: ввод показателей счетчиков (Задается в спр. Квартиры, например, с 20-25 число)
// 2 - Квартплата: оплата (с 5-10 число каждого месяца)
// 10 - Кредит (за 7 дн. до плановой даты платежа)

public class TASK implements Serializable{

    public String _id;
    public int type;
    public String name;
    public long date;
    public double summa;
    public int finish;

    public TASK(int pType, String pName, long pDate, double pSumma){
        type    = pType;
        name    = pName;
        date    = pDate;
        summa   = pSumma;
    }

}



