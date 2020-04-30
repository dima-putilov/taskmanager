package com.example.dmstaskmanager.graphic

import android.support.v4.app.Fragment
import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.format.DateUtils
import android.util.Log
import android.view.*
import android.widget.*
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.*
import com.example.dmstaskmanager.utils.*
import java.util.*
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.graphic_activity.*
import kotlinx.android.synthetic.main.graphic_fragment.*

class GraphicListFragment : Fragment() {

    val CM_DELETE_ID = 1
    val CM_PAYMENT_ID = 2
    val CM_EDIT_ID = 3

    var credit_id = -1 // ИД кредита

    // Дата  - определяет дату с которой считать плановые платежи
    // следующие даты будут = +1 месяц и т.д.
    var dateAndTime = Calendar.getInstance()

    lateinit var graphicListViewModel: GraphicListViewModel

    // Основной список - график платежей
    // включает как уже совешенные платежи, так и плановые платежи
    private lateinit var graphicAdapter: GraphicListAdapter  // адаптер списка

    // установка обработчика выбора даты
    var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateAndTime.set(Calendar.YEAR, year)
        dateAndTime.set(Calendar.MONTH, monthOfYear)
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        graphicListViewModel.setNextPaymentDate(dateAndTime.timeInMillis)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.graphic_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var needInitDate = true

        bindViewModel()

        // === Инициализация текущего кредита, по которому открывается график платежей ===
        activity?.intent?.let {
            val creditGson = it.getStringExtra(Navigator.EXTRA_CREDIT_KEY)
            val credit = Gson().fromJson(creditGson, Credit::class.java)
            credit_id = credit.id

            initAdapter()

            setListeners()

            graphicListViewModel.Init(credit)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun bindViewModel() {

        graphicListViewModel = ViewModelProviders.of(this).get(GraphicListViewModel::class.java)

        graphicListViewModel.graphicListData.observe(this, Observer { graphicListData ->
            graphicListData?.also { graphicListData ->
                updateAdapter(graphicListData)
            }
        })

        graphicListViewModel.credit.observe(this, Observer { credit ->
            setCreditHeader(credit)
        })

        graphicListViewModel.nextPaymentDate.observe(this, Observer { nextPaymentDate ->
            nextPaymentDate?.also { nextPaymentDate ->
                setNextPaymentDate(nextPaymentDate)
            }
        })

        graphicListViewModel.summaPayAll.observe(this, Observer { summa ->
            updateSummaPayAll(summa ?: 0.0)
        })

        graphicListViewModel.summaCreditAll.observe(this, Observer { summa ->
            updateSummaCreditAll(summa ?: 0.0)
        })

        graphicListViewModel.summaProcentAll.observe(this, Observer { summa ->
            updateSummaProcentAll(summa ?: 0.0)
        })

        graphicListViewModel.summaPayFactAll.observe(this, Observer { summa ->
            updateSummaPayFactAll(summa ?: 0.0)
        })

        graphicListViewModel.summaCreditFactAll.observe(this, Observer { summa ->
            updateSummaCreditFactAll(summa ?: 0.0)
        })

        graphicListViewModel.summaProcentFactAll.observe(this, Observer { summa ->
            updateSummaProcentFactAll(summa ?: 0.0)
        })

        graphicListViewModel.summaNextPay.observe(this, Observer { summa ->
            updateSummaNextPay()
        })

        graphicListViewModel.onlyProcentOnFirstPay.observe(this, Observer { onlyProcentOnFirstPay ->
            updateOnlyProcentOnFirstPay(onlyProcentOnFirstPay ?: false)
        })

        graphicListViewModel.currentPayPosition.observe(this, Observer { currentPayPosition ->
            setCurrentPosition(currentPayPosition ?: 0)
        })

        graphicListViewModel.showGraphicLoadingIndicatorEvent.observe(this, Observer {
            showGraphicLoadingIndicator()
        })

        graphicListViewModel.hideGraphicLoadingIndicatorEvent.observe(this, Observer {
            hideGraphicLoadingIndicator()
        })

        graphicListViewModel.showSaveGraphicLoadingIndicatorEvent.observe(this, Observer {
            showSaveGraphicLoadingIndicator()
        })

        graphicListViewModel.hideSaveGraphicLoadingIndicatorEvent.observe(this, Observer {
            hideSaveGraphicLoadingIndicator()
        })

        graphicListViewModel.graphicSavedEvent.observe(this, Observer {
            onGraphicSaved()
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NavigatorResultCode.Payment.resultCode) {
            if (resultCode == Activity.RESULT_OK) {
                graphicListViewModel.getGraphicList()
            }
        }
    }

    private fun initAdapter(){

        graphicAdapter = GraphicListAdapter(activity)
        lvGraphic.adapter = graphicAdapter

    }

    private fun updateAdapter(graphicListData: List<PAYMENT>){
        graphicAdapter.listData = graphicListData
        graphicAdapter.notifyDataSetChanged()
    }


    fun setListeners(){
        //  ==== Обработчики событий ====

//        // Set an OnMenuItemClickListener to handle menu item clicks
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar_actionbar_item)
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            Log.d("DMS", "ПУНКТ МЕНЮ ###" + item.itemId)

            val id = item.itemId

            when (id) {
                android.R.id.home    //button home
                -> {
                    Navigator.exitFromGraphicActivity(activity)
                    return@OnMenuItemClickListener true
                }
                R.id.action_OK -> {
                    onClickAdd(null)
                    return@OnMenuItemClickListener true
                }
                R.id.action_delete -> {
                    onDeleteClick()
                    return@OnMenuItemClickListener true
                }
                R.id.toolbar_payment_add -> {
                    onAddNewPaymentClick()
                    return@OnMenuItemClickListener true
                }
                R.id.toolbar_graphic_add -> {
                    onAddNewGraphicClick()
                    return@OnMenuItemClickListener true
                }
                R.id.toolbar_payment_add_all -> {
                    onAddAllPaymentsClick()
                    return@OnMenuItemClickListener true
                }

                R.id.toolbar_delete_all -> {
                    onDeleteAllClick()
                    return@OnMenuItemClickListener true
                }
            }// return super.onOptionsItemSelected(item);

            // Handle the menu item
            true
        })

