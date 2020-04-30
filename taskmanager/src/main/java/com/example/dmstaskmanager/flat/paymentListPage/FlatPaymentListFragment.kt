package com.example.dmstaskmanager.flat.paymentListPage

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dmsflatmanager.main_window.flatPage.FlatPaymentListViewModel
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.FlatPayment
import com.example.dmstaskmanager.classes.ARGUMENT_PAGE_NUMBER
import com.example.dmstaskmanager.classes.FlatPaymentOperationType
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.classes.SWIPE_REFRESH_DISTANCE_TO_TRIGGER
import com.example.dmstaskmanager.utils.Navigator
import com.example.dmstaskmanager.utils.Navigator.Companion.navigateToFlatPaymentActivity
import com.example.dmstaskmanager.utils.NavigatorResultCode
import com.example.dmstaskmanager.utils.gone
import com.example.dmstaskmanager.utils.visible
import com.google.gson.Gson
import kotlinx.android.synthetic.main.flat_payment_list_fragment.*

/**
 * Created by dima on 08.11.2018.
 */
class FlatPaymentListFragment : Fragment(), FlatPaymentListAdapterDelegate {

    var pageNumber: Int = 0
    private var flat_id : Int = -1
    var prevVisibleItem = 1

    var flat: HOME? = null

    lateinit var viewModel: FlatPaymentListViewModel
    lateinit var flatPaymentListAdapter: FlatPaymentListAdapter

    companion object {

        fun newInstance(page: Int): FlatPaymentListFragment {
            val pageFragment = FlatPaymentListFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.setArguments(arguments)
            return pageFragment
        }

        fun getTitle(): String  {
            return "Платежи"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.flat_payment_list_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        setListeners()

        initSwipeRefresh()

        bindViewModel()

        activity.intent?.also { intent ->
            if (intent.hasExtra(Navigator.EXTRA_FLAT_KEY)) {
                val taskGson = intent.getStringExtra(Navigator.EXTRA_FLAT_KEY)
                val flat = Gson().fromJson(taskGson, HOME::class.java)
                setCurrentFlat(flat)
            }
        }

    }

    fun setCurrentFlat(flat: HOME) {
        if (this.flat_id != flat._id) {
            this.flat = flat
            loadData()
        }
    }

    private fun loadData() {

        Log.d("RELOAD", "loadData flat = ${Gson().toJson(flat)}")

        flat?.also {
            Log.d("RELOAD", "reload")
            viewModel.setFlat(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NavigatorResultCode.FlatPayment.resultCode) {
            viewModel.loadFlatPaymentList()
        }
    }

    private fun bindViewModel() {

        viewModel = ViewModelProviders.of(this).get(FlatPaymentListViewModel::class.java)

        viewModel.flatPaymentList.observe(this, Observer { flatPaymentList ->
            flatPaymentList?.also { flatPaymentList ->
                updateAdapter(flatPaymentList)
            }
        })

        viewModel.accountToPayment.observe(this, Observer { accountToPayment ->
            accountToPayment?.also { accountToPayment ->
                createPayment(accountToPayment)
            }
        })

        viewModel.showFlatPaymemtListLoadingIndicatorEvent.observe(this, Observer {
            showFlatPaymentLoadingIndicator()
        })

        viewModel.hideFlatPaymemtListLoadingIndicatorEvent.observe(this, Observer {
            hidePaymentFlatLoadingIndicator()
        })

    }

    private fun initAdapter(){
        flatPaymentListAdapter = FlatPaymentListAdapter(activity, this)
        flatPaymentRecyclerView.layoutManager = LinearLayoutManager(activity)
        flatPaymentRecyclerView.adapter = flatPaymentListAdapter
        flatPaymentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    val firstVisibleItem = getCurrentItem()

                    if (prevVisibleItem != firstVisibleItem) {
                        if (prevVisibleItem < firstVisibleItem)
                            fabFlatPaymentList.show()
                        else
                            fabFlatPaymentList.show()

                        prevVisibleItem = firstVisibleItem
                    }

                }
            }
        })

    }

    private fun updateAdapter(flatPaymentItemList: List<FlatPayment>){

        Log.d("RELOAD", "flatPaymentItemList = ${Gson().toJson(flatPaymentItemList)}")

        flatPaymentListAdapter.flatPaymentItemList = flatPaymentItemList
        flatPaymentListAdapter.notifyDataSetChanged()
    }

    private fun getCurrentItem(): Int {
        return (flatPaymentRecyclerView.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    private fun setListeners() {
        fabFlatPaymentList.setOnClickListener {
            flat?.also {
                val listOperation = FlatPaymentOperationType.getFlatOperationsByType(it.type)
                showPaymentDialog(listOperation)
            }


//            val payment = FlatPayment(flat_id = flat_id)
//            Navigator.navigateToFlatPaymentActivity(activity, this, payment)
        }
    }

    // Comment dialog
    private fun showPaymentDialog(listOperation: List<FlatPaymentOperationType>) {

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(flat?.name)

        builder.setNegativeButton("Отмена", null)
        val commentDialogItems: MutableList<String> = mutableListOf()
        for (commentActionItem in listOperation) {
            commentDialogItems.add(commentActionItem.title)
        }
        builder.setItems(commentDialogItems.toTypedArray()) { dialog, which ->
                viewModel.onPaymentClick(listOperation[which])
        }
        builder.show()
    }

    private fun initSwipeRefresh() {
        flatSwipeRefreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                viewModel.loadFlatPaymentList()
            }
        })

        flatSwipeRefreshLayout.setDistanceToTriggerSync(SWIPE_REFRESH_DISTANCE_TO_TRIGGER)
    }

    private fun showFlatPaymentLoadingIndicator() {
        flatPaymentListProgressBar.visible()
        flatPaymentRecyclerView.gone()
    }

    private fun hidePaymentFlatLoadingIndicator() {
        flatSwipeRefreshLayout.isRefreshing = false
        flatPaymentListProgressBar.gone()
        flatPaymentRecyclerView.visible()
    }

    private fun createPayment(accountToPayment: FlatPayment) {
        navigateToFlatPaymentActivity(activity, this, accountToPayment)
    }

    override fun onItemClick(payment: FlatPayment) {
        Navigator.navigateToFlatPaymentActivity(activity, this, payment)
    }

}