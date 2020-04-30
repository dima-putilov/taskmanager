package com.example.dmstaskmanager.classes

import android.util.Log
import com.google.gson.Gson

class PAYMENT(@JvmField var _id : Int = -1,
              @JvmField var credit_id: Int = -1,
              @JvmField var date: Long = 0,
              @JvmField var summa: Double = 0.0,
              @JvmField var summa_credit: Double = 0.0,
              @JvmField var summa_procent: Double = 0.0,
              @JvmField var summa_addon: Double = 0.0,
              @JvmField var summa_plus: Double = 0.0,
              @JvmField var summa_minus: Double = 0.0,
              @JvmField var done: Int = 0,
              @JvmField var comment: String = ""){

    var rest : Double = 0.0

    constructor(credit_id: Int, date: Long, summa: Double, summa_credit: Double, summa_procent: Double, summa_addon: Double, summa_plus: Double, summa_minus: Double) : this(){
            this.credit_id      = credit_id
            this.date           = date
            this.summa          = summa
            this.summa_credit   = summa_credit
            this.summa_procent  = summa_procent
            this.summa_addon    = summa_addon
            this.summa_plus     = summa_plus

        }

    constructor(credit_id: Int) : this(){
        this.credit_id      = credit_id
    }

    fun clone() : PAYMENT {
        return PAYMENT(
            _id = this._id,
            credit_id = this.credit_id,
            date = this.date,
            summa = this.summa,
            summa_credit = this.summa_credit,
            summa_procent = this.summa_procent,
            summa_addon = this.summa_addon,
            summa_plus = this.summa_plus,
            summa_minus = this.summa_minus,
            comment = this.comment,
            done = this.done
        )
    }

    operator fun plus(p: PAYMENT) : PAYMENT {
        val result = PAYMENT()
        result.date = this.date
        result.rest = Math.min(this.rest , p.rest)

        result.done = Math.max(this.done, p.done)

        result.summa = this.summa + p.summa
        result.summa_credit = this.summa_credit + p.summa_credit
        result.summa_procent = this.summa_procent + p.summa_procent
        result.summa_addon = this.summa_addon + p.summa_addon
        result.summa_plus = this.summa_plus + p.summa_plus
        result.summa_minus = this.summa_minus + p.summa_minus

        return result
    }


//    operator fun plusAssign(p: PAYMENT?) {
//        p?.also { p ->
//            this.summa += p.summa
//            this.summa_credit += p.summa_credit
//            this.summa_procent += p.summa_procent
//            this.summa_addon += p.summa_addon
//            this.summa_plus += p.summa_plus
//            this.summa_minus += p.summa_minus
//        }
//    }

}

fun List<PAYMENT>.groupByDate() : List<PAYMENT> {

//    Log.d("DIAGRAM", "1 - this = ${Gson().toJson(this)}")

    return this.groupBy { hashMapOf(it.date to it.done) }
        .let {

//            Log.d("DIAGRAM", "2 - map = ${Gson().toJson(it)}")

            val reducedList = mutableListOf<PAYMENT>()
            for((key, value) in it) {
                reducedList.add(value.reduce { acc, payment ->
                    acc + payment
                })
            }

//            Log.d("DIAGRAM", "3 - reducedList = ${Gson().toJson(reducedList)}")

            reducedList
        }

}