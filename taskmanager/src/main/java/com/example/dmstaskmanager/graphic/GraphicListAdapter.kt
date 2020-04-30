package com.example.dmstaskmanager.graphic

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.PAYMENT
import com.example.dmstaskmanager.utils.formatD
import com.example.dmstaskmanager.utils.formatDate
import kotlinx.android.synthetic.main.item_graphic.view.*
import java.lang.reflect.Type

/**
 * Graphic list adapter
 */
class GraphicListAdapter constructor(private var context: Context) : BaseAdapter() {

    var listData: List<PAYMENT> = listOf()
    var currentPayPosition: Int = -1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: GraphicItemListViewHolder

        if (convertView != null) {
            view = convertView
            viewHolder = view.tag as GraphicItemListViewHolder
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_graphic, parent, false)
            viewHolder = GraphicItemListViewHolder(view, context)
            view.tag = viewHolder
        }

        val payment = listData.get(position)
        val isCurrentPayPosition = (position == currentPayPosition)

        viewHolder.initUI(payment, isCurrentPayPosition)

        return view
    }


    override fun getItem(position: Int): Any {
        return listData.get(position)
    }

    override fun getItemId(position: Int): Long {
        val item = listData.get(position)
        return item._id.toLong()
    }

    override fun getCount(): Int {
        return listData.count()
    }

    override fun isEnabled(position: Int): Boolean {
        return true
    }

}

private class GraphicItemListViewHolder(view: View, val context: Context) {
    var llGraphic: LinearLayout
    var imgCheck: ImageView
    var tvGrDate: TextView
    var tvRest: TextView
    var tvSumma: TextView
    var tvSumma_credit: TextView
    var tvSumma_procent: TextView

    val color_done = ContextCompat.getColor(context, R.color.color_done)
    val color_other = ContextCompat.getColor(context, R.color.color_other)
    val color_current_pay = ContextCompat.getColor(context, R.color.color_current_pay)
    val color_text_new = ContextCompat.getColor(context, R.color.color_text_new)
    val color_text = ContextCompat.getColor(context, R.color.color_text)
    val color_text_default = ContextCompat.getColor(context, R.color.color_text_default)

    init {
        this.llGraphic = view.llGraphic
        this.imgCheck = view.imgCheck
        this.tvGrDate = view.tvGrDate
        this.tvRest = view.tvRest
        this.tvSumma = view.tvSumma
        this.tvSumma_credit = view.tvSumma_credit
        this.tvSumma_procent = view.tvSumma_procent
    }

    fun initUI(payment : PAYMENT, isCurrentPayPosition: Boolean){

        if (payment.done == 1) {
            imgCheck.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.check))
            imgCheck.visibility = View.VISIBLE
        }
        else {
            if (isCurrentPayPosition){
                imgCheck.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.arrow_to_right))
                imgCheck.visibility = View.VISIBLE
            } else {
                imgCheck.visibility = View.INVISIBLE
            }
        }

        tvGrDate.text  = formatDate(payment.date, "dd.MM.yy")

        tvRest.text = formatD(payment.rest)

        //! Сделать в виде глобальной настройки "Отображать остаток по кредиту"
        tvRest.visibility = View.GONE

        tvSumma.text = formatD(payment.summa)
        tvSumma_credit.text = formatD(payment.summa_credit)
        tvSumma_procent.text = formatD(payment.summa_procent)

        val currentBackGroundColor = if (payment.done == 1) {
            color_done
        } else {
            color_other
        }

        val currentTextColor =  if (payment._id < 0){
            color_text_new
        } else {
            if (isCurrentPayPosition){
                color_text
            } else {
                color_text_default
            }
        }

        val currentTextTypeface = if (isCurrentPayPosition) {
            Typeface.BOLD_ITALIC
        } else {
            Typeface.NORMAL
        }

        llGraphic.setBackgroundColor(currentBackGroundColor)

        tvGrDate.setTextColor(currentTextColor)
        tvRest.setTextColor(currentTextColor)
        tvSumma.setTextColor(currentTextColor)
        tvSumma_credit.setTextColor(currentTextColor)
        tvSumma_procent.setTextColor(currentTextColor)

        tvGrDate.typeface = Typeface.create(Typeface.DEFAULT, currentTextTypeface)
        tvRest.setTypeface(null, currentTextTypeface)
        tvSumma.setTypeface(null, currentTextTypeface)
        tvSumma_credit.setTypeface(null, currentTextTypeface)
        tvSumma_procent.setTypeface(null, currentTextTypeface)

    }
}