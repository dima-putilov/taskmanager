package com.example.dmstaskmanager.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import com.example.dmstaskmanager.R

class RecyclerViewItemDecoration(context : Context) : RecyclerView.ItemDecoration() {

    val mDivider : Drawable

    init {
        mDivider = context.getDrawable(R.drawable.divider_layout)!!
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        val dividerLeft = parent.getPaddingLeft()
        val dividerRight = parent.getWidth() - parent.getPaddingRight()

        // Don't draw the separator after the last element
        val childCount = parent.getChildCount()
        for (i in 0..childCount - 2) {
            val child = parent.getChildAt(i)

            val dividerTop = child.getBottom()
            val dividerBottom = dividerTop + mDivider.getIntrinsicHeight()

            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            mDivider.draw(canvas)
        }
    }
}
