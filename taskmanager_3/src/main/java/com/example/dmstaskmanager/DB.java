package com.example.dmstaskmanager;;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Element;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DB {

    public static final int TASK_TYPE_CREDIT=1;
    public static final int TASK_TYPE_FLAT=2;
    public static final int TASK_TYPE_COUNTER=3;

    private static final String DB_NAME = "DMS_TASK";
    private static final int DB_VERSION = 7;


    // == Таблица ЗАДАЧИ ==
    private static final String DB_TASK_TABLE = "TASK";

    public static final String CL_ID = "_id";

    public static final String CL_TASK_TYPE = "type";
    public static final String CL_TASK_NAME = "name";
    public static final String CL_TASK_DATE = "date";
    public static final String CL_TASK_SUMMA = "summa";
    public static final String CL_TASK_FINISH = "finish";

    private static final String DB_TASK_CREATE =
            "create table " + DB_TASK_TABLE + "(" +
                    CL_ID + " integer primary key autoincrement, " +
                    CL_TASK_TYPE + " integer, " +
                    CL_TASK_NAME + " text," +
                    CL_TASK_DATE + " long," +
                    CL_TASK_SUMMA + " long," +
                    CL_TASK_FINISH + " integer" +
                    ");";

    private static final String DB_DROP =
            "drop table "+DB_TASK_TABLE+";";


    // == Таблица КВАРТИРЫ ==
    private static final String DB_FLAT_TABLE = "FLAT";

    public static final String CL_FLAT_NAME = "name";
    public static final String CL_FLAT_ADRES = "adres";
    public static final String CL_FLAT_ISCOUNTER = "isCounter";
    public static final String CL_FLAT_DAY_BEG = "day_beg";
    public static final String CL_FLAT_DAY_END = "day_end";

    private static final String DB_FLAT_CREATE =
            "create table " + DB_FLAT_TABLE + "(" +
                    CL_ID + " integer primary key autoincrement, " +
                    CL_FLAT_NAME + " text," +
                    CL_FLAT_ADRES + " text," +
                    CL_FLAT_ISCOUNTER + " tinyint(1) default 0," +
                    CL_FLAT_DAY_BEG + " tinyint(1)," +
                    CL_FLAT_DAY_END + " tinyint(1)" +
                    ");";

    private static final String DB_FLAT_DROP =
            "drop table "+DB_FLAT_TABLE+";";


    // == Таблица КРЕДИТЫ ==
    private static final String DB_CREDIT_TABLE = "CREDIT";

    public static final String CL_CREDIT_NAME = "name";
    public static final String CL_CREDIT_DATE = "date";
    public static final String CL_CREDIT_SUMMA = "summa";
    public static final String CL_CREDIT_SUMMA_PAY = "summa_pay";
    public static final String CL_CREDIT_PROCENT = "procent";
    public static final String CL_CREDIT_PERIOD = "period";
    public static final String CL_CREDIT_FINISH = "finish";

    private static final String DB_CREDIT_CREATE =
            "create table " + DB_CREDIT_TABLE + "(" +
                    CL_ID + " integer primary key autoincrement, " +
                    CL_CREDIT_NAME + " text," +
                    CL_CREDIT_DATE + " long," +
                    CL_CREDIT_SUMMA + " double," +
                    CL_CREDIT_SUMMA_PAY + " double," +
                    CL_CREDIT_PERIOD + " integer, " +
                    CL_CREDIT_PROCENT + " double," +
                    CL_CREDIT_FINISH + " tinyint(1)" +
                    ");";

    private static final String DB_CREDIT_DROP =
            "drop table "+DB_CREDIT_TABLE+";";


    // == Таблица ГРАФИК ==
    private static final String DB_GRAPHIC_TABLE = "GRAPHIC";

    public static final String CL_GRAPHIC_ID_CREDIT = "credit_id";
    public static final String CL_GRAPHIC_DATE = "date";
    public static final String CL_GRAPHIC_REST = "rest";
    public static final String CL_GRAPHIC_SUMMA = "summa";
    public static final String CL_GRAPHIC_SUMMA_CREDIT = "summa_credit";
    public static final String CL_GRAPHIC_SUMMA_PROCENT = "summa_procent";
    public static final String CL_GRAPHIC_SUMMA_ADDON = "summa_addon";
    public static final String CL_GRAPHIC_SUMMA_PLUS = "summa_plus";
    public static final String CL_GRAPHIC_SUMMA_MINUS = "summa_minus";
    public static final String CL_GRAPHIC_DONE = "done";

    private static final String DB_GRAPHIC_CREATE =
            "create table " + DB_GRAPHIC_TABLE + "(" +
                    CL_ID + " integer primary key autoincrement, " +
                    CL_GRAPHIC_ID_CREDIT + " integer," +
                    CL_GRAPHIC_REST + " double," +
                    CL_GRAPHIC_DATE + " long, " +
                    CL_GRAPHIC_SUMMA + " double," +
                    CL_GRAPHIC_SUMMA_CREDIT+ " double," +
                    CL_GRAPHIC_SUMMA_PROCENT + " double," +
                    CL_GRAPHIC_SUMMA_ADDON+ " double," +
                    CL_GRAPHIC_SUMMA_PLUS+ " double," +
                    CL_GRAPHIC_SUMMA_MINUS+ " double," +
                    CL_GRAPHIC_DONE + " tinyint(1)" +
                    ");";

    private static final String DB_GRAPHIC_DROP =
            "drop table "+DB_GRAPHIC_TABLE+";";

    // == Таблица ПЛАТЕЖИ ==
    private static final String DB_PAYMENT_TABLE = "PAYMENT";

    public static final String CL_PAYMENT_ID_CREDIT = "credit_id";
    public static final String CL_PAYMENT_DATE = "date";
    public static final String CL_PAYMENT_SUMMA = "summa";
    public static final String CL_PAYMENT_SUMMA_CREDIT = "summa_credit";
    public static final String CL_PAYMENT_SUMMA_PROCENT = "summa_procent";
    public static final String CL_PAYMENT_SUMMA_ADDON = "summa_addon";
    public static final String CL_PAYMENT_SUMMA_PLUS = "summa_plus";
    public static final String CL_PAYMENT_SUMMA_MINUS = "summa_minus";

    private static final String DB_PAYMENT_CREATE =
            "create table " + DB_PAYMENT_TABLE + "(" +
                    CL_ID + " integer primary key autoincrement, " +
                    CL_PAYMENT_ID_CREDIT + " integer," +
                    CL_PAYMENT_DATE + " long, " +
                    CL_PAYMENT_SUMMA + " double," +
                    CL_PAYMENT_SUMMA_CREDIT+ " double," +
                    CL_PAYMENT_SUMMA_PROCENT + " double," +
                    CL_PAYMENT_SUMMA_ADDON+ " double," +
                    CL_PAYMENT_SUMMA_PLUS+ " double," +
                    CL_PAYMENT_SUMMA_MINUS+ " double" +
                    ");";

    private static final String DB_PAYMENT_DROP =
            "drop table "+DB_PAYMENT_TABLE+";";

    ///////////////////////////////////////////////////

    public static final String CL_RESULT_CREDIT_ID = "CREDIT_id";
    public static final String CL_RESULT_CREDIT_DATE = "CREDIT_date";
    public static final String CL_RESULT_CREDIT_NAME = "CREDIT_name";
    public static final String CL_RESULT_CREDIT_SUMMA = "CREDIT_summa";
    public static final String CL_RESULT_CREDIT_SUMMA_PAY = "CREDIT_summa_pay";
    public static final String CL_RESULT_CREDIT_PROCENT = "CREDIT_procent";
    public static final String CL_RESULT_CREDIT_PERIOD = "CREDIT_period";
    public static final String CL_RESULT_CREDIT_FINISH = "CREDIT_finish";

    public static final String CL_RESULT_GRAPHIC_DATE = "GRAPHIC_date";
    public static final String CL_RESULT_GRAPHIC_SUMMA = "GRAPHIC_summa";
    public static final String CL_RESULT_GRAPHIC_SUMMA_CREDIT = "GRAPHIC_summa_credit";
    public static final String CL_RESULT_GRAPHIC_SUMMA_PROCENT = "GRAPHIC_summa_procent";

    public static final String CL_RESULT_PAYMENT_SUMMA = "PAYMENT_summa";
    public static final String CL_RESULT_PAYMENT_SUMMA_CREDIT = "PAYMENT_summa_credit";
    public static final String CL_RESULT_PAYMENT_SUMMA_PROCENT = "PAYMENT_summa_procent";

    public static final String CL_RESULT_PAYMENT_DATE_LAST_PAY= "PAYMENT_date_last_pay";
    public static final String CL_RESULT_PAYMENT_ADDON = "PAYMENT_summa_addon";
    public static final String CL_RESULT_PAYMENT_SUMMA_PLUS = "PAYMENT_summa_plus";
    public static final String CL_RESULT_PAYMENT_SUMMA_MINUS = "PAYMENT_summa_minus";

    public static final String CL_RESULT_RESULT_REST = "RESULT_rest";
    public static final String CL_RESULT_RESULT_PROCENT = "RESULT_procent";
    public static final String CL_RESULT_RESULT_FIN_RES = "RESULT_fin_res";

    ///////////////////////////////////////////////////

    public static final String ATTR_DONE            = "done";
    public static final String ATTR_REST            = "rest";
    public static final String ATTR_DATE            = "date";
    public static final String ATTR_SUMMA           = "summa";
    public static final String ATTR_SUMMA_CREDIT    = "summa_credit";
    public static final String ATTR_SUMMA_PROCENT   = "summa_procent";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    //  Статические методы

    public static String round2Str(double value, int digits){
        return (new BigDecimal(""+value).setScale(digits, BigDecimal.ROUND_HALF_UP)).toString();
    }

    public static String round2Str(String value, int digits){
        if(value.isEmpty()) value="0";
        return (new BigDecimal(value).setScale(digits, BigDecimal.ROUND_HALF_UP)).toString();
    }

    public static BigDecimal round2Dec(String value, int digits){
        if(value.isEmpty()) value="0";
        return new BigDecimal(value).setScale(digits, BigDecimal.ROUND_HALF_UP);
    }

    public void autoTask() {
        // Кредиты

        Log.d("DMS","AutoTask /CREDIT/");

        String sqlQuery="" +
                " SELECT GRAPHIC.credit_id as credit_id, " +
                " CREDIT.name as credit_name, " +
                " GRAPHIC.date as date, " +
                " GRAPHIC.summa as summa, " +
                " GRAPHIC.summa_credit as summa_credit, " +
                " GRAPHIC.summa_procent as summa_procent " +
                " from  GRAPHIC" +
                "       inner join ("+
                "           SELECT GRAPHIC.credit_id as credit_id, " +
                "                  min(GRAPHIC.date) as mindate " +
                "                  from GRAPHIC as GRAPHIC " +
                "                       inner join  (SELECT p.credit_id, " +
                "                                    max(p.date) as maxdatepay " +
                "                                    from PAYMENT as p" +
                "                                    group by p.credit_id) as payment" +
                "                       on GRAPHIC.credit_id = payment.credit_id" +
                "                                 and GRAPHIC.date > payment.maxdatepay " +
                "                   group by GRAPHIC.credit_id" +
                "           ) as t" +
                "      on  GRAPHIC.credit_id = t.credit_id" +
                "        and GRAPHIC.date = t.mindate" +
                "      inner join CREDIT as CREDIT" +
                "       on  GRAPHIC.credit_id = CREDIT._id" +
                "";

        Cursor c = mDB.rawQuery(sqlQuery, null);

        Date curdate = new Date();

        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    // Количество дней между датами в миллисекундах
                    long gr_date =c.getLong(c.getColumnIndex("date"));
                    long delta = gr_date - curdate.getTime();
                    // Перевод количества дней между датами из миллисекунд в дни
                    int days =  (int)(delta / (24 * 60 * 60 * 1000)); // миллисекунды

                    if (days>0 && days<=7) {
                        String name = c.getString(c.getColumnIndex("credit_name"));
                        Double sum  = c.getDouble(c.getColumnIndex("summa"));

                        TASK task = new TASK(TASK_TYPE_CREDIT, name, gr_date, sum);

                        addRec(task);

                        Log.d("DMS","Add task = "+task);
                    }

                } while (c.moveToNext());
            }
            c.close();
        }

    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    //  === TASK ===

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(DB_TASK_TABLE, null, null, null, null, null, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(TASK pTask) {

        ContentValues cv = new ContentValues();
        cv.put(CL_TASK_NAME, pTask.name);
        cv.put(CL_TASK_SUMMA, pTask.summa);
        cv.put(CL_TASK_DATE, pTask.date);

        //cv.put(COLUMN_IMG, img);
        mDB.insert(DB_TASK_TABLE, null, cv);
    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(DB_TASK_TABLE, CL_ID + " = " + id, null);
    }

    public void deleteAll(){
        mDB.delete(DB_TASK_TABLE, null, null);
    }


    //  === FLAT ===

    // получить все данные из таблицы FLAT
    public Cursor flat_GetAll() {
        return mDB.query(DB_FLAT_TABLE, null, null, null, null, null, null);
    }

    // добавить запись в FLAT
    public void flat_Add(HOME pFlat) {

        ContentValues cv = new ContentValues();
        cv.put(CL_FLAT_NAME, pFlat.name);
        cv.put(CL_FLAT_ADRES, pFlat.adres);
        cv.put(CL_FLAT_ISCOUNTER, pFlat.isCounter);
        cv.put(CL_FLAT_DAY_BEG, pFlat.day_beg);
        cv.put(CL_FLAT_DAY_END, pFlat.day_end);

        //cv.put(COLUMN_IMG, img);
        mDB.insert(DB_FLAT_TABLE, null, cv);
    }

    // удалить запись из FLAT
    public void flat_Delete(long id) {
        mDB.delete(DB_FLAT_TABLE, CL_ID + " = " + id, null);
    }

    
    public void flat_DeleteAll(){
        mDB.delete(DB_FLAT_TABLE, null, null);
    }


    //  === CREDIT ===
    // получить все данные из таблицы CREDIT
    public Cursor credit_GetCreditData() {

        Cursor cur;

        String sqlQuery = "select CREDIT._id as _id," +
                "CREDIT.name as CREDIT_name, " +
                "CREDIT.date as CREDIT_date, " +
                "CREDIT.summa as CREDIT_summa, " +
                "CREDIT.period as CREDIT_period, " +
                "CREDIT.procent as CREDIT_procent, " +
                "CREDIT.summa_pay as CREDIT_summa_pay, " +
                "CREDIT.finish as CREDIT_finish, " +

                "coalesce(GRAPHIC.GRAPHIC_date,0) as GRAPHIC_date, " +
                "coalesce(GRAPHIC.GRAPHIC_summa,0) as GRAPHIC_summa, " +
                "coalesce(GRAPHIC.GRAPHIC_summa_credit,0) as GRAPHIC_summa_credit, " +
                "coalesce(GRAPHIC.GRAPHIC_summa_procent,0) as GRAPHIC_summa_procent, " +


                "coalesce(PAYMENT.PAYMENT_date_last_pay, 0) as PAYMENT_date_last_pay, " +
                "coalesce(PAYMENT.PAYMENT_summa, 0) as PAYMENT_summa, " +
                "coalesce(PAYMENT.PAYMENT_summa_credit, 0) as PAYMENT_summa_credit, " +
                "PAYMENT.PAYMENT_summa_procent as PAYMENT_summa_procent, " +
                "PAYMENT.PAYMENT_summa_addon as PAYMENT_summa_addon, " +
                "PAYMENT.PAYMENT_summa_plus as PAYMENT_summa_plus, " +
                "PAYMENT.PAYMENT_summa_minus as PAYMENT_summa_minus, " +

                "CREDIT.summa - coalesce(PAYMENT.PAYMENT_summa_credit,0) as RESULT_rest, " +
                "coalesce(PAYMENT.PAYMENT_summa_fin_res, 0) as RESULT_fin_res " +

                "from CREDIT as CREDIT " +

                " left join ( select GRAPHIC.credit_id as credit_id, " +
                "                   GRAPHIC.date as GRAPHIC_date, " +
                "                   GRAPHIC.summa as GRAPHIC_summa, " +
                "                   GRAPHIC.summa_credit as GRAPHIC_summa_credit, " +
                "                   GRAPHIC.summa_procent as GRAPHIC_summa_procent " +
                "             from GRAPHIC as GRAPHIC " +
                "             inner join (select t.credit_id, " +
                "                           min(t.date) as min_date " +
                "                           from GRAPHIC as t " +
                "                           inner join (select p.credit_id, max(p.date) as max_pay_date " +
                "                                       from PAYMENT as p " +
                "                                       group by p.credit_id) as p " +
                "                           on t.credit_id = p.credit_id and t.date > p.max_pay_date " +
                "                           group by t.credit_id ) as t " +
                "                   on GRAPHIC.credit_id = t.credit_id " +
                "                       and GRAPHIC.date = t.min_date " +
                " ) as GRAPHIC " +
                " on CREDIT._id = GRAPHIC.credit_id " +

                " left join ( select PAYMENT.credit_id as PAYMENT_credit_id, " +
                "               max(PAYMENT.date) as PAYMENT_date_last_pay, " +
                "               sum(coalesce(PAYMENT.summa_plus, 0) - coalesce(PAYMENT.summa_minus, 0) - coalesce(PAYMENT.summa_procent, 0)) as PAYMENT_summa_fin_res, " +
                "               sum(PAYMENT.summa) as PAYMENT_summa, " +
                "               sum(PAYMENT.summa_credit) as PAYMENT_summa_credit, " +
                "               sum(PAYMENT.summa_procent) as PAYMENT_summa_procent, " +
                "               sum(PAYMENT.summa_addon) as PAYMENT_summa_addon, " +
                "               sum(PAYMENT.summa_plus) as PAYMENT_summa_plus, " +
                "               sum(PAYMENT.summa_minus) as PAYMENT_summa_minus " +
                "               from PAYMENT as PAYMENT " +
                "               group by PAYMENT.credit_id " +
                "           ) as PAYMENT " +
                "   on CREDIT._id = PAYMENT.PAYMENT_credit_id " +
                "order by RESULT_rest desc, CREDIT_date "+
                "";

        //cur  = mDB.rawQuery(sqlQuery, new String[] {"1"});

        cur  = mDB.rawQuery(sqlQuery, null);
        
        return cur;
    }

    Cursor GetGraphic(int credit_id, boolean withPlan){

        Log.d("DMS","GetGraphic");

        String sqlQuery="SELECT PAYMENT._id as _id," +
                "coalesce(PAYMENT.date,0) as date, " +
                "PAYMENT.summa as summa, " +
                "PAYMENT.summa_credit as summa_credit, " +
                "PAYMENT.summa_procent as summa_procent, " +
                "1 as done " +
                "from PAYMENT as PAYMENT " +
                "where PAYMENT.credit_id = "+credit_id;

        if (withPlan){
            sqlQuery+=" UNION " +
                    "SELECT -1 as _id, " +
                    "GRAPHIC.date as date, " +
                    "GRAPHIC.summa as summa, " +
                    "GRAPHIC.summa_credit as summa_credit, " +
                    "GRAPHIC.summa_procent as summa_procent, " +
                    "0 as done " +
                    "from GRAPHIC as GRAPHIC " +
                    "where GRAPHIC.credit_id = "+credit_id +
                    " and GRAPHIC.date > (SELECT max(t.date) from PAYMENT as t where t.credit_id = "+credit_id+") ";
        }

        sqlQuery+=" order by date";

        String str_id = ""+credit_id;
        return mDB.rawQuery(sqlQuery, null);

    }


    // получить все данные из таблицы CREDIT
    public CREDIT GetCredit(int credit_id) {

        Log.d("DMS","GetCredit");

        CREDIT cred = new CREDIT();

        Log.d("DMS","_1_2_");
        Cursor c = mDB.query(DB_CREDIT_TABLE, null, "_id = ?", new String[] { ""+credit_id }, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    cred._id        = c.getInt(c.getColumnIndex(CL_ID));
                    cred.name       = c.getString(c.getColumnIndex(CL_CREDIT_NAME));
                    cred.summa      = c.getDouble(c.getColumnIndex(CL_CREDIT_SUMMA));
                    cred.summa_pay  = c.getDouble(c.getColumnIndex(CL_CREDIT_SUMMA_PAY));
                    cred.date       = c.getLong(c.getColumnIndex(CL_CREDIT_DATE));
                    cred.procent    = c.getDouble(c.getColumnIndex(CL_CREDIT_PROCENT));
                    cred.period     = c.getInt(c.getColumnIndex(CL_CREDIT_PERIOD));
                    cred.finish     = c.getInt(c.getColumnIndex(CL_CREDIT_FINISH));
                } while (c.moveToNext());
            }
            c.close();
        }

        Log.d("DMS","_1_3_="+cred.name);

        return cred;
    }

    // получить все данные из таблицы CREDIT
    public PAYMENT GetPayment(int payment_id) {

        Log.d("DMS","GetPayment _id = "+payment_id);

        PAYMENT pay = new PAYMENT();

        Cursor c = mDB.query(DB_PAYMENT_TABLE, null, "_id = ?", new String[] { ""+payment_id }, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    pay._id        = c.getInt(c.getColumnIndex(CL_ID));
                    pay.credit_id  = c.getInt(c.getColumnIndex(CL_PAYMENT_ID_CREDIT));
                    pay.date       = c.getLong(c.getColumnIndex(CL_PAYMENT_DATE));
                    pay.summa      = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA));
                    pay.summa_credit      = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_CREDIT));
                    pay.summa_procent      = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_PROCENT));
                    pay.summa_addon      = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_ADDON));
                    pay.summa_plus      = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_PLUS));
                    pay.summa_minus      = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_MINUS));
                } while (c.moveToNext());
            }
            c.close();
        }

        return pay;
    }

    // получить все данные из таблицы CREDIT
    public PAYMENT GetGraphicItem(int payment_id) {

        Log.d("DMS","GetGraphicItem _id = "+payment_id);

        PAYMENT pay = new PAYMENT();

        Cursor c = mDB.query(DB_GRAPHIC_TABLE, null, "_id = ?", new String[] { ""+payment_id }, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    pay._id        = c.getInt(c.getColumnIndex(CL_ID));
                    pay.credit_id  = c.getInt(c.getColumnIndex(CL_PAYMENT_ID_CREDIT));
                    pay.date       = c.getLong(c.getColumnIndex(CL_PAYMENT_DATE));
                    pay.summa      = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA));
                    pay.summa_credit      = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_CREDIT));
                    pay.summa_procent      = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_PROCENT));
                } while (c.moveToNext());
            }
            c.close();
        }

        return pay;
    }

    // добавить запись в PAYMENT
    public void payment_Add(PAYMENT pPay) {

        ContentValues cv = new ContentValues();
        cv.put(CL_PAYMENT_ID_CREDIT, pPay.credit_id);
        cv.put(CL_PAYMENT_DATE, pPay.date);
        cv.put(CL_PAYMENT_SUMMA, pPay.summa);
        cv.put(CL_PAYMENT_SUMMA_CREDIT, pPay.summa_credit);
        cv.put(CL_PAYMENT_SUMMA_PROCENT, pPay.summa_procent);
        cv.put(CL_PAYMENT_SUMMA_ADDON, pPay.summa_addon);
        cv.put(CL_PAYMENT_SUMMA_PLUS, pPay.summa_plus);
        cv.put(CL_PAYMENT_SUMMA_MINUS, pPay.summa_minus);

        mDB.insert(DB_PAYMENT_TABLE, null, cv);
    }

    // добавить запись в PAYMENT
    public void graphic_Add(PAYMENT pPay) {

        ContentValues cv = new ContentValues();
        cv.put(CL_GRAPHIC_ID_CREDIT, pPay.credit_id);
        cv.put(CL_GRAPHIC_DATE, pPay.date);
        cv.put(CL_GRAPHIC_SUMMA, pPay.summa);
        cv.put(CL_GRAPHIC_SUMMA_CREDIT, pPay.summa_credit);
        cv.put(CL_GRAPHIC_SUMMA_PROCENT, pPay.summa_procent);

        mDB.insert(DB_GRAPHIC_TABLE, null, cv);
    }

    // получить все данные из таблицы CREDIT
    public Cursor credit_GetAll() {
        return mDB.query(DB_CREDIT_TABLE, null, null, null, null, null, null);
    }

    // добавить запись в CREDIT
    public void credit_Add(CREDIT pCredit) {

        ContentValues cv = new ContentValues();
        cv.put(CL_CREDIT_NAME, pCredit.name);
        cv.put(CL_CREDIT_DATE, pCredit.date);
        cv.put(CL_CREDIT_SUMMA, pCredit.summa);
        cv.put(CL_CREDIT_SUMMA_PAY, pCredit.summa_pay);
        cv.put(CL_CREDIT_PERIOD, pCredit.period);
        cv.put(CL_CREDIT_PROCENT, pCredit.procent);
        cv.put(CL_CREDIT_SUMMA_PAY, pCredit.summa_pay);

        mDB.insert(DB_CREDIT_TABLE, null, cv);
    }

    // добавить запись в CREDIT
    public void credit_Update(CREDIT pCredit) {

        ContentValues cv = new ContentValues();
        cv.put(CL_CREDIT_NAME, pCredit.name);
        cv.put(CL_CREDIT_DATE, pCredit.date);
        cv.put(CL_CREDIT_SUMMA, pCredit.summa);
        cv.put(CL_CREDIT_PERIOD, pCredit.period);
        cv.put(CL_CREDIT_PROCENT, pCredit.procent);
        cv.put(CL_CREDIT_SUMMA_PAY, pCredit.summa_pay);

        mDB.update(DB_CREDIT_TABLE, cv, "_id = ?", new String[]{ String.valueOf(pCredit._id) });
    }

    // изменить запись в PAYMENT
    public void payment_Update(PAYMENT pPay) {

        ContentValues cv = new ContentValues();
        cv.put(CL_PAYMENT_DATE, pPay.date);
        cv.put(CL_PAYMENT_SUMMA, pPay.summa);
        cv.put(CL_PAYMENT_SUMMA_CREDIT, pPay.summa_credit);
        cv.put(CL_PAYMENT_SUMMA_PROCENT, pPay.summa_procent);
        cv.put(CL_PAYMENT_SUMMA_ADDON, pPay.summa_addon);
        cv.put(CL_PAYMENT_SUMMA_PLUS, pPay.summa_plus);
        cv.put(CL_PAYMENT_SUMMA_MINUS, pPay.summa_minus);

        mDB.update(DB_PAYMENT_TABLE, cv, "_id = ? and credit_id=?", new String[]{ String.valueOf(pPay._id), ""+pPay.credit_id });
    }

    // изменить запись в PAYMENT
    public void graphic_Update(PAYMENT pPay) {

        ContentValues cv = new ContentValues();
        cv.put(CL_PAYMENT_DATE, pPay.date);
        cv.put(CL_PAYMENT_SUMMA, pPay.summa);
        cv.put(CL_PAYMENT_SUMMA_CREDIT, pPay.summa_credit);
        cv.put(CL_PAYMENT_SUMMA_PROCENT, pPay.summa_procent);

        mDB.update(DB_GRAPHIC_TABLE, cv, "_id = ? and credit_id=?", new String[]{ String.valueOf(pPay._id), ""+pPay.credit_id });
    }


    // удалить запись из CREDIT
    public void credit_Delete(long id) {
        mDB.delete(DB_CREDIT_TABLE, CL_ID + " = " + id, null);
    }

    // удалить запись из CREDIT
    public void payment_Delete(String id) {
        mDB.delete(DB_PAYMENT_TABLE, CL_ID + " = " + id, null);
    }

    // удалить запись из CREDIT
    public void graphic_Delete(String id) {
        mDB.delete(DB_GRAPHIC_TABLE, CL_ID + " = " + id, null);
    }

    public void credit_DeleteAll(){
        mDB.delete(DB_CREDIT_TABLE, null, null);
    }

    public void payment_DeleteAll(int credit_id){
        mDB.delete(DB_PAYMENT_TABLE, "credit_id=?", new String[]{""+credit_id});
    }

    public void graphic_DeleteAll(int credit_id){
        mDB.delete(DB_GRAPHIC_TABLE, "credit_id=?", new String[]{""+credit_id});
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_TASK_CREATE);
            db.execSQL(DB_FLAT_CREATE);
            db.execSQL(DB_CREDIT_CREATE);
            db.execSQL(DB_GRAPHIC_CREATE);
            db.execSQL(DB_PAYMENT_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if (newVersion==5) {
                Log.d("1", "START");

                db.execSQL(DB_DROP);
                //           db.execSQL(DB_FLAT_DROP);
//            db.execSQL(DB_GRAPHIC_DROP);
                //           db.execSQL(DB_PAYMENT_DROP);
//            db.execSQL(DB_CREDIT_DROP);

                Log.d("1", "DROP");

                db.execSQL(DB_TASK_CREATE);

                // Version 5
                db.execSQL(DB_FLAT_CREATE);

                Log.d("1", "DB_FLAT_CREATE");

                db.execSQL(DB_CREDIT_CREATE);

                Log.d("1", "DB_CREDIT_CREATE");

                db.execSQL(DB_GRAPHIC_CREATE);

                Log.d("1", "DB_GRAPHIC_CREATE");

                db.execSQL(DB_PAYMENT_CREATE);

                Log.d("1", "DB_PAYMENT_CREATE");
            }

            if (newVersion==7) {
                db.execSQL(DB_GRAPHIC_DROP);
                db.execSQL(DB_GRAPHIC_CREATE);
            }

        }
    }

}