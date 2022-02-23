package com.global.vtg.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.widget.NestedScrollView;

public class CustomScrollView extends NestedScrollView {

public CustomScrollView(Context context) {
    super(context);
}

public CustomScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
}

public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
}

@Override
public boolean onInterceptTouchEvent(MotionEvent ev) {
    final int action = ev.getAction();
    switch (action) {
        case MotionEvent.ACTION_DOWN:
            super.onTouchEvent(ev);
            break;

        case MotionEvent.ACTION_MOVE:
            return false;

        case MotionEvent.ACTION_CANCEL:
            super.onTouchEvent(ev);
            break;

        case MotionEvent.ACTION_UP:
            return false;

        default:
            break;
    }

    return false;
}

@Override
public boolean onTouchEvent(MotionEvent ev) {
    super.onTouchEvent(ev);
    return true;
  }
}