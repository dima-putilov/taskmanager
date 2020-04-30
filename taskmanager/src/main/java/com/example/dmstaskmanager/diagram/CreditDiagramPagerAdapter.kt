package com.example.dmstaskmanager.diagram

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.dmstaskmanager.classes.DiagramItem

/**
 * Flat pager adapter
 */
class CreditDiagramPagerAdapter constructor(fragmentManager : FragmentManager): FragmentStatePagerAdapter(fragmentManager) {

    var diagramItemsList: List<DiagramItem> = listOf()
        set(value) {
            createDiagramItemFragmentsList(value)
        }

    private var creditItemFragmentsList : List<DiagramItemFragment> = listOf()

    override fun getCount(): Int {
        return creditItemFragmentsList.count()
    }

    override fun getItem(position: Int) : Fragment {
        return creditItemFragmentsList.get(position)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    private fun createDiagramItemFragmentsList(diagramItemsList : List<DiagramItem>){
        val fragmentsList = mutableListOf<DiagramItemFragment>()
        for ((index, creditData) in diagramItemsList.withIndex()) {
            val fragment = DiagramItemFragment()
            fragment.diagramItem = creditData
            fragment.pageNumber = index
            fragmentsList.add(fragment)
        }
        creditItemFragmentsList = fragmentsList
    }

}
