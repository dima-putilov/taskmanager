package com.example.dmstaskmanager.flatPayment

import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.R

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.dmstaskmanager.classes.*

import com.example.dmstaskmanager.utils.Navigator
import com.example.dmstaskmanager.utils.ToolbarUtils
import com.example.dmstaskmanager.utils.hideKeyboard
import com.example.dmstaskmanager.utils.round2Str
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_flat_payment.*

import java.util.Calendar

class FlatPaymentActivity : AppCompatActivity() {

    lateinit var db: DB
    var flat_id: Int = -1
    var payment_id = -1
    lateinit var flat: HOME
    lateinit var payment: FlatPayment

    internal var dateAndTime = Calendar.getInstance()

    var listOperation : List<FlatPaymentOperationType> = listOf()

    // установка обработчика выбора даты
    internal var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateAndTime.set(Calendar.YEAR, year)
        dateAndTime.set(Calendar.MONTH, monthOfYear)
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        setInitialDateTime()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flat_payment)

        ToolbarUtils.initToolbar(this, true, R.string.toolbar_payment, R.color.FlatItemToolbar, R.color.FlatItemWindowsBar)

        // открываем подключение к БД
        db = DB(this)
        db.open()

        if (intent.hasExtra(Navigator.EXTRA_FLAT_PAYMENT_KEY)) {
            val paymentGson = intent.getStringExtra(Navigator.EXTRA_FLAT_PAYMENT_KEY)
            payment = Gson().fromJson(paymentGson, FlatPayment::class.java)
            flat_id = payment.flat_id
            payment_id = payment._id
        }

        if (flat_id <= 0) return  // нет ссылки на квартиру => выход

        initUI()

        setListeners()

    }

    // ====== Toolbar ======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_item, menu)
        return true
    }

    private fun setOperationSpinnerAdapter(){
        // адаптер
        val listOperationName = ArrayList<String>()
        for(oper in listOperation){
            listOperationName.add(oper.title)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOperationName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spOper.adapter = adapter

    }

    private fun initUI(){
        flat = db.getFlat(flat_id)

        if (payment_id > 0) {
            payment = db.getFlatPayment(payment_id)
        } else {
            ToolbarUtils.setNewFlag(this)

            etSumma.requestFocus()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

        listOperation = FlatPaymentOperationType.getFlatOperationsByType(flat.type)

        setOperationSpinnerAdapter()

        tvName.text = flat.name

        setInitialDateTime()

        if (payment.summa > 0) {
            etSumma.setText(round2Str(payment.summa, 2))
        }

        etComment.setText(payment.comment)

        if (payment.date > 0) {
            dateAndTime.timeInMillis = payment.date
            setInitialDateTime()
        }

        // выделяем элемент
        setCurrentOperation(payment.operation)

    }

    private fun setCurrentOperation(curOperType: FlatPaymentOperationType){
        var curpos = -1
        for ((index, value) in listOperation.withIndex()){
            if (value == curOperType) {
                curpos = index
            }
        }
        if (curpos >= 0) {
            spOper.setSelection(curpos)
        }
    }

    private fun setListeners(){
        // === ToolBar ===
        val toolbar = findViewById<Toolbar>(R.id.toolbar_actionbar_item)
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            Log.d("DMS", "ПУНКТ МЕНЮ ###" + item.itemId)

            when (item.itemId) {
                android.R.id.home    //button home
                -> {
                    finish()
                    return@OnMenuItemClickListener true
                }
                R.id.action_OK -> {
                    onClickAdd(null)
                    return@OnMenuItemClickListener true
                }
                R.id.action_delete    //button del
                -> {
                    if (payment_id > -1) {
                        db.flatAccount_Delete(payment_id)
                    }
                    finish()
                    return@OnMenuItemClickListener true
                }
            }// return super.onOptionsItemSelected(item);

            // Handle the menu item
            true
        })

        etSumma.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // Запрет на ввод более 2-х дробных знаков
                val str = s.toString()
                val p = str.indexOf(".")
                if (p != -1) {
                    val tmpStr = str.substring(p)
                    if (tmpStr.length == 4) {
                        s.delete(s.length - 1, s.length)
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

    }

    // отображаем диалоговое окно для выбора даты
    fun setDate(v: View) {
        DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show()
    }

    // установка начальных даты и времени
    private fun setInitialDateTime() {
        etDate.setText(DateUtils.formatDateTime(this,
                dateAndTime.timeInMillis,
                DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR
                //                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
                //                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
        ))
    }

    override fun onDestroy() {
        super.onDestroy()
        // закрываем подключение при выходе
        db.close()
    }

    fun onClickCancel(v: View) {
        finish()
    }


    fun onClickAdd(v: View?) {

        var summa: Double
        val summa_credit: Double
        val summa_procent: Double
        val summa_addon: Double
        val summa_plus: Double
        val summa_minus: Double
        summa_minus = 0.0
        summa_plus = summa_minus
        summa_addon = summa_plus
        summa_procent = summa_addon
        summa_credit = summa_procent
        summa = summa_credit

        val period = 0

        val etSumma = findViewById<View>(R.id.etSumma) as EditText


        val str_summa = etSumma.text.toString()

        if (TextUtils.isEmpty(str_summa)) {
            Toast.makeText(this, R.string.credit_item_error_summa, Toast.LENGTH_LONG).show()
            return
        }

        summa = java.lang.Double.parseDouble(str_summa)

        val date = dateAndTime.timeInMillis

        val comment = etComment.text.toString()
        val oper = listOperation[spOper.selectedItemPosition]
        val paymentType = listOperation[spOper.selectedItemPosition].flatPaymentType

        val pay = FlatPayment(flat_id = flat_id,
                paymentType = paymentType,
                operation = oper,
                date = date,
                summa = summa,
                comment = comment)

        if (payment_id > 0) {
            pay._id = payment_id
            db.flatAccount_Update(pay)
        } else
            db.flatAccount_Add(pay)


        hideKeyboard()

        //=====================================================
        // Автозакрытие задач
        //===================

        val taskType : Int? = when (pay.operation) {
            FlatPaymentOperationType.RENT -> TASK.TASK_TYPE_FLAT
            FlatPaymentOperationType.PROFIT -> TASK.TASK_TYPE_ARENDA
            else -> null
        }

        if (taskType != null) {
            val retTask = db.autoCloseTask(pay, taskType)
            if (retTask._id > 0) {
                Toast.makeText(baseContext, "${if (retTask.finish) "Закрыта" else "Изменена"} задача ${retTask.name} от ${DateUtils.formatDateTime(this,
                    retTask.date,
                        DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR)}", Toast.LENGTH_SHORT).show()
            }
        }
        //== Автозакрытие задач (конец)===

        Navigator.exitFromFlatPaymentActivity(this, pay )

    }

}
