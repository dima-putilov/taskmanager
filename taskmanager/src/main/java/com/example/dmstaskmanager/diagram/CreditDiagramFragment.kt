package com.example.dmstaskmanager.diagram

import android.support.v4.app.Fragment
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.text.format.DateUtils
import android.util.Log
import android.view.*
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.*
import com.example.dmstaskmanager.utils.*
import android.view.MenuItem
import com.google.gson.Gson
import kotlinx.android.synthetic.main.credit_diagram_fragment.*
import kotlin.math.abs

class CreditDiagramFragment : Fragment() {

    var credit_id = -1 // ИД кредита

    lateinit var viewModel: CreditDiagramViewModel
    lateinit var creditDiagramPagerAdapter : CreditDiagramPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.credit_diagram_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()

        setDiagramPager()

        setListeners()

        // === Инициализация текущего кредита, по которому открывается график платежей ===
        var credit : Credit? = null
        activity?.intent?.extras?.let { extras ->
            if (extras.containsKey(Navigator.EXTRA_CREDIT_KEY)) {
                val creditGson = extras.getString(Navigator.EXTRA_CREDIT_KEY)
                Gson().fromJson(creditGson, Credit::class.java)?.also {
                    credit = it
                    credit_id = it.id
                }
            }

        }

        viewModel.initInstance(credit)

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun bindViewModel() {

        viewModel = ViewModelProviders.of(this).get(CreditDiagramViewModel::class.java)

        viewModel.diagramItemList.observe(this, Observer { diagramItemList ->
            diagramItemList?.also { diagramItemList ->
                initDiagramPager(diagramItemList)
            }
        })

        viewModel.currentCredit.observe(this, Observer { currentCredit ->
            currentCredit?.also {
                setCreditHeader(it)
            }
        })

        viewModel.setCurrentPage.observe(this, Observer { currentPage ->
            currentPage?.also {
                setCurrentDiagramPage(it)
            }
        })

        viewModel.showGraphicLoadingIndicatorEvent.observe(this, Observer {
            showGraphicLoadingIndicator()
        })

        viewModel.hideGraphicLoadingIndicatorEvent.observe(this, Observer {
            hideGraphicLoadingIndicator()
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        Log.d("DMS_CREDIT", "ПУНКТ МЕНЮ " + item.itemId)

        val id = item.itemId

//        if (id == R.id.toolbar_payment_add) {
//            onAddNewPaymentClick()
//        }

        return super.onOptionsItemSelected(item)
    }

    private fun setDiagramPager() {
        var currentPosition = 0

        creditDiagramPagerAdapter = CreditDiagramPagerAdapter(activity.supportFragmentManager)
        diagramPager.adapter = creditDiagramPagerAdapter
        creditDiagramPagerAdapter.notifyDataSetChanged()

        // linking pager to tab dots
        creditTabs.setupWithViewPager(diagramPager, true)

        diagramPager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                viewModel.onChangePage(position)

                activity.supportFragmentManager.fragments.forEach {
                    if (it is DiagramItemFragment) {
                        it.updateUI()
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                positionOffsetPixels: Int) {

                val newPosition = if (positionOffset > 0.5) {
                    position + 1
                } else {
                    position
                }

                if (currentPosition != newPosition) {
                    currentPosition = newPosition
                    //viewModel.setCurrentDevice(currentPosition)
                }

                val alpha = abs(1 - positionOffset / 0.5).toFloat()

                diagramPager.alpha = alpha
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        diagramPager.currentItem = 0
    }


    private fun initDiagramPager(diagramItemList: List<DiagramItem>) {
        creditDiagramPagerAdapter.diagramItemsList = diagramItemList
        creditDiagramPagerAdapter.notifyDataSetChanged()
    }

    private fun setCurrentDiagramPage(currentPage: Int) {
        diagramPager.currentItem = currentPage
    }

    private fun setListeners(){

    }

    private fun setCreditHeader(credit : Credit?) {
        credit?.also {credit ->
            val name = credit.name +
                if (credit.date > 0 ) {
                    " от " + DateUtils.formatDateTime(activity,
                        credit.date, DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)
                } else {
                    ""
                }

            val param = "Сумма: " + String.format("%.0f", credit.summa) +
                if (credit.procent > 0) {
                    ", Процент: " + String.format("%.2f%%", credit.procent) + ", Срок: " + credit.period.toString()
                } else {
                    ""
                } +
                if (credit.period > 0) {
                    ", Срок: " + credit.period.toString()
                } else {
                    ""
                }

            tvName.text = name
            tvParam.text = param
        }
    }

    private fun showGraphicLoadingIndicator(){
        diagramPager.visibility = View.INVISIBLE
        graphicProgressBar.visibility = View.VISIBLE
    }

    private fun hideGraphicLoadingIndicator(){
        diagramPager.visibility = View.VISIBLE
        graphicProgressBar.visibility = View.GONE
    }

}
