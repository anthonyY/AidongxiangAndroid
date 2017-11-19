package com.aidongxiang.app.ui.login

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.widgets.CommonDialog
import kotlinx.android.synthetic.main.activity_set_new_password.*

/**
 * 忘记密码 - 设置新密码
 * @author Anthony
 * createTime 2017-11-19
 */
@ContentView(R.layout.activity_set_new_password)
class SetNewPasswordActivity : BaseKtActivity(), TextWatcher {

    lateinit var successDialog : CommonDialog
    /**
     * 输入内容变化监听，如果有内容，按钮就可以点击，否则不可点击
     */
    override fun afterTextChanged(p0: Editable?) {
        btnFinish.isEnabled = !TextUtils.isEmpty(etPassword.text.toString()) && !TextUtils.isEmpty(etPasswordAgain.text.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun init(savedInstanceState: Bundle?) {
        title = "忘记密码"
        btnFinish.setOnClickListener {
            if(TextUtils.isEmpty(etPassword.text.toString())){
                toast("请输入密码")
                return@setOnClickListener
            }
            if(etPassword.text.toString().length < 6){
                toast("密码不能小于6位数")
                return@setOnClickListener
            }
            if(etPassword.text.toString().length > 16){
                toast("密码不能大于16位数")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(etPasswordAgain.text.toString())){
                toast("请再次输入密码")
                return@setOnClickListener
            }
            if(etPasswordAgain.text.toString() != etPassword.text.toString()){
                toast("两次你们输入不一致，请重新输入")
                etPassword.setText("")
                etPasswordAgain.setText("")
                return@setOnClickListener
            }
            successDialog.show()
        }
        etPassword.addTextChangedListener(this)
        etPasswordAgain.addTextChangedListener(this)
        successDialog = CommonDialog(this)
        successDialog.setTitle("密码修改成功，请重新登录")
        successDialog.setContent(null)
        successDialog.view.findViewById<TextView>(R.id.tv_dialog_cancel).visibility = View.GONE
        successDialog.view.findViewById<View>(R.id.lineDialog).visibility = View.GONE
        successDialog.view.findViewById<TextView>(R.id.tv_dialog_confirm).setTextColor(ContextCompat.getColor(this, R.color.purple))
        successDialog.setOnConfirmClickListener {
            successDialog.dismiss()
            setResult(Activity.RESULT_OK)
            finish()
        }

    }


}