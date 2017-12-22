package com.aidongxiang.app.widgets

import android.content.Context
import android.view.MotionEvent
import android.support.v4.view.ViewPager
import android.util.AttributeSet


/**
 * 包裹PhotoView 的ViewPager 如果不重写以下内容，容易出现闪退现象
 * @author Anthony
 * createTime 2017/11/25.
 * @version 1.0
 */
class HackyViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            false
        }

    }
}