package com.example.dmstaskmanager.utils

import android.app.Activity
import android.content.Context
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}

fun View.select() {
    this.isSelected = true
}

fun View.deselect() {
    this.isSelected = false
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun MenuItem.visible() {
    this.isVisible = true
}

fun MenuItem.invisible() {
    this.isVisible = false
}

fun Activity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}
