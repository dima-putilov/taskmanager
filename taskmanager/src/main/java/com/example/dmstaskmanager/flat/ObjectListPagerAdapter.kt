package com.example.dmstaskmanager.flat

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.dmstaskmanager.classes.FlatItem
import com.example.dmstaskmanager.classes.HOME

/**
 * Flat pager adapter
 */
class ObjectListPagerAdapter constructor(fragmentManager : FragmentManager): FragmentStatePagerAdapter(fragmentManager) {

    var flatItemsList: List<HOME> = listOf()
        set(value) {
            createFlatItemFragmentsList(value)
        }

    private var flatItemFragmentsList : List<FlatPagerListItemFragment> = listOf()

    override fun getCount(): Int {
        return flatItemFragmentsList.count()
    }

    override fun getItem(position: Int) : Fragment {
        return flatItemFragmentsList.get(position)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    private fun createFlatItemFragmentsList(flatItemsList : List<HOME>){
        val fragmentsList = mutableListOf<FlatPagerListItemFragment>()
        for ((index, flat) in flatItemsList.withIndex()) {
            val fragment = FlatPagerListItemFragment()
            fragment.flat = flat
            fragment.pageNumber = index
            fragmentsList.add(fragment)
        }
        flatItemFragmentsList = fragmentsList
    }

}
