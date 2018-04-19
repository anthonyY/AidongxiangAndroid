package com.aidongxiang.app.ui.login

import android.os.Bundle
import android.os.Handler
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ACTION
import com.aidongxiang.app.ui.Main2Activity
import com.aiitec.openapi.utils.AiiUtil

class WelcomeActivity : BaseKtActivity() {
    override fun init(savedInstanceState: Bundle?) {

        val isFirstLauncher = AiiUtil.getBoolean(this, Constants.ARG_FIRST_LAUNCHER, true)
        Handler().postDelayed({
            if(supportFragmentManager.isDestroyed){
                return@postDelayed
            }
            if(isFirstLauncher){
                switchToActivity(GuideActivity::class.java)
            } else {
                if(Constants.user != null){
                    switchToActivity(Main2Activity::class.java)
                } else {
                    switchToActivity(LoginActivity::class.java, ARG_ACTION to 1)
                }
            }
            finish()
        },2000)


        requestUserDetails(false)

    }

}
