package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.ui.login.MobileAppealActivity
import com.aidongxiang.app.utils.SmscodeCountDown
import kotlinx.android.synthetic.main.activity_change_mobile.*

/**
 * 修改手机
 * @author Anthony
 * createTime 2017-11-24
 *
 */
@ContentView(R.layout.activity_change_mobile)
class ChangeMobileActivity : BaseKtActivity() {

    lateinit var smscodeCountDown : SmscodeCountDown
    override fun init(savedInstanceState: Bundle?) {
        smscodeCountDown = SmscodeCountDown(60*1000, 1000)
        smscodeCountDown?.setSmscodeBtn(btnSmscode)
        etSmscode.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                btnNext.isEnabled = !TextUtils.isEmpty(p0.toString())
            }
        })

        btnNext.setOnClickListener {
            if(TextUtils.isEmpty(etSmscode.text.toString().trim())){
                toast("请输入验证码")
                return@setOnClickListener
            }
            switchToActivity(ChangeMobileNextActivity::class.java)
        }
        tvMobileCannotUse.setOnClickListener {
            switchToActivity(MobileAppealActivity::class.java)
        }
        btnSmscode.setOnClickListener {
            smscodeCountDown.start()
        }
    }

    fun cancelCountDown(){
        smscodeCountDown.cancel()
        smscodeCountDown = SmscodeCountDown(1000, 60*1000)
        smscodeCountDown.setSmscodeBtn(btnSmscode)
    }

}
