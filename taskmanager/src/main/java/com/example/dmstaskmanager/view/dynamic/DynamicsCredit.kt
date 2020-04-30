package com.example.dmstaskmanager.view.dynamic

import com.example.dmstaskmanager.classes.CreditTotals

class DynamicsCredit(var creditTotals: CreditTotals, var targetCredit: CreditTotals) {

    companion object {
        private const val TOLERANCE = .0f
        private const val STEP = 30
    }

    var alpha = 0
    var targetAlpha = 255
    var now : Long = 0L

    fun update(now: Long) {
        alpha =  Math.min( alpha + STEP, targetAlpha)
    }

    fun isAtRest(): Boolean {
        val isAtTarget: Boolean = Math.abs(targetAlpha - alpha ) < TOLERANCE
        return isAtTarget
    }

}

