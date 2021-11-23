package com.global.vtg.utils.textView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet

class SBoldTextView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {
        val face = Typeface.createFromAsset(context.assets, "font/FontsFree-Net-Proxima-Nova-Sbold.otf")
        this.typeface = face
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val face = Typeface.createFromAsset(context.assets, "font/FontsFree-Net-Proxima-Nova-Sbold.otf")
        this.typeface = face
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        val face = Typeface.createFromAsset(context.assets, "font/FontsFree-Net-Proxima-Nova-Sbold.otf")
        this.typeface = face
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}