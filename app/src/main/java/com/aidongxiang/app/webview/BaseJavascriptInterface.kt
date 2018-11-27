package com.aidongxiang.app.webview

import android.content.Context
import android.text.TextUtils
import com.aidongxiang.app.base.Api
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.ui.square.BigImageActivity
import com.aiitec.openapi.utils.LogUtil

/**
 *
 * @author Anthony
 * createTime 2018-11-24.
 * @version 1.0
 */


open class BaseJavascriptInterface(val context: Context?) {

    companion object {
        val jsInterfaceObjectName = "AndroidJsInterface"
    }
    @android.webkit.JavascriptInterface
    fun openImage(img: String) {
        //以下跳转你自己的大图预览页面即可
        if (context is BaseKtActivity && !TextUtils.isEmpty(img)) {
            val images = ArrayList<String>()
            if (!img.startsWith("http")) {
                images.add(Api.BASE_URL + img)
            } else {
                images.add(img)
            }
            context.switchToActivity(BigImageActivity::class.java, BigImageActivity.ARG_IMAGES to images)
        }
    }

    @android.webkit.JavascriptInterface
    fun log(log : String){
        LogUtil.e("html log:"+log)
    }
}