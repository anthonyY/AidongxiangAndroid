package com.aidongxiang.app.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants.ARG_MOBILE
import com.aidongxiang.app.base.Constants.ARG_SMSCODE_ID
import com.aidongxiang.app.utils.SmscodeCountDown
import com.aidongxiang.business.model.Where
import com.aidongxiang.business.response.SMSResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.activity_forget_password.*

/**
 * 忘记密码
 * @author Anthony
 * createTime 2017-11-19
 */
@ContentView(R.layout.activity_forget_password)
class ForgetPasswordActivity : BaseKtActivity(), TextWatcher {

    var smscodeId = -1
    var smscodeCountDown : SmscodeCountDown?= null
    /**
     * 输入内容变化监听，如果有内容，按钮就可以点击，否则不可点击
     */
    override fun afterTextChanged(p0: Editable?) {
        btnNext.isEnabled = !TextUtils.isEmpty(etMobile.text.toString()) && !TextUtils.isEmpty(etSmscode.text.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun init(savedInstanceState: Bundle?) {
        title = "忘记密码"

        smscodeCountDown = SmscodeCountDown(60*1000, 1000)
        smscodeCountDown?.setSmscodeBtn(btnSmscode)
        setLitener()
    }
    fun setLitener(){
        btnNext.setOnClickListener {
            if(TextUtils.isEmpty(etMobile.text.toString())){
                toast("请输入手机号")
                return@setOnClickListener
            }
            if(etMobile.text.toString().length != 11){
                toast("手机号必须是11位数")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(etSmscode.text.toString())){
                toast("请输入验证码")
                return@setOnClickListener
            }
            requestSMSCode(2)
        }
        etMobile.addTextChangedListener(this)
        etSmscode.addTextChangedListener(this)

        btnSmscode.setOnClickListener {
            if(TextUtils.isEmpty(etMobile.text.toString())){
                toast("请输入手机号")
                return@setOnClickListener
            }
            if(etMobile.text.toString().length != 11){
                toast("手机号必须是11位数")
                return@setOnClickListener
            }
            smscodeCountDown?.start()
            requestSMSCode(1)
        }

        tvMobileCannotUse.setOnClickListener {
            switchToActivity(MobileAppealActivity::class.java)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            finish()
        }
    }


    fun requestSMSCode(action : Int){
        if(etMobile.text.toString().isEmpty()){
            toast("请输入手机号")
            return
        }
        val query = SubmitRequestQuery()
        query.namespace = "SMSCode"
        query.action = AIIAction.valueOf(action)
        query.type = 3
        query.mobile = etMobile.text.toString()
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
        App.aiiRequest.send(query, object : AIIResponse<SMSResponseQuery>(this, progressDialog){

            override fun onSuccess(response: SMSResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let {
                    if(index == 1) {
                        smscodeId = it.id.toInt()
                        it.code?.let { etSmscode.setText(it) }

                    } else if(index == 2){
                        switchToActivityForResult(SetNewPasswordActivity::class.java, 1, ARG_SMSCODE_ID to smscodeId, ARG_MOBILE to etMobile.text.toString())
                    } else {

                    }
                }

            }

            override fun onFailure(content: String?, index: Int) {
                super.onFailure(content, index)
                cancelCountDown()
            }

        }, action)
    }


    fun cancelCountDown(){
        smscodeCountDown?.cancel()
        smscodeCountDown = SmscodeCountDown(1000, 60*1000)
        smscodeCountDown?.setSmscodeBtn(btnSmscode)
        btnSmscode.text = "获取验证码"
    }
}
