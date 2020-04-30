package com.example.dmstaskmanager.graphic

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.utils.ToolbarUtils

class GraphicListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.graphic_activity)

        ToolbarUtils.initToolbar(this, true, R.string.toolbar_graphic, R.color.CreditItemToolbar, R.color.CreditItemWindowsBar)
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
        menuInflater.inflate(R.menu.menu_graphic, menu)
        return true
    }
}
