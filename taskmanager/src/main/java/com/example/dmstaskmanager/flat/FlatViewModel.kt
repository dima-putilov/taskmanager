package com.example.dmsflatmanager.main_window.flatPage

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.utils.SingleLiveEvent

class FlatViewModel(application: Application): AndroidViewModel(application) {

    var flatList = MutableLiveData<List<HOME>>()

    var reloadToFlatEvent = SingleLiveEvent<HOME>()
    var currentPage = SingleLiveEvent<Int>()

    var currentflat: HOME? = null

    fun initInstance(flat: HOME?) {
        this.currentflat = flat

        initFlatList(flat)

    }

    fun initFlatList(flat: HOME?){
        val db = DB(getApplication())
        db.open()
        db.getAllFlats()?.also { flatList ->
            this.flatList.value = flatList
            currentPage.value = flatList.indexOfFirst { it._id == flat?._id }
        }
        db.close()
    }

    fun updateFlat(flat: HOME) {
        val db = DB(getApplication())
        db.open()
        db.flat_Update(flat)
        db.close()
    }

    fun addFlat(flat: HOME) {
        val db = DB(getApplication())
        db.open()
        db.flat_Add(flat)
        db.close()
    }

    fun getFlatList() : List<HOME> {
        return flatList.value ?: listOf()
    }

    fun reloadInstance() {
        reloadToFlatEvent.value = currentflat
    }

    fun onChangeObjectPage(position : Int) {
        getFlatList()?.also { flatList ->
            if (flatList.size > position) {
                val newCurrentFlat = flatList[position]
                if (currentflat != newCurrentFlat) {
                    currentflat = newCurrentFlat
                    reloadInstance()
                }
            }
        }

    }

}