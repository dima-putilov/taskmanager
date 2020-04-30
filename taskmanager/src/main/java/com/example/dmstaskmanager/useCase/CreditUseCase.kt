package com.example.dmstaskmanager.useCase

import android.content.Context
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.classes.PAYMENT

class CreditUseCase {

    fun getNextCreditPayment(context: Context, creditId: Long) : PAYMENT {

            val db = DB(context)

            db.open()

            val payment = db.credit_GetNextPayment(creditId)

            db.close()

            return payment
    }
}

