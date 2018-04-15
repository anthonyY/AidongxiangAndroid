package com.aidongxiang.app.ui.mine

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_SMSCODE_ID
import com.aidongxiang.app.utils.SmscodeCountDown
import com.aidongxiang.business.model.Where
import com.aidongxiang.business.response.SMSResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_change_mobile_next.*

/**
 * 更改手机下一步
 * @author Anthony
 * createTime 2017-11-24
 */
@ContentView(R.layout.activity_change_mobile_next)
class ChangeMobileNextActivity : BaseKtActivity() {

    var mobile : String ?= null
    var smscodeId = -1
    lateinit var smscodeCountDown : SmscodeCountDown
    override fun init(savedInstanceState: Bundle?) {
        mobile = Constants.user?.mobile
        smscodeId = bundle.getInt(ARG_SMSCODE_ID)
        title = "修改手机"
        smscodeCountDown = SmscodeCountDown(60*1000, 1000)
        smscodeCountDown.setSmscodeBtn(btnSmscode)
        etSmscode.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                btnFinish.isEnabled = !TextUtils.isEmpty(p0.toString())
            }
        })

        btnSmscode.setOnClickListener {
            if(TextUtils.isEmpty(etMobile.text.toString().trim())){
                toast("请输入手机号码")
                return@setOnClickListener
            }
            if(etMobile.text.toString().length != 11){
                toast("手机号码长度必须是11位")
                return@setOnClickListener
            }
            smscodeCountDown.start()
            tvChangeMobileSendTo.text = "验证码已发送至${etMobile.text.toString()}手机"
        }

        btnFinish.setOnClickListener {
            if(TextUtils.isEmpty(etSmscode.text.toString().trim())){
                toast("请输入验证码")
                return@setOnClickListener
            }
            requestSMSCode(2)
        }
    }

//

    fun requestSMSCode(action : Int){
        val query = SubmitRequestQuery()
        query.namespace = "SMSCode"
        query.action = AIIAction.valueOf(action)
        query.type = 2
        query.mobile = mobile
        if(action == 2){
            if(etSmscode.text.toString().isEmpty()){
                toast("请输入验证码")
                return
            }
            val where = Where()
            val code = etSmscode.text.toString().toInt()
            where.code = code
            query.where = where
        }
        App.aiiRequest.send(query, object : AIIResponse<SMSResponseQuery>(this){

            override fun onSuccess(response: SMSResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                requestUserUpdateMobile()
            }

        }, action)
    }
    fun requestUserUpdateMobile(){
        val query = SubmitRequestQuery()
        query.namespace = "UserUpdateMobile"
        query.smscodeId = smscodeId
        query.mobile = mobile
        App.aiiRequest.send(query, object : AIIResponse<SMSResponseQuery>(this){

            override fun onSuccess(response: SMSResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                ToastUtil.show(this@ChangeMobileNextActivity, "修改完成")
                setResult(Activity.RESULT_OK)
                dismissDialog()
                finish()
            }

        })
    }

}