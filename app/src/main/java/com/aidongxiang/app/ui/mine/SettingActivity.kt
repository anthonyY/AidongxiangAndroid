package com.aidongxiang.app.ui.mine

import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.Api
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_TITLE
import com.aidongxiang.app.base.Constants.ARG_URL
import com.aidongxiang.app.ui.Main2Activity
import com.aidongxiang.app.ui.home.CommonWebViewActivity
import com.aidongxiang.app.utils.VersionCheck
import com.aiitec.openapi.constant.AIIConstant
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.CacheUtils
import com.aiitec.openapi.utils.PacketUtil
import kotlinx.android.synthetic.main.activity_setting.*

/**
 * 设置页
 * createTime 2017-11-24
 */
@ContentView(R.layout.activity_setting)
class SettingActivity : BaseKtActivity() {

    lateinit var versionCheck : VersionCheck
    lateinit var cacheUtils : CacheUtils
    override fun init(savedInstanceState: Bundle?) {

        title  = "设置"
        llAccountSecurity.setOnClickListener{
            switchToActivityForResult(AccountSecurityActivity::class.java, 1)
        }

        llScreen.setOnClickListener { switchToActivity(MyScreenActivity::class.java) }

        btnLogout.setOnClickListener {
            requestLogout()
        }
        llClearCache.setOnClickListener {
            cacheUtils.clearCache()
        }

        ll_about.setOnClickListener {
            switchToActivity(CommonWebViewActivity::class.java, ARG_TITLE to "关于我们", ARG_URL to "http://aidongxiang.com/about.html")
        }
        llVersion.setOnClickListener {
            versionCheck.startCheck(Api.VERSION_URL, true)
        }

        cacheUtils = CacheUtils(this)
        cacheUtils.getCacheSize{_, formatSize ->
            tvCacheSize.text = formatSize
        }
        cacheUtils.setOnClearCacheListener {
            tvCacheSize.text = "0.0k"
        }

        versionCheck = VersionCheck(this)
        tvVersion.text = "V"+PacketUtil.getVersionName(this)
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
