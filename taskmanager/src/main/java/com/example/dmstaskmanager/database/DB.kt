package com.example.dmstaskmanager.database

import android.app.Notification
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import android.util.Log

import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.*
import com.example.dmstaskmanager.main_window.main

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import com.example.dmstaskmanager.utils.*


class DB {

    companion object {

        private val DB_NAME = "DMS_TASK"
        private val DB_VERSION = 21

        // == Таблица Квартиры операции ==
        private val DB_FLAT_ACCOUNT_TABLE = "FLAT_ACCOUNT"

        val CL_ID = "_id"

        val CL_FLAT_ACC_FLAT_ID = "flat_id"
        val CL_FLAT_ACC_TYPE = "type"   //  1 - приход, -1 - расход
        val CL_FLAT_ACC_OPERATION = "operation"
        val CL_FLAT_ACC_DATE = "date"
        val CL_FLAT_ACC_SUMMA = "summa"
        val CL_FLAT_ACC_COMMENT = "comment"

        private val DB_FLAT_ACCOUNT_CREATE = "create table " + DB_FLAT_ACCOUNT_TABLE + "(" +
                CL_ID + " integer primary key autoincrement, " +
                CL_FLAT_ACC_FLAT_ID + " integer, " +
                CL_FLAT_ACC_TYPE + " integer, " +
                CL_FLAT_ACC_OPERATION + " text," +
                CL_FLAT_ACC_DATE + " long," +
                CL_FLAT_ACC_SUMMA + " double," +
                CL_FLAT_ACC_COMMENT + " text" +
                ");"

        private val DB_FLAT_ACCOUNT_DROP =
                "drop table $DB_FLAT_ACCOUNT_TABLE;"

        // == Таблица ЗАДАЧИ ==

        private val DB_TASK_TABLE = "TASK"

        val CL_TASK_TYPE = "type"
        val CL_TASK_NAME = "name"
        val CL_TASK_DATE = "date"
        val CL_TASK_SUMMA = "summa"
        val CL_TASK_FINISH = "finish"
        val CL_TASK_PARENT_ID = "parent_id"
        val CL_TASK_CREDIT_TYPE = "credit_type"
        val CL_TASK_COUNT = "count"


        private val DB_TASK_CREATE = "create table " + DB_TASK_TABLE + "(" +
                CL_ID + " integer primary key autoincrement, " +
                CL_TASK_TYPE + " integer, " +
                CL_TASK_PARENT_ID + " integer, " +
                CL_TASK_NAME + " text," +
                CL_TASK_DATE + " long," +
                CL_TASK_SUMMA + " long," +
                CL_TASK_FINISH + " integer" +
                ");"

        private val DB_DROP =
                "drop table $DB_TASK_TABLE;"

        // == Таблица ТИПОВ ЗАДАЧ ==
        private val DB_TASKTYPE_TABLE = "TASKTYPE"
        val CL_TASKTYPE_NAME = "name"

        private val DB_TASKTYPE_CREATE = "create table " + DB_TASKTYPE_TABLE + "(" +
                CL_ID + " integer primary key autoincrement, " +
                CL_TASK_NAME + " text" +
                ");"

        private val DB_TASKTYPE_DROP =
                "drop table $DB_TASKTYPE_TABLE;"

        // == Таблица КВАРТИРЫ ==
        private val DB_FLAT_TABLE = "FLAT"

        val CL_FLAT_NAME = "name"
        val CL_FLAT_ADRES = "adres"
        val CL_FLAT_PARAM = "param"
        val CL_FLAT_FOTO = "foto"
        val CL_FLAT_ISCOUNTER = "isCounter"
        val CL_FLAT_ISPAY = "isPay"
        val CL_FLAT_ISARENDA = "isArenda"
        val CL_FLAT_LICNUMBER = "licNumber"
        val CL_FLAT_DAY_BEG = "day_beg"
        val CL_FLAT_DAY_END = "day_end"
        val CL_FLAT_CREATE_TASK = "isTask"

        val CL_FLAT_CREDIT_ID = "credit_id"
        val CL_FLAT_SUMMA = "summa"
        val CL_FLAT_SUMMA_ARENDA = "summa_arenda"
        val CL_FLAT_DAY_ARENDA = "day_arenda"
        val CL_FLAT_FINISH = "finish"
        val CL_FLAT_TYPE = "type"

        private val DB_FLAT_CREATE = "create table " + DB_FLAT_TABLE + "(" +
                CL_ID + " integer primary key autoincrement, " +
                CL_FLAT_NAME + " text," +
                CL_FLAT_ADRES + " text," +
                CL_FLAT_ISCOUNTER + " tinyint(1) default 0," +
                CL_FLAT_ISPAY + " tinyint(1) default 0," +
                CL_FLAT_ISARENDA + " tinyint(1) default 0," +
                CL_FLAT_DAY_BEG + " tinyint(1)," +
                CL_FLAT_DAY_END + " tinyint(1)," +
                CL_FLAT_CREATE_TASK + " tinyint(1)," +
                CL_FLAT_LICNUMBER + " integer," +
                CL_FLAT_CREDIT_ID + " integer," +
                CL_FLAT_FINISH + " tinyint(1)," +
                CL_FLAT_TYPE + " tinyint(1)," +
                CL_FLAT_SUMMA + " double," +
                CL_FLAT_SUMMA_ARENDA + " double," +
                CL_FLAT_DAY_ARENDA + " tinyint(1)," +
                CL_FLAT_PARAM + " text," +
                CL_FLAT_FOTO + " blob" +
                ");"

        private val DB_FLAT_DROP =
                "drop table $DB_FLAT_TABLE;"

        private val DB_FLAT_UPDATE = "alter table " + DB_FLAT_TABLE + " add column " +
                CL_FLAT_ISPAY + " tinyint(1) default 0," +
                CL_FLAT_LICNUMBER + " integer" +
                ";"


        // == Таблица КРЕДИТЫ ==
        private val DB_CREDIT_TABLE = "CREDIT"

        val CL_CREDIT_NAME = "name"
        val CL_CREDIT_TYPE = "type"
        val CL_CREDIT_DATE = "date"
        val CL_CREDIT_SUMMA = "summa"
        val CL_CREDIT_SUMMA_PAY = "summa_pay"
        val CL_CREDIT_PROCENT = "procent"
        val CL_CREDIT_PERIOD = "period"
        val CL_CREDIT_FINISH = "finish"

        private val DB_CREDIT_CREATE = "create table " + DB_CREDIT_TABLE + "(" +
                CL_ID + " integer primary key autoincrement, " +
                CL_CREDIT_TYPE + " integer, " +
                CL_CREDIT_NAME + " text," +
                CL_CREDIT_DATE + " long," +
                CL_CREDIT_SUMMA + " double," +
                CL_CREDIT_SUMMA_PAY + " double," +
                CL_CREDIT_PROCENT + " double," +
                CL_CREDIT_PERIOD + " integer, " +
                CL_CREDIT_FINISH + " tinyint(1)" +
                ");"

        private val DB_CREDIT_DROP =
                "drop table $DB_CREDIT_TABLE;"


        // == Таблица ГРАФИК ==
        private val DB_GRAPHIC_TABLE = "GRAPHIC"

        val CL_GRAPHIC_ID_CREDIT = "credit_id"
        val CL_GRAPHIC_DATE = "date"
        val CL_GRAPHIC_REST = "rest"
        val CL_GRAPHIC_SUMMA = "summa"
        val CL_GRAPHIC_SUMMA_CREDIT = "summa_credit"
        val CL_GRAPHIC_SUMMA_PROCENT = "summa_procent"
        val CL_GRAPHIC_SUMMA_ADDON = "summa_addon"
        val CL_GRAPHIC_SUMMA_PLUS = "summa_plus"
        val CL_GRAPHIC_SUMMA_MINUS = "summa_minus"
        val CL_GRAPHIC_DONE = "done"
        val CL_GRAPHIC_COMMENT = "comment"

        private val DB_GRAPHIC_CREATE = "create table " + DB_GRAPHIC_TABLE + "(" +
                CL_ID + " integer primary key autoincrement, " +
                CL_GRAPHIC_ID_CREDIT + " integer," +
                CL_GRAPHIC_REST + " double," +
                CL_GRAPHIC_DATE + " long, " +
                CL_GRAPHIC_SUMMA + " double," +
                CL_GRAPHIC_SUMMA_CREDIT + " double," +
                CL_GRAPHIC_SUMMA_PROCENT + " double," +
                CL_GRAPHIC_SUMMA_ADDON + " double," +
                CL_GRAPHIC_SUMMA_PLUS + " double," +
                CL_GRAPHIC_SUMMA_MINUS + " double," +
                CL_GRAPHIC_DONE + " tinyint(1)," +
                CL_GRAPHIC_COMMENT + " text" +
                ");"

        private val DB_GRAPHIC_DROP =
                "drop table $DB_GRAPHIC_TABLE;"

        // == Таблица ПЛАТЕЖИ ==
        private val DB_PAYMENT_TABLE = "PAYMENT"

        val CL_PAYMENT_ID_CREDIT = "credit_id"
        val CL_PAYMENT_DATE = "date"
        val CL_PAYMENT_SUMMA = "summa"
        val CL_PAYMENT_SUMMA_CREDIT = "summa_credit"
        val CL_PAYMENT_SUMMA_PROCENT = "summa_procent"
        val CL_PAYMENT_SUMMA_ADDON = "summa_addon"
        val CL_PAYMENT_SUMMA_PLUS = "summa_plus"
        val CL_PAYMENT_SUMMA_MINUS = "summa_minus"
        val CL_PAYMENT_COMMENT = "comment"

        private val DB_PAYMENT_CREATE = "create table " + DB_PAYMENT_TABLE + "(" +
                CL_ID + " integer primary key autoincrement, " +
                CL_PAYMENT_ID_CREDIT + " integer," +
                CL_PAYMENT_DATE + " long, " +
                CL_PAYMENT_SUMMA + " double," +
                CL_PAYMENT_SUMMA_CREDIT + " double," +
                CL_PAYMENT_SUMMA_PROCENT + " double," +
                CL_PAYMENT_SUMMA_ADDON + " double," +
                CL_PAYMENT_SUMMA_PLUS + " double," +
                CL_PAYMENT_SUMMA_MINUS + " double," +
                CL_PAYMENT_COMMENT + " text" +
                ");"

        private val DB_PAYMENT_DROP =
                "drop table $DB_PAYMENT_TABLE;"

        ///////////////////////////////////////////////////

        val CL_RESULT_CREDIT_ID = "CREDIT_id"
        val CL_RESULT_CREDIT_DATE = "CREDIT_date"
        val CL_RESULT_CREDIT_TYPE = "CREDIT_type"
        val CL_RESULT_CREDIT_NAME = "CREDIT_name"
        val CL_RESULT_CREDIT_SUMMA = "CREDIT_summa"
        val CL_RESULT_CREDIT_SUMMA_PAY = "CREDIT_summa_pay"
        val CL_RESULT_CREDIT_PROCENT = "CREDIT_procent"
        val CL_RESULT_CREDIT_PERIOD = "CREDIT_period"
        val CL_RESULT_CREDIT_FINISH = "CREDIT_finish"

        val CL_RESULT_GRAPHIC_DATE = "GRAPHIC_date"
        val CL_RESULT_GRAPHIC_SUMMA = "GRAPHIC_summa"
        val CL_RESULT_GRAPHIC_SUMMA_CREDIT = "GRAPHIC_summa_credit"
        val CL_RESULT_GRAPHIC_SUMMA_PROCENT = "GRAPHIC_summa_procent"

        val CL_RESULT_PAYMENT_SUMMA = "PAYMENT_summa"
        val CL_RESULT_PAYMENT_SUMMA_CREDIT = "PAYMENT_summa_credit"
        val CL_RESULT_PAYMENT_SUMMA_PROCENT = "PAYMENT_summa_procent"

        val CL_RESULT_PAYMENT_DATE_LAST_PAY = "PAYMENT_date_last_pay"
        val CL_RESULT_PAYMENT_ADDON = "PAYMENT_summa_addon"
        val CL_RESULT_PAYMENT_SUMMA_PLUS = "PAYMENT_summa_plus"
        val CL_RESULT_PAYMENT_SUMMA_MINUS = "PAYMENT_summa_minus"

        val CL_RESULT_RESULT_REST = "RESULT_rest"
        val CL_RESULT_RESULT_PROCENT = "RESULT_procent"
        val CL_RESULT_RESULT_FIN_RES = "RESULT_fin_res"

        ///////////////////////////////////////////////////

        val ATTR_DONE = "done"
        val ATTR_REST = "rest"
        val ATTR_DATE = "date"
        val ATTR_SUMMA = "summa"
        val ATTR_SUMMA_CREDIT = "summa_credit"
        val ATTR_SUMMA_PROCENT = "summa_procent"
    }


