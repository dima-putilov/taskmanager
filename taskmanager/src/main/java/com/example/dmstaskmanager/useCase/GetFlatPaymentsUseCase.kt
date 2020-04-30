package com.example.dmstaskmanager.useCase

import android.content.Context
import com.example.dmstaskmanager.classes.FlatPayment
import com.example.dmstaskmanager.classes.FlatPaymentOperationType
import com.example.dmstaskmanager.classes.FlatPaymentType
import com.example.dmstaskmanager.database.DB
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class GetFlatPaymentsUseCase {

    var getFlatPaymentListSubject: PublishSubject<List<FlatPayment>> = PublishSubject.create<List<FlatPayment>>()

    private var threadJob: Job? = null

    fun cancel() {
        if (threadJob != null && threadJob!!.isActive) {
            threadJob?.cancel()
        }
    }

    fun init() {
        cancel()

        getFlatPaymentListSubject = PublishSubject.create<List<FlatPayment>>()
    }

    fun getFlatPaymentList(context: Context, flatId: Int) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val listData = createFlatList(context, flatId)

            getFlatPaymentListSubject.onNext(listData)
            getFlatPaymentListSubject.onComplete()

        }

    }

    private fun createFlatList(context: Context, flatId: Int) : MutableList<FlatPayment> {

        val listData: MutableList<FlatPayment> = mutableListOf()

        // очищаем список
        listData.clear()

        val db = DB(context)
        db.open()
        db.let{
            val cursor = it.getFlatPayments(flatId)
            cursor.let { cursor ->

                if (cursor.moveToFirst()) {
                    do {

                        val summa = cursor.getDouble(cursor.getColumnIndex(DB.CL_FLAT_ACC_SUMMA))

                        listData.add(
                            FlatPayment(
                                _id = cursor.getInt(cursor.getColumnIndex(DB.CL_ID)),
                                flat_id = cursor.getInt(cursor.getColumnIndex(DB.CL_FLAT_ACC_FLAT_ID)),
                                date = cursor.getLong(cursor.getColumnIndex(DB.CL_FLAT_ACC_DATE)),
                                summa = cursor.getDouble(cursor.getColumnIndex(DB.CL_FLAT_ACC_SUMMA)),
                                paymentType = FlatPaymentType.getById(cursor.getInt(cursor.getColumnIndex(DB.CL_FLAT_ACC_TYPE))),
                                operation = FlatPaymentOperationType.getById(cursor.getInt(cursor.getColumnIndex(DB.CL_FLAT_ACC_OPERATION))),
                                comment = cursor.getString(cursor.getColumnIndex(DB.CL_FLAT_ACC_COMMENT))
                            )
                        )

                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            db.close()
        }

        return listData

    }
}

