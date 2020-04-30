package com.example.dmstaskmanager.utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*


/**
 * Created by dima on 08.11.2018.
 */

var months = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")

enum class Actions(val type: Int){
    Create(0),
    Update(1),
    Delete(2)
}

fun Str2Int(str: String): Int {
    return if (str.isNotEmpty())
        Integer.parseInt(str)
    else
        0
}


fun getCreditFromList(position: Int): Int {
    return 0
}
//  Статические методы

fun isZero(v: Double) : Boolean{
    return D2L(v) == D2L(0.0)
}

fun D2L(v: Double?): Long {
    return v?.let{
        BigDecimal(v * 100).setScale(0, BigDecimal.ROUND_HALF_UP).toLong()
    } ?: 0
}


fun L2D(v: Long?): Double {
    return v?.let{
        it.toDouble() / 100
    } ?: 0.0
}

fun getCurrentDateStr() : String {
    val curDate = System.currentTimeMillis()
    return formatDate(curDate)
}

fun formatDate(date: Long): String {
    val months = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
    val cal = Calendar.getInstance()
    cal.clear()
    cal.timeInMillis = date

    return months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR).toString() + " г."
}

fun formatDate(date: Long, format: String) : String {
    return android.text.format.DateFormat.format(format, date).toString()
}

fun diffDateInDays(endDate: Calendar, begDate: Calendar) : Int {
    return diffDateInDays(getBegDay(endDate.timeInMillis), getBegDay(begDate.timeInMillis))
}

fun diffDateInDays(endDate: Long, begDate: Long) : Int {
    return if (begDate > endDate){
        -1
    } else {
        ((endDate - begDate) / (24 * 60 * 60 * 1000)).toInt()
    }
}

fun addDays(dt: Long, days: Int): Long {
    val mDay = days * (24 * 60 * 60 * 1000).toLong()
    return dt + days
}

fun getBegDay(dt: Long): Long {
    val mDay = (24 * 60 * 60 * 1000).toLong()
    return (dt / mDay).toInt() * mDay
}

fun getEndDay(dt: Long): Long {
    val mDay = (24 * 60 * 60 * 1000).toLong()
    return (dt / mDay).toInt() * mDay + mDay - 1
}

fun getBegMonth(dt: Long): Long {

    // get today and clear time of day
    val cal = Calendar.getInstance()
    cal.clear()
    cal.timeInMillis = dt
    cal.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
    cal.clear(Calendar.MINUTE)
    cal.clear(Calendar.SECOND)
    cal.clear(Calendar.MILLISECOND)

    // get start of the month
    cal.set(Calendar.DAY_OF_MONTH, 1)

    return cal.timeInMillis
}

fun getEndMonth(dt: Long): Long {
    // get today and clear time of day
    val cal = Calendar.getInstance()
    cal.clear()
    cal.timeInMillis = dt
    cal.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
    cal.clear(Calendar.MINUTE)
    cal.clear(Calendar.SECOND)
    cal.clear(Calendar.MILLISECOND)

    // get start of the month
    cal.set(Calendar.DAY_OF_MONTH, 1)

    // get start of the next month
    cal.add(Calendar.MONTH, 1)

    return cal.timeInMillis - 1
}

fun getBegYear(dt: Calendar): Long {
    return getBegYear(dt.timeInMillis)
}

fun getBegYear(dt: Long): Long {

    // get today and clear time of day
    val cal = Calendar.getInstance()
    cal.clear()
    cal.timeInMillis = dt
    cal.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
    cal.clear(Calendar.MINUTE)
    cal.clear(Calendar.SECOND)
    cal.clear(Calendar.MILLISECOND)

    // get start of the year
    cal.set(Calendar.DAY_OF_YEAR, 1)

    return cal.timeInMillis
}

fun getEndYear(dt: Calendar): Long {
    return getEndYear(dt.timeInMillis)
}

fun getEndYear(dt: Long): Long {

    // get today and clear time of day
    val cal = Calendar.getInstance()
    cal.clear()
    cal.timeInMillis = dt
    cal.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
    cal.clear(Calendar.MINUTE)
    cal.clear(Calendar.SECOND)
    cal.clear(Calendar.MILLISECOND)

    // get start of the year
    cal.set(Calendar.DAY_OF_YEAR, 1)

    // get start of the next year
    cal.add(Calendar.YEAR, 1)

    return cal.timeInMillis - 1
}



fun round2Long(value: Double, digits: Int): Long {
    return BigDecimal(value).setScale(digits, BigDecimal.ROUND_HALF_UP).toLong()
}

fun round2Str(value: Double, digits: Int): String {
    return BigDecimal("" + value).setScale(digits, BigDecimal.ROUND_HALF_UP).toString()
}

fun round2Str(value: String, digits: Int): String {
    var value = value
    if (value.isEmpty()) value = "0"
    return BigDecimal(value).setScale(digits, BigDecimal.ROUND_HALF_UP).toString()
}

fun round2Dec(value: String, digits: Int): BigDecimal {
    var value = value
    if (value.isEmpty()) value = "0"
    return BigDecimal(value).setScale(digits, BigDecimal.ROUND_HALF_UP)
}

fun formatD(v: Double): String {
    return String.format("%.2f", v)
}

fun formatD2(v: Double): String {
    val decFormat_2 : DecimalFormat = DecimalFormat("###,###.##")
    return decFormat_2.format(v)
}

fun formatD0(v: Double): String {
    val decFormat_2 : DecimalFormat = DecimalFormat("###,###")
    return decFormat_2.format(round2Long(v, 0)).replace(",", " ")
}

fun formatL(v: Long): String {
    return String.format("%.2f", L2D(v))
}

fun formatL(v: Long, pr: Int): String {
    return String.format("%.%f", L2D(v), pr)
}

fun str2Double(str: String) : Double{
    var result = 0.0
    if (str.isNotEmpty()) {
        try {
            result = java.lang.Double.parseDouble(str.replace(",", "."))
        } catch (e: Throwable){
            result = 0.0
        }
    }

    return result
}