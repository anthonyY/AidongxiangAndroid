package com.aidongxiang.app.widgets

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.aidongxiang.app.R
import kotlinx.android.synthetic.main.dialog_date_select.*
import java.util.*

/**
 *
 * @author Anthony
 * createTime 2017/12/10.
 * @version 1.0
 */
class DateSelectDialog(context : Context) : AbsCommonDialog(context){
    override fun widthScale(): Float = 1f
    override fun animStyle(): Int = R.style.BottomAnimationStyle

    private var startYear: Int = 0
    private var endYear: Int = 0
    override fun layoutId(): Int = R.layout.dialog_date_select


    override fun findView(view: View) {
        super.findView(view)
//        wheel_date.setMode(WheelDatePicker.MODE_GREGORIAN)
        wheel_date.setCurtain(true)
        wheel_date.isCurved = true
        wheel_date.isCyclic = false

        wheel_date.itemTextColor = ContextCompat.getColor(context, R.color.gray9)
        wheel_date.selectedItemTextColor = ContextCompat.getColor(context, R.color.black3)
        if (startYear > 0 && endYear > 0){
            wheel_date.setYearFrame(startYear, endYear)
        }

    }

    fun setYearFrame(startYear: Int, endYear: Int) {
        this.startYear = startYear
        this.endYear = endYear
        if (wheel_date != null) {
            wheel_date.setYearFrame(startYear, endYear)
        }

    }

    fun getCurrentDate(): Date? {
        return wheel_date?.currentDate
    }

    override fun setLayoutParams(lp: WindowManager.LayoutParams?): WindowManager.LayoutParams {
        lp?.gravity = Gravity.BOTTOM
        return super.setLayoutParams(lp)
    }

}