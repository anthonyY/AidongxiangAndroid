package com.aidongxiang.app.ui.login

import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.ui.Main2Activity
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 登录类
 * @author Anthony
 * createTime 2017-11-19
 */
@ContentView(R.layout.activity_login)
class LoginActivity : BaseKtActivity() {

    override fun init(savedInstanceState: Bundle?) {

        tv_login_forget.setOnClickListener { switchToActivity(ForgetPasswordActivity::class.java) }
        tv_login_register.setOnClickListener { switchToActivity(RegisterActivity::class.java) }
        btnLogin.setOnClickListener {
            switchToActivity(Main2Activity::class.java)
        }
    }

}
