package com.example.dmstaskmanager.flat

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import com.example.dmstaskmanager.classes.HOME
import kotlinx.android.synthetic.main.flat_activity.*
import kotlinx.android.synthetic.main.flat_main_fragment.view.*
import kotlinx.android.synthetic.main.flat_settings_fragment.view.*
import android.view.MenuItem
import com.example.dmstaskmanager.*
import com.example.dmstaskmanager.classes.HomeType
import com.example.dmstaskmanager.utils.Navigator
import com.example.dmstaskmanager.utils.Str2Int
import com.example.dmstaskmanager.utils.ToolbarUtils
import com.google.gson.Gson
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.view.ViewPager.OnPageChangeListener
import com.example.dmsflatmanager.main_window.flatPage.FlatViewModel
import com.example.dmstaskmanager.flat.mainPage.FlatMainFragment
import com.example.dmstaskmanager.flat.paymentListPage.FlatPaymentListFragment
import com.example.dmstaskmanager.flat.settingPage.FlatSettingsFragment
import kotlinx.android.synthetic.main.item_flat.*
import java.io.ByteArrayOutputStream
import kotlin.math.abs

/**
 * Created by dima on 08.11.2018.
 */

class FlatActivity : AppCompatActivity() {

    private val FOTO_COMPRESS_VALUE: Int = 85
    var flat_id = -1
    // открываем подключение к БД
    //var db : DB? = null

    lateinit var viewModel: FlatViewModel

