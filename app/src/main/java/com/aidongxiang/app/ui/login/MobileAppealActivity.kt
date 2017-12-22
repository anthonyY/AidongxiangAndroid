package com.aidongxiang.app.ui.login

import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.widgets.DateSelectDialog
import com.aiitec.openapi.utils.DateUtil
import kotlinx.android.synthetic.main.activity_mobile_appeal.*
import java.util.*

/**
 * @author Anthony
 * 手机申诉
 * createTime 2017-12-10
 */
@ContentView(R.layout.activity_mobile_appeal)
class MobileAppealActivity : BaseKtActivity() {

    private lateinit var dateSelectDialog : DateSelectDialog
    override fun init(savedInstanceState: Bundle?) {

        val calendar = Calendar.getInstance()
        val startYear = calendar.get(Calendar.YEAR)
        val endYear = startYear - 100
        dateSelectDialog = DateSelectDialog(this)
        dateSelectDialog.setYearFrame(startYear, endYear)
        dateSelectDialog.setTitle("选择时间")
        dateSelectDialog.setOnConfirmClickListener{
            val date = dateSelectDialog.getCurrentDate()
            val time = DateUtil.date2Str(date, "yyyy年MM月dd日")
            tvRegisterTime.text = time
            dateSelectDialog.cancel()
        }
        llRegisterTime.setOnClickListener { dateSelectDialog.show() }
    }
}
