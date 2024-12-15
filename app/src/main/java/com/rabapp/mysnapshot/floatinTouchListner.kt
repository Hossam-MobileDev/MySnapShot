package com.rabapp.mysnapshot

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager


class FloatingTouchListener(
    private val windowManager: WindowManager,  // The window manager should be passed into the constructor
    private val layoutParams: WindowManager.LayoutParams // The layout params of the floating icon
) : View.OnTouchListener {

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // Store initial positions of the view and the touch event
                initialX = layoutParams.x
                initialY = layoutParams.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // Calculate the new position based on movement
                layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()

                // Update the layout of the floating view as it moves
                windowManager.updateViewLayout(v, layoutParams)
                return true
            }
        }
        return false
    }
}
