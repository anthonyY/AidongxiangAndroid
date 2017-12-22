package com.aidongxiang.app.ui.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.Constants
import com.aidongxiang.business.response.UserDetailsResponseQuery
import com.aiitec.openapi.cache.AiiFileCache
import com.aiitec.openapi.constant.AIIConstant
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.PacketUtil

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome2)
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
