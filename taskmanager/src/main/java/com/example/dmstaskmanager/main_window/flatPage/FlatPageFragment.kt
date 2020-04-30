package com.example.dmsflatmanager.main_window.flatPage

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.FlatPayment
import com.example.dmstaskmanager.classes.ARGUMENT_PAGE_NUMBER
import com.example.dmstaskmanager.classes.FlatItem
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.classes.SWIPE_REFRESH_DISTANCE_TO_TRIGGER
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.flat.FlatActivity
import com.example.dmstaskmanager.flatPayment.FlatPaymentActivity
import com.example.dmstaskmanager.flatPayment.FlatPaymentListActivity
import com.example.dmstaskmanager.main_window.flatPage.FlatListAdapter
import com.example.dmstaskmanager.main_window.flatPage.FlatListAdapterDelegate
import com.example.dmstaskmanager.utils.Navigator
import com.example.dmstaskmanager.utils.NavigatorResultCode
import com.example.dmstaskmanager.utils.Settings
import com.example.dmstaskmanager.utils.ToolbarUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_flat.*

class FlatPageFragment : Fragment(), FlatListAdapterDelegate {

    companion object {
        private val CM_DELETE_ID = 1
        private val CM_PAYLIST_ID = 5
        private val CM_PAY_ID = 4

        val PAGE = 2

        fun newInstance(page: Int): FlatPageFragment {
            val pageFragment = FlatPageFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.setArguments(arguments)
            return pageFragment
        }
    }

    var pageNumber: Int = 0
    var backColor: Int = 0

    var prevVisibleItem = -1

    lateinit var db: DB

    lateinit var viewModel: FlatListViewModel
    lateinit var flatListAdapter: FlatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)

        setHasOptionsMenu(true)

        // ========= Work with data base ==============
        // открываем подключение к БД
        db = DB(activity)
        db.open()

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.activity_flat, null)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // добавляем контекстное меню к списку
        registerForContextMenu(flatRecyclerView)

        bindViewModel()

        initAdapter()

        setListeners()

        initSwipeRefresh()

        updateList()

    }

    fun updateList() {
        viewModel.getFlatList()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId

        if (id == R.id.action_option_show_closed) {
            Settings.setShowCloseFlatSettings(context, true)
            updateOptionalMenu()
            updateList()
        }

        if (id == R.id.action_option_hide_closed) {
            Settings.setShowCloseFlatSettings(context, false)
            updateOptionalMenu()
            updateList()
        }

        if (id == R.id.action_add) {
            Navigator.navigateToFlatActivity(activity, this, HOME())
        }

        if (id == R.id.action_option_add) {
            Navigator.navigateToFlatActivity(activity, this, HOME())
        }
        if (id == R.id.action_option_delete_all) {

            //  DeleteAll();

        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateOptionalMenu() {
        ToolbarUtils.setupActionBar(activity as AppCompatActivity, PAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                val name = data!!.getStringExtra("name")

                Toast.makeText(activity, "Создан $name", Toast.LENGTH_LONG).show()

            }
        }

        if (requestCode == NavigatorResultCode.FlatPayment.resultCode) {
            if (resultCode == Activity.RESULT_OK) {

                val name = data!!.getStringExtra(Navigator.EXTRA_FLAT_KEY)
                if (name != null) {
                    val flat = Gson().fromJson(name, HOME::class.java)
                    Toast.makeText(activity, "Изменен $flat.name", Toast.LENGTH_LONG).show()
                }

            }
        }

        viewModel.getFlatList()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        //menu.add(0, CM_DELETE_ID, 100, R.string.action_flat_delete);
        menu.add(1, CM_PAY_ID, 100, R.string.action_flat_pay)
        menu.add(2, CM_PAYLIST_ID, 100, R.string.action_flat_paylist)

        menu.setHeaderTitle("Операции")
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo
            // извлекаем id записи и удаляем соответствующую запись в БД
            db.flat_Delete(acmi.id)

            // получаем новый курсор с данными
            viewModel.getFlatList()
            return true
        }

        if (item.itemId == CM_PAYLIST_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            val intent = Intent(activity, FlatPaymentListActivity::class.java)
            intent.putExtra("id", "" + acmi.id)
            startActivityForResult(intent, 7)

            return true
        }

        if (item.itemId == CM_PAY_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterView.AdapterContextMenuInfo

            val intent = Intent(activity, FlatPaymentActivity::class.java)
            intent.putExtra("id", "" + acmi.id)
            startActivityForResult(intent, 5)

            return true
        }

        return super.onContextItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()
        // закрываем подключение при выходе
        db.close()
    }


    private fun bindViewModel() {

        viewModel = ViewModelProviders.of(this).get(FlatListViewModel::class.java)

        viewModel.flatItemList.observe(this, Observer { flatItemList ->
            flatItemList?.also { flatItemList ->
                updateAdapter(flatItemList)
            }
        })

        viewModel.showFlatLoadingIndicatorEvent.observe(this, Observer {
            showFlatLoadingIndicator()
        })

        viewModel.hideFlatLoadingIndicatorEvent.observe(this, Observer {
            hideFlatLoadingIndicator()
        })

    }

    private fun initAdapter(){
        flatListAdapter = FlatListAdapter(activity, this)
        flatRecyclerView.layoutManager = LinearLayoutManager(activity)
        flatRecyclerView.adapter = flatListAdapter
        flatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    val firstVisibleItem = getCurrentItem()

                    if (prevVisibleItem != firstVisibleItem) {
                        if (prevVisibleItem < firstVisibleItem)
                            PageFlatsFloatingActionButton.show()
                        else
                            PageFlatsFloatingActionButton.show()

                        prevVisibleItem = firstVisibleItem
                    }

                }
            }
        })

    }

    private fun getCurrentItem(): Int {
        return (flatRecyclerView.getLayoutManager() as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    private fun updateAdapter(flatItemList: List<FlatItem>){
        flatListAdapter.flatItemList = flatItemList
        flatListAdapter.notifyDataSetChanged()
    }

    private fun setListeners() {
        PageFlatsFloatingActionButton.setOnClickListener { view ->
            val intent = Intent(activity, FlatActivity::class.java)
            startActivityForResult(intent, 1)

            Snackbar.make(view, "Flat added №", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

    }

    private fun initSwipeRefresh() {
        flatSwipeRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                updateList()
            }
        })

        flatSwipeRefreshLayout.setDistanceToTriggerSync(SWIPE_REFRESH_DISTANCE_TO_TRIGGER)
    }

    fun DeleteAll() {
        db.flat_DeleteAll()
    }

    private fun showFlatLoadingIndicator() {
//        taskRecyclerView.visibility = View.GONE
//        taskProgressBar.visibility = View.VISIBLE
    }

    private fun hideFlatLoadingIndicator() {
        flatSwipeRefreshLayout.isRefreshing = false
//        taskRecyclerView.visibility = View.VISIBLE
//        taskProgressBar.visibility = View.GONE
    }

    // Delegate methods
    override fun onFlatItemClick(flat: HOME) {
        Navigator.navigateToFlatActivity(activity, this, flat)
    }

    override fun onArendaActionClick(flatPayment: FlatPayment) {
        navigateToFlatPaymentActivity(flatPayment)
    }

    override fun onRentActionClick(flatPayment: FlatPayment) {
        navigateToFlatPaymentActivity(flatPayment)
    }

    // Navigator methods
    private fun navigateToFlatPaymentActivity(acc: FlatPayment) {
        Navigator.navigateToFlatPaymentActivity(activity, this, acc)
    }

}