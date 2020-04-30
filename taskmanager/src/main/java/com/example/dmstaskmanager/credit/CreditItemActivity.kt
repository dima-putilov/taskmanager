package com.example.dmstaskmanager.credit

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.Credit
import com.example.dmstaskmanager.classes.CreditType
import com.example.dmstaskmanager.utils.round2Str
import kotlinx.android.synthetic.main.activity_credit_item.*
import java.util.*

class CreditItemActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var db: DB

    var credit_id = -1
    lateinit var cred: Credit

    var dateAndTime = Calendar.getInstance()

    // установка обработчика выбора даты
    internal var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateAndTime.set(Calendar.YEAR, year)
        dateAndTime.set(Calendar.MONTH, monthOfYear)
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        setInitialDateTime()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_item)
        // кнопка Назад

        // === ToolBar ===
        val toolbar = findViewById<Toolbar>(R.id.toolbar_actionbar_item)
        setSupportActionBar(toolbar)
        // Кнопка Назад
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Заголовок
        supportActionBar!!.setTitle(R.string.toolbar_credit)

        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.color.CreditItemToolbar))

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.CreditItemWindowsBar)


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
            }// return super.onOptionsItemSelected(item);

            // Handle the menu item
            true
        })

        // открываем подключение к БД
        db = DB(this)
        db.open()

        val intent = intent

        // Получаем экземпляр элемента Spinner

        // Настраиваем адаптер
        val adapter = ArrayAdapter.createFromResource(this, R.array.type_credit, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Вызываем адаптер
        spType.adapter = adapter

        if (intent.hasExtra("id")) {

            val str_id = intent.getStringExtra("id")
            credit_id = Integer.parseInt(str_id)

            cred = db.GetCredit(credit_id)

            etName.setText(cred.name)

            val etSumma = findViewById<View>(R.id.etSumma) as EditText
            etSumma.setText(round2Str(cred.summa, 2))

            val etSummaPay = findViewById<View>(R.id.etSummaPay) as EditText
            etSummaPay.setText(round2Str(cred.summa_pay, 2))


            val etProcent = findViewById<View>(R.id.etProcent) as EditText
            etProcent.setText(round2Str(cred.procent, 2))

            val etPeriod = findViewById<View>(R.id.etPeriod) as EditText
            etPeriod.setText(cred.period.toString())

            if (cred.date > 0) {

                dateAndTime.timeInMillis = cred.date
                setInitialDateTime()
            }

            spType.setSelection(cred.type.type)

            val swFinish = findViewById<View>(R.id.swFinish) as Switch
            swFinish.isChecked = cred.finish

        } else {
            supportActionBar!!.setSubtitle(resources.getString(R.string.toolbar_subtitle_new))
            setInitialDateTime()
        }

    }

    // отображаем диалоговое окно для выбора даты
    fun nameMicro(v: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_hint))
        startActivityForResult(intent, MICROPHONE_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == MICROPHONE_REQUEST_CODE) {
            val matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isEmpty()) {
                etName.setText(matches[0])
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // ====== Toolbar ======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_item, menu)
        return true
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
                dateAndTime.timeInMillis, DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR
                //                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
                //                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
        ))
    }


    override fun onClick(v: View) {
        // по id определяем кнопку, вызвавшую этот обработчик
        when (v.id) {
            R.id.action_add ->
                // кнопка ОК

                onClickAdd(v)
        }

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
        var procent: Double
        var summa_pay: Double
        summa_pay = 0.0
        procent = summa_pay
        summa = procent

        var period = 0

        val date: Long

        val etName = findViewById<View>(R.id.etName) as EditText
        val etSumma = findViewById<View>(R.id.etSumma) as EditText
        val etSummaPay = findViewById<View>(R.id.etSummaPay) as EditText
        val etProcent = findViewById<View>(R.id.etProcent) as EditText
        val etPeriod = findViewById<View>(R.id.etPeriod) as EditText

        val swFinish = findViewById<View>(R.id.swFinish) as Switch

        val str_summa = etSumma.text.toString()
        val str_summa_pay = etSummaPay.text.toString()
        val str_procent = etProcent.text.toString()
        val str_period = etPeriod.text.toString()

        if (TextUtils.isEmpty(str_summa)) {
            Toast.makeText(this, R.string.credit_item_error_summa, Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(str_procent)) {
            Toast.makeText(this, R.string.credit_item_error_procent, Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(str_period)) {
            Toast.makeText(this, R.string.credit_item_error_period, Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(str_summa_pay)) {
            Toast.makeText(this, R.string.credit_item_error_summa_pay, Toast.LENGTH_LONG).show()
            return
        }

        val name = etName.text.toString()
        summa = java.lang.Float.parseFloat(str_summa).toDouble()
        summa_pay = java.lang.Float.parseFloat(str_summa_pay).toDouble()
        procent = java.lang.Float.parseFloat(str_procent).toDouble()
        period = Integer.parseInt(str_period)

        Log.d("DMS", "SAVE Год = " + dateAndTime.get(Calendar.YEAR) + " Месяц " + dateAndTime.get(Calendar.MONTH) + " День " + dateAndTime.get(Calendar.DAY_OF_MONTH))

        date = dateAndTime.timeInMillis

        val cred = Credit(name = name,
            date = date,
            summa = summa,
            procent = procent,
            period = period,
            summa_pay = summa_pay)

        cred.type = CreditType.getById( spType.selectedItemPosition )

        cred.finish = swFinish.isChecked

        if (credit_id > 0) {
            cred.id = credit_id
            db.credit_Update(cred)
        } else
            db.credit_Add(cred)


        val intent = Intent()
        intent.putExtra("credit", cred)
        intent.putExtra("name", cred.name)
        setResult(Activity.RESULT_OK, intent)

        finish()

    }

    companion object {

        private val MICROPHONE_REQUEST_CODE = 121
    }

}
