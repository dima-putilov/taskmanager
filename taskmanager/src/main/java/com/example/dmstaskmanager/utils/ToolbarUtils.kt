package com.example.dmstaskmanager.utils

import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.WindowManager
import com.example.dmstaskmanager.R

class ToolbarUtils {
    companion object {

        fun initNavigationBar(activity: AppCompatActivity, isHomeBackButton: Boolean) {
            activity.supportActionBar?.also {
                it.setDisplayHomeAsUpEnabled(true)
                if (isHomeBackButton) {
                    // it.setHomeAsUpIndicator(R.drawable.ic_back)
                } else {
                    it.setHomeAsUpIndicator(R.drawable.ic_menu)
                }
            }
        }

        fun initToolbar(activity: AppCompatActivity, homeBottonEnable: Boolean, titleResId: Int, backgroundResId: Int, colorResID: Int){

            val toolbar = activity.findViewById<Toolbar>(R.id.toolbar_actionbar_item)
            activity.setSupportActionBar(toolbar)
            // Кнопка Назад
            activity.supportActionBar!!.setDisplayHomeAsUpEnabled(homeBottonEnable)
            // Заголовок
            activity.supportActionBar!!.setTitle(titleResId)

            activity.supportActionBar!!.setBackgroundDrawable(ContextCompat.getDrawable(activity, backgroundResId))

            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(activity, colorResID)

        }

        fun setNewFlag(activity: AppCompatActivity){
            activity.supportActionBar!!.subtitle = activity.getString(R.string.toolbar_subtitle_new)
        }

        fun initMenu(activity: AppCompatActivity, menuId: Int){
            // Inflate a menu to be displayed in the toolbar
            val toolbar = activity.findViewById<Toolbar>(R.id.toolbar_actionbar_item)
            toolbar.inflateMenu(menuId)

        }

        fun setupActionBar(activity: AppCompatActivity, position: Int? = null) {
            val toolbar = activity.findViewById<Toolbar>(R.id.toolbar_actionbar)
            if (position != null) {
                setSettingMenu(activity, toolbar.menu, position)
            }
        }

        private fun setSettingMenu(activity: AppCompatActivity, menu: Menu, position: Int) {

            val addMenuItem = menu.findItem(R.id.action_add)

            val microMenuItem = menu.findItem(R.id.action_micro)
            val removeMenuItem = menu.findItem(R.id.action_remove)
            val searchMenuItem = menu.findItem(R.id.action_search)
            val editMenuItem = menu.findItem(R.id.action_edit)
            val sortAbMenuItem = menu.findItem(R.id.action_sort)

            val action_options_submenu =  menu.findItem(R.id.action_options_submenu)
            val action_option_add = menu.findItem(R.id.action_option_add)
            val action_option_delete_all =  menu.findItem(R.id.action_option_delete_all)

            val hideClosedMenuItem = menu.findItem(R.id.action_option_hide_closed)
            val showClosedMenuItem = menu.findItem(R.id.action_option_show_closed)

            if (position == 0) {

                activity.supportActionBar!!.setTitle(R.string.title_activity_task)
                activity.supportActionBar!!.setBackgroundDrawable(activity.resources.getDrawable(R.color.TaskItemToolbar))

                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = activity.resources.getColor(R.color.TaskItemWindowsBar)

                addMenuItem?.visible()
                microMenuItem?.invisible()
                searchMenuItem?.invisible()
                sortAbMenuItem?.invisible()
                removeMenuItem?.invisible()
                editMenuItem?.invisible()

                action_options_submenu?.invisible()

            }

            if (position == 1) {
                activity.supportActionBar!!.setTitle(R.string.title_activity_credit)
                activity.supportActionBar!!.setBackgroundDrawable(activity.resources.getDrawable(R.color.CreditItemToolbar))

                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = activity.resources.getColor(R.color.CreditItemWindowsBar)

                addMenuItem?.visible()

                microMenuItem?.invisible()
                searchMenuItem?.invisible()
                sortAbMenuItem?.invisible()
                removeMenuItem?.invisible()
                editMenuItem?.invisible()

                action_options_submenu?.visible()

                action_option_delete_all?.invisible()
                action_option_add?.visible()

                if (Settings.getShowCloseCreditSettings(activity)) {
                    hideClosedMenuItem?.visible()
                    showClosedMenuItem?.invisible()
                } else {
                    hideClosedMenuItem?.invisible()
                    showClosedMenuItem?.visible()
                }

            }

            if (position == 2) {

                activity.supportActionBar!!.setTitle(R.string.title_activity_flat)
                activity.supportActionBar!!.setBackgroundDrawable(activity.resources.getDrawable(R.color.FlatItemToolbar))

                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = activity.resources.getColor(R.color.FlatItemWindowsBar)

                action_option_add?.visible()

                microMenuItem!!.invisible()
                searchMenuItem!!.invisible()
                sortAbMenuItem!!.invisible()
                removeMenuItem!!.invisible()
                editMenuItem!!.invisible()

                if (Settings.getShowCloseFlatSettings(activity)) {
                    hideClosedMenuItem?.visible()
                    showClosedMenuItem?.invisible()
                } else {
                    hideClosedMenuItem?.invisible()
                    showClosedMenuItem?.visible()
                }

            }

        }

    }
}