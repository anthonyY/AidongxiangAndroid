package com.aidongxiang.app.ui.login

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.ui.Main2Activity
import com.aidongxiang.business.response.SettingResponseQuery
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.AiiUtil
import com.aiitec.openapi.utils.LogUtil

class WelcomeActivity : BaseKtActivity() {
    override fun init(savedInstanceState: Bundle?) {
        if(intent.data != null){
            intent.data?.let {
                val videoId = it.getQueryParameter("vid")
                val audioId = it.getQueryParameter("aid")
                val newsId = it.getQueryParameter("nid")
                val microblogId = it.getQueryParameter("mid")
                val abstract = it.getQueryParameter("abstract")
                try {
                    if (!TextUtils.isEmpty(videoId)) {
                        val vid = videoId.toLong()
                        AiiUtil.putLong(this, Constants.ARG_VIDEO_ID, vid)
                        AiiUtil.putLong(this, Constants.ARG_AUDIO_ID, -1)
                        AiiUtil.putLong(this, Constants.ARG_NEWS_ID, -1)
                        AiiUtil.putString(this, Constants.ARG_ABSTRACT, null)
                        AiiUtil.putLong(this, Constants.ARG_MICROBLOG_ID, -1)
                    } else if (!TextUtils.isEmpty(audioId)) {
                        val aid = audioId.toLong()
                        AiiUtil.putLong(this, Constants.ARG_VIDEO_ID, -1)
                        AiiUtil.putLong(this, Constants.ARG_AUDIO_ID, aid)
                        AiiUtil.putLong(this, Constants.ARG_NEWS_ID, -1)
                        AiiUtil.putString(this, Constants.ARG_ABSTRACT, null)
                        AiiUtil.putLong(this, Constants.ARG_MICROBLOG_ID, -1)
                    } else if (!TextUtils.isEmpty(newsId)) {
                        val nid = newsId.toLong()
                        AiiUtil.putLong(this, Constants.ARG_NEWS_ID, nid)
                        AiiUtil.putString(this, Constants.ARG_ABSTRACT, abstract)
                        AiiUtil.putLong(this, Constants.ARG_VIDEO_ID, -1)
                        AiiUtil.putLong(this, Constants.ARG_AUDIO_ID, -1)
                        AiiUtil.putLong(this, Constants.ARG_MICROBLOG_ID, -1)
                    } else if (!TextUtils.isEmpty(microblogId)) {
                        val mid = microblogId.toLong()
                        AiiUtil.putLong(this, Constants.ARG_MICROBLOG_ID, mid)
                        AiiUtil.putLong(this, Constants.ARG_NEWS_ID, -1)
                        AiiUtil.putString(this, Constants.ARG_ABSTRACT, abstract)
                        AiiUtil.putLong(this, Constants.ARG_VIDEO_ID, -1)
                        AiiUtil.putLong(this, Constants.ARG_AUDIO_ID, -1)
                    } else {
                        AiiUtil.putLong(this, Constants.ARG_MICROBLOG_ID, -1)
                        AiiUtil.putLong(this, Constants.ARG_NEWS_ID, -1)
                        AiiUtil.putString(this, Constants.ARG_ABSTRACT, null)
                        AiiUtil.putLong(this, Constants.ARG_VIDEO_ID, -1)
                        AiiUtil.putLong(this, Constants.ARG_AUDIO_ID, -1)
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            }
        } else {
            AiiUtil.putLong(this, Constants.ARG_VIDEO_ID, -1)
            AiiUtil.putLong(this, Constants.ARG_AUDIO_ID, -1)
            AiiUtil.putLong(this, Constants.ARG_NEWS_ID, -1)
            AiiUtil.putLong(this, Constants.ARG_MICROBLOG_ID, -1)
        }





        val isFirstLauncher = AiiUtil.getBoolean(this, Constants.ARG_FIRST_LAUNCHER, true)
        Handler().postDelayed({
            if(supportFragmentManager.isDestroyed){
                return@postDelayed
            }
            if(isFirstLauncher){
                switchToActivity(GuideActivity::class.java)
            } else {
//                if(Constants.user != null){
                    switchToActivity(Main2Activity::class.java)
//                } else {
//                    switchToActivity(LoginActivity::class.java, ARG_ACTION to 1)
//                }
            }
            finish()
        },2000)

        val fields = ResponseQuery::class.java.declaredFields
        for(field in fields){
            LogUtil.e(field.name)
        }

        requestUserDetails(false)
        requestSetting()

    }

    private fun requestSetting(){
        val query = RequestQuery("Setting")
        App.aiiRequest.send(query, object : AIIResponse<SettingResponseQuery>(applicationContext, false){

            override fun onSuccess(response: SettingResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                Constants.poster = response?.poster
            }
        })
    }

}
