package com.example.dmstaskmanager.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.example.dmstaskmanager.R

class StrokeTextView : View {

    var lineColor : Int = 0
    var textOffset : Float = 0f
    var textColor : Int = 0
    var text : String = ""
    var textSize = 50f

    var space = 30

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView)
        lineColor = attributes.getColor(R.styleable.StrokeTextView_lineColor, ContextCompat.getColor(context, R.color.transparent))
        textColor = attributes.getColor(R.styleable.StrokeTextView_textColor, ContextCompat.getColor(context, R.color.color_text))

        textOffset = attributes.getDimension(R.styleable.StrokeTextView_textOffset, 0f)

        textSize = attributes.getDimension(R.styleable.StrokeTextView_textSize, 0f)

        text = attributes.getString(R.styleable.StrokeTextView_text) ?: ""

        attributes.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {

            val paint = Paint()
            paint.style = Style.STROKE
            paint.color = textColor
//            paint.strokeWidth = 2f
            paint.textSize = textSize

//        val asset = context.assets
//        val plain = Typeface.createFromAsset(asset, R.font.myfont)
//        val bold = Typeface.create(plain, Typeface.DEFAULT_BOLD)

            //val bold = paint.setTypeface(Typeface.create("Arial",Typeface.ITALIC + Typeface.BOLD))
        val bold = paint.setTypeface(Typeface.create("Arial",Typeface.ITALIC))

            paint.setTypeface(bold)

            var left = paddingLeft
            val top = paddingTop
            val right = width - paddingRight
            val bottom = height - paddingBottom

            val centerHorizontal = (bottom - top) / 2f + 10

            val startText = left + textOffset
            canvas!!.drawText(text, startText.toFloat(), centerHorizontal  , paint)

            val endText = startText + paint.measureText(text)


            paint.strokeWidth = 2f
            paint.color = lineColor

        val linesArray = FloatArray(20, { it.toFloat() })

        val topPoint = top.toFloat() + 3

        linesArray[0] = left.toFloat()
        linesArray[1] = centerHorizontal
        linesArray[2] = startText - space
        linesArray[3] = centerHorizontal

        linesArray[4] = startText - space
        linesArray[5] = centerHorizontal
        linesArray[6] = startText
        linesArray[7] = topPoint

        linesArray[8] = startText
        linesArray[9] = topPoint
        linesArray[10] = endText
        linesArray[11] = topPoint

        linesArray[12] = endText
        linesArray[13] = topPoint
        linesArray[14] = endText + space
        linesArray[15] = centerHorizontal

        linesArray[16] = endText + space
        linesArray[17] = centerHorizontal
        linesArray[18] = right.toFloat()
        linesArray[19] = centerHorizontal


        canvas!!.drawLines(linesArray, paint)

    }

}