    private var mCtx: Context? = null

    private var mDBHelper: DBHelper? = null
    private var mDB: SQLiteDatabase? = null

    //== Автозакрытие задач (конец)===

    // получить все типы задач из таблицы TASK
    val taskType: Cursor
        get() {
            val sqlQuery = "select distinct TASK.type as _id," +
                    " TASK.type as type, " +
                    " TASKTYPE.name as name," +
                    " Count(TASK._id) as count, " +
                    " Sum(TASK.summa) as summa " +
                    " from  " + DB_TASK_TABLE + " as TASK " +
                    "   left outer join " + DB_TASKTYPE_TABLE + " as TASKTYPE" +
                    "   on TASK.type = TASKTYPE._id" +
                    " where " + CL_TASK_FINISH + " = 0" +
                    " group by TASK.type," +
                    " TASKTYPE.name" +
                    " order by _id asc"

            return mDB!!.rawQuery(sqlQuery, null)
        }

    // получить все данные из таблицы DB_TABLE
    val allTask: Cursor
        get() = mDB!!.query(DB_TASK_TABLE, null, "$CL_TASK_FINISH = 0", null, null, null, null)


    //  === FLAT ===

    // получить все данные из таблицы FLAT
    //                " where "+CL_FLAT_FINISH+" = 0"+
    fun getFlats(showClosed: Boolean = true): Cursor {

            var sqlQuery = "select FLAT." + CL_ID + " as " + CL_ID + "," +
                    " FLAT." + CL_FLAT_NAME + " as " + CL_FLAT_NAME + "," +
                    " FLAT." + CL_FLAT_ADRES + " as " + CL_FLAT_ADRES + "," +
                    " FLAT." + CL_FLAT_PARAM + " as " + CL_FLAT_PARAM + "," +
                    " FLAT." + CL_FLAT_FOTO + " as " + CL_FLAT_FOTO + "," +
                    " FLAT." + CL_FLAT_ISCOUNTER + " as " + CL_FLAT_ISCOUNTER + "," +
                    " FLAT." + CL_FLAT_ISPAY + " as " + CL_FLAT_ISPAY + "," +
                    " FLAT." + CL_FLAT_DAY_BEG + " as " + CL_FLAT_DAY_BEG + "," +
                    " FLAT." + CL_FLAT_DAY_END + " as " + CL_FLAT_DAY_END + "," +
                    " FLAT." + CL_FLAT_ISARENDA + " as " + CL_FLAT_ISARENDA + "," +
                    " FLAT." + CL_FLAT_DAY_ARENDA + " as " + CL_FLAT_DAY_ARENDA + "," +
                    " FLAT." + CL_FLAT_SUMMA_ARENDA + " as " + CL_FLAT_SUMMA_ARENDA + "," +
                    " FLAT." + CL_FLAT_CREATE_TASK + " as " + CL_FLAT_CREATE_TASK + "," +
                    " FLAT." + CL_FLAT_LICNUMBER + " as " + CL_FLAT_LICNUMBER + "," +
                    " FLAT." + CL_FLAT_CREDIT_ID + " as " + CL_FLAT_CREDIT_ID + "," +
                    " FLAT." + CL_FLAT_SUMMA + " as " + CL_FLAT_SUMMA + "," +
                    " FLAT." + CL_FLAT_TYPE + " as " + CL_FLAT_TYPE + "," +
                    " FLAT." + CL_FLAT_FINISH + " as " + CL_FLAT_FINISH + "," +
                    " coalesce(ACC.last_date_summa_arenda,0) as last_date_summa_arenda," +
                    " coalesce(ACC.last_date_summa_exp,0) as last_date_summa_exp," +
                    " coalesce(ACC.last_date_summa_rent,0) as last_date_summa_rent," +
                    " coalesce(ACC.last_date_summa_first_pay,0) as last_date_summa_first_pay," +
                    " coalesce(ACC.last_date_summa_first_proc,0) as last_date_summa_first_proc," +
                    " coalesce(CRED.last_date_summa_pay,0) as last_date_summa_pay," +
                    " coalesce(CRED.last_date_summa_proc,0) as last_date_summa_proc," +
                    " coalesce(ACC.summa_arenda,0) as summa_arenda_all," +
                    " coalesce(ACC.summa_exp,0) as summa_exp," +
                    " coalesce(ACC.summa_rent,0) as summa_rent," +
                    " coalesce(ACC.summa_first_pay,0) as summa_first_pay," +
                    " coalesce(ACC.summa_first_proc,0) as summa_first_proc," +
                    " coalesce(CRED.summa_pay,0) as summa_pay," +
                    " coalesce(CRED.summa_proc,0) as summa_proc" +
                    " from  " + DB_FLAT_TABLE + " as FLAT " +
                    "   left outer join (" +
                    "       select t.flat_id as flat_id," +
                    "               max(case when t.operation = " + FlatPaymentOperationType.PROFIT.type + " then t.date else 0 end) as last_date_summa_arenda," +
                    "               max(case when t.operation = " + FlatPaymentOperationType.DAMAGE.type + " then t.date else 0 end) as last_date_summa_exp," +
                    "               max(case when t.operation = " + FlatPaymentOperationType.RENT.type + " then t.date else 0 end) as last_date_summa_rent," +
                    "               max(case when t.operation = " + FlatPaymentOperationType.PAY.type + " then t.date else 0 end) as last_date_summa_first_pay," +
                    "               max(case when t.operation = " + FlatPaymentOperationType.PROCENT.type + " then t.date else 0 end) as last_date_summa_first_proc," +

                    "               max(case when t.operation = " + FlatPaymentOperationType.INSURE.type + " then t.date else 0 end) as last_date_summa_first_insure," +
                    "               max(case when t.operation = " + FlatPaymentOperationType.GAS.type + " then t.date else 0 end) as last_date_summa_first_gas," +
                    "               max(case when t.operation = " + FlatPaymentOperationType.TO.type + " then t.date else 0 end) as last_date_summa_first_TO," +
                    "               max(case when t.operation = " + FlatPaymentOperationType.SELL.type + " then t.date else 0 end) as last_date_summa_first_sell," +

                    "               sum(case when t.operation = " + FlatPaymentOperationType.INSURE.type + " then t.summa else 0 end) as summa_first_insure," +
                    "               sum(case when t.operation = " + FlatPaymentOperationType.GAS.type + " then t.summa else 0 end) as summa_first_gas," +
                    "               sum(case when t.operation = " + FlatPaymentOperationType.TO.type + " then t.summa else 0 end) as summa_first_TO," +
                    "               sum(case when t.operation = " + FlatPaymentOperationType.SELL.type + " then t.summa else 0 end) as summa_first_sell," +

                    "               sum(case when t.operation = " + FlatPaymentOperationType.PROFIT.type + " then t.summa else 0 end) as summa_arenda," +
                    "               sum(case when t.operation = " + FlatPaymentOperationType.DAMAGE.type + " then t.summa else 0 end) as summa_exp," +
                    "               sum(case when t.operation = " + FlatPaymentOperationType.RENT.type + " then t.summa else 0 end) as summa_rent," +
                    "               sum(case when t.operation = " + FlatPaymentOperationType.PAY.type + " then t.summa else 0 end) as summa_first_pay," +
                    "               sum(case when t.operation = " + FlatPaymentOperationType.PROCENT.type + " then t.summa else 0 end) as summa_first_proc" +

                    "       from " + DB_FLAT_ACCOUNT_TABLE + " as t " +
                    "       group by t.flat_id) as ACC " +
                    "   on FLAT._id = ACC.flat_id" +
                    "   left outer join (" +
                    "       select t.credit_id as credit_id," +
                    "               max(t.date) as last_date_summa_pay," +
                    "               max(t.date) as last_date_summa_proc," +
                    "               sum(t.summa_credit) as summa_pay," +
                    "               sum(t.summa_procent) as summa_proc" +
                    "       from " + DB_PAYMENT_TABLE + " as t " +
                    "       group by t.credit_id) as CRED " +
                    "   on FLAT.credit_id = CRED.credit_id"

        if (!showClosed) {
            sqlQuery+=" where $CL_FLAT_FINISH = 0"
        }

        sqlQuery += " order by finish asc, type, " + CL_FLAT_NAME + " asc"

            val cursor =  mDB!!.rawQuery(sqlQuery, null)

            if (cursor != null) {
                try {
                    var i = 0
                    while (cursor.moveToNext()) {
                        Log.d("DMS_CREDIT", "i=$i" + cursor.getString(1))
                        i++
                      }
                } catch (e: Exception) {
                    Log.d("DMS_CREDIT", "" + e.message)
                } finally {
                    cursor.close()
                }
            }

            return mDB!!.rawQuery(sqlQuery, null)
        }

