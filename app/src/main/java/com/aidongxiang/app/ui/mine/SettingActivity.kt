package com.aidongxiang.app.ui.mine

import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import kotlinx.android.synthetic.main.activity_setting.*

@ContentView(R.layout.activity_setting)
class SettingActivity : BaseKtActivity() {

    override fun init(savedInstanceState: Bundle?) {

        title  = "设置"
        llAccountSecurity.setOnClickListener{
            switchToActivity(AccountSecurityActivity::class.java)
        }

        llScreen.setOnClickListener { switchToActivity(MyScreenActivity::class.java) }
    }

}
