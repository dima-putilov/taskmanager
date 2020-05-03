package com.example.dmstaskmanager.diagram

import android.support.v4.app.Fragment
import android.os.Bundle
import android.text.format.DateUtils
import android.view.*
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.*
import kotlinx.android.synthetic.main.credit_diagram_item_fragment.*

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

       // setListener()
    }

//    fun updateUI(){
//
//        diagramItem?.also {
//            circleDiagramView.setDiagramData(it)
//            creditParamsView.setDiagramData(it)
//            diagramView.setDiagramData(it)
//        }
//
//    }
//
//    fun setListener() {
//        diagramsLayout.setOnClickListener{
//            updateUI()
//        }
//    }

    fun updateUI() {
        diagramItem?.creditTotals?.credit?.also {credit ->
            val name = credit.name +
                if (credit.date > 0 ) {
                    " от " + DateUtils.formatDateTime(activity,
                        credit.date, DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)
                } else {
                    ""
                }

            val param = "Сумма: " + String.format("%.0f", credit.summa) +
                if (credit.procent > 0) {
                    ", Процент: " + String.format("%.2f%%", credit.procent) + ", Срок: " + credit.period.toString()
                } else {
                    ""
                } +
                if (credit.period > 0) {
                    ", Срок: " + credit.period.toString()
                } else {
                    ""
                }

            tvName.text = name
            tvParam.text = param
        }
    }


}
