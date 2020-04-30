package com.example.dmstaskmanager.view.dynamic

import com.example.dmstaskmanager.classes.PAYMENT

class DynamicsPayment(springiness: Float, damping: Float) {

    /** Коэффициент упругости */
    private var springiness = 0f

    /** Коэффициент затухания */
    private var damping = 0f

    init {
        this.springiness = springiness
        this.damping = damping
    }

    companion object {
        private const val TOLERANCE = 0.01f
    }

    //    /** Конечная позиция точки*/
//    var targetPosition = 0f
    var targetPayment = PAYMENT()

//
//    /** Текущая позиция точки*/
//    var position = 0f

    var payment = PAYMENT()

    /** Текущая скорость точки */
    private var velocity = 0f

    /** Время последнего обновления */
    private var lastTime = 0L


    var STEP_REST = 100000
    var STEP = 50000

//    fun update(now: Long) {
//        val dt = Math.min(now - lastTime, 50)
//        velocity += (targetPosition - position) * springiness
//        velocity *= (1 - damping)
//        position += velocity * dt / 1000
//        lastTime = now
//    }

    fun update(now: Long) {
//       val dt = Math.min(now - lastTime, 50)
//        velocity += (targetPosition - position) * springiness
//        velocity *= (1 - damping)

        payment.summa = Math.min(payment.summa + STEP, targetPayment.summa)
        payment.rest = Math.max(payment.rest - STEP_REST, targetPayment.rest)

        lastTime = now
    }

    fun isAtRest(): Boolean {
        val standingStill: Boolean = Math.abs(velocity) < TOLERANCE
        //val isAtTarget: Boolean = targetPosition - position < TOLERANCE

        val isAtTarget: Boolean = Math.abs(targetPayment.rest - payment.rest) < TOLERANCE

        val isAtTargetSumma: Boolean = Math.abs(targetPayment.summa - payment.summa) < TOLERANCE

        return standingStill && isAtTarget && isAtTargetSumma
    }

    fun setState(position: PAYMENT, lastTime: Long) {
        this.payment = position
        this.lastTime = lastTime
    }
}

