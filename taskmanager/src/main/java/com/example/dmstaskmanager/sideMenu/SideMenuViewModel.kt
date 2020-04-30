package com.example.dmstaskmanager.sideMenu

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.example.dmstaskmanager.useCase.GetSideMenuUseCase
import com.example.dmstaskmanager.classes.Credit
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.classes.SideMenuItem
import com.example.dmstaskmanager.classes.SideMenuItemType
import com.example.dmstaskmanager.classes.SideMenu
import com.example.dmstaskmanager.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Side menu
 */

class SideMenuViewModel(application: Application): AndroidViewModel(application) {

    var createMenuItemsUseCase = GetSideMenuUseCase()

    var menuItemsList = MutableLiveData<List<SideMenuItem>>()
    var sideMenu : List<SideMenu> = listOf()

    var activeMenuItem = MutableLiveData<SideMenuItem>()

    var creditToOpen = SingleLiveEvent<Credit>()
    var flatToOpen = SingleLiveEvent<HOME>()

    val showSettingsEvent = SingleLiveEvent<Void>()
    val showDiagramEvent = SingleLiveEvent<Void>()
    val closeEvent = SingleLiveEvent<Void>()

    private var getSideMenuSubscription: Disposable? = null

    override fun onCleared() {
        super.onCleared()

        getSideMenuSubscription?.dispose()
    }

    fun onItemClick(itemMenu: SideMenuItem) {
        when (itemMenu.type) {
            SideMenuItemType.CreditGroup, SideMenuItemType.FlatGroup -> {
                onGroupClick(itemMenu)
            }
            SideMenuItemType.FlatItem, SideMenuItemType.CreditItem -> {
                itemMenu.item?.also { item ->
                    when (item) {
                        is Credit -> creditToOpen.value = item
                        is HOME -> flatToOpen.value = item
                    }
                }
            }
            SideMenuItemType.Diagram -> showDiagramEvent.call()
            SideMenuItemType.Settings -> showSettingsEvent.call()
            SideMenuItemType.Exit -> closeEvent.call()
        }
    }

    fun onGroupClick(itemMenu: SideMenuItem) {
        for (group in sideMenu) {
            if (group.id == itemMenu.id) {
                group.isExpanded = !group.isExpanded
            }
        }
        updateMenuList()
    }

    fun createMenuItems(){

        getSideMenuSubscription?.dispose()

        getSideMenuSubscription = createMenuItemsUseCase.getMenuListSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onGetFlatItemsListStart() }
            .subscribe(
                { listData: List<SideMenu> -> onGetDataSuccess(listData) },
                { error: Throwable -> onGetDatasError(error) }
            )

        createMenuItemsUseCase.getSideMenuList(getApplication())

    }

    private fun onGetFlatItemsListStart() {
       // showFlatLoadingIndicatorEvent.call()
    }

    private fun onGetDataSuccess(listData: List<SideMenu>) {
        //hideFlatLoadingIndicatorEvent.call()

        this.sideMenu = listData

        updateMenuList()
    }

    private fun updateMenuList() {
        val newList = mutableListOf<SideMenuItem>()
        for (group in sideMenu) {
            val newGroup = group.copy()
            newGroup.isExpanded = group.isExpanded
            val newGroupItem = SideMenuItem(
                id = group.id,
                title = group.title,
                icon = group.icon,
                item = newGroup,
                type = group.type,
                itemsCountAll = group.itemsList?.size,
                itemsCountActive = group.itemsList?.count {
                    it.item?.let { item ->
                        when (item) {
                            is Credit -> !item.finish
                            is HOME -> !item.finish
                            else -> false
                        }
                    } ?: false
                })

            newGroupItem.isExpanded = newGroup.isExpanded

            newList.add(newGroupItem)

            if(group.isExpanded) {
                group.itemsList?.forEach {
                    newList.add(SideMenuItem(it.id, title = it.title, item = it.item, type = it.type))
                }
            }
        }

        menuItemsList.value = newList
    }

    private fun onGetDatasError(error: Throwable) {
        //Log.d("DMS_TASK", "onGetDatasError = $error")
        //hideGraphicLoadingIndicatorEvent.call()
        //showGraphicLoadErrorEvent.call()
    }

}
