package com.aidongxiang.app.ui.mine

import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.ui.Main2Activity
import com.aiitec.openapi.constant.AIIConstant
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.activity_setting.*

@ContentView(R.layout.activity_setting)
class SettingActivity : BaseKtActivity() {

    override fun init(savedInstanceState: Bundle?) {

        title  = "设置"
        llAccountSecurity.setOnClickListener{
            switchToActivity(AccountSecurityActivity::class.java)
        }

        llScreen.setOnClickListener { switchToActivity(MyScreenActivity::class.java) }

        btnLogout.setOnClickListener {
            requestLogout()
        }
    }

    private fun requestLogout() {

        val query = RequestQuery("UserLogout")
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                Constants.user = null
                AIIConstant.USER_ID = -1
                dismissDialog()
                switchToActivity(Main2Activity::class.java)
                App.app.closeAllActivity()
            }
        })
    }

}
