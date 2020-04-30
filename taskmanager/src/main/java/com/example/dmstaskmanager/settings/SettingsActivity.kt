package com.example.dmstaskmanager.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.utils.Settings
import com.example.dmstaskmanager.utils.ToolbarUtils
import kotlinx.android.synthetic.main.activity_settings.showClosedCreditSwitch
import kotlinx.android.synthetic.main.activity_settings.showClosedFlatSwitch

class SettingsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ToolbarUtils.initToolbar(this, true, R.string.toolbar_settings, R.color.FlatItemToolbar, R.color.FlatItemWindowsBar)

        initUI()

        setListeners()

    }

    private fun initUI() {
        showClosedCreditSwitch.isChecked = Settings.getShowCloseCreditSettings(this)
        showClosedFlatSwitch.isChecked = Settings.getShowCloseFlatSettings(this)
    }

    private fun setListeners() {
        showClosedCreditSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            Settings.setShowCloseCreditSettings(this, isChecked)
        }

        showClosedFlatSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            Settings.setShowCloseFlatSettings(this, isChecked)
        }

    }

}
