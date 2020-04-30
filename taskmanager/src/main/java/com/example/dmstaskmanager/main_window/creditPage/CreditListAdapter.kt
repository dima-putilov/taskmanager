package com.example.dmstaskmanager.main_window.creditPage

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.widget.SimpleCursorAdapter
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.utils.diffDateInDays
import com.example.dmstaskmanager.utils.formatD
import com.example.dmstaskmanager.utils.getBegDay
import com.example.dmstaskmanager.utils.isZero
import kotlinx.android.synthetic.main.item_credit.view.closeCreditStamp
import kotlinx.android.synthetic.main.item_credit.view.imgDate
import kotlinx.android.synthetic.main.item_credit.view.imgFinish
import kotlinx.android.synthetic.main.item_credit.view.imgType
import kotlinx.android.synthetic.main.item_credit.view.tvDate
import kotlinx.android.synthetic.main.item_credit.view.tvName
import kotlinx.android.synthetic.main.item_credit.view.tvSummaFinRes
import kotlinx.android.synthetic.main.item_credit.view.tvSummaPay
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

// Переопределеяем Адаптер списка - класс SimpleAdapter
class CreditListAdapter(_context: Context, private val layout: Int, _cursor: Cursor?, _from: Array<String>, _to: IntArray, _t: Int) : SimpleCursorAdapter(_context, layout, _cursor, _from, _to, _t) {

