package com.aidongxiang.app.utils

/**
 *
 * @author Anthony
 * createTime 2018-11-24.
 * @version 1.0
 */
import android.content.Context
import com.aidongxiang.app.R

/**
 * 广告过滤器， 需要过滤的广告地址请写在 R.array.adBlockUrl 里
 * @author  Anthony
 * @version 1.0
 * createTime 2018/5/22.
 */
object ADFilterTool {
    fun hasAd(context: Context?, url: String): Boolean {
        if(context == null){
            return false
        }
        val res = context.resources
        val adUrls = res.getStringArray(R.array.adBlockUrl)
        return adUrls.any { url.contains(it) }
    }
}
