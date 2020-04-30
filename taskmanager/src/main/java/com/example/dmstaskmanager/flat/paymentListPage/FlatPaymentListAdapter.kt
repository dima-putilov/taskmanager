package com.example.dmstaskmanager.flat.paymentListPage

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.FlatPayment
import com.example.dmstaskmanager.utils.RecyclerViewItemDecoration
import com.example.dmstaskmanager.utils.gone
import com.example.dmstaskmanager.utils.months
import com.example.dmstaskmanager.utils.visible
import kotlinx.android.synthetic.main.item_flat_pay.view.llFlatPay
import kotlinx.android.synthetic.main.item_flat_pay.view.tvComment
import kotlinx.android.synthetic.main.item_flat_pay.view.tvFPDate
import kotlinx.android.synthetic.main.item_flat_pay.view.tvFPOper
import kotlinx.android.synthetic.main.item_flat_pay.view.tvSumma
import java.util.Calendar

/**
 * Flat payment list adapter delegate
 */
interface FlatPaymentListAdapterDelegate{
    fun onItemClick(payment: FlatPayment)
}

/**
 * Flat payment list adapter
 */
class FlatPaymentListAdapter constructor(private var context: Context, val delegate: FlatPaymentListAdapterDelegate? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    FlatPaymentViewHolderDelegate {

    var flatPaymentItemList: List<FlatPayment> = listOf()

    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_flat_pay, parent, false)
        return FlatPaymentViewHolder(view, context, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (flatPaymentItemList.isEmpty()) return

        val flatPayment = flatPaymentItemList.get(position)

        (holder as FlatPaymentViewHolder).updateUI(flatPayment)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(RecyclerViewItemDecoration(context))
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return flatPaymentItemList.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Holder Delegate methods
    override fun onItemClick(payment: FlatPayment) {
        delegate?.onItemClick(payment)
    }

}

/**
 * Flat payment item view holder delegate
 */

interface FlatPaymentViewHolderDelegate{
    fun onItemClick(flatPayment: FlatPayment)
}

class FlatPaymentViewHolder constructor(view: View, val context: Context, val delegate: FlatPaymentViewHolderDelegate? = null): RecyclerView.ViewHolder(view) {

    private val llFlatPay = view.llFlatPay
    private val tvFPDate = view.tvFPDate
    private val tvFPOper = view.tvFPOper
    private val tvSumma = view.tvSumma
    private val tvComment = view.tvComment

    fun updateUI(flatPayment: FlatPayment) {
        llFlatPay.setOnClickListener {
            delegate?.onItemClick(flatPayment)
        }

        val dateAndTime = Calendar.getInstance()
        dateAndTime.timeInMillis = flatPayment.date
        val str_date = months[dateAndTime.get(Calendar.MONTH)] + " " + dateAndTime.get(
            Calendar.YEAR).toString() + " г."

        tvFPDate.text = str_date
        tvFPOper.text = flatPayment.operation.titleShort
        tvSumma.text = String.format("%.2f", flatPayment.summa)

        tvComment.text = flatPayment.comment
        if (flatPayment.comment.isNotEmpty()) {
            tvComment.visible()
        } else {
            tvComment.gone()
        }

    }
}