    constructor(ctx: Context) {
        mCtx = ctx
    }

    constructor() {
        mCtx = null
    }

    //////////////////////////////////////////////////////////////////

    fun sendTask(Activity: Context, pTask: TASK) {

        val NOTIFICATION_ID = pTask.type
        var title = ""
        var name = ""
        var date = ""
        var summa = ""

        when (pTask.type) {
            TASK.TASK_TYPE_CREDIT -> {
                title = "Платеж по кредиту"
                val dateFormat = SimpleDateFormat("dd MMMM yyyy 'г.'")
                name = pTask.name
                date = dateFormat.format(pTask.date)
                summa = pTask.summa.toString()
            }
            TASK.TASK_TYPE_ARENDA -> {
                title = "Аренда"
                name = pTask.name
                date = formatDate(pTask.date)
                summa = pTask.summa.toString()
            }
            TASK.TASK_TYPE_FLAT -> {
                title = "Квартплата"
                name = pTask.name
                date = formatDate(pTask.date)
            }
            TASK.TASK_TYPE_COUNTER -> {
                title = "Показания счетчиков"
                name = pTask.name
                date = formatDate(pTask.date)
            }
        }


        val mBuilder = NotificationCompat.Builder(Activity)
                .setTicker("TASK MANAGER")
                .setSmallIcon(R.drawable.day7)
                .setContentTitle(title)
                .setContentText(name)
                .setStyle(NotificationCompat.InboxStyle()
                        .addLine(name)
                        .addLine(date)
                        .addLine(summa))
        //.setStyle(new NotificationCompat.BigTextStyle().bigText(name))
        //.setPriority(2);

        // Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(Activity, main::class.java)

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        val stackBuilder = TaskStackBuilder.create(Activity)
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(main::class.java)
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)

        //NotificationManager mNotificationManager =
        //          (NotificationManager) Activity.getSystemService(Context.NOTIFICATION_SERVICE);

        var mNotificationManager: NotificationManagerCompat? = NotificationManagerCompat.from(Activity)

        // mId allows you to update the notification later on.

        var notification: Notification? = mBuilder.build()
        notification!!.defaults = Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE

        mNotificationManager!!.notify(NOTIFICATION_ID, notification)
        //startForeground(NOTIFICATION_ID, mBuilder);

