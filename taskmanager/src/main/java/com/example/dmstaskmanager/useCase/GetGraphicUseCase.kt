package com.example.dmstaskmanager.useCase

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.classes.Credit
import com.example.dmstaskmanager.classes.CreditTotals
import com.example.dmstaskmanager.classes.DiagramItem
import com.example.dmstaskmanager.classes.PAYMENT
import com.example.dmstaskmanager.utils.*
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.max

class GetGraphicUseCase {

    var getGetAllCreditDataSubject: PublishSubject<List<DiagramItem>> = PublishSubject.create<List<DiagramItem>>()
    var getGraphicListSubject: PublishSubject<List<PAYMENT>> = PublishSubject.create<List<PAYMENT>>()
    var saveGraphicSubject: PublishSubject<Boolean> = PublishSubject.create<Boolean>()

    private var threadJob: Job? = null

    fun cancel() {
        if (threadJob != null && threadJob!!.isActive) {
            threadJob?.cancel()
        }
    }

    fun getAllCreditData(context: Context, isOnlyActive: Boolean = false) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val resultList = mutableListOf<DiagramItem>()

            val creditList = getAllCredit(context)

            creditList.forEach {
                val listData = fillGraphic(context, it, true)

                val creditTotals= CreditTotals(it)

                listData.forEach { pay ->
                    creditTotals += pay
                }

                resultList.add(DiagramItem(creditTotals, listData))
            }