    //связывает данные с view на которые указывает курсор
    override fun bindView(view: View, _context: Context?, _cursor: Cursor) {

        val imgType = view.imgType
        val imgDate = view.imgDate
        val tvName = view.tvName
        val tvSummaPay = view.tvSummaPay
        val imgFinish = view.imgFinish
        val tvDate = view.tvDate
        val tvSummaFinRes = view.tvSummaFinRes
        val closeCreditStamp = view.closeCreditStamp

        val RUB = Html.fromHtml(" &#x20bd")

        val decFormat_2 = DecimalFormat("###,###.##")
        val decFormat = DecimalFormat("###,###")

        val name = _cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_NAME))

        Log.d("DMS", "credit name = $name")

        var date: Long = 0
        var date_pay: Long = 0
        var date_last_pay: Long = 0
        val date_end: Long = 0
        var curdate = Date().time
        try {

            Log.d("DMS", "CONVERT DATE: " + _cursor.getColumnIndex(DB.CL_RESULT_CREDIT_DATE))

            date = _cursor.getLong(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_DATE))

            date_pay = _cursor.getLong(_cursor.getColumnIndex(DB.CL_RESULT_GRAPHIC_DATE))

            date_last_pay = _cursor.getLong(_cursor.getColumnIndex(DB.CL_RESULT_PAYMENT_DATE_LAST_PAY))

            if (date_last_pay == 0L) date_last_pay = Date().time

        } catch (e: Exception) {
            Log.d("DMS", "ERROR CONVERT DATE FROM: $date")
        }

        val summa = _cursor.getDouble(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_SUMMA))
        val summa_payment = _cursor.getDouble(_cursor.getColumnIndex(DB.CL_RESULT_PAYMENT_SUMMA_CREDIT))
        val param_period = _cursor.getInt(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_PERIOD))
        val param_procent = _cursor.getDouble(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_PROCENT))
        val summa_rest = _cursor.getDouble(_cursor.getColumnIndex(DB.CL_RESULT_RESULT_REST))

        //            Log.d("DMS_CREDIT", "summa_rest = " + summa_rest +
        //                    " "+ _cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_RESULT_REST)) +
        //                    " = " + Double.parseDouble(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_RESULT_REST))) +
        //                    " === " + _cursor.getDouble(_cursor.getColumnIndex(DB.CL_RESULT_RESULT_REST)) );

        val credit_summa_to_pay = _cursor.getDouble(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_SUMMA_PAY))
        var summa_to_pay: Double = _cursor.getDouble(_cursor.getColumnIndex(DB.CL_RESULT_GRAPHIC_SUMMA))

        val summa_fin_res = _cursor.getDouble(_cursor.getColumnIndex(DB.CL_RESULT_RESULT_FIN_RES))

        // Дата кредита
        val dateFormatDec = SimpleDateFormat("dd.MM.yyyy 'г.'")
        val str_date_end = dateFormatDec.format(date_last_pay)

        val days = ((date_last_pay - date) / (24 * 60 * 60 * 1000)).toInt() // миллисекунды / (24ч * 60мин * 60сек * 1000мс)
        val years = days / 365
        val month = days % 365 / 30
        var str_period = ""
        if (years > 0) str_period += years.toString() + "г. "
        if (month > 0) str_period += month.toString() + "м. "
        if (str_period.isEmpty()) str_period += "<1м."

        // Тип кредита (1-ипотека, 2-авто-кредит, 3-потребительский, 4-страхование и 5-прочие)
        val type_credit = Integer.parseInt(_cursor.getString(_cursor.getColumnIndex(DB.CL_RESULT_CREDIT_TYPE)))
        Log.d("DMS", "credit type = $type_credit")
        when (type_credit + 1) {
            1 -> imgType.setImageResource(R.drawable.type_credit_flat)
            2 -> imgType.setImageResource(R.drawable.type_credit_auto)
            3 -> imgType.setImageResource(R.drawable.type_credit_things)
            4 -> imgType.setImageResource(R.drawable.type_credit_ensure)
            6 // было = 5
            -> imgType.setImageResource(R.drawable.delete)
            5 -> imgType.setImageResource(R.drawable.type_credit_parking)

            else -> imgType.visibility = View.INVISIBLE
        }

        date_pay = getBegDay(date_pay)
        curdate = getBegDay(curdate)

        // Кол-во дней до очередного платежа
        val dayRest = diffDateInDays(date_pay, curdate)
        //(int)((date_pay - curdate) / (24 * 60 * 60 * 1000)); // миллисекунды / (24ч * 60мин * 60сек * 1000мс);

        Log.d("DMS", "dayRest = $dayRest")

        imgDate.visibility = View.VISIBLE

        if (dayRest >= 0) {
            if (dayRest <= 14) {
                imgDate.visibility = View.VISIBLE
                when (dayRest) {
                    0 -> imgDate.setImageResource(R.drawable.day0)
                    1 -> imgDate.setImageResource(R.drawable.day1)
                    2 -> imgDate.setImageResource(R.drawable.day2)
                    3 -> imgDate.setImageResource(R.drawable.day3)
                    4 -> imgDate.setImageResource(R.drawable.day4)
                    5 -> imgDate.setImageResource(R.drawable.day5)
                    6 -> imgDate.setImageResource(R.drawable.day6)
                    7 -> imgDate.setImageResource(R.drawable.day7)
                    8 -> imgDate.setImageResource(R.drawable.day8)
                    9 -> imgDate.setImageResource(R.drawable.day9)
                    else -> imgDate.setImageResource(R.drawable.day9plus)
                }
            } else {
                imgDate.visibility = View.GONE
            }
        } else {
            imgDate.setImageResource(R.drawable.day0)
        }


        // Дата последнего платежа
        val str_date_cr = dateFormatDec.format(date)

        if (summa_rest > 1) { // Действующий кредит
            // Заголовок
            tvName.text = name

            // Сумма к оплате
            if (summa_to_pay.compareTo(0.001) <= 0 && summa_rest > 0) summa_to_pay = credit_summa_to_pay
            tvSummaPay.text = "к оплате " + decFormat_2.format(summa_to_pay) + RUB

            // Очередная дата оплаты
            val dateFormat = SimpleDateFormat("E, dd MMMM yyyy 'г.'")
            val str_date = dateFormat.format(if (date_pay == 0L) date else date_pay)

            tvDate.text = str_date

            if (dayRest <= 14 && dayRest > 7)
                tvDate.setTextColor(Color.parseColor("#FF098900"))
            else if (dayRest <= 7 && dayRest > 2)
                tvDate.setTextColor(Color.parseColor("#FF0227CC"))
            else if (dayRest <= 2)
                tvDate.setTextColor(Color.parseColor("#d63301"))
            else
                tvDate.setTextColor(Color.parseColor("#FF0D0C0C"))

            imgFinish.visibility = View.GONE
            // imgDate.setVisibility(View.VISIBLE);

            closeCreditStamp.visibility = View.GONE

            tvName.paintFlags = Paint.FAKE_BOLD_TEXT_FLAG

        } else { //Считаем что кредит закрыт

            // Типа зачеркнутый заголовок
            val myText = "<s><strike>$name</strike></s>"
            Log.d("DNS", "1=$myText")
            tvName.setText(Html.fromHtml(myText), TextView.BufferType.SPANNABLE)

            tvName.paintFlags = (Paint.FAKE_BOLD_TEXT_FLAG + Paint.STRIKE_THRU_TEXT_FLAG)

            tvSummaPay.text = "Закрыт"

            tvDate.text = ""

            imgFinish.visibility = View.VISIBLE
            imgDate.visibility = View.GONE

            closeCreditStamp.visibility = View.VISIBLE

        }

        // Остаток суммы
        val tvSummaRest = view.findViewById<View>(R.id.tvSummaRest) as TextView
        tvSummaRest.text = decFormat.format(summa_rest) + RUB

        // Финансовый результат
        tvSummaFinRes.text = decFormat.format(summa_fin_res) + RUB

        if (summa_fin_res < 0)
            tvSummaFinRes.setTextColor(Color.RED)
        else
            tvSummaFinRes.setTextColor(Color.BLUE)

        /// Прогресс бар

        val ProgBar = view.findViewById<View>(R.id.pbProgress) as ProgressBar

        ProgBar.max = Math.round(summa).toInt()
        ProgBar.progress = Math.round(summa_payment).toInt()

        val tvInProgressSumma = view.findViewById<View>(R.id.tvInProgressSumma) as TextView
        tvInProgressSumma.text = decFormat.format(summa) + RUB


        // Процент выполнения

        val proc : Double =
            if (isZero(summa))
                0.0
            else
                summa_payment / summa * 100

        val summa_pay = decFormat.format(summa_payment) + RUB + String.format(" (%.0f%%)", proc)

        val tvInProgressSumma_credit = view.findViewById<View>(
            R.id.tvInProgressSumma_credit) as TextView
        tvInProgressSumma_credit.text = summa_pay

        val tvParam = view.findViewById<View>(R.id.tvParam) as TextView
        tvParam.text = "Сумма " + decFormat.format(summa) + " под " + formatD(param_procent) + "% на " + param_period + " мес." + "\nДата " + str_date_cr + " - " + str_date_end + " (" + str_period + ")"

    }

    //сoздаёт нввую view для хранения данных на которую указывает курсор
    override fun newView(_context: Context?, _cursor: Cursor?, parent: ViewGroup): View {
        val inflater = _context!!.getSystemService(
            Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(layout, parent, false)
    }

}


