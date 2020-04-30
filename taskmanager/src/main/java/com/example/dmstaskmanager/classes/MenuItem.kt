package com.example.dmstaskmanager.classes

import android.util.Log

enum class SideMenuItemType(val type: Int){
    NotSpecified(0),
    CreditGroup(10),
    CreditItem(20),
    FlatGroup(30),
    FlatItem(40),
    Settings(50),
    Diagram(70),
    Exit(1000)
}

/**
 * Menu item model
 */
data class SideMenuItem(
    var id: Int,
    var title: String,
    var icon : Int? = null,
    var type: SideMenuItemType? = null,
    var item: Any? = null,
    var itemsCountAll: Int? = null,
    var itemsCountActive: Int? = null
) {
    var isExpanded: Boolean = false

    fun areItemsTheSame(newItem: SideMenuItem): Boolean {
        return (this.type == newItem.type) && (this.id == newItem.id)
    }

    fun areContentsTheSame(newItem: SideMenuItem): Boolean {
         return    areItemsTheSame(newItem)
            && (itemsCountAll === newItem.itemsCountAll)
            && (itemsCountActive === newItem.itemsCountActive)
            && (isExpanded == newItem.isExpanded)
    }
}


data class SideMenu(
    var id: Int,
    var title: String,
    var type: SideMenuItemType,
    var icon : Int? = null,
    var itemsList: List<SideMenuItem>? = null
) {
    var isExpanded: Boolean = false
}