package com.example.dmstaskmanager.graphic

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.example.dmstaskmanager.useCase.GetGraphicUseCase
import com.example.dmstaskmanager.classes.Credit
import com.example.dmstaskmanager.classes.PAYMENT
import com.example.dmstaskmanager.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class GraphicListViewModel: AndroidViewModel {

    constructor(application: Application) : super(application)

    var needInitDate = true

    var getGraphicUseCase =  GetGraphicUseCase()

    var graphicListData = MutableLiveData<List<PAYMENT>>()

    var credit = MutableLiveData<Credit>()
    var nextPaymentDate = MutableLiveData<Long>()

    var summaPayAll = MutableLiveData<Double>()
    var summaCreditAll = MutableLiveData<Double>()
    var summaProcentAll = MutableLiveData<Double>()

    var summaPayFactAll = MutableLiveData<Double>()
    var summaCreditFactAll = MutableLiveData<Double>()
    var summaProcentFactAll = MutableLiveData<Double>()

    var summaNextPay = MutableLiveData<Double>()
    var onlyProcentOnFirstPay = MutableLiveData<Boolean>()

    var currentPayPosition = MutableLiveData<Int>()

    val graphicSavedEvent = SingleLiveEvent<Void>()
    val showGraphicLoadingIndicatorEvent = SingleLiveEvent<Void>()
    val hideGraphicLoadingIndicatorEvent = SingleLiveEvent<Void>()
    val showGraphicLoadErrorEvent = SingleLiveEvent<Void>()
    val showSaveGraphicLoadingIndicatorEvent = SingleLiveEvent<Void>()
    val hideSaveGraphicLoadingIndicatorEvent = SingleLiveEvent<Void>()

    private var getGraphicListSubscription: Disposable? = null
    private var getCalcGraphicListSubscription: Disposable? = null
    private var getAddAllPaymentsToCurrentDateSubscription: Disposable? = null

    private var getSaveGraphicSubscription: Disposable? = null

    private var getGraphicSubscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()

        getGraphicSubscription?.dispose()
        getGraphicListSubscription?.dispose()
        getCalcGraphicListSubscription?.dispose()
        getAddAllPaymentsToCurrentDateSubscription?.dispose()

        getGraphicUseCase.cancel()
    }

    fun getGraphicListData(): List<PAYMENT> {
        return graphicListData.value?.let {
            it
        } ?: listOf()
    }

    fun getCredit(): Credit? {
        return credit.value
    }

    fun getSummaNextPay(): Double {
        return summaNextPay.value ?: 0.0
    }

    fun getOnlyProcentOnFirstPay(): Boolean {
        return onlyProcentOnFirstPay.value ?: false
    }

    fun getNextPaymentDate(): Long {
        return nextPaymentDate.value ?: 0
    }

    fun getCurrentPayPosition() : Int{
        return currentPayPosition.value ?: 0
    }


    fun setCurrentPayPosition(pos: Int) {
        currentPayPosition.value = pos
    }

    fun setGraphicListData(listData: List<PAYMENT>) {
        graphicListData.value = listData
    }

    fun setOnlyProcentOnFirstPay(flag: Boolean) {
        onlyProcentOnFirstPay.value = flag
    }

    fun setNextPaymentDate(date: Long){
        nextPaymentDate.value = date
    }

    fun setSummaNextPay(summa: Double){
        summaNextPay.value = summa
    }

    fun setSummaPayAll(summa: Double){
        summaPayAll.value = summa
    }

    fun setSummaPayAll(summa: Long){
        summaPayAll.value = L2D(summa)
    }

    fun setSummaCreditAll(summa: Double){
        summaCreditAll.value = summa
    }

    fun setSummaCreditAll(summa: Long){
        summaCreditAll.value = L2D(summa)
    }

    fun setSummaProcentAll(summa: Double){
        summaProcentAll.value = summa
    }

    fun setSummaProcentAll(summa: Long){
        summaProcentAll.value = L2D(summa)
    }

    fun setSummaPayFactAll(summa: Long){
        summaPayFactAll.value = L2D(summa)
    }

    fun setSummaCreditFactAll(summa: Long){
        summaCreditFactAll.value = L2D(summa)
    }

    fun setSummaProcentFactAll(summa: Long){
        summaProcentFactAll.value = L2D(summa)
    }

    fun Init(cred: Credit){
        credit.value = cred
        summaNextPay.value = cred.summa_pay

        needInitDate = true

        getGraphicList()
    }

    private fun initGraphicList(listData: List<PAYMENT>){
        setGraphicListData(listData)
        updateParameters()
    }

    fun getGraphicList(){

        getGraphicListSubscription?.dispose()
        getGraphicUseCase.cancel()

        getCredit()?.let { credit ->
            getGraphicListSubscription = getGraphicUseCase.getGraphicListSubject
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { onGetGraphicStart() }
                    .subscribe(
                            { listData: List<PAYMENT> -> onGetGraphicSuccess(listData) },
                            { error: Throwable -> onGetGraphicError(error) }
                    )

            getGraphicUseCase.getGraphicList(getApplication(), credit)

        }

    }

    fun calculateCredit(){

        getCalcGraphicListSubscription?.dispose()
        getGraphicUseCase.cancel()

        getCredit()?.let { credit ->
            getCalcGraphicListSubscription = getGraphicUseCase.getGraphicListSubject
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { onGetGraphicStart() }
                    .subscribe(
                            { listData: List<PAYMENT> -> onGetGraphicSuccess(listData) },
                            { error: Throwable -> onGetGraphicError(error) }
                    )

            getGraphicUseCase.getCalculateGraphicList(getApplication(), credit, getNextPaymentDate(), getSummaNextPay(), getOnlyProcentOnFirstPay())

        }

    }

    fun addAllPaymentsToCurrentDate(){
        getCredit()?.also { credit ->

            getAddAllPaymentsToCurrentDateSubscription?.dispose()
            getGraphicUseCase.cancel()

            getCredit()?.let { credit ->
                getAddAllPaymentsToCurrentDateSubscription = getGraphicUseCase.getGraphicListSubject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { onGetGraphicStart() }
                        .subscribe(
                                { listData: List<PAYMENT> -> onGetGraphicSuccess(listData) },
                                { error: Throwable -> onGetGraphicError(error) }
                        )

                getGraphicUseCase.addAllPaymentsToCurrentDate(getApplication(), credit, getGraphicListData())

            }

        }
    }

    fun deleteAllPayments(){

        getGraphicSubscription?.dispose()
        getGraphicUseCase.cancel()

        getCredit()?.let { credit ->
            getGraphicSubscription = getGraphicUseCase.getGraphicListSubject
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { onGetGraphicStart() }
                    .subscribe(
                            { listData: List<PAYMENT> -> onGetGraphicSuccess(listData) },
                            { error: Throwable -> onGetGraphicError(error) }
                    )

            getGraphicUseCase.deleteAllPayments(getApplication(), credit)

        }
    }

    fun deleteGraphic(){

        getGraphicSubscription?.dispose()
        getGraphicUseCase.cancel()

        getCredit()?.also { credit ->

        getGraphicSubscription = getGraphicUseCase.getGraphicListSubject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { onGetGraphicStart() }
                        .subscribe(
                                { listData: List<PAYMENT> -> onGetGraphicSuccess(listData) },
                                { error: Throwable -> onGetGraphicError(error) }
                        )

                getGraphicUseCase.deleteGraphic(getApplication(), credit)

        }
    }

    fun deletePayment(payment: PAYMENT){

        getGraphicSubscription?.dispose()
        getGraphicUseCase.cancel()

        getCredit()?.also { credit ->
                getGraphicSubscription = getGraphicUseCase.getGraphicListSubject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { onGetGraphicStart() }
                        .subscribe(
                                { listData: List<PAYMENT> -> onGetGraphicSuccess(listData) },
                                { error: Throwable -> onGetGraphicError(error) }
                        )

                getGraphicUseCase.deletePayment(getApplication(), credit, payment)

        }

    }

    private fun onGetGraphicStart() {
        showGraphicLoadingIndicatorEvent.call()
    }

    private fun onGetGraphicSuccess(listData: List<PAYMENT>) {
        hideGraphicLoadingIndicatorEvent.call()
        initGraphicList(listData)
    }

    private fun onGetGraphicError(error: Throwable) {
        Log.d("DMS_CREDIT", "onGetGraphicError = $error")
        hideGraphicLoadingIndicatorEvent.call()
        showGraphicLoadErrorEvent.call()
    }

    private fun updateParameters(){
        // Если график заполнен, то определяем очередную дату платежа
        // = +1 месяц от последней даты платежа
        getCredit()?.also { credit ->

            var summaPayAll = 0L
            var summaProcentAll = 0L
            var summaCreditAll = 0L

            var summaPayFactAll = 0L
            var summaProcentFactAll = 0L
            var summaCreditFactAll = 0L

            var currentPayPosition = 0

            var nextPaymentDate = 0L

            getGraphicListData()?.also { listData ->
                if (listData.isNotEmpty()) {

                    var t_date: Long = 0
                    var cur_date: Long = 0
                    for (cv in listData) {
                        if (cv.done == 1) {
                            cur_date = cv.date
                            if (t_date < cur_date) t_date = cur_date

                            summaPayFactAll += D2L(cv.summa)
                            summaCreditFactAll += D2L(cv.summa_credit)
                            summaProcentFactAll += D2L(cv.summa_procent)

                            currentPayPosition++

                        }

                        summaPayAll += D2L(cv.summa)
                        summaCreditAll += D2L(cv.summa_credit)
                        summaProcentAll += D2L(cv.summa_procent)

                    }

                    if (t_date == 0L && credit.date > 0) {
                        t_date = credit.date
                    }

                    val dateAndTime = Calendar.getInstance()
                    dateAndTime.timeInMillis = t_date // устанавливаем дату последнего платежа
                    dateAndTime.add(Calendar.MONTH, 1) // следующий месяц

                    nextPaymentDate = dateAndTime.timeInMillis
                } else {
                    // пустой график - дата платежа = дата кредита (условно)
                    if (credit.date > 0) {
                        nextPaymentDate = credit.date
                    }
                }
            }

            setSummaPayAll(summaPayAll)
            setSummaCreditAll(summaCreditAll)
            setSummaProcentAll(summaProcentAll)

            setSummaPayFactAll(summaPayFactAll)
            setSummaCreditFactAll(summaCreditFactAll)
            setSummaProcentFactAll(summaProcentFactAll)

            if (needInitDate) {
                setNextPaymentDate(nextPaymentDate)
                needInitDate = false
            }

            setCurrentPayPosition(currentPayPosition)
        }

    }

    fun saveGraphic() {

        getCredit()?.also { credit ->

            getSaveGraphicSubscription?.dispose()
            getGraphicUseCase.cancel()

            getCredit()?.let { credit ->
                getSaveGraphicSubscription = getGraphicUseCase.saveGraphicSubject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { onSaveGraphicStart() }
                        .subscribe(
                                { void -> onSaveGraphicSuccess() },
                                { error: Throwable -> onSaveGraphicError(error) }
                        )

                getGraphicUseCase.saveGraphic(getApplication(), credit, getGraphicListData())

            }

        }

    }

    fun onSaveGraphicStart() {
        showSaveGraphicLoadingIndicatorEvent.call()
    }

    fun onSaveGraphicSuccess() {
        hideSaveGraphicLoadingIndicatorEvent.call()
        graphicSavedEvent.call()
    }

    fun onSaveGraphicError(error: Throwable) {
        //graphicSavedEvent.call()
    }

}