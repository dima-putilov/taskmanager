package com.example.dmstaskmanager.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.graphics.Path.Direction.CW
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.DiagramItem
import com.example.dmstaskmanager.utils.formatDate
import com.example.dmstaskmanager.utils.round2Str
import com.example.dmstaskmanager.view.dynamic.DynamicsPayment
import java.util.Date
import kotlin.math.roundToInt

class DiagramView : View {

    private var dataPoints : List<DynamicsPayment> = listOf()

    //private var graphicListData: List<Dynamics> = listOf()
    var maxDataValue : Float = 0f

   // private var dataPoints : FloatArray = floatArrayOf()

    var lineColor : Int = 0
    var textColor : Int = 0
    var coordColor : Int = 0
    var addedLineColor : Int = 0

    var text : String = ""
    var textSize = 50f

    val xOffset = 50f
    val yOffset = 50f
    val countAddedLines = 10

    val maxApproximation = 12

    var maxRest = 0f

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

        coordColor =  ContextCompat.getColor(context, R.color.color_text)

        addedLineColor =  ContextCompat.getColor(context, R.color.diagram_coordinates_added_lines_color)

        textSize = attributes.getDimension(R.styleable.StrokeTextView_textSize, 0f)

        text = attributes.getString(R.styleable.StrokeTextView_text) ?: ""

