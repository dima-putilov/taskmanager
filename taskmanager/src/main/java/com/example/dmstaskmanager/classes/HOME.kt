package com.example.dmstaskmanager.classes

import java.io.Serializable



/**
 * Created by dima on 09.03.2018.
 */

enum class HomeType(val type: Int, val title: String){
    None(0, "<Пусто>"),
    Flat(1, "Квартира"),
    Parking(2, "Паркинг"),
    Building(3, "Нежилое помещение"),
    Automobile(4, "Автомобиль");


    companion object {
        fun getById(id: Int): HomeType {
            for (e in values()) {
                if (e.type.equals(id)) return e
            }
            return None
        }

        fun getHomeTypeList(): List<HomeType> {
            return listOf(None, Flat, Parking, Building, Automobile)
        }
    }

}

class HOME(
    var _id: Int = 0,
    var type: HomeType = HomeType.None,
    var name: String = "",
    var adres: String = "",
    var param: String = "",
    var isCounter: Boolean = false,
    var isPay: Boolean = false,
    var isArenda: Boolean = false,
    var day_arenda: Int = 0,
    var summa_arenda: Double = 0.0,
    var day_beg: Int = 0,
    var day_end: Int = 0,
    var credit_id: Int = 0,
    var lic: String? = null,
    var summa: Double = 0.0,
    var finish: Boolean = false,
    var foto: ByteArray? = null,
    var summaFinRes: Double? = null

) : Serializable