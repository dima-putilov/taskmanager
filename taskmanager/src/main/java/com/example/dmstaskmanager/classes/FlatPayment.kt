package com.example.dmstaskmanager.classes
import java.io.Serializable

/**
 * Created by dima on 09.03.2018.
 */

// Операции с Квартирами
class FlatPayment(var _id: Int = 0,
              var flat_id: Int = 0,
              var operation: FlatPaymentOperationType = FlatPaymentOperationType.NONE,
              var paymentType: FlatPaymentType = FlatPaymentType.None, // 1- приход, -1 - расход
              var date: Long = 0,
              var summa: Double = 0.0,
              var comment: String = ""

) : Serializable