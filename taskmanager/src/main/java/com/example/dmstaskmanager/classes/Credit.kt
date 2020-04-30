package com.example.dmstaskmanager.classes

import java.io.Serializable

// Кредиты

enum class CreditType(val type: Int, val title: String){
    None(0, "<Не задано>"),
    Flat(1, "Квартира"),
    Auto(2, "Автомобиль"),
    Stuff(3, "Потребительский"),
    Ensure(4, "Страховка"),
    Parking(5, "Паркинг");

    companion object {
        fun getById(id: Int): CreditType {
            for (e in values()) {
                if (e.type.equals(id)) return e
            }
            return None
        }
    }

}

data class Credit (
    var id: Int = 0,
    var type: CreditType = CreditType.None,
    var name: String = "",
    var date: Long = 0,
    var summa: Double = 0.toDouble(),
    var summa_pay: Double = 0.toDouble(),
    var procent: Double = 0.toDouble(),
    var period: Int = 0,
    var finish: Boolean = false

//    var summaRest: Double = 0.toDouble(),
//    var summaPayFact: Double = 0.toDouble(),
//    var summaCreditFact: Double = 0.toDouble(),
//    var summaProcentFact: Double = 0.toDouble()

) : Serializable {
    fun clone() = Credit(
        id = this.id,
        type = this.type,
        name = this.name,
        date = this.date,
        summa = this.summa,
        summa_pay = this.summa_pay,
        procent = this.procent,
        period = this.period,
        finish = this.finish

//        summaRest = this.summaRest,
//        summaPayFact = this.summaPayFact,
//        summaCreditFact = this.summaCreditFact,
//        summaProcentFact = this.summaProcentFact
    )

    operator fun plusAssign(credit: Credit?) {
        credit?.also { credit ->
            this.summa += credit.summa
            this.summa_pay += credit.summa_pay
//            this.summaRest += credit.summaRest
//            this.summaCreditFact += credit.summaCreditFact
//            this.summaProcentFact += credit.summaProcentFact
        }
    }
}
