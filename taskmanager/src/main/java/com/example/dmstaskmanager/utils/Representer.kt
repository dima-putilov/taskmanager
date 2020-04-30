package com.example.dmstaskmanager.utils

import kotlin.math.abs

class Representer {
    companion object {
        fun financeResult(sum: Double) : String {
            return if (sum < 0) {
                "- "
            } else {
                "+ "
            } + formatD0(abs(sum))
        }
    }
}