package com.example.dmstaskmanager.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ScrollView

class HorizontalViewPager: ViewPager {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        gestureDetector = GestureDetector(context, VerticalScrollDetector())
        setFadingEdgeLength(0)
    }

    private var gestureDetector: GestureDetector? = null

    private var xDistance = 0f
    private  var yDistance:kotlin.Float = 0f
    private  var lastX:kotlin.Float = 0f
    private  var lastY:kotlin.Float = 0f

    private var isVerticalScrolling = false

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {


        return false

        if (ev?.action == MotionEvent.ACTION_MOVE) {
            if (gestureDetector!!.onTouchEvent(ev)) {
                return true
            }
        }


        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//            when (ev?.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    xDistance = 0f
//                    yDistance = 0f
//                    lastX = ev.getX();
//                    lastY = ev.getY();
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    val curX = ev.getX();
//                    val curY = ev.getY();
//                    xDistance += Math.abs(curX - lastX);
//                    yDistance += Math.abs(curY - lastY);
//                    lastX = curX;
//                    lastY = curY;
//                    if (xDistance < yDistance)
//                       return true;
//                }
//            }
//
//            return true // super.onInterceptTouchEvent(ev);

        // false - pager

        val res = super.onInterceptTouchEvent(ev)
        Log.d("SCROLL", "ViewPager onInterceptTouchEvent = $res")

        Log.d("SCROLL", "ViewPager gestureDetector = ${gestureDetector!!.onTouchEvent(ev)}")
        return false

        //return super.onInterceptTouchEvent(ev) && gestureDetector!!.onTouchEvent(ev)
    }

   override fun onTouchEvent(ev: MotionEvent?): Boolean {

       val res = super.onTouchEvent(ev)
       Log.d("SCROLL", "ViewPager onTouchEvent = $res")

       Log.d("SCROLL", "ViewPager gestureDetector = ${gestureDetector!!.onTouchEvent(ev)}")

       if (ev?.action == MotionEvent.ACTION_MOVE) {
           if (gestureDetector!!.onTouchEvent(ev)) {
               return false
           }
       }
//
//     //  return true

       return res
   }

//        if (isVerticalScrolling) {
//            return false
//        }
//
//
//            when (ev?.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    xDistance = 0f
//                    yDistance = 0f
//                    lastX = ev.getX();
//                    lastY = ev.getY();
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    val curX = ev.getX();
//                    val curY = ev.getY();
//                    xDistance += Math.abs(curX - lastX);
//                    yDistance += Math.abs(curY - lastY);
//                    lastX = curX;
//                    lastY = curY;
//                    if (xDistance < yDistance) {
//                        Log.d("SCROLL", "FALSE")
//                        isVerticalScrolling = true;
//                        return false
//                    } else {
//                        isVerticalScrolling = true
//                    }
//                }
//                MotionEvent.ACTION_UP -> {
//                    isVerticalScrolling = false
//                }
//            }

//        val isVerticalScrolling = gestureDetector?.onTouchEvent(ev)
//        if (isVerticalScrolling != null && isVerticalScrolling) {
//            //return false
//        }

//      // Log.d("SCROLL", "gestureDetector = ${gestureDetector?.onTouchEvent(ev)}")
//        return super.onTouchEvent(ev) && gestureDetector!!.onTouchEvent(ev)
//    }

    // false - scroll Vert

    // Return false if we're scrolling in the Y direction
    internal class VerticalScrollDetector : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float,
            distanceY: Float): Boolean {

            Log.d("SCROLL", "ViewPager distanceY = $distanceY  distanceX =$distanceX")
            return Math.abs(distanceY) > Math.abs(distanceX);
            //return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

    }

}