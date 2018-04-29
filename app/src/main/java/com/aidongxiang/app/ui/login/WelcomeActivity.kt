package com.aidongxiang.app.ui.login

import android.os.Bundle
import android.os.Handler
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ACTION
import com.aidongxiang.app.ui.Main2Activity
import com.aidongxiang.business.response.SettingResponseQuery
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.net.AIIResponse
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
        requestSetting()

    }

    private fun requestSetting(){
        val query = RequestQuery("Setting")
        App.aiiRequest.send(query, object : AIIResponse<SettingResponseQuery>(this, false){

            override fun onSuccess(response: SettingResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                Constants.poster = response?.poster
            }
        })
    }

}