        // Нажатие на пункт списка
        lvGraphic.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // По нажатию открывается либо текущий фактический платеж (если у него задан _id > 0)
            // либо новый платеж (_id<0) на основании параметров планового платежа

            val listData = graphicListViewModel.getGraphicListData()
            val index = position
            val payment = listData[index]

            onItemPaymentClick(payment)

        }

        switchOnlyProcentOnFirstPay.setOnCheckedChangeListener({v, isChecked ->
            graphicListViewModel.setOnlyProcentOnFirstPay(isChecked)
        })

        etSummaPay.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (activity.currentFocus != etSummaPay) {
                    return
                }

                val text = s.toString()

                graphicListViewModel.setSummaNextPay(str2Double(text))

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        etSummaPayClearButton.setOnClickListener {
            onClickSummaPayClearButton()
        }

        btExec.setOnClickListener {
            execGraphic()
        }

        btSetDate.setOnClickListener {
            setDate()
        }

        // добавляем контекстное меню к списку
        registerForContextMenu(lvGraphic)

    }

    fun getCurrentCredit(): Credit{
        return graphicListViewModel.getCredit()?.let {
            it
        } ?: Credit()
    }

    fun onAddNewPaymentClick() {
        Navigator.navigateToPaymentActivity(activity, this, PAYMENT(credit_id = credit_id))
    }

    fun onAddNewGraphicClick() {
        Navigator.navigateToGraphicItemActivity(activity, this, PAYMENT(credit_id = credit_id))
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        Log.d("DMS_CREDIT", "ПУНКТ МЕНЮ " + item.itemId)

        val id = item.itemId

        if (id == R.id.toolbar_payment_add) {
            onAddNewPaymentClick()
        }

        if (id == R.id.toolbar_graphic_add) {
            onAddNewGraphicClick()
        }

        if (id == R.id.toolbar_delete_all) {
            onDeleteAllClick()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)

        menu.add(0, CM_PAYMENT_ID, 1, R.string.action_credit_addpayment)

        menu.add(0, CM_DELETE_ID, 100, R.string.action_payment_delete)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            val index = acmi.position
            val listData = graphicListViewModel.getGraphicListData()

            val payment = listData[index]

            onDeletePaymentClick(payment)

            return true
        }

        if (item.itemId == CM_PAYMENT_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            val listData = graphicListViewModel.getGraphicListData()
            val index = acmi.position

            val payment = listData[index]

            onAddPaymentClick(payment)

            return true
        }

        return super.onContextItemSelected(item)
    }

    // Внимание! Объект cred (текущий кредит) должен быть инициализирован
    fun setCreditHeader(credit : Credit?) {
        credit?.also {credit ->
            val name = credit.name + " от " + DateUtils.formatDateTime(activity,
                    credit.date, DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)

            val param = "Сумма: " + String.format("%.0f", credit.summa) + ", Процент: " + String.format("%.2f%%", credit.procent) + ", Срок: " + credit.period.toString()

            tvName.text = name
            tvParam.text = param
        }
    }

    // отображаем диалоговое окно для выбора даты
    fun setDate() {
        DatePickerDialog(activity, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show()
    }

    // установка начальных даты и времени
    private fun setInitialDateTime(date: Long) {
        dateAndTime.timeInMillis = date
        etDate.setText(DateUtils.formatDateTime(activity,
                date,
                //                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR))
    }


    // Обработчик кнопки Отмена / Закрыть / Назад
    fun onClickCancel(v: View) {
        Navigator.exitFromGraphicActivity(activity)
    }

    // Обработчик кнопки Записать / ОК - сохранение графика в БД
    fun onClickAdd(v: View?) {
        graphicListViewModel.saveGraphic()
    }

    fun execGraphic(){
        hideKeyboard()
        graphicListViewModel.calculateCredit()
    }

    fun hideKeyboard(){
        val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun showKeyboard(){
        val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
    }

    private fun setCurrentPosition(currentPayPosition: Int){

        graphicAdapter.currentPayPosition = currentPayPosition
        graphicAdapter.notifyDataSetChanged()

        if (currentPayPosition>0) {
            try {
                lvGraphic.setSelection(currentPayPosition - 1)
            } catch (e: Throwable) {

            }
        }

    }

    private fun setNextPaymentDate(date: Long){
        setInitialDateTime(date)
    }

    private fun updateSummaPayAll(summa: Double){
        infoSummaValue.text = formatD2(summa)
    }

    private fun updateSummaCreditAll(summa: Double){
        infoSummaCreditValue.text = formatD2(summa)
    }

    private fun updateSummaProcentFactAll(summa: Double){
        infoSummaProcentFactValue.text = formatD2(summa)
    }

    private fun updateSummaPayFactAll(summa: Double){
        infoSummaFactValue.text = formatD2(summa)
    }

    private fun updateSummaCreditFactAll(summa: Double){
        infoSummaCreditFactValue.text = formatD2(summa)
    }

    private fun updateSummaProcentAll(summa: Double){
        infoSummaProcentValue.text = formatD2(summa)
    }

    private fun showGraphicLoadingIndicator(){
        lvGraphic.visibility = View.INVISIBLE
        graphicProgressBar.visibility = View.VISIBLE
    }

    private fun hideGraphicLoadingIndicator(){
        lvGraphic.visibility = View.VISIBLE
        graphicProgressBar.visibility = View.GONE
    }

    private fun showSaveGraphicLoadingIndicator(){
        calcLayout.visibility = View.INVISIBLE
        saveIndicatorLayout.visibility = View.VISIBLE
    }

    private fun hideSaveGraphicLoadingIndicator(){
        calcLayout.visibility = View.VISIBLE
        saveIndicatorLayout.visibility = View.GONE
    }

    private fun updateSummaNextPay() {
        val summaNextPay = graphicListViewModel.getSummaNextPay()
        val currentSummaStr = etSummaPay.text.toString()
        val currentSumma =
                if (currentSummaStr.isNotEmpty())
                    str2Double(currentSummaStr)
                else {
                    0.0
                }

        if (D2L(currentSumma) != D2L(summaNextPay)) {
            //Log.d("DMS_CREDIT", "etSummaPay.text.toString() = ${etSummaPay.text.toString()}  and summaNextPayStr = ${summaNextPay}" )
            etSummaPay.setText(formatD(summaNextPay))
        }
    }

    private fun updateOnlyProcentOnFirstPay(flag: Boolean){
        if (switchOnlyProcentOnFirstPay.isChecked != flag) {
            switchOnlyProcentOnFirstPay.isChecked = flag
        }
    }

    private fun onGraphicSaved(){
        Navigator.exitFromGraphicListActivity(activity, getCurrentCredit())
    }

    private fun onAddAllPaymentsClick(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getAlertTitle())
                .setMessage("Ввести все платежи до текущей даты ${getCurrentDateStr()}?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Добавить платежи",
                        {dialog, id ->
                            graphicListViewModel.addAllPaymentsToCurrentDate()
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onDeleteClick(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getAlertTitle())
                .setMessage("График платежей будет удален!\nПродолжить?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Удалить график",
                        {dialog, id ->
                            graphicListViewModel.deleteGraphic()
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onDeleteAllClick(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getAlertTitle())
                .setMessage("Все платежи будут удалены!\nПродолжить?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Удалить все платежи",
                        {dialog, id ->
                            graphicListViewModel.deleteAllPayments()
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onDeletePaymentClick(payment: PAYMENT){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getAlertTitle())
                .setMessage("Удалить платеж?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Удалить платеж",
                        {dialog, id ->
                            graphicListViewModel.deletePayment(payment)
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onAddPaymentClick(payment: PAYMENT){
        val done = payment.done

        if (done == 0) {
            payment._id = -1
            payment.credit_id = credit_id
            Navigator.navigateToPaymentActivity(activity, this, payment)
        } else {
            Toast.makeText(activity, "Платеж уже существует", Toast.LENGTH_LONG).show()
        }
    }

    private fun onItemPaymentClick(payment: PAYMENT){
        // получаем ИД платежа из элемента списка, по его позиции

        val done = payment.done

        val payment = PAYMENT(_id = payment._id,
                credit_id = credit_id,
                date = payment.date,
                summa = payment.summa,
                summa_credit = payment.summa_credit,
                summa_procent = payment.summa_procent
        )

        if (done == 1)
        // Если это факт - открываем для редактирования карточку платежа
            Navigator.navigateToPaymentActivity(activity, this, payment)
        else
        // Если это план - редактируем строку графика платежей
            Navigator.navigateToGraphicItemActivity(activity, this, payment)

    }

    private fun getAlertTitle(): String{
        return getString(R.string.graphic_alert_title)
    }

    private fun onClickSummaPayClearButton() {
        etSummaPay.setText("")
        etSummaPay.requestFocus()
        showKeyboard()
    }
}
