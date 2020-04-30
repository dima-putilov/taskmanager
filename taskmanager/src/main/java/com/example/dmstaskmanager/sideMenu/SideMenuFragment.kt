package com.example.dmstaskmanager.sideMenu

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.Credit
import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.classes.SideMenuItem
import com.example.dmstaskmanager.classes.SideMenuItemType
import com.example.dmstaskmanager.classes.TaskItem
import com.example.dmstaskmanager.utils.Navigator
import kotlinx.android.synthetic.main.activity_flat.flatRecyclerView
import kotlinx.android.synthetic.main.side_menu_fragment.sideMenuRecyclerView


/**
 * Side menu fragment
 */
class SideMenuFragment : Fragment(), SideMenuAdapterDelegate {

    lateinit var viewModel : SideMenuViewModel

    private lateinit var sideMenuAdapter : SideMenuAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.side_menu_fragment, container, false)

        bindViewModel()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        setupSideMenuAdapter()

        viewModel.createMenuItems()
    }

    // Helper methods
    private fun bindViewModel(){
        viewModel = ViewModelProviders.of(this).get(SideMenuViewModel::class.java)

        viewModel.menuItemsList.observe(this, Observer { menuItemsList ->
           menuItemsList?.also { menuItemsList ->
               updateAdapter(menuItemsList)
            }
        })

        viewModel.activeMenuItem.observe(this, Observer {activeMenuItem ->
            activeMenuItem?.also {
                //selectMenuItemAction?.invoke(it)
            }
        })

        viewModel.flatToOpen.observe(this, Observer {flatToOpen ->
            flatToOpen?.also {
                showFlatView(flatToOpen)
            }
        })

        viewModel.creditToOpen.observe(this, Observer {creditToOpen ->
            creditToOpen?.also {
                showCreditView(creditToOpen)
            }
        })

        viewModel.showSettingsEvent.observe(this, Observer {
            showSettingsView()
        })

        viewModel.showDiagramEvent.observe(this, Observer {
            showDiagramView()
        })

        viewModel.closeEvent.observe(this, Observer {
            closeApplication()
        })
    }

    private fun setListeners(){
//        closeButton.setOnClickListener(View.OnClickListener {
//            closeDrawerAction?.invoke()
//        })
    }

    private fun setupSideMenuAdapter() {
        activity?.let {
        sideMenuAdapter = SideMenuAdapter(it, this)
            sideMenuRecyclerView.layoutManager = LinearLayoutManager(activity)
            sideMenuRecyclerView.adapter = sideMenuAdapter
        }
    }

    private fun updateAdapter(menuItemsList: List<SideMenuItem>){
        sideMenuAdapter.updateList(menuItemsList)
    }

    // Public methods
    fun syncWithDrawer(closeDrawerAction: () -> Unit, selectMenuItemAction: (menuItem: SideMenuItem) -> Unit){
//        this.closeDrawerAction = closeDrawerAction
//        this.selectMenuItemAction = selectMenuItemAction
    }

    private fun showFlatView(flat: HOME) {
        Navigator.navigateToFlatActivity(activity, this, flat)
    }

    private fun showCreditView(credit: Credit) {
        Navigator.navigateToGraphicActivity(activity, this, credit)
    }

    private fun showSettingsView() {
        Navigator.navigateToSettingsActivity(activity, this)
    }

    private fun showDiagramView() {
        Navigator.navigateToCreditDiagramActivity(activity, this)
    }

    private fun closeApplication() {
        activity?.finish()
    }

    override fun onSideMenuGroupClick(item: SideMenuItem) {
        viewModel.onItemClick(item)
    }

    override fun onSideMenuFlatItemClick(item: SideMenuItem) {
        viewModel.onItemClick(item)
    }

    override fun onSideMenuCreditItemClick(item: SideMenuItem) {
        viewModel.onItemClick(item)
    }

    override fun onSideMenuItemClick(item: SideMenuItem) {
        viewModel.onItemClick(item)
    }

}
