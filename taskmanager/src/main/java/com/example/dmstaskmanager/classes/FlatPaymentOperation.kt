package com.example.dmstaskmanager.classes

/**
 * Created by dima on 09.03.2018.
 */

// Операции с Квартирами

enum class FlatPaymentType(val type: Int){
    None(0),
    Profit(1),
    Outlay(-1);

    companion object {
        fun getById(id: Int): FlatPaymentType {
            for (e in values()) {
                if (e.type == id) return e
            }
            return None
        }
    }
}


enum class FlatPaymentOperationType(val type: Int, val title: String, val titleShort: String, val flatPaymentType: FlatPaymentType) {
    NONE(0, "<Пусто>", "<Пусто>", FlatPaymentType.None),
    PROFIT(1, "Доход (Аренда)", "Аренда", FlatPaymentType.Profit), // доход , например, от аренды квартиры
    DAMAGE(2, "Расход (Ремонт)", "Расходы", FlatPaymentType.Outlay), // расходы на ремонт и обслуживание
    PROCENT(3, "Процент (перв. взнос)", "Перв. %",  FlatPaymentType.Outlay), // расходы - банковский процент (не используется - проценты берутся из кредитов)
    RENT(4, "Квартплата", "Квартплата", FlatPaymentType.Outlay), // расходы - квартплата
    PAY(5, "Платеж (перв. взнос)", "Перв. взнос", FlatPaymentType.Profit), // платеж за квартиру (в основном первоначальный)
    INSURE(6, "Страховка","Страховка",  FlatPaymentType.Outlay),  // страховка
    TO(7, "Плановое ТО", "Плановое ТО", FlatPaymentType.Outlay),      // плановое тех. обслуживание
    GAS(8, "Бензин", "Бензин", FlatPaymentType.Outlay),     // бензин
    SELL(9, "Продажа", "Продажа", FlatPaymentType.Profit),    // продажа
    OTHER_PROFIT(10, "Прочие доходы", "Прочие доходы", FlatPaymentType.Profit),    // прочие доходы
    OTHER_OUTLAY(11, "Прочие расходы", "Прочие расходы", FlatPaymentType.Outlay);    // прочие расходы

    companion object {
        fun getById(id: Int): FlatPaymentOperationType {
            for (e in values()) {
                if (e.type == id) return e
            }
            return NONE
        }

        fun getFlatOperationsByType(homeType: HomeType): List<FlatPaymentOperationType> {

            return when (homeType) {
                HomeType.Flat, HomeType.Parking -> {
                    getFlatOperationsOfFlat()
                }
                HomeType.Automobile -> {
                    getFlatOperationsOfAutomobile()
                }
                else -> {
                    getFlatOperationsOfAll()
                }
            }
        }

        private fun getFlatOperationsOfFlat(): List<FlatPaymentOperationType> {
            return  listOf(NONE, PROFIT, DAMAGE, PROCENT, RENT, PAY, SELL, OTHER_PROFIT, OTHER_OUTLAY)
        }

        private fun getFlatOperationsOfAutomobile(): List<FlatPaymentOperationType> {
            return  listOf(NONE, PROFIT, DAMAGE, PAY, INSURE, TO, GAS, SELL, OTHER_PROFIT, OTHER_OUTLAY)
        }

        private fun getFlatOperationsOfAll(): List<FlatPaymentOperationType> {
            return  listOf(NONE, PROFIT, DAMAGE, PROCENT, RENT, PAY, INSURE, TO, GAS, SELL, OTHER_PROFIT, OTHER_OUTLAY)
        }
    }


    
}



