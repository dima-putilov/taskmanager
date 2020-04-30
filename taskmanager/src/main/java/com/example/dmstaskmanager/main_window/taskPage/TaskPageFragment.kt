package com.example.dmstaskmanager.main_window.taskPage

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener

import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.*
import com.example.dmstaskmanager.task.TaskItemActivity

import com.example.dmstaskmanager.utils.*
import kotlinx.android.synthetic.main.activity_credit.creditSwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_maintask.*

class TaskPageFragment : Fragment(), TaskListAdapterDelegate {

    companion object {
        fun newInstance(page: Int): TaskPageFragment {
            val pageFragment = TaskPageFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.setArguments(arguments)
            return pageFragment
        }
    }

    val PAGE = 0
    private val CM_DELETE_ID = 1

    var pageNumber: Int = 0
    var backColor: Int = 0

    lateinit var viewModel: TaskViewModel
    lateinit var taskListAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)

        // ========= Work with data base ==============
        // открываем подключение к БД
        val db = DB(activity)
        db.open()

        // автоформирование задач
        db.autoTask(activity)
        db.autoFlatTask(activity)

        db.close()

        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.activity_maintask, container, false)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // добавляем контекстное меню к списку
        registerForContextMenu(taskRecyclerView)

        bindViewModel()

        initAdapter()

        setListeners()

        initSwipeRefresh()

        viewModel.getTaskList()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NavigatorResultCode.CreateTask.resultCode) {
            if (resultCode == Activity.RESULT_OK) {

                //val name = data!!.getStringExtra("name")

                Toast.makeText(activity, "Создана новая задача", Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode == NavigatorResultCode.EditTask.resultCode) {
            if (resultCode == Activity.RESULT_OK) {

//                val summa = 0.0
//                val name = data!!.getStringExtra("name")

                Toast.makeText(activity, "Изменена задача", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.getTaskList()

    }

    private fun bindViewModel() {

        viewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)

        viewModel.taskItemList.observe(this, Observer { taskItemList ->
            taskItemList?.also { taskItemList ->
                updateAdapter(taskItemList)
            }
        })

        viewModel.taskToOpen.observe(this, Observer { taskToOpen ->
            taskToOpen?.also { taskToOpen ->
                showTaskView(taskToOpen)
            }
        })

        viewModel.taskToClose.observe(this, Observer { taskToClose ->
            taskToClose?.also { taskToClose ->
                beforeCloseTask(taskToClose)
            }
        })

        viewModel.taskIsClosed.observe(this, Observer { task ->
            task?.also { task ->
                onTaskIsClosed(task)
            }
        })

        viewModel.creditTaskToClose.observe(this, Observer { creditId ->
            creditId?.also { creditId ->
                onCreditCloseClick(creditId)
            }
        })

        viewModel.creditPayToOpen.observe(this, Observer { creditPayToOpen ->
            creditPayToOpen?.also { creditPayToOpen ->
                showCurrentPaymentView(creditPayToOpen)
            }
        })

        viewModel.flatArendaTaskToClose.observe(this, Observer { flatArendaTaskToClose ->
            flatArendaTaskToClose?.also { flatArendaTaskToClose ->
                onFlatArendaCloseClick(flatArendaTaskToClose)
            }
        })

        viewModel.flatTaskToClose.observe(this, Observer { flatTaskToClose ->
            flatTaskToClose?.also { flatTaskToClose ->
                onFlatPayCloseClick(flatTaskToClose)
            }
        })

        viewModel.flatPayToOpen.observe(this, Observer { flatPayToOpen ->
            flatPayToOpen?.also { flatPayToOpen ->
                showFlatPayView(flatPayToOpen)
            }
        })

        viewModel.showTaskLoadingIndicatorEvent.observe(this, Observer {
            showTaskLoadingIndicator()
        })

        viewModel.hideTaskLoadingIndicatorEvent.observe(this, Observer {
            hideTaskLoadingIndicator()
        })

    }

    private fun initSwipeRefresh() {
        taskSwipeRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                viewModel.getTaskList()
            }
        })

        taskSwipeRefreshLayout.setDistanceToTriggerSync(SWIPE_REFRESH_DISTANCE_TO_TRIGGER)
    }

    private fun initAdapter(){
        taskListAdapter = TaskListAdapter(activity, this)
        taskRecyclerView.layoutManager = LinearLayoutManager(activity)
        taskRecyclerView.adapter = taskListAdapter

        //(taskRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

    }

    private fun updateAdapter(taskItemList: List<TaskItem>){
        taskListAdapter.updateList(taskItemList)
    }

    private fun setListeners() {
        taskFloatingActionButton.setOnClickListener {
            Navigator.navigateToTaskActivity(activity, this, TASK(), NavigatorResultCode.CreateTask.resultCode)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId


        if (id == R.id.action_add) {
            val intent = Intent(activity, TaskItemActivity::class.java)
            startActivityForResult(intent, 1)
        }

        if (id == R.id.action_option_add) {
            val intent = Intent(activity, TaskItemActivity::class.java)
            startActivityForResult(intent, 1)
        }

        if (id == R.id.action_option_delete_all) {

            onDeleteAllTaskClick()

        }

        return super.onOptionsItemSelected(item)
    }

    fun DeleteTaskAll() {
       // db.deleteAllTask()
    }

    private fun getAlertTitle(): String{
        return getString(R.string.task_alert_title)
    }

    fun onCreditCloseClick(task: Task){
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        builder.setTitle(getAlertTitle())
                .setMessage("Ввести очередной платеж?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Создать платеж",
                        {dialog, id ->
                            viewModel.onCreditCloseApprove(task)
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onFlatArendaCloseClick(task: Task){

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        builder.setTitle(getAlertTitle())
                .setMessage("Ввести поступление по аренде?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Платеж за аренду",
                        {dialog, id ->
                            viewModel.onFlatArendaCloseApprove(task)
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onFlatPayCloseClick(task: Task){

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        builder.setTitle(getAlertTitle())
                .setMessage("Ввести платеж за коммунальные услуги?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Оплатить квартплату",
                        {dialog, id ->
                            viewModel.onFlatCloseApprove(task)
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun onDeleteAllTaskClick(){

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        builder.setTitle(getAlertTitle())
                .setMessage("Удалить все задачи?")
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(true)
                .setPositiveButton("Удалить все задачи",
                        {dialog, id ->
                            DeleteTaskAll()
                        })
                .setNegativeButton("Отмена",
                        { dialog, id ->
                            dialog.cancel()
                        })
        val alert = builder.create()
        alert.show()
    }

    private fun showCurrentPaymentView(payment: PAYMENT){
        Navigator.navigateToPaymentActivity(activity, this, payment)
    }

    private fun showFlatPayView(flatPayment: FlatPayment) {
        Navigator.navigateToFlatPaymentActivity(activity, this, flatPayment)
    }

    private fun showTaskView(task: TASK) {
        Navigator.navigateToTaskActivity(activity, this, task, NavigatorResultCode.EditTask.resultCode)
    }

    private fun beforeCloseTask(task: Task) {
        val ad = AlertDialog.Builder(context, R.style.AlertDialogStyle)
        ad.setTitle(getAlertTitle())  // заголовок
        ad.setMessage("Закрыть задачу?") // сообщение
        ad.setPositiveButton("Да") { dialog, arg1 ->
            task.isFinishChecked = true
            viewModel.closeTask(task)
        }
        ad.setNegativeButton("Отмена") { dialog, arg1 ->
            task.isFinishChecked = false
            viewModel.closeTask(task)
        }

        ad.setCancelable(true)
        ad.setOnCancelListener { }

        ad.show()
    }

    private fun onTaskIsClosed(task: Task) {
        Toast.makeText(activity, "Завершена ${task.id}", Toast.LENGTH_LONG).show()
    }

    private fun showTaskLoadingIndicator() {
//        taskRecyclerView.visibility = View.GONE
//        taskProgressBar.visibility = View.VISIBLE
    }

    private fun hideTaskLoadingIndicator() {
        taskSwipeRefreshLayout.isRefreshing = false
//        taskRecyclerView.visibility = View.VISIBLE
//        taskProgressBar.visibility = View.GONE
    }

    override fun onTaskGroupClick(taskGroup: TaskGroup) {
        viewModel.onGroupClick(taskGroup)
    }

    override fun onTaskItemClick(task: Task) {
        viewModel.onTaskItemClick(task)
    }

    override fun onFinishCheckBoxClick(task: Task) {
        viewModel.onFinishTaskClick(task)
    }
}