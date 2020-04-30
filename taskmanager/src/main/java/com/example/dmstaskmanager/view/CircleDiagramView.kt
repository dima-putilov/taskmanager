package com.example.dmstaskmanager.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.Rect
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.DiagramItem
import com.example.dmstaskmanager.classes.PAYMENT
import com.example.dmstaskmanager.view.dynamic.DynamicsCircleDiagram
import kotlin.math.roundToInt

class CircleDiagramView : View {

    private var dataPoints: List<DynamicsCircleDiagram> = listOf()

   // private var graphicListData: List<PAYMENT> = listOf()
//    var maxDataValue : Float = 0f

    var lineColor : Int = 0
    var textColor : Int = 0
  //  var coordColor : Int = 0
    var addedLineColor : Int = 0

//    var text : String = ""
    var textSize = 50f

    private val animator = object: Runnable {
        override fun run() {

            var scheduleNewFrame = false
            val now = AnimationUtils.currentAnimationTimeMillis()

            for (dataPoint in dataPoints) {
                dataPoint.update(now)
                if (!dataPoint.isAtRest()) {
                    scheduleNewFrame = true
                }
            }

            if (scheduleNewFrame) {
                postDelayed(this, 15)
            }
            invalidate()
        }
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView)
        lineColor = attributes.getColor(R.styleable.StrokeTextView_lineColor, ContextCompat.getColor(context, R.color.color_text))
        textColor = attributes.getColor(R.styleable.StrokeTextView_textColor, ContextCompat.getColor(context, R.color.color_text))

//        coordColor =  ContextCompat.getColor(context, R.color.color_text)

        addedLineColor =  ContextCompat.getColor(context, R.color.diagram_coordinates_added_lines_color)

        textSize = attributes.getDimension(R.styleable.StrokeTextView_textSize, 0f)

  //      text = attributes.getString(R.styleable.StrokeTextView_text) ?: ""

        attributes.recycle()
    }

    fun setDiagramData(diagramItem: DiagramItem) {

        val creditInit = diagramItem.creditTotals.credit.summa.toFloat()

        val creditSummaDone = diagramItem.creditTotals.fact.summa_credit

        var procent =
            if (creditSummaDone >= creditInit) {
                100
            } else {
                (creditSummaDone / creditInit * 100).roundToInt()
            }

        if (procent > 100) {
            procent = 100
        }

        val now = AnimationUtils.currentAnimationTimeMillis()

        val dyn = DynamicsCircleDiagram(70f, 0.5f)
        dyn.setState(0f, now)
        dyn.targetPosition = procent.toFloat()

        val dynMax = DynamicsCircleDiagram(70f, 0.5f)
        dynMax.setState(0f, now)
        dynMax.targetPosition = 100f

        dataPoints = listOf(dyn, dynMax)

//            invalidate()

        removeCallbacks(animator)
        post(animator)

    }


    override fun onDraw(canvas: Canvas) {
        drawDiagrams(canvas)
    }

    private fun drawDiagrams(canvas: Canvas) {

        drawCircleChart(canvas)

    }

    private fun drawCircleChart(canvas: Canvas) {

        if (dataPoints.isEmpty()) {
            return
        }

        lineColor = ContextCompat.getColor(context, R.color.diagram_line_color)

        val STROKE_WIDTH = 90f

        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = STROKE_WIDTH
        paint.color = lineColor

        val centerX = measuredWidth / 2f
        val centerY = measuredHeight / 2f
        val radius = Math.min(centerX/1.9, centerY/1.9)

        val mRect = RectF((centerX - radius - STROKE_WIDTH).toFloat(),
            (centerY - radius - STROKE_WIDTH).toFloat(),
            (centerX + radius + STROKE_WIDTH).toFloat(),
            (centerY + radius + STROKE_WIDTH).toFloat())

        val angle = 360f * dataPoints[0].position / 100

        canvas.drawArc(mRect, 270f, angle, false, paint)

        if (dataPoints[0].isAtRest()) {

            val angleTo =  (360f - angle)* dataPoints[1].position / 100

            paint.color = ContextCompat.getColor(context, R.color.diagram_line_color_2)
            canvas.drawArc(mRect, 270f + angle, angleTo, false, paint)
        }

        val paintText = Paint()
        paintText.style = FILL
        paintText.color = lineColor
        paintText.textSize = 120f
        paintText.setTextAlign(Paint.Align.CENTER)

        val procText = "${dataPoints[0].position.roundToInt()}"

        var textX = centerX
        var textY = centerY + paintText.textSize / 3

        canvas.drawText(procText, textX, textY, paintText)

        val paintLabel = Paint()
        paintLabel.style = STROKE
        paintLabel.color = lineColor
        paintLabel.textSize = paintText.textSize / 2
        paintLabel.textAlign = Paint.Align.LEFT

        textX = centerX + paintText.measureText(procText) - 60

//        ////
//        var rect = Rect (0,0 ,0,0)
//        paintText.getTextBounds(procText, 0 , procText.length, rect)
//        textX = centerX + rect.right
//
//        Log.d("DYN", "rect = $rect")
//        ////

        textY = centerY  //- paintText.textSize / 3 + paintLabel.textSize

        canvas.drawText("%", textX, textY, paintLabel)

//
//        // test
//        paint.style = Paint.Style.STROKE
//        paint.strokeWidth = 1f
//        //paint.color = lineColor
//
//        canvas.drawLine(centerX, centerY, centerX, top.toFloat(),  paint)
//
//        canvas.drawLine(centerX, centerY, right.toFloat(), centerY,  paint)

    }



}