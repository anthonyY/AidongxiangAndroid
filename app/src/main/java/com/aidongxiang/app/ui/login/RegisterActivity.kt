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
import com.aidongxiang.app.base.Constants.ARG_ACTION
import com.aidongxiang.app.base.Constants.ARG_MOBILE
import com.aidongxiang.app.base.Constants.ARG_SMSCODE_ID
import com.aidongxiang.app.base.Constants.ARG_TITLE
import com.aidongxiang.app.ui.home.ArticleDetailsActivity
import com.aidongxiang.app.utils.SmscodeCountDown
import com.aidongxiang.business.model.Where
import com.aidongxiang.business.response.SMSResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.activity_register.*

/**
 * 注册
 * @author Anthony
 * createTime 2017-11-19
 * @version 1.0
 */
@ContentView(R.layout.activity_register)
class RegisterActivity : BaseKtActivity(), TextWatcher {

    var smscodeId = -1
    var smscodeCountDown : SmscodeCountDown ?= null
    /**
     * 输入内容变化监听，如果有内容，按钮就可以点击，否则不可点击
     */
    override fun afterTextChanged(p0: Editable?) {
        btnNext.isEnabled = !TextUtils.isEmpty(etMobile.text.toString()) && !TextUtils.isEmpty(etSmscode.text.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun init(savedInstanceState: Bundle?) {
        title = "注册"

        smscodeCountDown = SmscodeCountDown(60*1000, 1000)
        smscodeCountDown?.setSmscodeBtn(btnSmscode)
        setListener()
    }

    private fun setListener(){
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
//            if(!cbRegisterAgree.isChecked){
//                toast("请勾选同意按钮")
//                return@setOnClickListener
//            }
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

        tvAgreement.setOnClickListener {
            switchToActivity(ArticleDetailsActivity::class.java, ARG_ACTION to 2, ARG_TITLE to "爱侗乡使用条款")
        }
        tvPrivacyPolicy.setOnClickListener {
            switchToActivity(ArticleDetailsActivity::class.java, ARG_ACTION to 3, ARG_TITLE to "隐私保护政策")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            finish()
        }
    }


    fun cancelCountDown(){
        smscodeCountDown?.cancel()
        smscodeCountDown = SmscodeCountDown(1000, 60*1000)
        smscodeCountDown?.setSmscodeBtn(btnSmscode)
        btnSmscode.text = "获取验证码"
    }

    fun requestSMSCode(action : Int){
        if(etMobile.text.toString().isEmpty()){
            toast("请输入手机号")
            return
        }
        val query = SubmitRequestQuery()
        query.namespace = "SMSCode"
        query.action = AIIAction.valueOf(action)
        query.type = 1
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
        App.aiiRequest.send(query, object : AIIResponse<SMSResponseQuery>(this){

            override fun onSuccess(response: SMSResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let {
                    if(index == 1) {
                        smscodeId = it.id.toInt()
                        it.code?.let { etSmscode.setText(it) }

                    } else if(index == 2){
                        switchToActivityForResult(PerfectDataActivity::class.java, 1, ARG_MOBILE to etMobile.text.toString(), ARG_SMSCODE_ID to smscodeId)
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
}
