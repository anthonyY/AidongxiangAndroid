package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants.ARG_ACTION
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.business.response.SMSResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.activity_update_password.*

/**
 * 修改密码
 * @author Anthony
 * createTime 2017-11-22
 */
@ContentView(R.layout.activity_update_password)
class UpdatePasswordActivity : BaseKtActivity(), TextWatcher {

    /**
     * 输入内容变化监听，如果有内容，按钮就可以点击，否则不可点击
     */
    override fun afterTextChanged(p0: Editable?) {
        btnFinish.isEnabled = !TextUtils.isEmpty(etCurrentPassword.text.toString()) && !TextUtils.isEmpty(etNewPassword.text.toString()) && !TextUtils.isEmpty(etNewPasswordAgain.text.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun init(savedInstanceState: Bundle?) {

        title = "修改密码"
        btnFinish.setOnClickListener {
            if(TextUtils.isEmpty(etCurrentPassword.text.toString())){
                toast("请输入当前密码")
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(etNewPassword.text.toString())){
                toast("请输入新密码")
                return@setOnClickListener
            }
            if(etNewPassword.text.toString().length < 6){
                toast("密码不能小于6位数")
                return@setOnClickListener
            }
            if(etNewPassword.text.toString().length > 16){
                toast("密码不能大于16位数")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(etNewPasswordAgain.text.toString())){
                toast("请再次输入新密码")
                return@setOnClickListener
            }
            if(etNewPassword.text.toString() != etNewPasswordAgain.text.toString()){
                toast("两次密码输入不一致，请重新输入")
                etNewPasswordAgain.setText("")
                etNewPasswordAgain.setText("")
                return@setOnClickListener
            }
            requestUserUpdatePassword()
        }
        etCurrentPassword.addTextChangedListener(this)
        etNewPassword.addTextChangedListener(this)
        etNewPasswordAgain.addTextChangedListener(this)
    }


    /**
     * 请求更改密码
     */
    private fun requestUserUpdatePassword(){
        val passwordOld = etCurrentPassword.text.toString()
        val passwordNew = etNewPassword.text.toString()

        val query = SubmitRequestQuery()
        query.namespace = "UserUpdatePassword"
        query.password = passwordOld
        query.passwordNew = passwordNew

        App.aiiRequest.send(query, object : AIIResponse<SMSResponseQuery>(this){

            override fun onSuccess(response: SMSResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                toast("修改成功，请重新登录")
                dismissDialog()
                switchToActivity(LoginActivity::class.java, ARG_ACTION to 1)
                App.app.closeAllActivity()
            }

        })
    }
}
