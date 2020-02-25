package com.atk.app.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.*
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.constraintlayout.widget.ConstraintLayout

const val TAG = "ADAD"

class ExpandableView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    fun expand() {

        val matchParentMeasureSpec = makeMeasureSpec(this@ExpandableView.width, EXACTLY)
        val wrapContentMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
        this@ExpandableView.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = this@ExpandableView.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
//        if(layoutParams.height<targetHeight){
        this@ExpandableView.layoutParams.height = 1
        this@ExpandableView.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                this@ExpandableView.layoutParams.height = if (interpolatedTime == 1f)
                    LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                this@ExpandableView.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration =
            (targetHeight / this@ExpandableView.context.resources.displayMetrics.density * 4).toLong()
        this@ExpandableView.startAnimation(a)
    }

    fun collapse() {
        val initialHeight = this.measuredHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    this@ExpandableView.visibility = View.INVISIBLE
                } else {
                    this@ExpandableView.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    this@ExpandableView.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.duration =
            (initialHeight / this@ExpandableView.context.resources.displayMetrics.density * 4).toLong()
        this@ExpandableView.startAnimation(a)
    }
}