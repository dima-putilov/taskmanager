package com.example.dmstaskmanager.main_window.creditPage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener

import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import java.util.concurrent.TimeUnit
import com.example.dmstaskmanager.credit.CreditItemActivity
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.ARGUMENT_PAGE_NUMBER
import com.example.dmstaskmanager.classes.PAYMENT
import com.example.dmstaskmanager.classes.SWIPE_REFRESH_DISTANCE_TO_TRIGGER
import com.example.dmstaskmanager.diagram.CreditDiagramActivity
import com.example.dmstaskmanager.utils.Navigator
import com.example.dmstaskmanager.utils.NavigatorResultCode
import com.example.dmstaskmanager.utils.Settings
import com.example.dmstaskmanager.utils.ToolbarUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_credit.*

class CreditPageFragment : Fragment(), LoaderCallbacks<Cursor> {


    companion object {

        fun newInstance(page: Int): CreditPageFragment {
            val pageFragment = CreditPageFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.setArguments(arguments)
            return pageFragment
        }

    }

    val PAGE = 1

    val DIALOG_DELETE_ALL = 1

    var pageNumber: Int = 0

    val CM_DELETE_ID = 1
    val CM_PAYMENT_ID = 2
    val CM_EDIT_ID = 3
    val CM_DIAGRAM_ID = 4

