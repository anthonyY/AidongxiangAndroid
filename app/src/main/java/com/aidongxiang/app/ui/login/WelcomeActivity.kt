package com.aidongxiang.app.ui.login

import android.os.Bundle
import android.os.Handler
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.ui.Main2Activity
import com.aidongxiang.business.response.UserDetailsResponseQuery
import com.aiitec.openapi.cache.AiiFileCache
import com.aiitec.openapi.constant.AIIConstant
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.AiiUtil
import com.aiitec.openapi.utils.PacketUtil

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
                    switchToActivity(LoginActivity::class.java)
                }
            }
            finish()
        },2000)
//        requestUserDetails()
    }


    fun requestUserDetails(){

        App.aiiRequest?.send(RequestQuery("UserDetails"), object : AIIResponse<UserDetailsResponseQuery>(this) {
            override fun onSuccess(response: UserDetailsResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                Constants.user = response?.user
                response?.user?.let { AIIConstant.USER_ID = it.id }
                AiiFileCache.changeDir(PacketUtil.getCacheDir(this@WelcomeActivity))
            }
        })
    }
}
