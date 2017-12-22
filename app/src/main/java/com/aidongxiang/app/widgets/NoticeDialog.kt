package com.aidongxiang.app.widgets

import android.content.Context
import android.view.View
import android.view.WindowManager
import com.aidongxiang.app.R
import com.aiitec.openapi.utils.ScreenUtils
import kotlinx.android.synthetic.main.dialog_notice.*

/**
 *
 * @author Anthony
 * createTime 2017/12/10.
 * @version 1.0
 */
class NoticeDialog(context: Context) : AbsCommonDialog(context){
    override fun widthScale(): Float =  1f

    override fun layoutId(): Int = R.layout.dialog_notice

    override fun findView(view: View?) {
        super.findView(view)
        view?.setOnClickListener { dismiss() }
    }

    override fun setLayoutParams(lp: WindowManager.LayoutParams?): WindowManager.LayoutParams {
        lp?.height = (ScreenUtils.getScreenHeight(context) * 0.8).toInt() // 设置宽度
        return super.setLayoutParams(lp)
    }
}