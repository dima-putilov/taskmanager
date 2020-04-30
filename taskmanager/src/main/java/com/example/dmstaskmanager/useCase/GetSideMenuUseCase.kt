package com.example.dmstaskmanager.useCase

import android.content.Context
import android.database.Cursor
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.Credit
import com.example.dmstaskmanager.classes.CreditType
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.classes.HomeType
import com.example.dmstaskmanager.classes.SideMenuItem
import com.example.dmstaskmanager.classes.SideMenuItemType
import com.example.dmstaskmanager.classes.SideMenu
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GetSideMenuUseCase {

    var getMenuListSubject: PublishSubject<List<SideMenu>> = PublishSubject.create<List<SideMenu>>()

    private var threadJob: Job? = null

    fun cancel() {
        if (threadJob != null && threadJob!!.isActive) {
            threadJob?.cancel()
        }
    }

    fun getSideMenuList(context: Context) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val listData = createMenuItemList(context)

            getMenuListSubject.onNext(listData)

        }

    }

    private fun createMenuItemList(context: Context) : MutableList<SideMenu> {

        val listData: MutableList<SideMenu> = mutableListOf()

        val db = DB(context)
        db.open()

        listData.clear()

        val listSubData: MutableList<SideMenuItem> = mutableListOf()

        var id = 0

        var c: Cursor? = db.credit_GetAll()
        if (c != null) {

            if (c.moveToFirst()) {
                do {
                    val credit = Credit(
                        id = c.getInt(c.getColumnIndex(DB.CL_ID)),
                        name = c.getString(c.getColumnIndex(DB.CL_CREDIT_NAME)),
                        type = CreditType.getById(c.getInt(c.getColumnIndex(DB.CL_CREDIT_TYPE)) + 1),
                        summa = c.getDouble(c.getColumnIndex(DB.CL_CREDIT_SUMMA)),
                        summa_pay = c.getDouble(c.getColumnIndex(DB.CL_CREDIT_SUMMA_PAY)),
                        date = c.getLong(c.getColumnIndex(DB.CL_CREDIT_DATE)),
                        procent = c.getDouble(c.getColumnIndex(DB.CL_CREDIT_PROCENT)),
                        period = c.getInt(c.getColumnIndex(DB.CL_CREDIT_PERIOD)),
                        finish = (c.getInt(c.getColumnIndex(DB.CL_CREDIT_FINISH)) == 1)
                    )

                    listSubData.add(SideMenuItem(id++,  credit.name, null, SideMenuItemType.CreditItem, credit))

                } while (c.moveToNext())
            }
            c.close()
        }


        listData.add(SideMenu(id++, "Кредиты", SideMenuItemType.CreditGroup, null, listSubData.toList()))


        listSubData.clear()

        c = db.getFlats()
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

                    listSubData.add(SideMenuItem(id++, flat.name, null, SideMenuItemType.FlatItem, flat))

                } while (c.moveToNext())
            }
            c.close()
        }

        db.close()

        listData.add(SideMenu(id++, "Квартиры", SideMenuItemType.FlatGroup, null, listSubData.toList()))

        listData.add(SideMenu(id++, "Диаграммы", SideMenuItemType.Diagram, R.drawable.ic_side_menu_diagram))

        listData.add(SideMenu(id++, "Настройки", SideMenuItemType.Settings, R.drawable.ic_side_menu_settings))

        listData.add(SideMenu(id++, "Выход", SideMenuItemType.Exit, R.drawable.ic_side_menu_exit))

        return listData
    }

}

