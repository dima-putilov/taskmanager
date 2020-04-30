package com.example.dmstaskmanager.main_window

import com.example.dmstaskmanager.R

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.example.dmsflatmanager.main_window.flatPage.FlatPageFragment
import com.example.dmstaskmanager.classes.SideMenuItem
import com.example.dmstaskmanager.main_window.creditPage.CreditPageFragment
import com.example.dmstaskmanager.main_window.taskPage.TaskPageFragment
import com.example.dmstaskmanager.sideMenu.SideMenuFragment
import com.example.dmstaskmanager.utils.Settings
import com.example.dmstaskmanager.utils.ToolbarUtils
import com.example.dmstaskmanager.utils.invisible
import com.example.dmstaskmanager.utils.visible
import kotlinx.android.synthetic.main.activity_main.drawerLayout

import java.util.ArrayList

class main : AppCompatActivity() {

    lateinit var sideMenuFragment: SideMenuFragment

    lateinit var pager: ViewPager
    lateinit var pagerAdapter: PagerAdapter

    internal val DIALOG_DELETE_ALL = 1

    internal var myClickListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            // положительная кнопка
            Dialog.BUTTON_POSITIVE -> {
                val position = pager.currentItem
            }
        }//Fragment fragment = (Fragment) getFragmentManager().findFragmentById(R.id.PageCredit);
        //fragment.DeleteAll();
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tool = findViewById<Toolbar>(R.id.toolbar_actionbar)
        setSupportActionBar(tool)

        ToolbarUtils.initNavigationBar(this, false)

        setupSideMenu()

        /*
        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < 3; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Tab " + (i + 1))
                            .setTabListener(tabListener));
        }
*/


        val inflater = LayoutInflater.from(this)
        val pages = ArrayList<View>()

        var page = inflater.inflate(R.layout.activity_maintask, null)
        pages.add(page)

        page = inflater.inflate(R.layout.activity_credit, null)
        pages.add(page)

        page = inflater.inflate(R.layout.activity_payment, null)
        pages.add(page)

        pager = findViewById<View>(R.id.pager) as ViewPager
        pagerAdapter = MyFragmentPagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter

        pager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                Log.d(TAG, "onPageSelected, position = $position")

                ToolbarUtils.setupActionBar(this@main, pager.currentItem)

            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        pager.currentItem = 0

    }

    override fun onDestroy() {
        super.onDestroy()
        // закрываем подключение при выходе
    }

    override fun onCreateDialog(id: Int): Dialog {
        if (id == DIALOG_DELETE_ALL) {
            val adb = AlertDialog.Builder(this)
            // заголовок
            adb.setTitle(R.string.dialog_delete)
            // сообщение
            adb.setMessage(R.string.dialog_delete_all)
            // иконка
            adb.setIcon(android.R.drawable.ic_dialog_info)
            // кнопка положительного ответа
            adb.setPositiveButton(R.string.dialog_button_yes, myClickListener)
            // кнопка нейтрального ответа
            adb.setNeutralButton(R.string.dialog_button_cancel, myClickListener)
            // создаем диалог
            return adb.create()
        }
        return super.onCreateDialog(id)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d("DMS", "TOOLBAR CREATE #1")
        menuInflater.inflate(R.menu.menu_activity_main, menu)

        ToolbarUtils.setupActionBar(this, pager.currentItem)

        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupSideMenu(){
        sideMenuFragment = supportFragmentManager.findFragmentById(R.id.sideMenuFragment) as SideMenuFragment
        sideMenuFragment.syncWithDrawer(closeDrawerAction = {
            drawerLayout.closeDrawers()
        }, selectMenuItemAction = { menuItem: SideMenuItem ->
            //viewModel.selectMenuItem(menuItem)
        })

        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                //sideMenuFragment.startBgrVideo()
            }

            override fun onDrawerClosed(drawerView: View) {
                //sideMenuFragment.stopBgrVideo()
            }
        })
    }




    private inner class MyFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val resFragment: Fragment

            if (position == 1)
                resFragment = CreditPageFragment.newInstance(position)
            else if (position == 2)
                resFragment = FlatPageFragment.newInstance(position)
            else
                resFragment = TaskPageFragment.newInstance(position)

            return resFragment
        }

        override fun getCount(): Int {
            return PAGE_COUNT
        }

        override fun getPageTitle(position: Int): CharSequence {
            var title = "title"
            when (position) {
                0 -> title = "Задачи"
                1 -> title = "Кредиты"
                2 -> title = "Квартплата"
            }

            return title
        }

    }

    companion object {

        internal val TAG = "myLogs"
        internal val PAGE_COUNT = 3
    }

}
