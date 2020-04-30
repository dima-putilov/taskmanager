package com.example.dmstaskmanager.flat

import android.content.Context
import android.widget.ArrayAdapter
import com.example.dmstaskmanager.database.DB
import java.util.*

// Биндер списка - переопределение отображения полей
class CreditListAdapter(val context: Context) {

    var listData: ArrayList<Int> = ArrayList<Int>()
    var listDataName: ArrayList<String> = ArrayList<String>()

    fun getAdapter() : ArrayAdapter<String> {

        updateListCreditData()

        // адаптер
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listDataName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return adapter
    }

    fun updateListCreditData() {

        // очищаем список
        listData.clear()
        listDataName.clear()

        val db = DB(context)
        // открываем подключение к БД
        db?.open()

        listData?.add(-99999)
        listDataName?.add("<Не выбрано>")

        val cursor = db?.credit_GetAll()
        cursor?.let{ cursor ->
            if (cursor.moveToFirst()) {
                do {
                    listData.add(cursor.getInt(cursor.getColumnIndex(DB.CL_ID)))
                    listDataName.add(cursor.getString(cursor.getColumnIndex(DB.CL_CREDIT_NAME)))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }

    }


}