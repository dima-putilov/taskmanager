package com.example.dmstaskmanager.diagram

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.example.dmstaskmanager.useCase.GetGraphicUseCase
import com.example.dmstaskmanager.classes.Credit
import com.example.dmstaskmanager.classes.CreditTotals
import com.example.dmstaskmanager.classes.DiagramItem
import com.example.dmstaskmanager.classes.PAYMENT
import com.example.dmstaskmanager.classes.groupByDate
import com.example.dmstaskmanager.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CreditDiagramViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        const val MAX_APPROXIMATION = 12
    }

    var getGraphicUseCase =  GetGraphicUseCase()

    var diagramItemList = MutableLiveData<List<DiagramItem>>()

    var currentDiagramItem = MutableLiveData<DiagramItem>()

    var setCurrentPage= SingleLiveEvent<Int>()

    val isOnlyActiveCredit: Boolean = false

    val showGraphicLoadingIndicatorEvent = SingleLiveEvent<Void>()
    val hideGraphicLoadingIndicatorEvent = SingleLiveEvent<Void>()
    val showGraphicLoadErrorEvent = SingleLiveEvent<Void>()

    private var getGraphicListSubscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()

        getGraphicListSubscription?.dispose()

    }

    fun setGraphicListData(listData: List<DiagramItem>) {
        diagramItemList.value = listData

        if (listData.isNotEmpty()) {
            currentDiagramItem.value = listData[0]
        }
    }

    fun getGraphicListData() : List<DiagramItem> {
        return diagramItemList.value ?: listOf()
    }

    fun initInstance(credit: Credit?){
        getGraphicList(credit)
    }

    private fun initGraphicList(listData: List<DiagramItem>){

        val resultList = mutableListOf<DiagramItem>()
        var cntApprox = 0

        val sourceListData = listData.toMutableList()

        // Добавляем итоговый элемент
        val allCreditTotals = CreditTotals(credit = Credit(name = "Все кредиты"))

        val allCreditItemDataList = mutableListOf<PAYMENT>()

        sourceListData.forEach {

            allCreditTotals += it.creditTotals

            allCreditItemDataList.addAll(it.diagramData)
        }

        allCreditItemDataList.sortBy{ it.date }

        sourceListData.add(0, DiagramItem(allCreditTotals, allCreditItemDataList))

        // Формируем итоговый список
        for ( item in sourceListData) {
            val newList = mutableListOf<PAYMENT>()

            cntApprox = 0
            val diagramData = item.diagramData
            diagramData.forEachIndexed { i, payment ->
                if (cntApprox < MAX_APPROXIMATION) {

                    payment.date = getBegMonth(payment.date)

                    newList.add(payment)

                    if (payment.done != 1) {
                        cntApprox++
                    }
                }

            }


            val reducedList = newList.groupByDate()

            resultList.add(DiagramItem(item.creditTotals, reducedList))
        }

        setGraphicListData(resultList)
    }

    fun onChangePage(position: Int){
        getGraphicListData()?.also{
            if (it.size > position) {
                //currentCredit.value = it[position].creditTotals?.credit
                currentDiagramItem.value = it[position]
            }
        }
    }

    fun getGraphicList(credit: Credit?){

        getGraphicListSubscription?.dispose()
        getGraphicUseCase.cancel()

        getGraphicListSubscription = getGraphicUseCase.getGetAllCreditDataSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onGetGraphicStart() }
                .subscribe(
                        { listData: List<DiagramItem> -> onGetGraphicSuccess(listData) },
                        { error: Throwable -> onGetGraphicError(error) }
                )

        getGraphicUseCase.getAllCreditData(getApplication(), isOnlyActiveCredit)

    }

    private fun onGetGraphicStart() {
        showGraphicLoadingIndicatorEvent.call()
    }

    private fun onGetGraphicSuccess(listData: List<DiagramItem>) {
        hideGraphicLoadingIndicatorEvent.call()
        initGraphicList(listData)
    }

    private fun onGetGraphicError(error: Throwable) {
        Log.d("DMS_CREDIT", "onGetGraphicError = $error")
        hideGraphicLoadingIndicatorEvent.call()
        showGraphicLoadErrorEvent.call()
    }


}