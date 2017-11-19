package com.aidongxiang.app.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.utils.SmscodeCountDown
import kotlinx.android.synthetic.main.activity_forget_password.*

/**
 * 忘记密码
 * @author Anthony
 * createTime 2017-11-19
 */
@ContentView(R.layout.activity_forget_password)
class ForgetPasswordActivity : BaseKtActivity(), TextWatcher {

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
            switchToActivityForResult(SetNewPasswordActivity::class.java, 1)
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
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            finish()
        }
    }

}
