package com.example.dmstaskmanager.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.DiagramItem
import com.example.dmstaskmanager.utils.Representer
import com.example.dmstaskmanager.utils.formatD0
import com.example.dmstaskmanager.view.dynamic.DynamicsCredit

class CreditTableView : View {

    private var dataPoint : DynamicsCredit? = null

    var lineColor : Int = 0
    var textColor : Int = 0

    var textSize = 50f
    val xOffset = 20f
    val yOffset = 20f

    private val animator = object: Runnable {
        override fun run() {

            var scheduleNewFrame = false
            val now = AnimationUtils.currentAnimationTimeMillis()

            dataPoint?.also { dataPoint ->
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

        textSize = attributes.getDimension(R.styleable.StrokeTextView_textSize, 0f)

        attributes.recycle()
    }


    fun setDiagramData(diagramItem: DiagramItem) {
        diagramItem.creditTotals?.also { creditTotals ->
            val now = AnimationUtils.currentAnimationTimeMillis()

            val dyn = DynamicsCredit(creditTotals, creditTotals)

            dataPoint = dyn

            removeCallbacks(animator)
            post(animator)
        }
    }

    private fun drawTable(canvas: Canvas) {
        val left = paddingLeft.toFloat() + xOffset
        val top = paddingTop.toFloat() + yOffset
        val right = (width - paddingRight).toFloat() - xOffset
        val bottom = (height - paddingBottom).toFloat() - yOffset


        val paint = Paint()
        paint.style = STROKE
        paint.strokeWidth = 4f
        paint.color = lineColor

        val paintText = Paint()
        paintText.style = STROKE
        paintText.color = textColor
        paintText.textSize = textSize
        paintText.textAlign = Paint.Align.CENTER

        val paintRest = Paint()
        paintRest.style = STROKE
        paintRest.color = textColor // ContextCompat.getColor(context, R.color.diagram_table_rest_text_color)
        paintRest.textSize = textSize + 60f
        paintRest.textAlign = Paint.Align.CENTER

        val paintLabel = Paint()
        paintLabel.style = STROKE
        paintLabel.color = ContextCompat.getColor(context, R.color.diagram_table_text_lavel_color)
        paintLabel.textSize = 30f
        paintLabel.textAlign = Paint.Align.CENTER

        // Таблица
        val columnWidth = ( right - left ) / 2
        val columnHeight = ( bottom - top ) / 3

        val centerRow = (bottom + top) / 2

        val centerColumnN1 = left + columnWidth / 2
        val centerColumnN2 = (right + left) / 2
        val centerColumnN3 = right - columnWidth / 2

        val rowOffset = 20f

        val lineRowN2 = top + columnHeight - rowOffset
        val lineRowN3 = bottom - columnHeight + rowOffset

        // Text
        var centerRowN1Text = (top + lineRowN2) / 2
        var centerRowN2Text = (top + bottom) / 2
        var centerRowN3Text = (lineRowN3 + bottom) / 2

        centerRowN1Text += paintText.textSize / 2
        centerRowN2Text += paintRest.textSize / 2
        centerRowN3Text += paintText.textSize / 2

        val centerRowN1Label = (top + centerRowN1Text - paintText.textSize / 2) / 2
        val centerRowN2Label = (lineRowN2 + centerRowN2Text - paintRest.textSize / 2) / 2
        val centerRowN3Label = (lineRowN3 + centerRowN3Text - paintText.textSize / 2) / 2

        //
        //  ---------------------
        //    Кредит |  Выпл.
        //  ---------------------
        //  |      ОСТАТОК      |
        //  ---------------------
        //    Проц   |  Фин.рез
        //  ---------------------
        //

        //// Горизонтальные линии (Строки)

        val path = Path()
        path.moveTo(left, top)
        path.lineTo(right, top)

        path.moveTo(left, lineRowN2)
        path.lineTo(right, lineRowN2 )

        path.moveTo(left, lineRowN3)
        path.lineTo(right, lineRowN3)

        path.moveTo(left, bottom)
        path.lineTo(right, bottom)


        //// Вертикальные линии (Столбцы)

        path.moveTo(centerColumnN2, top)
        path.lineTo(centerColumnN2, lineRowN2)

//        path.moveTo(left, lineRowN2)
//        path.lineTo(left, lineRowN3)
//
//        path.moveTo(right, lineRowN2)
//        path.lineTo(right, lineRowN3)

        path.moveTo(centerColumnN2, lineRowN3)
        path.lineTo(centerColumnN2, bottom)


        canvas.drawPath(path, paint)

        dataPoint?.also { dataPoint ->
            paintLabel.alpha = dataPoint.alpha
            paintText.alpha = dataPoint.alpha
            paintRest.alpha = dataPoint.alpha

            /////////////////////////////////////////////////////////
            if(true) // Test reason
            {

                paint.color = ContextCompat.getColor(context, R.color.color_credit_close)

                path.reset()

//                path.moveTo(left, centerRow)
//                path.lineTo(right, centerRow)
//
//                path.moveTo(centerColumnN1, top)
//                path.lineTo(centerColumnN1, bottom)
//
//                path.moveTo(centerColumnN2, top)
//                path.lineTo(centerColumnN2, bottom)
//
//                path.moveTo(centerColumnN3, top)
//                path.lineTo(centerColumnN3, bottom)

                canvas.drawPath(path, paint)

            }
            /////////////////////////////////////////////////////////////

            canvas.drawText("Сумма кредита", centerColumnN1, centerRowN1Label,  paintLabel)
            canvas.drawText(formatD0(dataPoint.creditTotals.credit.summa), centerColumnN1, centerRowN1Text,  paintText)

            canvas.drawText("Выплачено",   centerColumnN3, centerRowN1Label, paintLabel)
            canvas.drawText(formatD0(dataPoint.creditTotals.fact.summa_credit), centerColumnN3, centerRowN1Text,  paintText)

            canvas.drawText("Остаток", centerColumnN2, centerRowN2Label,  paintLabel)
            canvas.drawText(formatD0(dataPoint.creditTotals.getActualRest()), centerColumnN2, centerRowN2Text,  paintRest)

            canvas.drawText("Проценты",  centerColumnN1, centerRowN3Label,  paintLabel)
            canvas.drawText(formatD0(dataPoint.creditTotals.fact.summa_procent), centerColumnN1, centerRowN3Text,  paintText)

            canvas.drawText("Финансовый результат",  centerColumnN3, centerRowN3Label,  paintLabel)
            canvas.drawText(Representer.financeResult(dataPoint.creditTotals.getFinanceResult()), centerColumnN3, centerRowN3Text,  paintText)

        }

    }

    override fun onDraw(canvas: Canvas) {
        drawTable(canvas)
    }

}