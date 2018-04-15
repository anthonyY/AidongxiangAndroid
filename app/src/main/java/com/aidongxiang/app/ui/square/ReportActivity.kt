package com.aidongxiang.app.ui.square

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayout
import android.text.TextUtils
import android.util.TypedValue
import android.widget.CheckBox
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants.ARG_ACTION
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.business.request.ReportSubmitRequestQuery
import com.aidongxiang.business.response.CategoryListResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.activity_report.*

/**
 * 举报
 */
@ContentView(R.layout.activity_report)
class ReportActivity : BaseKtActivity() {

    var action = 1
    var id : Long = -1
    var selectCategoryId : Long = -1
    override fun init(savedInstanceState: Bundle?) {
        title = "举报"

        action = bundle.getInt(ARG_ACTION)
        id = bundle.getLong(ARG_ID)
        btnReportSubmit.setOnClickListener {
            requestReportSubmit()
        }
        requestCategoryList()
    }

    private fun requestCategoryList(){
        val query = ListRequestQuery("CategoryList")
        query.action = AIIAction.FOUR
        App.aiiRequest.send(query, object : AIIResponse<CategoryListResponseQuery>(this, progressDialog){
            override fun onSuccess(response: CategoryListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let { getCategoryList(it) }
            }

            override fun onCache(content: CategoryListResponseQuery?, index: Int) {
                super.onCache(content, index)
                content?.let { getCategoryList(it) }
            }
        })
    }


    private fun requestReportSubmit(){
        val content = etReport.text.toString()
        if(TextUtils.isEmpty(content)){
            toast("请输入举报内容")
           return
        }
        val query = ReportSubmitRequestQuery()
        query.action = AIIAction.valueOf(action)
        query.id = id
        query.categoryId = selectCategoryId
        query.content = content
        App.aiiRequest.send(query, object : AIIResponse<CategoryListResponseQuery>(this, progressDialog){
            override fun onSuccess(response: CategoryListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                toast("举报成功")
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
    }

    private fun getCategoryList(response: CategoryListResponseQuery){
        response.categorys?.let {
            gridview_report.removeAllViews()
            for (i in 0 until it.size){
                val catagory = it[i]
                val view = CheckBox(this)
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                view.setTextColor(ContextCompat.getColor(this, R.color.black3))
                view.text = catagory.name
                val param = GridLayout.LayoutParams()
                param.columnSpec = GridLayout.spec(i%2, 1)
                view.layoutParams = param
                if(i == 0){
                    //默认选中第一个
                    view.isSelected = true
                    selectCategoryId = catagory.id
                }
                view.setOnClickListener {
                    view.isSelected = true
                    selectCategoryId = catagory.id
                }
                gridview_report.addView(view, param)

            }
        }
    }
}
