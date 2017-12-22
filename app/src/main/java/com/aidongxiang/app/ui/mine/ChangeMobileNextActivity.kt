package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.utils.SmscodeCountDown
import kotlinx.android.synthetic.main.activity_change_mobile_next.*

/**
 * 更改手机下一步
 * @author Anthony
 * createTime 2017-11-24
 */
@ContentView(R.layout.activity_change_mobile_next)
class ChangeMobileNextActivity : BaseKtActivity() {

    lateinit var smscodeCountDown : SmscodeCountDown
    override fun init(savedInstanceState: Bundle?) {

        title = "修改手机"
        smscodeCountDown = SmscodeCountDown(60*1000, 1000)
        smscodeCountDown.setSmscodeBtn(btnSmscode)
        etSmscode.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                btnNext.isEnabled = !TextUtils.isEmpty(p0.toString())
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

        btnNext.setOnClickListener {
            if(TextUtils.isEmpty(etSmscode.text.toString().trim())){
                toast("请输入验证码")
                return@setOnClickListener
            }
        }
    }

}