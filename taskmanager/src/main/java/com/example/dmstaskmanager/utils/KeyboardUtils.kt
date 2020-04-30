package com.example.dmstaskmanager.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * This provides methods to deal with the keyboard
 */
class KeyboardUtils {

    companion object {
        fun hideKeyboard(context: Context, view: View?){
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view?.windowToken, 0)

            view?.clearFocus()
        }

        fun showKeyboard(context: Context, view: View?){
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}
