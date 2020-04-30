package com.example.dmstaskmanager.flat.settingPage

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.ARGUMENT_PAGE_NUMBER
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.utils.Navigator
import com.example.dmstaskmanager.utils.gone
import com.example.dmstaskmanager.utils.visible
import com.google.gson.Gson
import kotlinx.android.synthetic.main.flat_settings_fragment.*

/**
 * Created by dima on 08.11.2018.
 */
class FlatSettingsFragment : Fragment() {

    var pageNumber: Int = 0
    private var flat_id : Int = -1

    companion object {

        fun newInstance(page: Int): FlatSettingsFragment {
            val pageFragment = FlatSettingsFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.setArguments(arguments)
            return pageFragment
        }

        fun getTitle(): String  {
            return "Настройки"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.flat_settings_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setParam()

        loadData()

        setListeners()

        updateUI()
    }

    fun setListeners(){
        cbIsCounter.setOnCheckedChangeListener { buttonView, isChecked ->
            updateUI()
        }

        cbIsArenda.setOnCheckedChangeListener { buttonView, isChecked ->
            updateUI()
        }
    }

    private fun updateUI(){
        if (cbIsCounter.isChecked) {
            ltCounter.visible()
        } else {
            ltCounter.gone()
        }

        if (cbIsArenda.isChecked) {
            ltArenda.visible()
        } else {
            ltArenda.gone()
        }
    }

    private fun setParam() {
        val intent = activity.intent

        if (intent.hasExtra(Navigator.EXTRA_FLAT_KEY)) {
            val taskGson = intent.getStringExtra(Navigator.EXTRA_FLAT_KEY)
            val flat = Gson().fromJson(taskGson, HOME::class.java)
            flat_id = flat._id
        }
    }

    fun setCurrentFlat(flat: HOME) {
        if (this.flat_id != flat._id) {
            this.flat_id = flat._id
            loadData()
        }
    }

    fun loadData(){
        // открываем подключение к БД
        val db = DB(activity)
        db?.open()

        if (flat_id > 0) {

            val flat = db?.getFlat(flat_id)

            flat?.let { flat ->
                etDayBeg.setText(flat.day_beg.toString())
                etDayEnd.setText(flat.day_end.toString())
                etLic.setText(flat.lic)

                cbIsCounter.isChecked = flat.isCounter
                cbIsPay.isChecked = flat.isPay
                cbIsArenda.isChecked = flat.isArenda

                etDayArenda.setText(flat.day_arenda.toString())

                etSummaArenda.setText(flat.summa_arenda.toString())

            }

        }
    }

}