    lateinit var pagerAdapter : FlatPagerAdapter
    lateinit var objectListPagerAdapter : ObjectListPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flat_activity)

        ToolbarUtils.initToolbar(this, true, R.string.toolbar_flat, R.color.FlatItemToolbar, R.color.FlatItemWindowsBar)

        bindViewModel()

        setListener()

        setFlatPager()

        setObjectListPager()

        initInstance()
    }

    private fun bindViewModel() {

        viewModel = ViewModelProviders.of(this).get(FlatViewModel::class.java)

        viewModel.flatList.observe(this, Observer { flatList ->
            flatList?.also { flatList ->
                initFlatListPager(flatList)
            }
        })

        viewModel.currentPage.observe(this, Observer { currentPage ->
            currentPage?.also { currentPage ->
                setCurrentListFlatPage(currentPage)
            }
        })

        viewModel.reloadToFlatEvent.observe(this, Observer { flat ->
            flat?.also { flat ->
                reloadInstance(flat)
            }
        })

    }


    private fun setObjectListPager() {
        var currentPosition = 0

        objectListPagerAdapter = ObjectListPagerAdapter(supportFragmentManager)
        objectListPager.adapter = objectListPagerAdapter
        objectListPagerAdapter.notifyDataSetChanged()

        objectListPager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                Log.d("RELOAD", "1 position = $position object =${objectListPagerAdapter.flatItemsList}")

                viewModel.onChangeObjectPage(position)

            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                positionOffsetPixels: Int) {

                val newPosition = if (positionOffset > 0.5) {
                    position + 1
                } else {
                    position
                }

                if (currentPosition != newPosition) {
                    currentPosition = newPosition
                    //viewModel.setCurrentDevice(currentPosition)
                }

                val alpha = abs(1 - positionOffset / 0.5).toFloat()

                //flatPager.alpha = alpha
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        objectListPager.currentItem = 0
    }

    private fun setFlatPager() {
        pagerAdapter = FlatPagerAdapter(supportFragmentManager)
        flatPager.adapter = pagerAdapter
        pagerAdapter.notifyDataSetChanged()

        flatPager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                //Log.d(main.TAG, "onFlatPageSelected, position = $position")

                val menu = navigation.menu
                if (menu.size() > position) {
                    val item = navigation.menu.getItem(position)
                    navigation.selectedItemId = item.itemId
                }

                viewModel.reloadInstance()

            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        flatPager.currentItem = 0
    }


    private fun reloadInstance(flat: HOME) {
        //if (flat_id != flat._id) {
            flat_id = flat._id


            val flatJson = Gson().toJson(flat)
            intent?.extras?.putString(Navigator.EXTRA_FLAT_KEY, flatJson)

            supportFragmentManager.fragments.forEach {
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

            //pagerAdapter.reloadInstance(flat)

            pagerAdapter.notifyDataSetChanged()

       // }

    }

    private fun initInstance() {

        val flat = if (intent.hasExtra(Navigator.EXTRA_FLAT_KEY)) {
                        val flatGson = intent.getStringExtra(Navigator.EXTRA_FLAT_KEY)
                        val flat = Gson().fromJson(flatGson, HOME::class.java)
                        flat_id = flat._id
                        flat
                    } else {
                        null
                    }

        viewModel.initInstance(flat)

        if (flat_id < 0 ){
            ToolbarUtils.setNewFlag(this)
        }
    }

    private fun initFlatListPager(flatList: List<HOME>) {
        objectListPagerAdapter.flatItemsList = flatList
        objectListPagerAdapter.notifyDataSetChanged()
    }

    private fun setCurrentListFlatPage(position : Int) {
        objectListPager.currentItem = position
    }


    private fun setListener(){
        // === ToolBar ===
        val toolbar = findViewById<Toolbar>(R.id.toolbar_actionbar_item)
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            Log.d("android.R.id.home", "ПУНКТ МЕНЮ ###" + item.itemId)

            when (item.itemId) {
                android.R.id.home    //button home
                -> {
                    finish()
                    return@OnMenuItemClickListener true
                }

                R.id.action_delete    //button del
                -> {
                    onClickDelete()
                    finish()
                    return@OnMenuItemClickListener true
                }

                R.id.action_OK -> {
                    onClickAdd()
                    return@OnMenuItemClickListener true
                }
            }// return super.onOptionsItemSelected(item);

            // Handle the menu item
            true
        })

        // Inflate a menu to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.menu_graphic)

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    flatPager.currentItem = 0
                    true
                }
                R.id.action_settings -> {
                    flatPager.currentItem = 1
                    true
                }
                R.id.action_payment -> {
                    flatPager.currentItem = 2
                    true
                }
                else -> {
                    false
                }

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> onBackPressed()
        }

        return true
    }

    // ====== Toolbar ======
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_item, menu)
        return true
    }

    private fun onClickDelete() {


    }


    private fun onClickAdd() {

        val cur_flat = HOME()

        // Main fragment
        pagerAdapter.getItem(0)?.view?.let { flatMainFragmentView ->
        //fragmentManager.findFragmentByTag("flatMainFragment")?.view?.let { flatMainFragmentView ->

            cur_flat.name = flatMainFragmentView.etFlatName.text.toString()
            cur_flat.adres = flatMainFragmentView.etAdres.text.toString()
            cur_flat.param = flatMainFragmentView.etParam.text.toString()


            // TODO : переделать на нормальный возврат ИД кредита
            val creditListAdapter = CreditListAdapter(this)
            creditListAdapter?.let { adapter ->
                adapter.updateListCreditData()
                val listData = adapter.listData
                cur_flat.credit_id = listData.get(flatMainFragmentView.spCredit.selectedItemPosition)
            }


            val listHomeTypes = HomeType.getHomeTypeList()
            cur_flat.type = listHomeTypes.get(flatMainFragmentView.spType.selectedItemPosition)

            var str_summa = flatMainFragmentView.etSumma.text.toString()
            if (TextUtils.isEmpty(str_summa)) {
                str_summa = "0"
            }
            cur_flat.summa = java.lang.Double.parseDouble(str_summa)

            cur_flat.finish = flatMainFragmentView.cbFinish.isChecked

        } ?: return

        // Settings fragment
        pagerAdapter.getItem(1)?.view?.let { flatSettingsFragmentView ->
        //fragmentManager.findFragmentByTag("flatSettingsFragment")?.view?.let{ flatSettingsFragmentView ->
            cur_flat.isCounter = flatSettingsFragmentView.cbIsCounter.isChecked
            cur_flat.isPay = flatSettingsFragmentView.cbIsPay.isChecked
            cur_flat.isArenda = flatSettingsFragmentView.cbIsArenda.isChecked

            cur_flat.lic = flatSettingsFragmentView.etLic.text.toString()

            cur_flat.day_beg = Str2Int(flatSettingsFragmentView.etDayBeg.text.toString())
            cur_flat.day_end = Str2Int(flatSettingsFragmentView.etDayEnd.text.toString())

            cur_flat.day_arenda = Str2Int(flatSettingsFragmentView.etDayArenda.text.toString())

            var str_summa_arenda = flatSettingsFragmentView.etSummaArenda.text.toString()
            if (TextUtils.isEmpty(str_summa_arenda)) {
                str_summa_arenda = "0.0"
            }
            cur_flat.summa_arenda = java.lang.Double.parseDouble(str_summa_arenda)


            if (imgFoto.drawable != null) {
                val baos = ByteArrayOutputStream()
                val bitmap = (imgFoto.drawable as BitmapDrawable).bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG, FOTO_COMPRESS_VALUE, baos)
                cur_flat.foto = baos.toByteArray()
            }

        } ?: return

        if (flat_id > 0) {
            cur_flat._id = flat_id
            viewModel.updateFlat(cur_flat)
        } else {
            viewModel.addFlat(cur_flat)
        }

        Navigator.exitFromFlatActivity(this, cur_flat)

    }

}