        attributes.recycle()
    }


    fun setDiagramData(diagramItem: DiagramItem) {

        maxRest = diagramItem.creditTotals.credit.summa.toFloat()
        val maxDataValue = diagramItem.diagramData.maxBy { it.summa }?.summa?.toFloat() ?: 0f

        val step_rest = maxRest.roundToInt() / 10

        val step = maxDataValue.roundToInt() / 10

        val newListData = diagramItem.diagramData

        val newDataPoints = mutableListOf<DynamicsPayment>()
        val now = AnimationUtils.currentAnimationTimeMillis()

        var rest = maxRest

        newListData.forEach {
            val dyn = DynamicsPayment(70f, 0.5f)
            dyn.setState(it, now)
            dyn.payment = it.clone()
            dyn.payment.summa = 0.toDouble()
            dyn.payment.rest = maxRest.toDouble()

            dyn.targetPayment = it.clone()

            rest -= it.summa_credit.toFloat()

            dyn.STEP_REST = step_rest

            dyn.STEP = step

     //       Log.d("DYN", "init rest=$rest ($maxRest)")

            dyn.targetPayment.rest = rest.toDouble()

            newDataPoints.add(dyn)
        }

        dataPoints = newDataPoints

        removeCallbacks(animator)
        post(animator)
    }

    private fun getXPos(value: Float): Float {
        var value = value
        val width = width - paddingLeft - 2*xOffset - paddingRight.toFloat()
        val maxValue = dataPoints.size - 1 + 2
        // масштабирования под размер view
        value = value / maxValue * width
        // смещение чтобы учесть padding
        value += paddingLeft.toFloat() + xOffset
        return value
    }

    private fun getYPos(value: Float): Float {
        var value = value
        val height = height - paddingTop - 2*yOffset - paddingBottom.toFloat()
        val maxValue = maxDataValue + 30
        // масштабирования под высоту view
        value = value / maxValue * height
        // инверсия
        value = height - value
        // смещение чтобы учесть padding
        value += paddingTop.toFloat() + yOffset
        return value
    }

    fun getBottomLine(): Float {
        return (height - paddingBottom).toFloat() - yOffset
    }

    private fun drawCoordinatesLines(canvas: Canvas) {
        val left = paddingLeft.toFloat() + xOffset
        val top = paddingTop.toFloat() + yOffset
        val right = (width - paddingRight).toFloat() - xOffset
        val bottom = (height - paddingBottom).toFloat() - yOffset

        val arrowLength = 30f
        val arrowWidth = 5f

        val paint = Paint()
        paint.style = STROKE
        paint.strokeWidth = 4f
        paint.color = ContextCompat.getColor(context, R.color.diagram_coordinates_line_color)

        val paintText = Paint()
        paintText.style = STROKE
        paintText.strokeWidth = 1f
        paintText.color = lineColor
        paintText.textSize = 20f
        paintText.textAlign = Paint.Align.RIGHT

        // Оси X и Y
        val path = Path()
        path.moveTo(left, top)
        path.lineTo(left, bottom)
        path.lineTo(right, bottom)

        // Стрелка Y
        path.moveTo(left, top)
        path.lineTo(left - arrowWidth, top + arrowLength)
        path.moveTo(left, top)
        path.lineTo(left + arrowWidth, top + arrowLength)

        // Стрелка X
        path.moveTo(right, bottom)
        path.lineTo(right - arrowLength, bottom + arrowWidth)
        path.moveTo(right, bottom)
        path.lineTo(right - arrowLength, bottom - arrowWidth)

        path.addCircle(left, bottom, 4f, Path.Direction.CW)

        canvas.drawPath(path, paint)

        // Дополительные линии на оси Y
        paint.strokeWidth = 2f
        paint.color = addedLineColor

        path.reset()

        maxDataValue = dataPoints.maxBy { it.targetPayment.summa }?.targetPayment?.summa?.toFloat() ?: 0f

        var step = maxDataValue / countAddedLines
        if (step < 1000) {
            step = 1000f
        } else {
            if (step < 10000) {
                step = 10000f
            } else {
                if (step < 25000) {
                    step = 25000f
                } else {
                    if (step < 50000) {
                        step = 50000f
                    } else {
                        if (step < 100000) {
                            step = 100000f
                        } else {
                            step = 300000f
                        }
                    }
                }
            }
        }




        var i = step
        while (i < maxDataValue) {
            val y = getYPos(i)
            path.moveTo(left, y)
            path.lineTo(right, y)

            val textPoint = i / 1000
            canvas.drawText(round2Str(textPoint.toDouble(), 0), left - 10,
            y + 5,
            paintText)

            i += step
        }

        canvas.drawPath(path, paint)

    }

    override fun onDraw(canvas: Canvas) {

        drawCoordinatesLines(canvas)

        drawDiagrams(canvas)
    }

    private fun drawDiagrams(canvas: Canvas) {

        drawSummaPayChart(canvas, true)

        drawRestChart(canvas, true)

    }

    fun drawSummaPayChart(canvas: Canvas, isNeedDrawXPoints: Boolean = false, isNeedDrawValues: Boolean = true) {

        maxDataValue = dataPoints.maxBy { it.targetPayment.summa }?.targetPayment?.summa?.toFloat() ?: 0f

        Log.d("DYN", "-1- maxDataValue = $maxDataValue")


        lineColor = ContextCompat.getColor(context, R.color.diagram_line_color)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 2f
        paint.color = lineColor

        val paintFuture = Paint()
        paintFuture.style = Paint.Style.FILL
        paintFuture.strokeWidth = 2f
        paintFuture.color = ContextCompat.getColor(context, R.color.diagram_line_color_future)

        val paintText = Paint()
        paintText.style = STROKE
        paintText.color = lineColor
        paintText.textSize = 20f
        paintText.setTextAlign(Paint.Align.CENTER)


        val paintXPoint = Paint()
        paintXPoint.style = STROKE
        paintXPoint.strokeWidth = 2f
        paintXPoint.color = coordColor

        // График платежей
        val pathFuture = Path()
        val path = Path()
        val pathXPoint = Path()
        val pathAddedLines = Path()
        var prevYear = 0
        var year = 0

        val lwd = ( getXPos(2f) - getXPos(1f) ) / 4


        Log.d("DYN", "-111-  one = ${getXPos(1f)}  two = ${getXPos(2f)}  three = ${getXPos(3f)}   lwd = $lwd ")


        var cntApprox = 0
        dataPoints.forEachIndexed { i, p ->
            // Рисуем точку

                val payment = p.payment

                val index = i+ 1


                val bottom = getBottomLine() - 3

                val x = getXPos(index.toFloat())
                val y = getYPos(payment.summa.toFloat())

                val rect = RectF(x - lwd, y, x + lwd, bottom)

                if (payment.done == 1) {
                    path.addRect(rect, CW)
                } else {
                    pathFuture.addRect(rect, CW)
                }

                // x point
                if ( i != dataPoints.size) {
                    pathXPoint.moveTo(x, getBottomLine() - 5)
                    pathXPoint.lineTo(x, getBottomLine() + 5)

                    if (isNeedDrawXPoints) {
                        year = Date(payment.date).year
                        if (year != prevYear
                            && ((i == 1) || Date(payment.date).month == 0)) {

                            canvas.drawText(formatDate(payment.date, "yyyy г."), x,
                                getBottomLine() + 30,
                                paintText)

                            prevYear = Date(payment.date).year
                        }
                    }
                }

//                if (isNeedDrawValues && (i % 5 == 0 )) {
//
//                    val textPoint = (graphicListData[i].summa / 1000).toInt()
//
//                    canvas.drawText("${textPoint}k", x - 30,
//                    y - 30,
//                    paintText)
//                }

        }

        paint.color = addedLineColor
        canvas.drawPath(pathAddedLines, paintXPoint)

        paint.color = ContextCompat.getColor(context, R.color.diagram_coordinates_line_color)
        canvas.drawPath(pathXPoint, paint)

        paint.isAntiAlias = true
        paint.setShadowLayer(4f, 2f, 2f, ContextCompat.getColor(context, R.color.color_text))

        paint.color = lineColor

        canvas.drawPath(path, paint)

        canvas.drawPath(pathFuture, paintFuture)
    }



    fun drawRestChart(canvas: Canvas, isNeedDrawValues: Boolean = true) {

        maxDataValue = maxRest // dataPoints.maxBy { it.targetPayment.rest }?.targetPayment?.rest?.toFloat() ?: 0f

//        Log.d("DYN", "-2- maxDataValue = $maxDataValue")

        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.color = ContextCompat.getColor(context, R.color.diagram_line_color_2)

        val paintText = Paint()
        paintText.style = STROKE
        paintText.color = ContextCompat.getColor(context, R.color.TaskItemToolbar)
        paintText.textSize = 30f

        // График платежей
        val path = Path()

        var pointSum = maxDataValue

        var x = getXPos(0f)
        var y = getYPos(pointSum)
        path.moveTo(x, y)

        dataPoints.forEachIndexed { i, p ->
            // Рисуем точку

            val payment = p.payment

            val index = i + 1

            val bottom = getBottomLine() - 3

           // var pointSum = (payment.rest - payment.summa_credit).toFloat()

            var pointSum = (payment.rest).toFloat()

            //pointSum -= payment.summa_credit.toFloat()

//            Log.d("DYN", "x=$index y=$pointSum")

            x = getXPos(index.toFloat())
            y = getYPos(pointSum)

            if (i == 0 ) {

            } else {
                path.lineTo(x, y)

               // path.addCircle(x, y, 3f, CW)
            }

            if (isNeedDrawValues && (i % 5 == 0 )) {
                val textPoint = (pointSum / 1000).toInt()

                canvas.drawText("${textPoint}k", x - 30,
                    y - 30,
                    paintText)
            }

        }

        paint.isAntiAlias = true
        paint.setShadowLayer(4f, 2f, 2f, ContextCompat.getColor(context, R.color.color_text))

        canvas.drawPath(path, paint)
    }
}