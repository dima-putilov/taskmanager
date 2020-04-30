package com.example.dmstaskmanager.classes;

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

    public static final int TASK_TYPE_CREDIT    = 1;     // Кредит
    public static final int TASK_TYPE_FLAT      = 2;     // Квартплата
    public static final int TASK_TYPE_COUNTER   = 3;     // Показания счетчиков
    public static final int TASK_TYPE_ARENDA    = 10;     // Аренда
    public static final int TASK_TYPE_OTHER     = 100;   // Прочие задачи

    public int _id;
    public int type;
    public String name;
    public long date;
    public double summa;
    public boolean finish;

    public int parent_id;


    public static class Builder {
        // Обязательные параметры
        public int type;

        // Необязательные параметры
        public String   name        = "";
        public long     date        = 0;
        public double   summa       = 0;
        public int      parent_id   = -1;
        public boolean  finish      = false;

        public Builder(int type) {
            this.type = type;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDate(long date) {
            this.date = date;
            return this;
        }

        public Builder setSumma( double summa) {
            this.summa = summa;
            return this;
        }

        public Builder setParentID( int parent_id) {
            this.parent_id = parent_id;
            return this;
        }

        public Builder setFinish(boolean finish) {
            this.finish = finish;
            return this;
        }

        public TASK build() {
            return new TASK(this);
        }
    }

    private TASK(Builder builder) {
        _id = -1; // по умолчанию
        type = builder.type;
        name = builder.name;
        date = builder.date;
        summa = builder.summa;
        parent_id = builder.parent_id;
        finish = builder.finish;
    }
/*
    public TASK(int pType, String pName, long pDate, double pSumma){
        type    = pType;
        name    = pName;
        date    = pDate;
        summa   = pSumma;
    }

    public TASK(int pType, int pParent_id, String pName, long pDate, double pSumma){
        type    = pType;
        name    = pName;
        date    = pDate;
        summa   = pSumma;
        parent_id = pParent_id;
    }
*/

    public TASK(){
        this._id = -1;
    }


    public void setFinish(boolean flag){
        this.finish = flag;
    }

    public TASK(Task task){
        this._id = task.getId();
        this.type = task.getType().getType();
        this.name = task.getName();
        this.date = task.getDate();
        this.summa = task.getSumma();
        this.parent_id = task.getParentId();
        this.finish = task.getFinish();
    }

}



