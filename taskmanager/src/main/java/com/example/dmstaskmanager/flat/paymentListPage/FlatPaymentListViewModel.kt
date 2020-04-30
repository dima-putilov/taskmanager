package com.example.dmsflatmanager.main_window.flatPage

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.example.dmstaskmanager.useCase.GetFlatPaymentsUseCase
import com.example.dmstaskmanager.classes.FlatPayment
import com.example.dmstaskmanager.classes.FlatPaymentOperationType
import com.example.dmstaskmanager.classes.FlatPaymentOperationType.PROFIT
import com.example.dmstaskmanager.classes.FlatPaymentOperationType.RENT
import com.example.dmstaskmanager.classes.FlatPaymentType.Outlay
import com.example.dmstaskmanager.classes.FlatPaymentType.Profit
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FlatPaymentListViewModel(application: Application): AndroidViewModel(application) {

    var getFlatPaymentListUseCase = GetFlatPaymentsUseCase()

    var flatPaymentList = MutableLiveData<List<FlatPayment>>()

    private var flat : HOME? = null

    var accountToPayment = SingleLiveEvent<FlatPayment>()
//    var flatPayToOpen = SingleLiveEvent<FlatPayment>()

    var showFlatPaymemtListLoadingIndicatorEvent = SingleLiveEvent<Void>()
    var hideFlatPaymemtListLoadingIndicatorEvent = SingleLiveEvent<Void>()

    private var getFlatPaymentSubscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()

        getFlatPaymentSubscription?.dispose()
    }

    fun setFlat(flat: HOME) {
        this.flat = flat
        loadFlatPaymentList()
    }

    fun onPaymentClick(type: FlatPaymentOperationType) {
        flat?.also {
            when (type) {
                FlatPaymentOperationType.PROFIT -> {
                    accountToPayment.value = FlatPayment(flat_id = it._id, operation = PROFIT,
                        paymentType = Profit)
                }
                FlatPaymentOperationType.RENT -> {
                    accountToPayment.value = FlatPayment(flat_id = it._id, operation = RENT,
                        paymentType = Outlay)
                }
            }
        }

    }

    fun loadFlatPaymentList(){

        flat?.also { flat ->

            getFlatPaymentSubscription?.dispose()
            getFlatPaymentListUseCase.init()

            getFlatPaymentSubscription = getFlatPaymentListUseCase.getFlatPaymentListSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onGetDataStart() }
                .doOnComplete { onGetDataStop() }
                .subscribe(
                    { listData: List<FlatPayment> -> onGetDataSuccess(listData) },
                    { error: Throwable -> onGetDatasError(error) }
                )

            getFlatPaymentListUseCase.getFlatPaymentList(getApplication(), flat._id)

        }

    }

    private fun onGetDataStart() {
        showFlatPaymemtListLoadingIndicatorEvent.call()
    }

    private fun onGetDataStop() {
        hideFlatPaymemtListLoadingIndicatorEvent.call()
    }

    private fun onGetDataSuccess(listData: List<FlatPayment>) {
//        hideFlatPaymemtListLoadingIndicatorEvent.call()

        flatPaymentList.value = listData.sortedByDescending { it.date }

    }

    private fun onGetDatasError(error: Throwable) {
        hideFlatPaymemtListLoadingIndicatorEvent.call()
        //showGraphicLoadErrorEvent.call()
    }

}