    lateinit var db: DB
    lateinit var scAdapter: SimpleCursorAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.activity_credit, null)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)

        // ========= Work with data base ==============
        // открываем подключение к БД
        db = DB(activity)
        db.open()

        setHasOptionsMenu(true)

        setupListAdapter()

        initSwipeRefresh()

        setListeners()

        initLoader()

    }

    private fun initLoader() {
        // создаем лоадер для чтения данных
        activity.supportLoaderManager.initLoader(PAGE, null, this)
    }

    private fun setupListAdapter() {
        // формируем столбцы сопоставления
        val from = arrayOf(DB.CL_RESULT_CREDIT_NAME, DB.CL_RESULT_CREDIT_DATE, DB.CL_RESULT_CREDIT_SUMMA, DB.CL_RESULT_PAYMENT_SUMMA, DB.CL_RESULT_PAYMENT_SUMMA_CREDIT, DB.CL_RESULT_CREDIT_PROCENT, DB.CL_RESULT_CREDIT_PERIOD, DB.CL_RESULT_RESULT_REST)

        val to = intArrayOf(R.id.tvName, R.id.tvDate, R.id.tvSumma, R.id.pbProgress, R.id.pbProgress, R.id.pbProgress, R.id.pbProgress, R.id.pbProgress)

        // создаем адаптер и настраиваем список
        //scAdapter = new SimpleCursorAdapter(this, R.layout.item_task, null, from, to, 0);
        scAdapter = CreditListAdapter(activity, R.layout.item_credit, null, from, to, 0)

        lvCredit.adapter = scAdapter

        // добавляем контекстное меню к списку
        registerForContextMenu(lvCredit)

    }


    private fun setListeners() {
        lvCredit.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Log.d("DMS_LOG", "itemClick: position = $position, id = $id")

            val credit = db.getCreditById(id)
            Navigator.navigateToGraphicActivity(activity, this, credit)

        }

        lvCredit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View,
                position: Int, id: Long) {
                Log.d("DMS_LOG", "itemSelect: position = " + position + ", id = "
                    + id)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("DMS_LOG", "itemSelect: nothing")
            }
        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(0, CM_DELETE_ID, 100, R.string.action_credit_delete)
        menu.add(0, CM_PAYMENT_ID, 1, R.string.action_credit_addpayment)
        menu.add(0, CM_EDIT_ID, 2, R.string.action_credit_editcredit)

        menu.add(1, CM_DIAGRAM_ID, 3, R.string.action_credit_diagram)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == CM_DELETE_ID) {

            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo
            // извлекаем id записи и удаляем соответствующую запись в БД
            val id = acmi.id

            val ad = AlertDialog.Builder(activity)
            ad.setTitle("Кредит")  // заголовок
            ad.setMessage("Все данные по кредиту будут удалены!\n\nУдалить кредит?") // сообщение
            ad.setPositiveButton("Да") { dialog, arg1 ->
                db.credit_Delete(id)

                // получаем новый курсор с данными
                loadCreditList()
            }

            ad.setNegativeButton("Отмена") { dialog, arg1 -> }

            ad.setCancelable(true)
            ad.setOnCancelListener { }

            ad.show()

        }

        if (item.itemId == CM_PAYMENT_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            val credit_id = acmi.id

            val payment = db.credit_GetNextPayment(credit_id)

            Navigator.navigateToPaymentActivity(activity, this, payment)

            return true
        }

        if (item.itemId == CM_EDIT_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo


            // извлекаем id записи
            val intent = Intent(activity, CreditItemActivity::class.java)
            intent.putExtra("id", "" + acmi.id)
            startActivityForResult(intent, 3)

            return true
        }

        if (item.itemId == CM_DIAGRAM_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            val credit = db.getCreditById(acmi.id)
            Navigator.navigateToCreditDiagramActivity(activity, this, credit)

            return true
        }

        return super.onContextItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        // закрываем подключение при выходе
        db.close()
    }

    override fun onCreateLoader(id: Int, bndl: Bundle?): Loader<Cursor> {
        return MyCursorLoader(activity, db)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        creditSwipeRefreshLayout.isRefreshing = false
        scAdapter.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}

    internal class MyCursorLoader(context: Context, var db: DB) : CursorLoader(context) {

        override fun loadInBackground(): Cursor {
            val showClosed = Settings.getShowCloseCreditSettings(context)
            val cursor = db.credit_GetCreditData(showClosed)
            try {
                TimeUnit.SECONDS.sleep(1)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return cursor
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId

        if (id == R.id.action_option_show_closed) {
            Settings.setShowCloseCreditSettings(context, true)
            ToolbarUtils.setupActionBar(activity as AppCompatActivity, PAGE)
            loadCreditList()
        }

        if (id == R.id.action_option_hide_closed) {
            Settings.setShowCloseCreditSettings(context, false)
            ToolbarUtils.setupActionBar(activity as AppCompatActivity, PAGE)
            loadCreditList()
        }

        if (id == R.id.action_add) {
            val intent = Intent(activity, CreditItemActivity::class.java)
            startActivityForResult(intent, 1)
        }

        if (id == R.id.action_option_add) {
            val intent = Intent(activity, CreditItemActivity::class.java)
            startActivityForResult(intent, 1)
        }

        if (id == R.id.action_option_delete_all) {

            DeleteAll_Alert()

        }

        return super.onOptionsItemSelected(item)
    }

    private fun initSwipeRefresh() {
        creditSwipeRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                loadCreditList()
            }
        })

        creditSwipeRefreshLayout.setDistanceToTriggerSync(SWIPE_REFRESH_DISTANCE_TO_TRIGGER)
    }

    private fun loadCreditList() {
        activity.supportLoaderManager.getLoader<Any>(PAGE).forceLoad()
    }

    fun DeleteAll_Alert() {
        activity.showDialog(DIALOG_DELETE_ALL)
    }

    fun DeleteAll() {
        db.credit_DeleteAll()
        loadCreditList()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //CREDIT cred = data.getExtras("credit");
                //CREDIT cred = data.getExtras("credit");

                val cred_name = data!!.getStringExtra("name")

                Toast.makeText(activity, "Создан новый кредит $cred_name", Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode == NavigatorResultCode.Payment.resultCode) {
            if (resultCode == Activity.RESULT_OK) {

                data?.let { data ->
                    if (data.hasExtra(Navigator.EXTRA_PAYMENT_KEY)) {
                        val payGson = data.getStringExtra(Navigator.EXTRA_PAYMENT_KEY)
                        val pay: PAYMENT = Gson().fromJson(payGson, PAYMENT::class.java)
                        val summa = data.getStringExtra("summa")
                        Toast.makeText(activity, "Оплата кредита на сумму ${pay.summa}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        if (requestCode == NavigatorResultCode.Graphic.resultCode) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }

        loadCreditList()
    }

}