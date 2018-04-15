package com.aidongxiang.app.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.View
import android.view.WindowManager
import com.aidongxiang.app.R
import com.aiitec.openapi.utils.ScreenUtils
import java.net.URL

/**
 *
 * @author Anthony
 * createTime 2017/12/10.
 * @version 1.0
 */
class NoticeDialog(context: Context) : AbsCommonDialog(context){
    override fun widthScale(): Float =  1f

    override fun layoutId(): Int = R.layout.dialog_notice

    override fun findView(view: View?) {
        super.findView(view)
        view?.setOnClickListener { dismiss() }
    }

    override fun setLayoutParams(lp: WindowManager.LayoutParams?): WindowManager.LayoutParams {
        lp?.height = (ScreenUtils.getScreenHeight(context) * 0.8).toInt() // 设置宽度
        return super.setLayoutParams(lp)
    }

    override fun setContent(content: String?) {
        val charSequence = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, imgGetter, null)
        } else {
            Html.fromHtml(content, imgGetter, null)
        }
        super.setContent(charSequence)
    }

    private var imgGetter: Html.ImageGetter = Html.ImageGetter { source ->
        val drawable: Drawable?
        val url: URL
        try {
            url = URL(source)
            drawable = Drawable.createFromStream(url.openStream(), "")  //获取网路图片
        } catch (e: Exception) {
            return@ImageGetter null
        }
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        return@ImageGetter drawable
    }
}