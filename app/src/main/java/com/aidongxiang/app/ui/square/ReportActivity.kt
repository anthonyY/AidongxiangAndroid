package com.aidongxiang.app.ui.square

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayout
import android.util.TypedValue
import android.widget.CheckBox
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import kotlinx.android.synthetic.main.activity_report.*

/**
 * 举报
 */
@ContentView(R.layout.activity_report)
class ReportActivity : BaseKtActivity() {

    override fun init(savedInstanceState: Bundle?) {
        title = "举报"

        gridview_report.removeAllViews()
        for (i in 0..8){

            val view = CheckBox(this)
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            view.setTextColor(ContextCompat.getColor(this, R.color.black3))
            view.text = "内容啊${i}实打实大师的"
            val param = GridLayout.LayoutParams()
            param.columnSpec = GridLayout.spec(i%2, 1)
            view.layoutParams = param
            gridview_report.addView(view, param)

        }
    }

}
