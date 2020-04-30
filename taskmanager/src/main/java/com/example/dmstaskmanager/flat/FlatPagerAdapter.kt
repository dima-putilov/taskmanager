package com.example.dmstaskmanager.flat

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.flat.mainPage.FlatMainFragment
import com.example.dmstaskmanager.flat.paymentListPage.FlatPaymentListFragment
import com.example.dmstaskmanager.flat.settingPage.FlatSettingsFragment

/**
 * Flat pager adapter
 */
class FlatPagerAdapter constructor(fragmentManager : FragmentManager): FragmentPagerAdapter(fragmentManager) {

    val PAGE_COUNT = 3

    val pageList = listOf<Fragment>(FlatMainFragment.newInstance(0), FlatSettingsFragment.newInstance(1), FlatPaymentListFragment.newInstance(2))
    val TitleList = listOf<String>("Объект", "Настройки", "Платежи")

    fun reloadInstance(flat: HOME) {
        pageList.forEach {
            when (it) {
                is FlatMainFragment -> {
                    it.setCurrentFlat(flat)
                }
                is FlatSettingsFragment -> {
                    it.setCurrentFlat(flat)
                }
                is FlatPaymentListFragment -> {
                    it.setCurrentFlat(flat)
                }

            }
        }
    }

    override fun getItem(position: Int): Fragment {
        return pageList[position]
    }

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItemPosition(`object`: Any): Int {
        return pageList.indexOf(`object`)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return TitleList[position]
    }
}
