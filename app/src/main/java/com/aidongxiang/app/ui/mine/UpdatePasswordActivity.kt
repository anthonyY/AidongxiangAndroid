package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
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
        }
        etCurrentPassword.addTextChangedListener(this)
        etNewPassword.addTextChangedListener(this)
        etNewPasswordAgain.addTextChangedListener(this)
    }
}