            getGetAllCreditDataSubject.onNext(resultList)

        }

    }

    fun getAllCredit(context: Context) : List<Credit> {

        val db = DB(context)
        db.open()
        val allCredit = db.getAllCredits()
        db.close()

        return allCredit
    }

    fun getGraphicList(context: Context, credit: Credit) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val listData = fillGraphic(context, credit, true)

            getGraphicListSubject.onNext(listData)

        }

    }


    fun getCalculateGraphicList(context: Context, credit: Credit, nextPaymentDate: Long, summaNextPay: Double, onlyProcentOnFirstPay: Boolean) {
        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val listData = calculateCredit(context, credit, nextPaymentDate, summaNextPay, onlyProcentOnFirstPay)

            getGraphicListSubject.onNext(listData)

        }
    }

    fun addAllPaymentsToCurrentDate(context: Context, credit: Credit, listData: List<PAYMENT>) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            addAllPayments(context, credit, listData)

            val newListData = fillGraphic(context, credit, true)

            getGraphicListSubject.onNext(newListData)

        }

    }

    fun deleteAllPayments(context: Context, credit: Credit) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val db = DB(context)
            db.open()
            db.payment_DeleteAll(credit.id)
            db.close()

            val listData = fillGraphic(context, credit, true)

            getGraphicListSubject.onNext(listData)

        }

    }

    fun deleteGraphic(context: Context, credit: Credit) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val db = DB(context)
            db.open()
            db.graphic_DeleteAll(credit.id)
            db.close()

            val listData = fillGraphic(context, credit, true)

            getGraphicListSubject.onNext(listData)

        }

    }

    fun deletePayment(context: Context, credit: Credit, payment: PAYMENT) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val db = DB(context)
            db.open()

            if (payment.done == 1) {
                // Если это факт - удаляем платеж
                db.payment_Delete(payment._id)
            } else {
                // Если это план - удаляем строку графика
                db.graphic_Delete(payment._id)
            }

            db.close()

            val listData = fillGraphic(context, credit, true)

            getGraphicListSubject.onNext(listData)

        }

    }

    private fun addAllPayments(context: Context, credit: Credit, listData: List<PAYMENT>) {

        val curDate = System.currentTimeMillis()

        val db = DB(context)
        db.open()

        for (cv in listData) {

            if (cv.done == 1) continue

            if (curDate >= cv.date) {

                val pay = PAYMENT(
                        credit_id = credit.id,
                        date = cv.date,
                        summa = cv.summa,
                        summa_credit = cv.summa_credit,
                        summa_procent = cv.summa_procent)

                db.payment_Add(pay)

            }

        }

        db.close()

    }


    fun saveGraphic(context: Context, credit: Credit, listData: List<PAYMENT>) {
        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val db = DB(context)
            db.open()
            db.graphic_DeleteAll(credit.id)

            for (cv in listData) {
                val pay = PAYMENT(credit.id)
                pay.date = cv.date
                pay.summa = cv.summa
                pay.summa_credit = cv.summa_credit
                pay.summa_procent = cv.summa_procent

                db.graphic_Add(pay)

            }

            db.close()

            saveGraphicSubject.onNext(true)
        }


    }


    private fun calculateCredit(context: Context, credit: Credit, nextPaymentDate: Long, summaNextPay: Double, onlyProcentOnFirstPay: Boolean) : MutableList<PAYMENT>{

            val listData = fillGraphic(context, credit, false)

            var nextDate = Calendar.getInstance()
            nextDate.timeInMillis = nextPaymentDate

            var prevDate = Calendar.getInstance()
            prevDate.timeInMillis = credit.date

            var summaRestCreditAll: Long = D2L(credit.summa)

            var graphicSummaPay: Long = D2L(summaNextPay)

            var isFirstPay = true

            var pay: PAYMENT

            var summaCreditAll = 0L

            var payDate = 0L
            for (cv in listData) {
                summaRestCreditAll -= D2L(cv.summa_credit)
                payDate = cv.date
                prevDate.timeInMillis = payDate

                if (nextDate.timeInMillis <= payDate) {
                    nextDate.add(Calendar.MONTH, 1)
                }

                summaCreditAll+=D2L(cv.summa_credit)
            }

            var countDaysInYear = 0
            var countDaysInPrevYear = 0
            var countPrevDays = 0
            var countDays = 0

            var summaPay: Long = 0
            var summaCredit: Long = 0
            var summaProcent: Long = 0

            var tempSummaProcent: Double = 0.0

            while (summaRestCreditAll > 0) {

                summaPay = if (summaRestCreditAll < graphicSummaPay) {
                    summaRestCreditAll
                } else {
                    graphicSummaPay
                }

                tempSummaProcent = if (prevDate != null && nextDate != null) {

                    countDaysInPrevYear = diffDateInDays(getEndYear(prevDate), getBegYear(prevDate)) + 1

                    countDaysInYear = diffDateInDays(getEndYear(nextDate.timeInMillis), getBegYear(nextDate.timeInMillis)) + 1

                    if (countDaysInPrevYear != countDaysInYear) {
                        countDays = diffDateInDays(getBegDay(nextDate.timeInMillis), getBegYear(nextDate.timeInMillis))
                        countPrevDays = diffDateInDays(getEndYear(prevDate.timeInMillis), getBegDay(prevDate.timeInMillis)) + 1

                        summaRestCreditAll * credit.procent / 100.0 / countDaysInYear * countDays +
                                summaRestCreditAll * credit.procent / 100.0 / countDaysInPrevYear * countPrevDays
                    } else {
                        countDays = diffDateInDays(nextDate, prevDate)
                        summaRestCreditAll * credit.procent / 100.0 / countDaysInYear * countDays
                    }

                } else {
                    summaRestCreditAll * credit.procent / 12.0 / 100.0
                }

                summaProcent = round2Long(tempSummaProcent, 0)

                if (isFirstPay && onlyProcentOnFirstPay) {
                    summaPay = summaProcent
                    isFirstPay = false
                }

                // Проверка на "отрицательный кредит", когда процент больше суммы платежа
                if(summaPay < summaProcent) {
                    Log.d("DMS_CREDIT", "ERROR: summa pay ($summaPay) < summa Procent ($summaProcent)")
                    return listData
                }

                summaCredit = max(summaPay - summaProcent, 0)

                if (summaRestCreditAll - summaCredit <= D2L(1000.0 )) {
                    summaCredit = summaRestCreditAll
                }

                summaPay = summaCredit + summaProcent

                summaCreditAll+=summaCredit

                pay = PAYMENT(credit.id)
                pay.done = 0
                pay.date = nextDate.timeInMillis
                pay.rest = L2D(summaRestCreditAll)
                pay.summa = L2D(summaPay)
                pay.summa_credit = L2D(summaCredit)
                pay.summa_procent = L2D(summaProcent)
                pay._id = -1

                listData.add(pay)

                summaRestCreditAll -= summaCredit

                Log.d("DMS_CREDIT", "summa_credit_rest=${summaRestCreditAll} , proc_sum=${summaProcent} , summa_credit=$summaCredit summa_credit_all =$summaCreditAll")

                prevDate.timeInMillis = nextDate.timeInMillis
                nextDate.add(Calendar.MONTH, 1)

            }

        return listData

    }

    // Заполняет график из БД
    // ФАКТ - табл. PAYMENT, ПЛАН - табл. GRAPHIC (все даты после последнего платежа)
    // withPlan - если ИСТИНА, то получаем полный график из базы ФАКТ + ПЛАН
    // иначе, только ФАКТ
    private fun fillGraphic(context: Context, credit: Credit, withPlan: Boolean) : MutableList<PAYMENT> {
        // Создаем адаптер списка
        // упаковываем данные в понятную для адаптера структуру

        val listData: MutableList<PAYMENT> = mutableListOf()

        val credit_id = credit.id

        val db = DB(context)

        db.open()

        // очищаем список

        listData.clear()

        val c: Cursor? = db.GetGraphic(credit_id, withPlan) // ФАКТ [+ ПЛАН (если withPlan=true)]

        if (c != null) {

            var pay: PAYMENT

            var summa_credit_all = credit.summa

            if (c.moveToFirst()) {
                do {

                    val summaPay = c.getDouble(c.getColumnIndex(DB.ATTR_SUMMA))
                    val summaCredit = c.getDouble(c.getColumnIndex(DB.ATTR_SUMMA_CREDIT))
                    val summaProcent = c.getDouble(c.getColumnIndex(DB.ATTR_SUMMA_PROCENT))

                    val isDone = c.getInt(c.getColumnIndex(DB.ATTR_DONE))

                    pay = PAYMENT(credit_id)
                    pay.done = isDone
                    pay.date = c.getLong(c.getColumnIndex(DB.ATTR_DATE))
                    pay.rest = summa_credit_all
                    pay.summa = summaPay
                    pay.summa_credit = summaCredit
                    pay.summa_procent = summaProcent
                    pay._id = c.getInt(c.getColumnIndex(DB.CL_ID))

                    listData.add(pay)

                    summa_credit_all -= summaCredit

                } while (c.moveToNext())
            }
            c.close()
        }

        db.close()

        return listData
    }

}