        mNotificationManager = null
        notification = null

    }


    fun autoTask(Activity: Context) {
        // Кредиты

        Log.d("DMS", "AutoTask /CREDIT/")

        val sqlQuery = "" +
                " SELECT GRAPHIC.credit_id as credit_id, " +
                " CREDIT.name as credit_name, " +
                " GRAPHIC.date as date, " +
                " GRAPHIC.summa as summa, " +
                " GRAPHIC.summa_credit as summa_credit, " +
                " GRAPHIC.summa_procent as summa_procent " +
                " from  GRAPHIC" +
                "       inner join (" +
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
                ""

        val c = mDB!!.rawQuery(sqlQuery, null)

        val curdate = Date()

        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    // Количество дней между датами в миллисекундах
                    val gr_date = c.getLong(c.getColumnIndex("date"))
                    val delta = gr_date / (24 * 60 * 60 * 1000) - curdate.time / (24 * 60 * 60 * 1000)
                    // Перевод количества дней между датами из миллисекунд в дни
                    //int days =  (int)(delta / (24 * 60 * 60 * 1000)); // миллисекунды
                    val days = delta.toInt() // миллисекунды

                    if (days > 0 && days <= 7) {
                        val name = c.getString(c.getColumnIndex("credit_name"))
                        val sum = c.getDouble(c.getColumnIndex("summa"))
                        val parent_id = c.getInt(c.getColumnIndex("credit_id"))

                        //TASK task = new TASK(TASK.TASK_TYPE_CREDIT, parent_id, name, gr_date, sum);
                        val task = TASK.Builder(TASK.TASK_TYPE_CREDIT)
                                .setParentID(parent_id)
                                .setName(name)
                                .setDate(gr_date)
                                .setSumma(sum).build()

                        // Создать задачу
                        task_Add(Activity, task)

                        Log.d("DMS", "Add task = $task")
                    }

                } while (c.moveToNext())
            }
            c.close()
        }

    }

    fun autoFlatTask(Activity: Context) {
        // Кредиты

        Log.d("DMS", "AutoTask /Flat/")

        val sqlQuery = "" +
                " SELECT FLAT._id as _id, " +
                " FLAT.name as name, " +
                " FLAT.isCounter as isCounter, " +
                " FLAT.isArenda as isArenda, " +
                " FLAT.summa_arenda as summa_arenda, " +
                " FLAT.day_arenda as day_arenda, " +
                " FLAT.isPay as isPay, " +
                " FLAT.day_beg as day_beg, " +
                " FLAT.day_end as day_end " +
                " from  FLAT" +
                ""

        val c = mDB!!.rawQuery(sqlQuery, null)

        val curdate = Date()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = curdate.time

        // Текущий месяц (Счетчики) / Аренда
        val curMonth = Calendar.getInstance()
        curMonth.clear()
        curMonth.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        curMonth.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        curMonth.set(Calendar.DAY_OF_MONTH, 1)

        // Предыдущий месяц (Квартплата)
        val prevMonth = Calendar.getInstance()
        prevMonth.clear()
        prevMonth.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        prevMonth.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        prevMonth.set(Calendar.DAY_OF_MONTH, 1)

        prevMonth.add(Calendar.MONTH, -1)


        val date_of_month = curMonth.timeInMillis
        val date_of_month_prev = prevMonth.timeInMillis

        val sum = 0.0

        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    val name = c.getString(c.getColumnIndex("name"))
                    val parent_id = c.getInt(c.getColumnIndex("_id"))
                    val isCounter = c.getInt(c.getColumnIndex("isCounter")).toBoolean()
                    val isPay = c.getInt(c.getColumnIndex("isPay")).toBoolean()

                    val isArenda = c.getInt(c.getColumnIndex("isArenda")).toBoolean()

                    if (isArenda) {
                        // Задача по аренде за ТЕКУЩИЙ месяц с ____ (даты берутся из настройки кв-ры)
                        // По умолчанию с 10 числа

                        var day_arenda = c.getInt(c.getColumnIndex("day_arenda"))
                        val summa_arenda = c.getDouble(c.getColumnIndex("summa_arenda"))

                        if (day_arenda == 0) {
                            day_arenda = 10
                        }

                        if (calendar.get(Calendar.DAY_OF_MONTH) >= day_arenda) {

                            val task = TASK.Builder(TASK.TASK_TYPE_ARENDA)
                                    .setParentID(parent_id)
                                    .setName(name)
                                    .setDate(date_of_month)
                                    .setSumma(summa_arenda)
                                    .build()

                            task_Add(Activity, task)

                            Log.d("DMS", "Add task = $task")
                        }
                    }

                    if (isPay) {
                        // Для каждой квартиры создаем задачу на оплату ежемесячных комунальных платежей
                        // за ПРЕДЫДУЩИЙ месяц. После 5го числа (платеж ~ 5-10 число месяца)
                        if (calendar.get(Calendar.DAY_OF_MONTH) >= 5) {
                            //TASK task = new TASK(TASK.TASK_TYPE_FLAT, parent_id, name, date_of_month_prev, sum);
                            val task = TASK.Builder(TASK.TASK_TYPE_FLAT)
                                    .setParentID(parent_id)
                                    .setName(name)
                                    .setDate(date_of_month_prev)
                                    .setSumma(sum)
                                    .build()

                            task_Add(Activity, task)

                            Log.d("DMS", "Add task = $task")
                        }
                    }

                    if (isCounter) {
                        // Задача по счетчикам за ТЕКУЩИЙ месяц с ____ по ____ (даты берутся из настройки кв-ры)
                        // Например, с 20-25

                        val day_beg = c.getInt(c.getColumnIndex("day_beg"))
                        val day_end = c.getInt(c.getColumnIndex("day_end"))

                        if (calendar.get(Calendar.DAY_OF_MONTH) >= day_beg && calendar.get(Calendar.DAY_OF_MONTH) <= day_end) {

                            val task = TASK.Builder(TASK.TASK_TYPE_COUNTER)
                                    .setParentID(parent_id)
                                    .setName(name)
                                    .setDate(date_of_month)
                                    .setSumma(sum)
                                    .build()

                            task_Add(Activity, task)

                            Log.d("DMS", "Add task = $task")
                        }


                    }

                } while (c.moveToNext())
            }
            c.close()
        }

    }

    // открыть подключение
    fun open() {
        mCtx?.also { mCtx ->
            mDBHelper = DBHelper(mCtx, DB_NAME, null, DB_VERSION)
            mDB = mDBHelper!!.writableDatabase
        }
    }

    // закрыть подключение
    fun close() {
        if (mDBHelper != null) mDBHelper!!.close()
    }

    //  === TASK ===

    // получить все данные из таблицы TASK
    fun getTask(id: Int): TASK {

        val task = TASK()

        val c = mDB!!.query(DB_TASK_TABLE, null, "$CL_ID = $id", null, null, null, null)

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    task._id = c.getInt(c.getColumnIndex(CL_ID))
                    task.name = c.getString(c.getColumnIndex(CL_TASK_NAME))
                    task.type = c.getInt(c.getColumnIndex(CL_TASK_TYPE))
                    task.parent_id = c.getInt(c.getColumnIndex(CL_TASK_PARENT_ID))
                    task.date = c.getLong(c.getColumnIndex(CL_TASK_DATE))
                    task.summa = c.getDouble(c.getColumnIndex(CL_TASK_SUMMA))
                    task.finish = c.getInt(c.getColumnIndex(CL_TASK_FINISH)).toBoolean()
                } while (c.moveToNext())
            }
            c.close()
        }

        return task
    }

    //=====================================================
    // Автозакрытие задач
    //===================
    fun autoCloseTask(`object`: FlatPayment, type : Int): TASK {
        return autoCloseTask(TASK.Builder(type)
                .setParentID(`object`.flat_id)
                .setDate(`object`.date)
                .setSumma(`object`.summa)
                .build())
    }

    fun autoCloseTask(Object: PAYMENT): TASK {
        return autoCloseTask(TASK.Builder(TASK.TASK_TYPE_CREDIT)
                .setParentID(Object.credit_id)
                .setDate(Object.date)
                .setSumma(Object.summa)
                .build())
    }

    fun autoCloseTask(task: TASK): TASK {

        val res = TASK()

        Log.d("DMS_TASK_CLOSE", "cred = " + TASK.TASK_TYPE_CREDIT.toString() + " date =" + task.date.toString())
        Log.d("DMS_TASK_CLOSE", "begdate = " + getBegMonth(task.date).toString() +
                " enddate =" + getEndMonth(task.date).toString())

        var sqlQuery = "select TASK._id as _id," +
                " TASK.type as type, " +
                " TASK.name as name, " +
                " TASK.date as date, " +
                " TASK.summa as summa " +
                " from  " + DB_TASK_TABLE + " as TASK " +
                " where " + CL_TASK_TYPE + " = " + task.type.toString() +
                " AND " + CL_TASK_PARENT_ID + " = " + task.parent_id.toString() +
                " AND " + CL_TASK_FINISH + " = 0 "
        if (task.type == TASK.TASK_TYPE_FLAT) {
            sqlQuery += " AND " + CL_TASK_DATE + " >= " + getBegMonth(task.date).toString() +
                    " AND " + CL_TASK_DATE + " <= " + getEndMonth(task.date).toString()
        } else {
            sqlQuery += " AND " + CL_TASK_DATE + " >= " + getBegDay(task.date).toString() +
                    " AND " + CL_TASK_DATE + " <= " + getEndDay(task.date).toString()
        }
        sqlQuery += " order by _id asc"

        val c = mDB!!.rawQuery(sqlQuery, null)

        if (c != null) {
            if (c.moveToFirst()) {

                val id = c.getInt(c.getColumnIndex(CL_ID))
                res._id = id
                res.date = c.getLong(c.getColumnIndex(CL_TASK_DATE))
                res.summa = c.getDouble(c.getColumnIndex(CL_TASK_SUMMA))
                res.name = c.getString(c.getColumnIndex(CL_TASK_NAME))

                if (task.summa < res.summa) {
                    res.summa -= task.summa
                    task_UpdateSumma(res)
                } else {
                    res.finish = true
                    Log.d("DMS_TASK_CLOSE", "CLOSE id = $id")
                    task_setFinish(id)
                }
            }
            c.close()
        }

        return res

    }

    // получить все данные из таблицы DB_TABLE
    fun getTaskByType(type: Int): Cursor {

        val sqlQuery = "select TASK.*," +
                " coalesce(CREDIT_TABLE." + CL_CREDIT_TYPE + ",0) as " + CL_TASK_CREDIT_TYPE +
                " from  " + DB_TASK_TABLE + " as TASK " +
                "   left outer join " + DB_CREDIT_TABLE + " as CREDIT_TABLE" +
                "   on TASK.parent_id = CREDIT_TABLE._id" +
                "   and TASK." + CL_TASK_TYPE + "=" + TASK.TASK_TYPE_CREDIT.toString() +
                " where TASK." + CL_TASK_FINISH + " = 0 and TASK." + CL_TASK_TYPE + " = " + type.toString() +
                " order by date asc"

        return mDB!!.rawQuery(sqlQuery, null)

        //return mDB.query(DB_TASK_TABLE, null, CL_TASK_FINISH+" = 0 and "+CL_TASK_TYPE+" = "+String.valueOf(type), null, null, null, null);
    }

    // добавить запись в DB_TABLE
    fun taskExist(pTask: TASK): Boolean {

        var res = false

        val sel = CL_TASK_TYPE + " = ? " +
                "AND " + CL_TASK_PARENT_ID + " = ? " +
                "AND " + CL_TASK_DATE + " = ? "


        val arg = arrayOf(pTask.type.toString(), pTask.parent_id.toString(), pTask.date.toString())

        val c = mDB!!.query(DB_TASK_TABLE, null, sel, arg, null, null, null)

        if (c != null) {
            if (c.moveToFirst()) res = true
            c.close()
        }

        return res
    }

    // добавить запись в DB_TABLE
    fun task_Add(pTask: TASK) {
        task_Add(null, pTask)
    }

    fun task_Add(Activity: Context?, pTask: TASK) {

        Log.d("DMS_CREDIT", "go to task add " + pTask.name + " . parent_id = " + pTask.parent_id + " . type = " + pTask.type + " . date = " + pTask.date)

        // дубли пропускаем
        if (taskExist(pTask)) {
            Log.d("DMS_CREDIT", "TASK EXIST")
            return
        }

        Log.d("DMS_CREDIT", "TASK ADDED")

        val cv = ContentValues()
        cv.put(CL_TASK_NAME, pTask.name)
        cv.put(CL_TASK_TYPE, pTask.type)
        cv.put(CL_TASK_PARENT_ID, pTask.parent_id)
        cv.put(CL_TASK_SUMMA, pTask.summa)
        cv.put(CL_TASK_DATE, pTask.date)
        cv.put(CL_TASK_FINISH, if (pTask.finish) 1 else 0)

        //cv.put(COLUMN_IMG, img);
        mDB!!.insert(DB_TASK_TABLE, null, cv)

        if (Activity != null)
        // Создать уведомления
            sendTask(Activity, pTask)

    }

    // добавить запись в DB_TABLE
    fun taskTypeAdd(_id: Int, text: String) {
        val cv = ContentValues()
        cv.put(CL_ID, _id)
        cv.put(CL_TASKTYPE_NAME, text)
        mDB!!.insert(DB_TASKTYPE_TABLE, null, cv)
    }


    // добавить запись в FLAT
    fun task_Update(pTask: TASK) {

        val cv = ContentValues()
        cv.put(CL_TASK_NAME, pTask.name)
        cv.put(CL_TASK_TYPE, pTask.type)
        cv.put(CL_TASK_PARENT_ID, pTask.parent_id)
        cv.put(CL_TASK_SUMMA, pTask.summa)
        cv.put(CL_TASK_DATE, pTask.date)
        cv.put(CL_TASK_FINISH, if (pTask.finish) 1 else 0)

        //cv.put(COLUMN_IMG, img);
        mDB!!.update(DB_TASK_TABLE, cv, "_id = ?", arrayOf(pTask._id.toString()))
    }

    //
    fun task_UpdateSumma(pTask: TASK) {

        val cv = ContentValues()
        cv.put(CL_TASK_SUMMA, pTask.summa)

        //cv.put(COLUMN_IMG, img);
        mDB!!.update(DB_TASK_TABLE, cv, "_id = ?", arrayOf(pTask._id.toString()))
    }

    // добавить запись в FLAT
    fun task_setFinish(task_id: Int) {

        val cv = ContentValues()
        cv.put(CL_TASK_FINISH, 1)

        //cv.put(COLUMN_IMG, img);
        mDB!!.update(DB_TASK_TABLE, cv, "_id = ?", arrayOf(task_id.toString()))
    }

    // удалить запись из DB_TABLE
    fun task_Delete(id: Long) {
        mDB!!.delete(DB_TASK_TABLE, "$CL_ID = $id", null)
    }

    fun deleteAllTask() {
        mDB!!.delete(DB_TASK_TABLE, null, null)
    }

    // получить все данные из таблицы FLAT
    fun getFlat(id: Int): HOME {

        val flat = HOME()

        val c = mDB!!.query(DB_FLAT_TABLE, null, "$CL_ID = $id", null, null, null, null)

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    flat._id = c.getInt(c.getColumnIndex(CL_ID))
                    flat.name = c.getString(c.getColumnIndex(CL_FLAT_NAME))
                    flat.adres = c.getString(c.getColumnIndex(CL_FLAT_ADRES))
                    flat.param = c.getString(c.getColumnIndex(CL_FLAT_PARAM)) ?: ""
                    flat.isCounter = c.getInt(c.getColumnIndex(CL_FLAT_ISCOUNTER)).toBoolean()
                    flat.isPay = c.getInt(c.getColumnIndex(CL_FLAT_ISPAY)).toBoolean()
                    flat.lic = c.getString(c.getColumnIndex(CL_FLAT_LICNUMBER))
                    flat.day_beg = c.getInt(c.getColumnIndex(CL_FLAT_DAY_BEG))
                    flat.day_end = c.getInt(c.getColumnIndex(CL_FLAT_DAY_END))
                    flat.credit_id = c.getInt(c.getColumnIndex(CL_FLAT_CREDIT_ID))
                    flat.summa = c.getInt(c.getColumnIndex(CL_FLAT_SUMMA)).toDouble()
                    flat.finish = c.getInt(c.getColumnIndex(CL_FLAT_FINISH)).toBoolean()
                    flat.type = HomeType.getById(c.getInt(c.getColumnIndex(CL_FLAT_TYPE)))
                    flat.foto = c.getBlob(c.getColumnIndex(CL_FLAT_FOTO))

                    flat.isArenda = c.getInt(c.getColumnIndex(CL_FLAT_ISARENDA)).toBoolean()
                    flat.day_arenda = c.getInt(c.getColumnIndex(CL_FLAT_DAY_ARENDA))
                    flat.summa_arenda = c.getInt(c.getColumnIndex(CL_FLAT_SUMMA_ARENDA)).toDouble()

                } while (c.moveToNext())
            }
            c.close()
        }

        return flat
    }

    // добавить запись в FLAT
    fun flat_Add(pFlat: HOME) {

        val cv = ContentValues()
        cv.put(CL_FLAT_NAME, pFlat.name)
        cv.put(CL_FLAT_ADRES, pFlat.adres)
        cv.put(CL_FLAT_ISCOUNTER, pFlat.isCounter)
        cv.put(CL_FLAT_DAY_BEG, pFlat.day_beg)
        cv.put(CL_FLAT_DAY_END, pFlat.day_end)
        cv.put(CL_FLAT_ISPAY, pFlat.isPay)
        cv.put(CL_FLAT_LICNUMBER, pFlat.lic)
        cv.put(CL_FLAT_SUMMA, pFlat.summa)
        cv.put(CL_FLAT_CREDIT_ID, pFlat.credit_id)
        cv.put(CL_FLAT_TYPE, pFlat.type.type)
        cv.put(CL_FLAT_FINISH, pFlat.finish)
        cv.put(CL_FLAT_PARAM, pFlat.param)
        cv.put(CL_FLAT_FOTO, pFlat.foto)

        cv.put(CL_FLAT_ISARENDA, pFlat.isArenda)
        cv.put(CL_FLAT_DAY_ARENDA, pFlat.day_arenda)
        cv.put(CL_FLAT_SUMMA_ARENDA, pFlat.summa_arenda)

        //cv.put(COLUMN_IMG, img);
        mDB!!.insert(DB_FLAT_TABLE, null, cv)
    }

    // добавить запись в FLAT
    fun flat_Update(pFlat: HOME) {

        val cv = ContentValues()
        cv.put(CL_FLAT_NAME, pFlat.name)
        cv.put(CL_FLAT_ADRES, pFlat.adres)
        cv.put(CL_FLAT_ISCOUNTER, pFlat.isCounter)
        cv.put(CL_FLAT_ISPAY, pFlat.isPay)
        cv.put(CL_FLAT_LICNUMBER, pFlat.lic)
        cv.put(CL_FLAT_DAY_BEG, pFlat.day_beg)
        cv.put(CL_FLAT_DAY_END, pFlat.day_end)
        cv.put(CL_FLAT_SUMMA, pFlat.summa)
        cv.put(CL_FLAT_CREDIT_ID, pFlat.credit_id)
        cv.put(CL_FLAT_TYPE, pFlat.type.type)
        cv.put(CL_FLAT_FINISH, pFlat.finish)
        cv.put(CL_FLAT_PARAM, pFlat.param)
        cv.put(CL_FLAT_FOTO, pFlat.foto)

        cv.put(CL_FLAT_ISARENDA, pFlat.isArenda)
        cv.put(CL_FLAT_DAY_ARENDA, pFlat.day_arenda)
        cv.put(CL_FLAT_SUMMA_ARENDA, pFlat.summa_arenda)


        //cv.put(COLUMN_IMG, img);
        mDB!!.update(DB_FLAT_TABLE, cv, "_id = ?", arrayOf(pFlat._id.toString()))
    }

    // удалить запись из FLAT
    fun flat_Delete(id: Long) {
        mDB!!.delete(DB_FLAT_TABLE, "$CL_ID = $id", null)
    }


    fun flat_DeleteAll() {
        mDB!!.delete(DB_FLAT_TABLE, null, null)
    }

    // получить все данные из таблицы DB_TABLE
    fun getFlatPayments(flat_id: Int): Cursor {
        return mDB!!.query(DB_FLAT_ACCOUNT_TABLE, null, "flat_id = ?", arrayOf("" + flat_id), null, null, CL_FLAT_ACC_DATE)
    }

    // получить все данные из таблицы DB_TABLE
    fun getFlatPayment(pay_id: Int): FlatPayment {

        val pay = FlatPayment()

        val c = mDB!!.query(DB_FLAT_ACCOUNT_TABLE, null, "_id = ?", arrayOf("" + pay_id), null, null, null)

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    pay._id = c.getInt(c.getColumnIndex(CL_ID))
                    pay.date = c.getLong(c.getColumnIndex(CL_FLAT_ACC_DATE))
                    pay.summa = c.getDouble(c.getColumnIndex(CL_FLAT_ACC_SUMMA))
                    pay.paymentType = FlatPaymentType.getById(c.getInt(c.getColumnIndex(CL_FLAT_ACC_TYPE)))
                    pay.operation = FlatPaymentOperationType.getById(c.getInt(c.getColumnIndex(CL_FLAT_ACC_OPERATION)))
                    pay.comment = c.getString(c.getColumnIndex(CL_FLAT_ACC_COMMENT))
                    pay.flat_id = c.getInt(c.getColumnIndex(CL_FLAT_ACC_FLAT_ID))
                } while (c.moveToNext())
            }
            c.close()
        }

        return pay
    }

    // получить все данные из таблицы DB_TABLE
    fun getAllFlats(): List<HOME> {
        val c: Cursor? =  mDB!!.query(DB_FLAT_TABLE, null, null, null, null, null, null)

        val listData = mutableListOf<HOME>()

        if (c != null) {

            if (c.moveToFirst()) {
                do {

                    val type = c.getInt(c.getColumnIndex(CL_FLAT_TYPE))
                    val taskType = HomeType.getById(type)

                    val foto = c.getBlob(c.getColumnIndex(CL_FLAT_FOTO))

                    val flat = HOME(
                        _id = c.getInt(c.getColumnIndex(DB.CL_ID)),
                        name = c.getString(c.getColumnIndex(DB.CL_FLAT_NAME)),
                        adres = c.getString(c.getColumnIndex(DB.CL_FLAT_ADRES)),
                        param = c.getString(c.getColumnIndex(DB.CL_FLAT_PARAM)),
                        type = taskType,
                        credit_id = c.getInt(c.getColumnIndex(DB.CL_FLAT_CREDIT_ID)),
                        isPay = (c.getInt(c.getColumnIndex(DB.CL_FLAT_ISPAY)).toBoolean()),
                        isArenda = (c.getInt(c.getColumnIndex(DB.CL_FLAT_ISARENDA)).toBoolean()),
                        summa_arenda = c.getDouble(c.getColumnIndex(DB.CL_FLAT_SUMMA_ARENDA)),
                        isCounter = (c.getInt(c.getColumnIndex(DB.CL_FLAT_ISCOUNTER)).toBoolean()),
                        finish = (c.getInt(c.getColumnIndex(DB.CL_FLAT_FINISH)).toBoolean()),
                        foto = foto
                    )

                    listData.add(flat)

                } while (c.moveToNext())
            }
            c.close()
        }

        return listData
    }

    //  === CREDIT ===
    fun getCreditById(cred_id: Long): Credit {

        val credit = Credit()

        val c = mDB!!.query(DB_CREDIT_TABLE, null, "_id = ?", arrayOf("" + cred_id), null, null, null)

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    credit.id = c.getInt(c.getColumnIndex(CL_ID))
                    credit.name = c.getString(c.getColumnIndex(CL_CREDIT_NAME))
                    credit.date = c.getLong(c.getColumnIndex(CL_CREDIT_DATE))
                    credit.type = CreditType.getById(c.getInt(c.getColumnIndex(CL_CREDIT_TYPE)))
                    credit.procent = c.getDouble(c.getColumnIndex(CL_CREDIT_PROCENT))
                    credit.period = c.getInt(c.getColumnIndex(CL_CREDIT_PERIOD))
                    credit.summa_pay = c.getDouble(c.getColumnIndex(CL_CREDIT_SUMMA_PAY))
                    credit.summa = c.getDouble(c.getColumnIndex(CL_CREDIT_SUMMA))
                    credit.finish = c.getInt(c.getColumnIndex(CL_CREDIT_FINISH)).toBoolean()
                } while (c.moveToNext())
            }
            c.close()
        }

        return credit
    }

    // получить все данные из таблицы CREDIT
    fun credit_GetCreditData(showClosed: Boolean = true): Cursor {

        val cur: Cursor

        var sqlQuery = "select CREDIT._id as _id," +
                "CREDIT.name as CREDIT_name, " +
                "coalesce(CREDIT.type,0) as CREDIT_type, " +
                "CREDIT.date as CREDIT_date, " +
                "CREDIT.summa as CREDIT_summa, " +
                "CREDIT.period as CREDIT_period, " +
                "CREDIT.procent as CREDIT_procent, " +
                "CREDIT.summa_pay as CREDIT_summa_pay, " +
                "coalesce(CREDIT.finish,0) as CREDIT_finish, " +

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

                " left join ( select distinct GRAPHIC.credit_id as credit_id, " +
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
                "   on CREDIT._id = PAYMENT.PAYMENT_credit_id "


        if (!showClosed) {
            sqlQuery+= "  where CREDIT_finish = 0 and RESULT_rest > 0 "
        }

        sqlQuery+= ""+
                //"order by RESULT_rest desc, CREDIT_date "+
                "order by CREDIT_finish, GRAPHIC_date, CREDIT_date desc" +
                ""

        //cur  = mDB.rawQuery(sqlQuery, new String[] {"1"});

        cur = mDB!!.rawQuery(sqlQuery, null)

        return cur
    }


    // получить очередной платеж по кредиту
    fun credit_GetNextPayment(credit_id: Long): PAYMENT {

        val cur: Cursor

        val sqlQuery = "select CREDIT._id as _id," +
                "coalesce(GRAPHIC.credit_id,0) as credit_id, " +
                "coalesce(GRAPHIC.GRAPHIC_date,0) as date, " +
                "coalesce(GRAPHIC.GRAPHIC_summa,0) as summa, " +
                "coalesce(GRAPHIC.GRAPHIC_summa_credit,0) as summa_credit, " +
                "coalesce(GRAPHIC.GRAPHIC_summa_procent,0) as summa_procent " +
                " from CREDIT as CREDIT " +
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
                " where CREDIT._id = " + credit_id.toString() +
                ""

        val c = mDB!!.rawQuery(sqlQuery, null)

        val pay = PAYMENT()

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    pay.credit_id = c.getInt(c.getColumnIndex(CL_GRAPHIC_ID_CREDIT))
                    pay.date = c.getLong(c.getColumnIndex(CL_GRAPHIC_DATE))
                    pay.summa = c.getDouble(c.getColumnIndex(CL_GRAPHIC_SUMMA))
                    pay.summa_credit = c.getDouble(c.getColumnIndex(CL_GRAPHIC_SUMMA_CREDIT))
                    pay.summa_procent = c.getDouble(c.getColumnIndex(CL_GRAPHIC_SUMMA_PROCENT))
                } while (c.moveToNext())
            }
            c.close()
        }

        return pay
    }


    // получить все данные из таблицы CREDIT
    fun credit_GetAll(): Cursor {
        return mDB!!.query(DB_CREDIT_TABLE, null, null, null, null, null, null)
    }

    // добавить запись в CREDIT
    fun credit_Add(credit: Credit) {

        val cv = ContentValues()
        cv.put(CL_CREDIT_NAME, credit.name)
        cv.put(CL_CREDIT_TYPE, credit.type.type)
        cv.put(CL_CREDIT_DATE, credit.date)
        cv.put(CL_CREDIT_SUMMA, credit.summa)
        cv.put(CL_CREDIT_SUMMA_PAY, credit.summa_pay)
        cv.put(CL_CREDIT_PERIOD, credit.period)
        cv.put(CL_CREDIT_PROCENT, credit.procent)
        cv.put(CL_CREDIT_SUMMA_PAY, credit.summa_pay)
        cv.put(CL_CREDIT_FINISH, credit.finish.toInt())

        mDB!!.insert(DB_CREDIT_TABLE, null, cv)
    }

    // добавить запись в CREDIT
    fun credit_Update(credit: Credit) {

        val cv = ContentValues()
        cv.put(CL_CREDIT_NAME, credit.name)
        cv.put(CL_CREDIT_TYPE, credit.type.type)
        cv.put(CL_CREDIT_DATE, credit.date)
        cv.put(CL_CREDIT_SUMMA, credit.summa)
        cv.put(CL_CREDIT_PERIOD, credit.period)
        cv.put(CL_CREDIT_PROCENT, credit.procent)
        cv.put(CL_CREDIT_SUMMA_PAY, credit.summa_pay)
        cv.put(CL_CREDIT_FINISH, credit.finish.toInt())

        mDB!!.update(DB_CREDIT_TABLE, cv, "_id = ?", arrayOf(credit.id.toString()))
    }


    // получить все данные из таблицы CREDIT
    fun GetCredit(credit_id: Int): Credit {

        Log.d("DMS", "GetCredit")

        val cred = Credit()

        val c = mDB!!.query(DB_CREDIT_TABLE, null, "_id = ?", arrayOf("" + credit_id), null, null, null)
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    cred.id = c.getInt(c.getColumnIndex(CL_ID))
                    cred.name = c.getString(c.getColumnIndex(CL_CREDIT_NAME))
                    cred.type = CreditType.getById( c.getInt(c.getColumnIndex(CL_CREDIT_TYPE)) )
                    cred.summa = c.getDouble(c.getColumnIndex(CL_CREDIT_SUMMA))
                    cred.summa_pay = c.getDouble(c.getColumnIndex(CL_CREDIT_SUMMA_PAY))
                    cred.date = c.getLong(c.getColumnIndex(CL_CREDIT_DATE))
                    cred.procent = c.getDouble(c.getColumnIndex(CL_CREDIT_PROCENT))
                    cred.period = c.getInt(c.getColumnIndex(CL_CREDIT_PERIOD))
                    cred.finish = c.getInt(c.getColumnIndex(CL_CREDIT_FINISH)).toBoolean()
                } while (c.moveToNext())
            }
            c.close()
        }

        return cred
    }

    // получить список CREDIT
    fun getAllCredits(isOnlyActive: Boolean = false): List<Credit> {

        val creditList = mutableListOf<Credit>()

        val c = mDB!!.query(DB_CREDIT_TABLE,
            null,
            if (isOnlyActive) {
                "$CL_CREDIT_FINISH = 0"
            } else {
                null
            },
            null,
            null,
            null,
            "$CL_CREDIT_FINISH asc, $CL_CREDIT_SUMMA desc")

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    val credit =Credit()

                    credit.id = c.getInt(c.getColumnIndex(CL_ID))
                    credit.name = c.getString(c.getColumnIndex(CL_CREDIT_NAME))
                    credit.type = CreditType.getById(c.getInt(c.getColumnIndex(CL_CREDIT_TYPE)))
                    credit.summa = c.getDouble(c.getColumnIndex(CL_CREDIT_SUMMA))
                    credit.summa_pay = c.getDouble(c.getColumnIndex(CL_CREDIT_SUMMA_PAY))
                    credit.date = c.getLong(c.getColumnIndex(CL_CREDIT_DATE))
                    credit.procent = c.getDouble(c.getColumnIndex(CL_CREDIT_PROCENT))
                    credit.period = c.getInt(c.getColumnIndex(CL_CREDIT_PERIOD))
                    credit.finish = c.getInt(c.getColumnIndex(CL_CREDIT_FINISH)).toBoolean()

                    creditList.add(credit)
                } while (c.moveToNext())
            }
            c.close()
        }

        return creditList
    }

    // ======= GRAPHIC ========== //

    fun GetGraphic(credit_id: Int, withPlan: Boolean): Cursor {

        Log.d("DMS", "GetGraphic. credit_id =$credit_id")

        var sqlQuery = "SELECT PAYMENT._id as _id," +
                "coalesce(PAYMENT.date,0) as date, " +
                "PAYMENT.summa as summa, " +
                "PAYMENT.summa_credit as summa_credit, " +
                "PAYMENT.summa_procent as summa_procent, " +
                "1 as done " +
                "from PAYMENT as PAYMENT " +
                "where PAYMENT.credit_id = " + credit_id

        if (withPlan) {
            sqlQuery += " UNION " +
                    "SELECT GRAPHIC._id as _id, " +
                    "GRAPHIC.date as date, " +
                    "GRAPHIC.summa as summa, " +
                    "GRAPHIC.summa_credit as summa_credit, " +
                    "GRAPHIC.summa_procent as summa_procent, " +
                    "0 as done " +
                    "from GRAPHIC as GRAPHIC " +
                    "where GRAPHIC.credit_id = " + credit_id +
                    " and GRAPHIC.date > coalesce((SELECT max(t.date) from PAYMENT as t where t.credit_id = " + credit_id + "), 0)" +
                    ""
        }

        sqlQuery += " order by date"

        val str_id = "" + credit_id
        return mDB!!.rawQuery(sqlQuery, null)

    }


    // получить все данные из таблицы CREDIT
    fun GetGraphicItem(payment_id: Int): PAYMENT {

        Log.d("DMS", "GetGraphicItem _id = $payment_id")

        val pay = PAYMENT()

        val c = mDB!!.query(DB_GRAPHIC_TABLE, null, "_id = ?", arrayOf("" + payment_id), null, null, null)
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    pay._id = c.getInt(c.getColumnIndex(CL_ID))
                    pay.credit_id = c.getInt(c.getColumnIndex(CL_GRAPHIC_ID_CREDIT))
                    pay.date = c.getLong(c.getColumnIndex(CL_GRAPHIC_DATE))
                    pay.summa = c.getDouble(c.getColumnIndex(CL_GRAPHIC_SUMMA))
                    pay.summa_credit = c.getDouble(c.getColumnIndex(CL_GRAPHIC_SUMMA_CREDIT))
                    pay.summa_procent = c.getDouble(c.getColumnIndex(CL_GRAPHIC_SUMMA_PROCENT))
                    pay.comment = c.getString(c.getColumnIndex(CL_GRAPHIC_COMMENT))
                } while (c.moveToNext())
            }
            c.close()
        }

        return pay
    }


    // добавить запись в PAYMENT
    fun graphic_Add(pPay: PAYMENT) {

        //Log.d("DMS_CREDIT", "ADD GRAPHIC. credit_id = "+String.valueOf(pPay.credit_id)+" date ="+String.valueOf(pPay.date)+" summa="+String.valueOf(pPay.summa));

        val cv = ContentValues()
        cv.put(CL_GRAPHIC_ID_CREDIT, pPay.credit_id)
        cv.put(CL_GRAPHIC_DATE, pPay.date)
        cv.put(CL_GRAPHIC_SUMMA, pPay.summa)
        cv.put(CL_GRAPHIC_SUMMA_CREDIT, pPay.summa_credit)
        cv.put(CL_GRAPHIC_SUMMA_PROCENT, pPay.summa_procent)
        cv.put(CL_GRAPHIC_COMMENT, pPay.comment)

        mDB!!.insert(DB_GRAPHIC_TABLE, null, cv)
    }


    // изменить запись в PAYMENT
    fun graphic_Update(pPay: PAYMENT) {

        val cv = ContentValues()
        cv.put(CL_GRAPHIC_DATE, pPay.date)
        cv.put(CL_GRAPHIC_SUMMA, pPay.summa)
        cv.put(CL_GRAPHIC_SUMMA_CREDIT, pPay.summa_credit)
        cv.put(CL_GRAPHIC_SUMMA_PROCENT, pPay.summa_procent)
        cv.put(CL_GRAPHIC_COMMENT, pPay.comment)

        mDB!!.update(DB_GRAPHIC_TABLE, cv, "_id = ? and credit_id=?", arrayOf(pPay._id.toString(), "" + pPay.credit_id))
    }


    // удалить запись из CREDIT
    fun credit_Delete(id: Long) {
        mDB!!.delete(DB_CREDIT_TABLE, "$CL_ID = $id", null)
    }


    // удалить запись из CREDIT
    fun graphic_Delete(id: Int) {
        mDB!!.delete(DB_GRAPHIC_TABLE, "$CL_ID = ?", arrayOf("" + id))
    }

    fun credit_DeleteAll() {
        mDB!!.delete(DB_CREDIT_TABLE, null, null)
    }


    fun graphic_DeleteAll(credit_id: Int) {
        mDB!!.delete(DB_GRAPHIC_TABLE, "credit_id=?", arrayOf("" + credit_id))
    }


    // ======= PAYMENT ========== //

    // получить все данные из таблицы PAYMENT
    fun GetPayment(payment_id: Int): PAYMENT {

        Log.d("DMS", "GetPayment _id = $payment_id")

        val pay = PAYMENT()

        val c = mDB!!.query(DB_PAYMENT_TABLE, null, "_id = ?", arrayOf("" + payment_id), null, null, null)
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    pay._id = c.getInt(c.getColumnIndex(CL_ID))
                    pay.credit_id = c.getInt(c.getColumnIndex(CL_PAYMENT_ID_CREDIT))
                    pay.date = c.getLong(c.getColumnIndex(CL_PAYMENT_DATE))
                    pay.summa = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA))
                    pay.summa_credit = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_CREDIT))
                    pay.summa_procent = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_PROCENT))
                    pay.summa_addon = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_ADDON))
                    pay.summa_plus = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_PLUS))
                    pay.summa_minus = c.getDouble(c.getColumnIndex(CL_PAYMENT_SUMMA_MINUS))
                    pay.comment = c.getString(c.getColumnIndex(CL_PAYMENT_COMMENT))
                } while (c.moveToNext())
            }
            c.close()
        }

        return pay
    }

    // добавить запись в PAYMENT
    fun payment_Add(pPay: PAYMENT) {

        Log.d("DMS_CREDIT", "ADD PAYMENT. credit_id = " + pPay.credit_id.toString() + " date =" + pPay.date.toString() + " summa=" + pPay.summa.toString())

        val cv = ContentValues()
        cv.put(CL_PAYMENT_ID_CREDIT, pPay.credit_id)
        cv.put(CL_PAYMENT_DATE, pPay.date)
        cv.put(CL_PAYMENT_SUMMA, pPay.summa)
        cv.put(CL_PAYMENT_SUMMA_CREDIT, pPay.summa_credit)
        cv.put(CL_PAYMENT_SUMMA_PROCENT, pPay.summa_procent)
        cv.put(CL_PAYMENT_SUMMA_ADDON, pPay.summa_addon)
        cv.put(CL_PAYMENT_SUMMA_PLUS, pPay.summa_plus)
        cv.put(CL_PAYMENT_SUMMA_MINUS, pPay.summa_minus)
        cv.put(CL_PAYMENT_COMMENT, pPay.comment)

        mDB!!.insert(DB_PAYMENT_TABLE, null, cv)
    }

    // изменить запись в PAYMENT
    fun payment_Update(pPay: PAYMENT) {

        val cv = ContentValues()
        cv.put(CL_PAYMENT_DATE, pPay.date)
        cv.put(CL_PAYMENT_SUMMA, pPay.summa)
        cv.put(CL_PAYMENT_SUMMA_CREDIT, pPay.summa_credit)
        cv.put(CL_PAYMENT_SUMMA_PROCENT, pPay.summa_procent)
        cv.put(CL_PAYMENT_SUMMA_ADDON, pPay.summa_addon)
        cv.put(CL_PAYMENT_SUMMA_PLUS, pPay.summa_plus)
        cv.put(CL_PAYMENT_SUMMA_MINUS, pPay.summa_minus)
        cv.put(CL_PAYMENT_COMMENT, pPay.comment)

        mDB!!.update(DB_PAYMENT_TABLE, cv, "_id = ? and credit_id=?", arrayOf(pPay._id.toString(), "" + pPay.credit_id))
    }


    // удалить запись из PAYMENT
    fun payment_Delete(id: Int) {
        mDB!!.delete(DB_PAYMENT_TABLE, "$CL_ID = ?", arrayOf("" + id))
    }

    fun payment_DeleteAll(credit_id: Int) {
        mDB!!.delete(DB_PAYMENT_TABLE, "credit_id=?", arrayOf("" + credit_id))
    }

    // добавить запись в FLAT_OPERATION
    fun flatAccount_Add(pAcc: FlatPayment) {

        Log.d("DMS", "ADD PAYMENT. flat_id = " + pAcc.flat_id.toString() + " date =" + pAcc.date.toString() + " summa=" + pAcc.summa.toString())

        val cv = ContentValues()
        cv.put(CL_FLAT_ACC_FLAT_ID, pAcc.flat_id)
        cv.put(CL_FLAT_ACC_TYPE, pAcc.paymentType.type)
        cv.put(CL_FLAT_ACC_DATE, pAcc.date)
        cv.put(CL_FLAT_ACC_SUMMA, pAcc.summa)
        cv.put(CL_FLAT_ACC_OPERATION, pAcc.operation.type)
        cv.put(CL_FLAT_ACC_COMMENT, pAcc.comment)

        mDB!!.insert(DB_FLAT_ACCOUNT_TABLE, null, cv)
    }

    // изменить запись в PAYMENT
    fun flatAccount_Update(pAcc: FlatPayment) {

        val cv = ContentValues()
        cv.put(CL_FLAT_ACC_FLAT_ID, pAcc.flat_id)
        cv.put(CL_FLAT_ACC_TYPE, pAcc.paymentType.type)
        cv.put(CL_FLAT_ACC_DATE, pAcc.date)
        cv.put(CL_FLAT_ACC_SUMMA, pAcc.summa)
        cv.put(CL_FLAT_ACC_OPERATION, pAcc.operation.type)
        cv.put(CL_FLAT_ACC_COMMENT, pAcc.comment)

        mDB!!.update(DB_FLAT_ACCOUNT_TABLE, cv, "_id = ? and flat_id=?", arrayOf(pAcc._id.toString(), "" + pAcc.flat_id))
    }


    // удалить запись из PAYMENT
    fun flatAccount_Delete(id: Int) {
        mDB!!.delete(DB_FLAT_ACCOUNT_TABLE, "$CL_ID = $id", null)
    }

    fun flatAccount_DeleteAll(credit_id: Int) {
        mDB!!.delete(DB_FLAT_ACCOUNT_TABLE, "flat_id=?", arrayOf("" + credit_id))
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    // класс по созданию и управлению БД
    private inner class DBHelper(context: Context, name: String, factory: CursorFactory?,
                                 version: Int) : SQLiteOpenHelper(context, name, factory, version) {

        // создаем и заполняем БД
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(DB_TASK_CREATE)
            db.execSQL(DB_FLAT_CREATE)
            db.execSQL(DB_CREDIT_CREATE)
            db.execSQL(DB_GRAPHIC_CREATE)
            db.execSQL(DB_PAYMENT_CREATE)

            db.execSQL(DB_FLAT_ACCOUNT_CREATE)

            db.execSQL(DB_TASKTYPE_CREATE)
            db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_CREDIT.toString() + ",'Платежи по кредиту');")
            db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_FLAT.toString() + ",'Квартплата');")
            db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_COUNTER.toString() + ",'Показания счетчиков');")
            db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_OTHER.toString() + ",'Прочие задачи');")

        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

            if (newVersion == 5) {
                Log.d("1", "START")

                db.execSQL(DB_DROP)
                //           db.execSQL(DB_FLAT_DROP);
                //            db.execSQL(DB_GRAPHIC_DROP);
                //           db.execSQL(DB_PAYMENT_DROP);
                //            db.execSQL(DB_CREDIT_DROP);

                Log.d("1", "DROP")

                db.execSQL(DB_TASK_CREATE)

                // Version 5
                db.execSQL(DB_FLAT_CREATE)

                Log.d("1", "DB_FLAT_CREATE")

                db.execSQL(DB_CREDIT_CREATE)

                Log.d("1", "DB_CREDIT_CREATE")

                db.execSQL(DB_GRAPHIC_CREATE)

                Log.d("1", "DB_GRAPHIC_CREATE")

                db.execSQL(DB_PAYMENT_CREATE)

                Log.d("1", "DB_PAYMENT_CREATE")
            }

            if (newVersion == 7) {
                db.execSQL(DB_GRAPHIC_DROP)
                db.execSQL(DB_GRAPHIC_CREATE)
            }

            if (newVersion == 8) {
                db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_ISPAY tinyint(1) default 0;")
                //db.execSQL("alter table "+DB_FLAT_TABLE+" add column "+ CL_FLAT_LICNUMBER + " int;");
            }

            if (newVersion == 9) {
                db.execSQL(DB_TASKTYPE_CREATE)
                db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_CREDIT.toString() + ",'Платежи по кредиту');")
                db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_FLAT.toString() + ",'Квартплата');")
                db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_COUNTER.toString() + ",'Показания счетчиков');")
                db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_OTHER.toString() + ",'Прочие задачи');")

            }

            if (newVersion == 10) {
                db.execSQL(DB_FLAT_ACCOUNT_CREATE)

                db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_CREDIT_ID integer;")
            }

            if (newVersion == 11) {
                db.execSQL(DB_FLAT_ACCOUNT_DROP)
                db.execSQL(DB_FLAT_ACCOUNT_CREATE)

                db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_SUMMA double;")
            }

            if (newVersion == 12) {
                db.execSQL("alter table $DB_CREDIT_TABLE add column $CL_CREDIT_TYPE integer;")
                db.execSQL(DB_FLAT_ACCOUNT_CREATE)
                db.execSQL(DB_TASKTYPE_CREATE)
                db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_CREDIT.toString() + ",'Платежи по кредиту');")
                db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_FLAT.toString() + ",'Квартплата');")
                db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_COUNTER.toString() + ",'Показания счетчиков');")
                db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_OTHER.toString() + ",'Прочие задачи');")

            }

            if (newVersion == 15) {

                // CREDIT
                db.execSQL("alter table $DB_CREDIT_TABLE RENAME to tmp_credit_table;")
                db.execSQL(DB_CREDIT_CREATE)
                db.execSQL("INSERT INTO " + DB_CREDIT_TABLE + " (_id, name, type, date, summa, summa_pay, procent, period, finish) " +
                        "   SELECT _id, name, type, date, summa*100, summa_pay*100, procent*100, period, finish " +
                        "   FROM tmp_credit_table;")
                db.execSQL(" DROP TABLE tmp_credit_table;")

                // PAYMENT
                db.execSQL("alter table $DB_PAYMENT_TABLE RENAME to tmp_payment_table;")
                db.execSQL(DB_PAYMENT_CREATE)
                db.execSQL("INSERT INTO " + DB_PAYMENT_TABLE + " (_id, credit_id, date, summa, summa_credit, summa_procent, summa_addon, summa_plus, summa_minus, comment) " +
                        "   SELECT _id, credit_id, date, summa*100, summa_credit*100, summa_procent*100, summa_addon*100, summa_plus*100, summa_minus*100, comment " +
                        "   FROM tmp_payment_table;")
                db.execSQL(" DROP TABLE tmp_payment_table;")

                // GRAPHIC
                db.execSQL("alter table $DB_GRAPHIC_TABLE RENAME to tmp_graphic_table;")
                db.execSQL(DB_GRAPHIC_CREATE)
                db.execSQL("INSERT INTO " + DB_GRAPHIC_TABLE + " (_id, credit_id, date, rest, summa, summa_credit, summa_procent, summa_addon, summa_plus, summa_minus, done, comment) " +
                        "   SELECT _id, credit_id, date, rest*100, summa*100, summa_credit*100, summa_procent*100, summa_addon*100, summa_plus*100, summa_minus*100, done, comment " +
                        "   FROM tmp_graphic_table;")
                db.execSQL(" DROP TABLE tmp_graphic_table;")

                // FLAT FlatPayment
                db.execSQL("alter table $DB_FLAT_ACCOUNT_TABLE RENAME to tmp_flat_acc_table;")
                db.execSQL(DB_FLAT_ACCOUNT_CREATE)
                db.execSQL("INSERT INTO " + DB_FLAT_ACCOUNT_TABLE + " (_id, flat_id, type, operation, date, summa, comment) " +
                        "   SELECT _id, flat_id, type, operation, date, summa*100, comment " +
                        "   FROM tmp_flat_acc_table;")
                db.execSQL(" DROP TABLE tmp_flat_acc_table;")

                // FLAT
                db.execSQL("alter table $DB_FLAT_TABLE RENAME to tmp_flat_table;")
                db.execSQL(DB_FLAT_CREATE)
                db.execSQL("INSERT INTO " + DB_FLAT_TABLE + " (_id, name, adres, isCounter, isPay, licNumber, day_beg, day_end, isTask, credit_id, summa) " +
                        "   SELECT _id, name, adres, isCounter, isPay, licNumber, day_beg, day_end, isTask, credit_id, summa*100 " +
                        "   FROM tmp_flat_table;")
                db.execSQL(" DROP TABLE tmp_flat_table;")

            }

            if (newVersion == 16) {

                // CREDIT
                db.execSQL("alter table $DB_CREDIT_TABLE RENAME to tmp_credit_table;")
                db.execSQL(DB_CREDIT_CREATE)
                db.execSQL("INSERT INTO " + DB_CREDIT_TABLE + " (_id, name, type, date, summa, summa_pay, procent, period, finish) " +
                        "   SELECT _id, name, type, date, summa/100, summa_pay/100, procent/100, period, finish " +
                        "   FROM tmp_credit_table;")
                db.execSQL(" DROP TABLE tmp_credit_table;")

                // PAYMENT
                db.execSQL("alter table $DB_PAYMENT_TABLE RENAME to tmp_payment_table;")
                db.execSQL(DB_PAYMENT_CREATE)
                db.execSQL("INSERT INTO " + DB_PAYMENT_TABLE + " (_id, credit_id, date, summa, summa_credit, summa_procent, summa_addon, summa_plus, summa_minus, comment) " +
                        "   SELECT _id, credit_id, date, summa/100, summa_credit/100, summa_procent/100, summa_addon/100, summa_plus/100, summa_minus/100, comment " +
                        "   FROM tmp_payment_table;")
                db.execSQL(" DROP TABLE tmp_payment_table;")

                // GRAPHIC
                db.execSQL("alter table $DB_GRAPHIC_TABLE RENAME to tmp_graphic_table;")
                db.execSQL(DB_GRAPHIC_CREATE)
                db.execSQL("INSERT INTO " + DB_GRAPHIC_TABLE + " (_id, credit_id, date, rest, summa, summa_credit, summa_procent, summa_addon, summa_plus, summa_minus, done, comment) " +
                        "   SELECT _id, credit_id, date, rest/100, summa/100, summa_credit/100, summa_procent/100, summa_addon/100, summa_plus/100, summa_minus/100, done, comment " +
                        "   FROM tmp_graphic_table;")
                db.execSQL(" DROP TABLE tmp_graphic_table;")

                // FLAT FlatPayment
                db.execSQL("alter table $DB_FLAT_ACCOUNT_TABLE RENAME to tmp_flat_acc_table;")
                db.execSQL(DB_FLAT_ACCOUNT_CREATE)
                db.execSQL("INSERT INTO " + DB_FLAT_ACCOUNT_TABLE + " (_id, flat_id, type, operation, date, summa, comment) " +
                        "   SELECT _id, flat_id, type, operation, date, summa/100, comment " +
                        "   FROM tmp_flat_acc_table;")
                db.execSQL(" DROP TABLE tmp_flat_acc_table;")

                // FLAT
                db.execSQL("alter table $DB_FLAT_TABLE RENAME to tmp_flat_table;")
                db.execSQL(DB_FLAT_CREATE)
                db.execSQL("INSERT INTO " + DB_FLAT_TABLE + " (_id, name, adres, isCounter, isPay, licNumber, day_beg, day_end, isTask, credit_id, summa) " +
                        "   SELECT _id, name, adres, isCounter, isPay, licNumber, day_beg, day_end, isTask, credit_id, summa/100 " +
                        "   FROM tmp_flat_table;")
                db.execSQL(" DROP TABLE tmp_flat_table;")

            }

            if (newVersion == 17) {
                db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_TYPE integer;")
                db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_FINISH integer;")
            }

            if (newVersion == 18) {
                db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_PARAM text;")
                db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_FOTO blob;")
            }
            if (newVersion == 20) {

              //  db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_PARAM text;")
              //  db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_FOTO blob;")
                db.execSQL("update $DB_FLAT_TABLE set $CL_FLAT_FOTO=null;")

            }

            if (newVersion == 21) {
                db.execSQL("insert into " + DB_TASKTYPE_TABLE + " (_id, name) VALUES (" + TASK.TASK_TYPE_ARENDA.toString() + ",'Аренда');")

                db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_ISARENDA tinyint(1) default 0;")
                db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_DAY_ARENDA tinyint(1);")
                db.execSQL("alter table $DB_FLAT_TABLE add column $CL_FLAT_SUMMA_ARENDA double;")

            }

        }
    }

}