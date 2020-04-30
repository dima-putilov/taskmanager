package com.example.dmstaskmanager.payment

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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.Credit
import com.example.dmstaskmanager.classes.PAYMENT
import com.example.dmstaskmanager.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_payment.*
import java.math.BigDecimal
import java.util.*

class PaymentActivity : AppCompatActivity() {

    lateinit var db: DB
    var credit_id: Int = 0
    var payment_id = -1
    lateinit var cred: Credit
    lateinit var payment: PAYMENT

    internal var dateAndTime = Calendar.getInstance()
    internal var tvNew: TextView? = null

    // установка обработчика выбора даты
    internal var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateAndTime.set(Calendar.YEAR, year)
        dateAndTime.set(Calendar.MONTH, monthOfYear)
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        setInitialDateTime()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_payment)

        // === ToolBar ===
        ToolbarUtils.initToolbar(this, true, R.string.toolbar_payment, R.color.PaymentItemToolbar, R.color.PaymentItemWindowsBar)

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

        etSumma.requestFocus()

    }

    // ====== Toolbar ======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_item, menu)
        return true
    }

    fun initUI(){
        cred = db.GetCredit(credit_id)

        val name = cred.name + " от " + DateUtils.formatDateTime(this,
                cred.date, DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)
        val param = "Сумма: " + String.format("%.0f", cred.summa) + ", Процент: " + String.format("%.2f%%", cred.procent) + ", Срок: " + cred.period.toString()

        tvName.text = name
        tvParam.text = param

        setInitialDateTime()


        if (payment_id > 0) {
            payment = db.GetPayment(payment_id)
        } else {
            ToolbarUtils.setNewFlag(this)
        }

        etSumma.setText(round2Str(payment.summa, 2))
        etSumma_credit.setText(round2Str(payment.summa_credit, 2))
        etSumma_procent.setText(round2Str(payment.summa_procent, 2))
        etSumma_addon.setText(round2Str(payment.summa_addon, 2))
        etSumma_plus.setText(round2Str(payment.summa_plus, 2))
        etSumma_minus.setText(round2Str(payment.summa_minus, 2))

        etComment.setText(payment.comment)

        if (payment.date > 0) {
            dateAndTime.timeInMillis = payment.date
            setInitialDateTime()
        }

    }

    private fun setListeners(){

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
                    onClickDelete()
                    return@OnMenuItemClickListener true
                }
            }// return super.onOptionsItemSelected(item);

            // Handle the menu item
            true
        })

        etSumma_credit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(etSumma_credit.hasFocus()) {
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
                            round2Dec(etSumma_credit.text.toString(), 2)).max(BigDecimal.ZERO)

                    etSumma_procent.setText(new_proc.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        etSumma_procent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(etSumma_procent.hasFocus()) {
                    // Запрет на ввод более 2-х дробных знаков
                    val str = s.toString()
                    val p = str.indexOf(".")
                    if (p != -1) {
                        val tmpStr = str.substring(p)
                        if (tmpStr.length == 4) {
                            s.delete(s.length - 1, s.length)
                        }
                    }

                    val new_sum = round2Dec(etSumma.text.toString(), 2).subtract(
                            round2Dec(etSumma_procent.text.toString(), 2)).max(BigDecimal.ZERO)

                    etSumma_credit.setText(new_sum.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })


        etSumma.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if(etSumma.hasFocus()) {
                    // Запрет на ввод более 2-х дробных знаков
                    val str = s.toString()
                    val p = str.indexOf(".")
                    if (p != -1) {
                        val tmpStr = str.substring(p)
                        if (tmpStr.length == 4) {
                            s.delete(s.length - 1, s.length)
                        }
                    }

                    val new_sum = round2Dec(etSumma.text.toString(), 2).subtract(
                            round2Dec(etSumma_procent.text.toString(), 2)).max(BigDecimal.ZERO)

                    etSumma_credit.setText(new_sum.toString())

                    try {
                        val new_sum_addon = round2Dec(etSumma.text.toString(), 2).subtract(
                                round2Dec(payment.summa.toString(), 2)).max(BigDecimal.ZERO)

                       // Log.d("DMS_CREDIT", "payment.summa_credit=${payment.summa_credit} (${round2Dec(payment.summa_credit.toString(), 2)}) new_sum_addon = $new_sum_addon")

                        etSumma_addon.setText(new_sum_addon.toString())
                    }
                    catch (e: Throwable){

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

    fun onClickAddonSumm(v: View) {
        etSumma_addon.text = etSumma_credit.text
    }

    fun onClickSummaCreditClearButton(v: View) {
        etSumma_credit.setText("")
    }

    fun onClickSummaProcentClearButton(v: View) {
        etSumma_procent.setText("")
    }

    fun onClickAddonSummaClearButton(v: View) {
        etSumma_addon.setText("")
    }

    fun onClickAdd(v: View?) {

        var summa: Double
        var summa_credit: Double
        var summa_procent: Double
        var summa_addon: Double
        var summa_plus: Double
        var summa_minus: Double
        summa_minus = 0.0
        summa_plus = summa_minus
        summa_addon = summa_plus
        summa_procent = summa_addon
        summa_credit = summa_procent
        summa = summa_credit

        val period = 0

        val etSumma = findViewById<View>(R.id.etSumma) as EditText
        val etSumma_credit = findViewById<View>(R.id.etSumma_credit) as EditText
        val etSumma_procent = findViewById<View>(R.id.etSumma_procent) as EditText
        val etSumma_addon = findViewById<View>(R.id.etSumma_addon) as EditText
        val etSumma_plus = findViewById<View>(R.id.etSumma_plus) as EditText
        val etSumma_minus = findViewById<View>(R.id.etSumma_minus) as EditText


        val str_summa = etSumma.text.toString()

        var str_summa_credit = etSumma_credit.text.toString()
        var str_summa_procent = etSumma_procent.text.toString()
        var str_summa_addon = etSumma_addon.text.toString()
        var str_summa_plus = etSumma_plus.text.toString()
        var str_summa_minus = etSumma_minus.text.toString()



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

        if (TextUtils.isEmpty(str_summa_plus)) {
            str_summa_plus = "0"
        }

        if (TextUtils.isEmpty(str_summa_minus)) {
            str_summa_minus = "0"
        }

        if (TextUtils.isEmpty(str_summa_addon)) {
            str_summa_addon = "0"
        }

        summa = java.lang.Double.parseDouble(str_summa)
        summa_credit = java.lang.Double.parseDouble(str_summa_credit)
        summa_procent = java.lang.Double.parseDouble(str_summa_procent)

        summa_plus = java.lang.Double.parseDouble(str_summa_plus)
        summa_minus = java.lang.Double.parseDouble(str_summa_minus)
        summa_addon = java.lang.Double.parseDouble(str_summa_addon)

        val date = dateAndTime.timeInMillis

        payment = PAYMENT(credit_id, date, summa, summa_credit, summa_procent, summa_addon, summa_plus, summa_minus)
        payment.comment = etComment.text.toString()

        if (!checkPayment(payment)){
            return
        }

        if (payment_id > 0) {
            payment._id = payment_id
            db.payment_Update(payment)
        } else
            db.payment_Add(payment)

        //=====================================================
        // Автозакрытие задач
        //===================
        val t = db.autoCloseTask(payment)
        if (t._id > 0) {
            Toast.makeText(baseContext, "Закрыта задача " + t.name + " от " + DateUtils.formatDateTime(this,
                    t.date,
                    DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR), Toast.LENGTH_SHORT).show()
        }
        //== Автозакрытие задач (конец)===

        Navigator.exitFromPaymentActivity(this,
                if (payment_id > 0)
                    Actions.Update.type
                else
                    Actions.Create.type,
                payment)

    }

    private fun checkPayment(payment: PAYMENT) : Boolean {
        var result = true
        if (D2L(payment.summa) != D2L(payment.summa_credit + payment.summa_procent) ) {
            Toast.makeText(baseContext, "Общая сумма платежа должна совпадать с Кредит + Процент", Toast.LENGTH_SHORT).show()
            result = false
        }
        return result
    }

    private fun onClickDelete(){
        db.payment_Delete(payment_id)
        Toast.makeText(baseContext, "Удален платеж от " + DateUtils.formatDateTime(this,
                dateAndTime.timeInMillis,
                DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR), Toast.LENGTH_SHORT).show()


        Navigator.exitFromPaymentActivity(this, Actions.Delete.type, payment)

    }

}
