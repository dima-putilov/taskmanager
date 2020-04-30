package com.example.dmstaskmanager.classes

import java.io.Serializable

/**
 * Created by dima on 09.03.2018.
 */

enum class TaskType(val type: Int, val title: String){
    None(0, "<Пусто>"),
    Credit(1, "Платежи по кредиту"),
    Flat(2, "Квартплата"),
    FlatCounter(3, "Показания счетчиков"),
    Arenda(10, "Счета на аренду"),
    Other(100, "Прочие задачи");

    companion object {
        fun getById(id: Int): TaskType {
            for (e in values()) {
                if (e.type.equals(id)) return e
            }
            return None
        }
    }

}

data class Task (
    var id: Int = 0,
    var type: TaskType = TaskType.None,
    var name: String = "",
    var date: Long = 0,
    var summa: Double = 0.toDouble(),
    var finish: Boolean = false,

    var parentId: Int = 0,

    var creditType: CreditType = CreditType.None
) : Serializable
{
    var isFinishChecked: Boolean = false
    var updateRevision: Int = 0
}