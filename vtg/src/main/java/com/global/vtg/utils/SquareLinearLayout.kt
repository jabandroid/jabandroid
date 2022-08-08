package com.global.vtg.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.vtg.R


class SquareLinearLayout : LinearLayout {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val widthDesc = MeasureSpec.getMode(widthMeasureSpec)
        val heightDesc = MeasureSpec.getMode(heightMeasureSpec)
        var size = 0
        size = if (widthDesc == MeasureSpec.UNSPECIFIED
            && heightDesc == MeasureSpec.UNSPECIFIED
        ) {
            context.resources.getDimensionPixelSize(R.dimen.default_size) // Use your own default size, for example 125dp
        } else if ((widthDesc == MeasureSpec.UNSPECIFIED || heightDesc == MeasureSpec.UNSPECIFIED)
            && !(widthDesc == MeasureSpec.UNSPECIFIED && heightDesc == MeasureSpec.UNSPECIFIED)
        ) {
            //Only one of the dimensions has been specified so we choose the dimension that has a value (in the case of unspecified, the value assigned is 0)
            if (width > height) width else height
        } else {
            //In all other cases both dimensions have been specified so we choose the smaller of the two
            if (width > height) height else width
        }
        setMeasuredDimension(size, size)
    }
}