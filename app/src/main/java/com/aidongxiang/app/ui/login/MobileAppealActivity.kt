package com.aidongxiang.app.ui.login

import android.os.Bundle
import android.text.TextUtils
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.widgets.DateSelectDialog
import com.aidongxiang.business.request.MobileAppealSubmitRequestQuery
import com.aidongxiang.business.response.SMSResponseQuery
import com.aiitec.openapi.net.AIIResponse
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
        val endYear = calendar.get(Calendar.YEAR)
        val startYear = endYear - 100
        dateSelectDialog = DateSelectDialog(this)
        dateSelectDialog.setYearFrame(startYear, endYear)
        dateSelectDialog.setTitle("选择时间")
        dateSelectDialog.setOnConfirmClickListener{
            val date = dateSelectDialog.getCurrentDate()
            val time = DateUtil.date2Str(date, "yyyy年MM月dd日")
            tvAppealRegisterTime.text = time
            dateSelectDialog.cancel()
        }
        llRegisterTime.setOnClickListener { dateSelectDialog.show() }
        btnAppealSubmit.setOnClickListener {
            requestMobileAppealSubmit()
        }
    }

    /**
     * 请求手机申诉
     */
    private fun requestMobileAppealSubmit(){
        val password = etAppealPassword.text.toString()
        val oldMobile = etAppealOldMobile.text.toString()
        val newMobile = etAppealNewMobile.text.toString()
        val registerTime = tvAppealRegisterTime.text.toString()
        if(TextUtils.isEmpty(oldMobile)){
            toast("请输入原手机号码")
            return
        }
        if(oldMobile.length != 11){
            toast("原手机号码不正确")
            return
        }
        if(TextUtils.isEmpty(newMobile)){
            toast("请输入新手机号码")
            return
        }
        if(newMobile.length != 11){
            toast("新手机号码长度必须是11位")
            return
        }
        if(TextUtils.isEmpty(password)){
            toast("请输入密码")
            return
        }
        if(password.length < 6 || password.length > 16){
            toast("密码长度必须是6-16位")
            return
        }
        if(TextUtils.isEmpty(registerTime)){
            toast("请选择注册日期")
            return
        }
        val query = MobileAppealSubmitRequestQuery()
        query.namespace = "MobileAppealSubmit"
        query.mobile = oldMobile
        query.newMobile = newMobile
        query.password = password
        query.registerTime = registerTime

        App.aiiRequest.send(query, object : AIIResponse<SMSResponseQuery>(this){

            override fun onSuccess(response: SMSResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                toast("提交成功，我们将在一周内为您审核完成！")
                dismissDialog()
                finish()
            }

        })
    }
}
