package com.example.dmstaskmanager.task

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.View

import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.PAYMENT
import com.example.dmstaskmanager.classes.TASK
import com.example.dmstaskmanager.utils.Navigator
import com.example.dmstaskmanager.utils.ToolbarUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_task_item.*

import java.util.Calendar

class TaskItemActivity : AppCompatActivity() {

    lateinit var db: DB
    private var task: TASK = TASK()

    private var task_id = -1

    var type = TASK.TASK_TYPE_OTHER
    var parent_id = -1

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
        setContentView(R.layout.activity_task_item)

        ToolbarUtils.initToolbar(this, true, R.string.toolbar_task, R.color.TaskItemToolbar, R.color.TaskItemWindowsBar)

        // открываем подключение к БД
        db = DB(this)
        db.open()

        if (intent.hasExtra(Navigator.EXTRA_TASK_KEY)) {
            val taskGson = intent.getStringExtra(Navigator.EXTRA_TASK_KEY)
            task = Gson().fromJson(taskGson, TASK::class.java)
            task_id = task._id
        }

        initUI()

        setListeners()

    }

    // ====== Toolbar ======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_item, menu)
        return true
    }

    private fun initUI(){
        if (task_id > 0) {
            task = db.getTask(task_id)
        } else {
            ToolbarUtils.setNewFlag(this)
        }

        etName.setText(task.name)
        etSumma.setText(task.summa.toString())

        if (task.finish) {
            cbFinish.isChecked = true
        }

        if (task.date > 0) {
            dateAndTime.timeInMillis = task.date
            setInitialDateTime()
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
                    if (task_id > -1) {
                        db.task_Delete(task_id.toLong())
                    }
                    finish()
                    return@OnMenuItemClickListener true
                }
            }// return super.onOptionsItemSelected(item);

            // Handle the menu item
            true
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

    // отображаем диалоговое окно для выбора даты
    fun nameMicro(v: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_hint))
        startActivityForResult(intent, MICROPHONE_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MICROPHONE_REQUEST_CODE) {
            if (data != null) {
                val matches = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS)
                if (!matches.isEmpty()) {
                    val str = etName.text.toString()
                    etName.setText(str + " " + matches[0])
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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

        val cur_task = TASK()

        cur_task.name = etName.text.toString()
        cur_task.type = type

        cur_task.parent_id = parent_id

        var str_summa = etSumma.text.toString()
        if (TextUtils.isEmpty(str_summa)) {
            str_summa = "0"
        }

        cur_task.summa = java.lang.Double.parseDouble(str_summa)

        cur_task.finish = cbFinish.isChecked

        cur_task.date = dateAndTime.timeInMillis


        if (task_id > 0) {
            cur_task._id = task_id
            db.task_Update(cur_task)
        } else
            db.task_Add(cur_task)

        Navigator.exitFromTaskActivity(this, cur_task)

    }

    companion object {

        private val MICROPHONE_REQUEST_CODE = 121
    }

}
