package com.example.dmstaskmanager.diagram

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.utils.ToolbarUtils

class CreditDiagramActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.credit_diagram_activity)

        ToolbarUtils.initToolbar(this, true, R.string.toolbar_diagram, R.color.CreditDiagramToolbar, R.color.CreditDiagramWindowsBar)
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
