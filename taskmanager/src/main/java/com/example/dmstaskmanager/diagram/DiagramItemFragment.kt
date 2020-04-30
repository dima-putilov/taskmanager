package com.example.dmstaskmanager.diagram

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.*
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.*
import com.example.dmstaskmanager.view.DiagramView
import kotlinx.android.synthetic.main.credit_diagram_item_fragment.circleDiagramView
import kotlinx.android.synthetic.main.credit_diagram_item_fragment.creditParamsView
import kotlinx.android.synthetic.main.credit_diagram_item_fragment.diagramView
import kotlinx.android.synthetic.main.credit_diagram_item_fragment.diagramsLayout

class DiagramItemFragment : Fragment() {

    var pageNumber: Int = 0
    var diagramItem: DiagramItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.credit_diagram_item_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        setListener()
    }

    fun updateUI(){

        diagramItem?.also {
            circleDiagramView.setDiagramData(it)
            creditParamsView.setDiagramData(it)
            diagramView.setDiagramData(it)
        }

    }

    fun setListener() {
        diagramsLayout.setOnClickListener{
            updateUI()
        }
    }

}
