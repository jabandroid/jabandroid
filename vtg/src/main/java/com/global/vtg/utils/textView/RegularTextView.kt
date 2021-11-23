package com.global.vtg.utils.textView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.vtg.R

class RegularTextView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {
        val face = Typeface.createFromAsset(context.assets, "font/FontsFree-Net-proxima_nova_reg.ttf")
        this.typeface = face
        this.setTextColor(ContextCompat.getColor(context, R.color.regular_text_color))
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val face = Typeface.createFromAsset(context.assets, "font/FontsFree-Net-proxima_nova_reg.ttf")
        this.typeface = face
        this.setTextColor(ContextCompat.getColor(context, R.color.regular_text_color))
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        val face = Typeface.createFromAsset(context.assets, "font/FontsFree-Net-proxima_nova_reg.ttf")
        this.typeface = face
        this.setTextColor(ContextCompat.getColor(context, R.color.regular_text_color))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}