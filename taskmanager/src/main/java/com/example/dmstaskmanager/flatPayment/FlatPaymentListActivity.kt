package com.example.dmstaskmanager.flatPayment

import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.R

import android.content.Intent
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView

import com.example.dmstaskmanager.classes.FlatPayment
import com.example.dmstaskmanager.classes.FlatPaymentOperationType
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.graphic.GraphicItemActivity
import com.example.dmstaskmanager.payment.PaymentActivity
import com.example.dmstaskmanager.utils.formatD2

import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap

class FlatPaymentListActivity : AppCompatActivity() {

     val CM_DELETE_ID = 1
     val CM_PAYMENT_ID = 2
     val CM_EDIT_ID = 3

     var flat_id: Int = 0
     var payment_id = -1
     lateinit var flat: HOME
     var payment: FlatPayment? = null

     var btnOk: Button? = null
     lateinit var db: DB

     var dateAndTime = Calendar.getInstance()

    // Основной список - список платежей
     lateinit var listData: ArrayList<Map<String, Any>>
     lateinit var lvFlatPaymentList: ListView // интерфейс списка
     lateinit var sAdapter: SimpleAdapter  // адаптер списка

     var months = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flat_payment_list)

        // === ToolBar ===
        val toolbar = findViewById<Toolbar>(R.id.toolbar_actionbar_item)
        setSupportActionBar(toolbar)
        // Кнопка Назад
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Заголовок
        supportActionBar!!.setTitle(R.string.toolbar_payment)

        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.color.FlatItemToolbar))

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.FlatItemWindowsBar)


        // === Инициализация ===
        // === Инициализация текущего кредита, по которому открывается график платежей ===
        // Он передается в intent
        val intent = intent
        val str_id = intent.getStringExtra("id")
        if (str_id.isEmpty()) {
            return  // нет ссылки на кредит => выход
        }

        // ИД кредита
        flat_id = Integer.parseInt(str_id)

        // открываем подключение к БД
        db = DB(this)
        db.open()
        // получаем объект Кредит - текущий кредит
        flat = db.getFlat(flat_id)

        // инициализируем основной список
        listData = ArrayList()

        // Первоначальное заполнение списка Факт+План платежей
        fillGraphic()


        // ===== Инициализируем адаптер списка =====
        // массив имен атрибутов, из которых будут читаться данные
        val from = arrayOf(DB.CL_FLAT_ACC_DATE, DB.CL_FLAT_ACC_OPERATION, DB.CL_FLAT_ACC_SUMMA)
        // массив ID View-компонентов, в которые будут вставлять данные
        val to = intArrayOf(R.id.tvFPDate, R.id.tvFPOper, R.id.tvSumma)

        // создаем адаптер
        sAdapter = SimpleAdapter(this, listData, R.layout.item_flat_pay, from, to)

        // Указываем адаптеру свой биндер
        sAdapter.viewBinder = MyViewBinder()

        // определяем список и присваиваем ему адаптер
        lvFlatPaymentList = findViewById<View>(R.id.lvFlatPaymentList) as ListView

        lvFlatPaymentList.adapter = sAdapter
        // ===== //

        // добавляем контекстное меню к списку
        registerForContextMenu(lvFlatPaymentList)


        //  ==== Обработчики событий ====


        // Нажатие на пункт списка
        lvFlatPaymentList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // По нажатию открывается либо текущий фактический платеж (если у него задан _id > 0)
            // либо новый платеж (_id<0) на основании параметров планового платежа

            // получаем ИД платежа из элемента списка, по его позиции
            val str_payment_id = listData[position]["_id"].toString()

            Log.d("DMS_LOG", "itemClick: position = $position, id = $str_payment_id")


            // извлекаем id записи
            val intent: Intent
            intent = Intent(view.context, FlatPaymentActivity::class.java)

            // Параметры платежа
            intent.putExtra("id", "" + flat_id)
            intent.putExtra("payment_id", "" + str_payment_id)

            startActivityForResult(intent, 5)
        }


    }

    fun AddPaymentActivity() {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("id", "" + flat_id)
        startActivityForResult(intent, 1)
    }

    fun AddGrapgicActivity() {
        Log.d("DMS", "AddGrapgicActivity")
        val intent = Intent(this, GraphicItemActivity::class.java)
        intent.putExtra("id", "" + flat_id)
        startActivityForResult(intent, 2)
    }

    // ====== Toolbar ======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_graphic, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        Log.d("DMS", "ПУНКТ МЕНЮ " + item.itemId)


        val id = item.itemId

        if (id == R.id.toolbar_payment_add) {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("id", "" + flat_id)
            startActivityForResult(intent, 1)
        }

        if (id == R.id.toolbar_graphic_add) {
            val intent = Intent(this, GraphicItemActivity::class.java)
            intent.putExtra("id", "" + flat_id)
            startActivityForResult(intent, 1)
        }


        if (id == R.id.toolbar_delete_all) {
            db.payment_DeleteAll(flat_id)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)

        menu.add(0, CM_PAYMENT_ID, 1, R.string.action_payment_add)

        menu.add(0, CM_DELETE_ID, 100, R.string.action_payment_deleteAll)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo
            // извлекаем id записи и удаляем соответствующую запись в БД

            // получаем ИД платежа из элемента списка, по его позиции
            val index = acmi.position - 1
            val str_payment_id = listData[index]["_id"].toString()
            val done = Integer.parseInt(listData[index]["done"].toString())

            //            // извлекаем id записи
            //            Intent intent;
            //            if (done==1)
            //                // Если это факт - удаляем платеж
            //                db.payment_Delete(str_payment_id);
            //            else
            //                // Если это план - удаляем строку графика
            //                db.graphic_Delete(str_payment_id);

            return true
        }

        if (item.itemId == CM_PAYMENT_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            Log.d("DMS_LOG", "itemClick: position = " + acmi.position + ", id = " + acmi.id)

            // получаем ИД платежа из элемента списка, по его позиции
            val index = acmi.position - 1
            val str_payment_id = listData[index]["_id"].toString()
            val done = Integer.parseInt(listData[index]["done"].toString())

            // извлекаем id записи
            val intent: Intent
            if (done != 0) return true

            // Если это факт - открываем для редактирования карточку платежа
            intent = Intent(this, PaymentActivity::class.java)
            // Параметры платежа
            intent.putExtra("id", "" + flat_id)
            intent.putExtra("payment_id", "" + str_payment_id)
            intent.putExtra("date", "" + listData[index]["date"].toString())
            intent.putExtra("summa", "" + listData[index]["summa"].toString())
            intent.putExtra("summa_credit", "" + listData[index]["summa_credit"].toString())
            intent.putExtra("summa_procent", "" + listData[index]["summa_procent"].toString())

            startActivityForResult(intent, 5)

            return true
        }

        return super.onContextItemSelected(item)
    }

    // Биндер списка - переопределение отображения полей
    internal inner class MyViewBinder : SimpleAdapter.ViewBinder {

        var color_done = resources.getColor(R.color.color_done)
        var color_other = resources.getColor(R.color.color_other)
        var color_current_pay = resources.getColor(R.color.color_current_pay)

        override fun setViewValue(view: View, data: Any,
                                  textRepresentation: String): Boolean {
            var i = 0
            var d = 0.0
            when (view.id) {
                // LinearLayout
                R.id.llFlatPay -> {
                    i = (data as Int).toInt()
                    if (i == 1)
                        view.setBackgroundColor(color_done)
                    else
                        view.setBackgroundColor(color_other)
                    return true
                }
                // Date
                R.id.tvFPDate -> {

                    val str = data.toString()

                    // Log.d("DMS", "OBJECT="+str);

                    val ldata = java.lang.Long.valueOf(str)

                    //                   String str_date = DateUtils.formatDateTime(view.getContext(),
                    //                           ldata,DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);


                    val dateAndTime = Calendar.getInstance()
                    dateAndTime.timeInMillis = ldata
                    val str_date = months[dateAndTime.get(Calendar.MONTH)] + " " + dateAndTime.get(Calendar.YEAR).toString() + " г."

                    (view as TextView).text = str_date
                    return true
                }

                // Rest
                R.id.tvFPOper -> {

                    i = (data as Int).toInt()

                    val oper = FlatPaymentOperationType.getById(i)
                    (view as TextView).text = oper.titleShort
                    return true
                }

                // Summa
                R.id.tvSumma -> {
                    d = (data as Double).toDouble()
                    (view as TextView).text = formatD2(d)
                    return true
                }
            }
            return false
        }
    }


    // Заполняет график из БД
    fun fillGraphic() {
        // Создаем адаптер списка
        // упаковываем данные в понятную для адаптера структуру

        // очищаем список
        listData.clear()

        val c: Cursor?
        c = db.getFlatPayments(flat_id)

        if (c != null) {

            var m: MutableMap<String, Any>

            if (c.moveToFirst()) {
                do {

                    val summa = c.getDouble(c.getColumnIndex(DB.CL_FLAT_ACC_SUMMA))

                    m = HashMap()
                    m[DB.CL_FLAT_ACC_COMMENT] = c.getString(c.getColumnIndex(DB.CL_FLAT_ACC_COMMENT))
                    m[DB.CL_FLAT_ACC_DATE] = c.getLong(c.getColumnIndex(DB.CL_FLAT_ACC_DATE))
                    m[DB.CL_FLAT_ACC_SUMMA] = c.getDouble(c.getColumnIndex(DB.CL_FLAT_ACC_SUMMA))
                    m[DB.CL_FLAT_ACC_TYPE] = c.getInt(c.getColumnIndex(DB.CL_FLAT_ACC_TYPE))
                    m[DB.CL_FLAT_ACC_OPERATION] = c.getInt(c.getColumnIndex(DB.CL_FLAT_ACC_OPERATION))
                    m[DB.CL_FLAT_ACC_FLAT_ID] = c.getInt(c.getColumnIndex(DB.CL_FLAT_ACC_FLAT_ID))
                    m[DB.CL_ID] = c.getInt(c.getColumnIndex(DB.CL_ID))

                    listData.add(m)

                } while (c.moveToNext())
            }
            c.close()
        }

        //  Log.d("DMS","Cnt = "+listData.size());

    }


}
