package com.example.dmsflatmanager.main_window.flatPage

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.example.dmstaskmanager.useCase.GetFlatsUseCase
import com.example.dmstaskmanager.classes.FlatItem
import com.example.dmstaskmanager.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FlatListViewModel(application: Application): AndroidViewModel(application) {

    var getFlatListUseCase =  GetFlatsUseCase()

    var flatItemList = MutableLiveData<List<FlatItem>>()

//    var flatToOpen = SingleLiveEvent<HOME>()
//    var flatPayToOpen = SingleLiveEvent<FlatPayment>()

    var showFlatLoadingIndicatorEvent = SingleLiveEvent<Void>()
    var hideFlatLoadingIndicatorEvent = SingleLiveEvent<Void>()

    private var getFlatsSubscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()

        getFlatsSubscription?.dispose()
    }

    fun getFlatList(){

        getFlatsSubscription?.dispose()

        getFlatsSubscription = getFlatListUseCase.getFlatListSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onGetFlatItemsListStart() }
                .subscribe(
                        { listData: List<FlatItem> -> onGetDataSuccess(listData) },
                        { error: Throwable -> onGetDatasError(error) }
                )

        getFlatListUseCase.getFlatList(getApplication())

    }

    private fun onGetFlatItemsListStart() {
        showFlatLoadingIndicatorEvent.call()
    }

    private fun onGetDataSuccess(listData: List<FlatItem>) {
        hideFlatLoadingIndicatorEvent.call()

        flatItemList.value = listData

    }

    private fun onGetDatasError(error: Throwable) {
        Log.d("DMS_TASK", "onGetDatasError = $error")
        //hideGraphicLoadingIndicatorEvent.call()
        //showGraphicLoadErrorEvent.call()
    }

}