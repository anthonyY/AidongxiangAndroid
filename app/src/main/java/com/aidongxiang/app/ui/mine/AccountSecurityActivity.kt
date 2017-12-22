package com.aidongxiang.app.ui.mine

import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import kotlinx.android.synthetic.main.activity_account_security.*

/**
 * 账号与安全
 * @author Anthony
 * createTime 2017-11-22
 */
@ContentView(R.layout.activity_account_security)
class AccountSecurityActivity : BaseKtActivity() {

    override fun init(savedInstanceState: Bundle?) {
        title = "账号与安全"
        llChangePassword.setOnClickListener{ switchToActivity(UpdatePasswordActivity::class.java) }
        llChangeMobile.setOnClickListener { switchToActivity(ChangeMobileActivity::class.java) }
    }


}
