package com.example.dmstaskmanager.useCase

import android.content.Context
import android.database.Cursor
import android.text.Html
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.FlatItem
import com.example.dmstaskmanager.classes.FlatPaymentOperationType
import com.example.dmstaskmanager.classes.FlatPaymentState
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.classes.HomeType
import com.example.dmstaskmanager.utils.Settings
import com.example.dmstaskmanager.utils.formatDate
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class GetFlatsUseCase {

    var getFlatListSubject: PublishSubject<List<FlatItem>> = PublishSubject.create<List<FlatItem>>()

    private var threadJob: Job? = null

    fun cancel() {
        if (threadJob != null && threadJob!!.isActive) {
            threadJob?.cancel()
        }
    }

    fun getFlatList(context: Context) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val listData = createFlatList(context)

            getFlatListSubject.onNext(listData)

        }

    }

    private fun createFlatList(context: Context) : MutableList<FlatItem> {

        val listData: MutableList<FlatItem> = mutableListOf()

        val RUB = Html.fromHtml(" &#x20bd")

        val decFormat_2 = DecimalFormat("###,###.##")
        val decFormat = DecimalFormat("###,###")

        var summa: Double
        var summa_payment: Double
        var summa_payment_sec: Double

        // Дата последнего платежа
        var last_date: Long
        var last_date_str = ""
        var lastSumma: Double = 0.0

        val million = 1000000.0
        
        val db = DB(context)

        db.open()

        // очищаем список

        listData.clear()

        val showClosed = Settings.getShowCloseFlatSettings(context)
        val c: Cursor? = db.getFlats(showClosed)

        if (c != null) {

            if (c.moveToFirst()) {
                do {

                    val type = c.getInt(c.getColumnIndex(DB.CL_FLAT_TYPE))
                    val taskType = HomeType.getById(type)
                    
                    val foto = c.getBlob(c.getColumnIndex(DB.CL_FLAT_FOTO))

                    val summaFinRes = (c.getInt(c.getColumnIndex("summa_arenda")) -
                        c.getInt(c.getColumnIndex("summa_exp")) -
                        c.getInt(c.getColumnIndex("summa_rent")) -
                        c.getInt(c.getColumnIndex("summa_proc"))).toDouble()
                    
                    val flat = HOME(
                        _id = c.getInt(c.getColumnIndex(DB.CL_ID)),
                        name = c.getString(c.getColumnIndex(DB.CL_FLAT_NAME)),
                        adres = c.getString(c.getColumnIndex(DB.CL_FLAT_ADRES)),
                        param = c.getString(c.getColumnIndex(DB.CL_FLAT_PARAM)),
                        type = taskType,
                        credit_id = c.getInt(c.getColumnIndex(DB.CL_FLAT_CREDIT_ID)),
                        isPay = (c.getInt(c.getColumnIndex(DB.CL_FLAT_ISPAY)) == 1),
                        isArenda = (c.getInt(c.getColumnIndex(DB.CL_FLAT_ISARENDA)) == 1),
                        summa_arenda = c.getDouble(c.getColumnIndex(DB.CL_FLAT_SUMMA_ARENDA)),
                        isCounter = (c.getInt(c.getColumnIndex(DB.CL_FLAT_ISCOUNTER)) == 1),
                        finish = (c.getInt(c.getColumnIndex(DB.CL_FLAT_FINISH)) == 1),
                        foto = foto,
                        summaFinRes = summaFinRes
                    )

                    val stateList = mutableListOf<FlatPaymentState>()

                    // Аренда
                    summa_payment = c.getInt(c.getColumnIndex("summa_arenda_all")).toDouble()
                    last_date = c.getLong(c.getColumnIndex("last_date_summa_arenda"))

                    stateList.add(getFlatOperation(FlatPaymentOperationType.PROFIT, last_date, null, R.drawable.progress_bar_color_arenda,
                    summa_payment, null, million,
                    "Аренда, ${formatDate(last_date)}", null, decFormat.format(summa_payment) + RUB,
                    {summa_payment > 0}))


                    // Расходы
                    summa_payment = c.getInt(c.getColumnIndex("summa_exp")).toDouble()
                    last_date = c.getLong(c.getColumnIndex("last_date_summa_exp"))

                    stateList.add(getFlatOperation(FlatPaymentOperationType.DAMAGE, last_date, null, R.drawable.progress_bar_color_exp,
                        summa_payment, null, million,
                        "Расходы / ремонт, ${formatDate(last_date)}",  null, decFormat.format(summa_payment) + RUB,
                        {summa_payment > 0}))

                    // Квартплата
                    summa_payment = c.getInt(c.getColumnIndex("summa_rent")).toDouble()
                    last_date = c.getLong(c.getColumnIndex("last_date_summa_rent"))

                    stateList.add(getFlatOperation(FlatPaymentOperationType.RENT, last_date, null, R.drawable.progress_bar_color_rent,
                        summa_payment, null, million,
                        "Квартплата, ${formatDate(last_date)}", null, decFormat.format(summa_payment) + RUB,
                        {summa_payment > 0}))

                    // Проценты
                    summa_payment = c.getInt(c.getColumnIndex("summa_proc")).toDouble()
                    last_date = c.getLong(c.getColumnIndex("last_date_summa_proc"))

                    stateList.add(getFlatOperation(FlatPaymentOperationType.PROCENT, last_date, null, R.drawable.progress_bar_color_proc,
                        summa_payment, null, million,
                        "Проценты, ${formatDate(last_date)}", null, decFormat.format(summa_payment) + RUB,
                        {summa_payment > 0}))

                    // Покупка
                    summa_payment = c.getInt(c.getColumnIndex("summa_first_pay")).toDouble()
                    summa_payment_sec = c.getInt(c.getColumnIndex("summa_pay")).toDouble()
                    val summa_pay_all = summa_payment + summa_payment_sec

                    summa = c.getInt(c.getColumnIndex(DB.CL_FLAT_SUMMA)).toDouble()

                    last_date = c.getLong(c.getColumnIndex("last_date_summa_proc"))

                    val proc = if (summa == 0.0) 0 else summa_pay_all / summa * 100

                    val progressText = try {
                        decFormat.format(summa_pay_all) + RUB + String.format(" (%.0f%%)", proc)
                    } catch (er: Exception) {
                        ""
                    }

                    stateList.add(getFlatOperation(FlatPaymentOperationType.PAY, last_date, null, R.drawable.progress_bar_color_flat,
                        summa_payment, summa_pay_all, summa,
                        "Покупка, ${formatDate(last_date)}", progressText,decFormat.format(summa) + RUB,
                        {summa > 0}))

                    val newFlat = FlatItem(
                            flat = flat,
                            flatPaymentStateList = stateList
                    )

                    listData.add(newFlat)

                } while (c.moveToNext())
            }
            c.close()
        }

        db.close()

        return listData
    }
    
    private fun getFlatOperation(type: FlatPaymentOperationType, lastDate: Long, lastSumma: Double?,
        progressDrawable: Int?,
        summaProgress: Double, secondSummaProgress: Double?,  summaMax: Double,
        title: String, summaProgressText: String?, summaMaxText: String?,
        checkVisible : () -> Boolean) : FlatPaymentState {

        return FlatPaymentState(
            type = type,
            title = title,
            lastDate = lastDate,
            lastSumma = lastSumma,
            color = progressDrawable,
            summaProgressFirst = summaProgress,
            summaProgressSecond = secondSummaProgress,
            summaMaximum = summaMax,
            summaMaximumText = summaMaxText ?: "",
            summaProgressText = summaProgressText ?: "",
            isVisible = checkVisible()
        )
    }
}

