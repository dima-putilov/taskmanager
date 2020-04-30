package com.example.dmstaskmanager.graphic

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.Credit
import com.example.dmstaskmanager.classes.PAYMENT
import com.example.dmstaskmanager.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_graphic_item.*
import java.util.*

class GraphicItemActivity : AppCompatActivity() {

    lateinit var db: DB
    var credit_id: Int = 0
    var payment_id = -1
    lateinit var cred: Credit
    lateinit var payment: PAYMENT

    internal var dateAndTime = Calendar.getInstance()

    // установка обработчика выбора даты
    internal var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateAndTime.set(Calendar.YEAR, year)
        dateAndTime.set(Calendar.MONTH, monthOfYear)
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        setInitialDateTime()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_graphic_item)

        ToolbarUtils.initToolbar(this, true, R.string.toolbar_credit, R.color.CreditItemToolbar, R.color.CreditItemWindowsBar)

        // открываем подключение к БД
        db = DB(this)
        db.open()

        if (!intent.hasExtra(Navigator.EXTRA_PAYMENT_KEY)) return

        val paymentGson = intent.getStringExtra(Navigator.EXTRA_PAYMENT_KEY)
        payment = Gson().fromJson(paymentGson, PAYMENT::class.java)
        credit_id = payment.credit_id
        payment_id = payment._id

        if (credit_id <= 0) return  // нет ссылки на кредит => выход

        initUI()

        setListeners()

    }

    private fun initUI(){
        cred = db.GetCredit(credit_id)

        val name = cred.name + " от " + DateUtils.formatDateTime(this,
                cred.date, DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)
        val param = "Сумма: " + String.format("%.0f", cred.summa) + ", Процент: " + String.format("%.2f%%", cred.procent) + ", Срок: " + cred.period.toString()

        tvName.text = name
        tvParam.text = param

        setInitialDateTime()

        if (payment_id > 0) {
            payment = db.GetGraphicItem(payment_id)
        } else {
            ToolbarUtils.setNewFlag(this)
        }

        etSumma.setText(round2Str(payment.summa, 2))
        etSummaCredit.setText(round2Str(payment.summa_credit, 2))
        etSummaProcent.setText(round2Str(payment.summa_procent, 2))

        etComment.setText(payment.comment)

        if (payment.date > 0) {
            dateAndTime.timeInMillis = payment.date
            setInitialDateTime()
        }

    }

    // ====== Toolbar ======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_item, menu)
        return true
    }

    fun setListeners() {
        // Set an OnMenuItemClickListener to handle menu item clicks
        val toolbar = findViewById<Toolbar>(R.id.toolbar_actionbar_item)
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
                R.id.action_delete -> {
                    db.graphic_Delete(credit_id)
                    finish()
                    return@OnMenuItemClickListener true
                }
            }// return super.onOptionsItemSelected(item);

            // Handle the menu item
            true
        })

        etSummaCredit.addTextChangedListener(object : TextWatcher {
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


                val new_proc = round2Dec(etSumma.text.toString(), 2).subtract(
                        round2Dec(etSummaCredit.text.toString(), 2))

                etSummaProcent.setText(new_proc.toString())

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
        var summa_credit: Double
        var summa_procent: Double
        summa_procent = 0.0
        summa_credit = summa_procent
        summa = summa_credit

        val period = 0

        val str_summa = etSumma.text.toString()

        var str_summa_credit = etSummaCredit.text.toString()
        var str_summa_procent = etSummaProcent.text.toString()


        if (TextUtils.isEmpty(str_summa)) {
            Toast.makeText(this, R.string.credit_item_error_summa, Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(str_summa_credit)) {
            str_summa_credit = "0"
        }

        if (TextUtils.isEmpty(str_summa_procent)) {
            str_summa_procent = "0"
        }


        summa = java.lang.Double.parseDouble(str_summa)
        summa_credit = java.lang.Double.parseDouble(str_summa_credit)
        summa_procent = java.lang.Double.parseDouble(str_summa_procent)

        val date = dateAndTime.timeInMillis

        payment = PAYMENT(credit_id, date, summa, summa_credit, summa_procent, 0.0, 0.0, 0.0)
        payment.comment = etComment.text.toString()

        if (payment_id > 0) {
            payment._id = payment_id
            db.graphic_Update(payment)
        } else
            db.graphic_Add(payment)

        Navigator.exitFromGraphicItemActivity(this,
                if (payment_id > 0)
                    Actions.Update.type
                else
                    Actions.Create.type,
                payment
        )

    }


}
