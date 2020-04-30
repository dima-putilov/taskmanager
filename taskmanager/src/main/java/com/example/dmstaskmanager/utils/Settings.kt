package com.example.dmstaskmanager.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.preference.PreferenceManager

class Settings {

    companion object {
        const val PREFERENCES_SHOW_CLOSE_CREDIT = "PREFERENCES_SHOW_CLOSE_CREDIT"
        const val PREFERENCES_SHOW_CLOSE_FLAT = "PREFERENCES_SHOW_CLOSE_FLAT"

        fun setShowCloseCreditSettings(context: Context, value: Boolean) {
            val settingPref = PreferenceManager.getDefaultSharedPreferences(context).edit()
            settingPref.putBoolean(PREFERENCES_SHOW_CLOSE_CREDIT, value)
            settingPref.apply()
        }

        fun getShowCloseCreditSettings(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREFERENCES_SHOW_CLOSE_CREDIT, true)
        }


        fun setShowCloseFlatSettings(context: Context, value: Boolean) {
            val settingPref = PreferenceManager.getDefaultSharedPreferences(context).edit()
            settingPref.putBoolean(PREFERENCES_SHOW_CLOSE_FLAT, value)
            settingPref.apply()
        }

        fun getShowCloseFlatSettings(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREFERENCES_SHOW_CLOSE_FLAT, true)
        }

    }

}