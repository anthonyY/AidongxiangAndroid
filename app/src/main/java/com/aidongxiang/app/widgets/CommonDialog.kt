package com.aidongxiang.app.widgets

import android.content.Context
import com.aidongxiang.app.R

/**
 *
 * @author Anthony
 * createTime 2017/11/19.
 * @version 1.0
 */
class CommonDialog(context : Context) : AbsCommonDialog(context){
    override fun widthScale(): Float = 0.7f

    override fun layoutId(): Int = R.layout.dialog_common

    override fun animStyle(): Int = R.style.dialogAnimationStyle

}