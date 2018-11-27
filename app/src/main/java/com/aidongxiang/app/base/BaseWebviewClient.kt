package com.aidongxiang.app.base

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.support.annotation.RequiresApi
import android.webkit.*
import com.aidongxiang.app.utils.ADFilterTool

/**
 *
 * @author Anthony
 * createTime 2018-11-24.
 * @version 1.0
 */
open class BaseWebviewClient(val context: Context?, private val isAddImageListener : Boolean = false) : WebViewClient(){



    var homeurl = Api.BASE_URL

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {

        val url = request.url.toString()
        if (url.startsWith("tel:") || url.startsWith("scheme:") || url.startsWith("scheme:")){
            val intent = Intent(Intent.ACTION_VIEW, request.url)
            context?.startActivity(intent)
        } else if(url.startsWith("http:") || url.startsWith("https:")) {
            view.loadUrl(url)
        }
        return true
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        if(isAddImageListener){
            addImageClickListener(view)
        }
        if(context is BaseKtActivity){
            context.progressDialogDismiss()
        }

    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        // handler.cancel();// Android默认的处理方式
        handler.proceed()// 接受所有网站的证书
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        request?.url?.let {
            val url = it.toString().toLowerCase()
            return if (!url.contains(homeurl)) {
                if (!ADFilterTool.hasAd(context, url)) {
                    super.shouldInterceptRequest(view, request)
                } else {
                    WebResourceResponse(null, null, null)
                }
            } else {
                super.shouldInterceptRequest(view, request)
            }
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        url?.let {
            return if (!url.contains(homeurl)) {
                if (!ADFilterTool.hasAd(context, url)) {
                    super.shouldInterceptRequest(view, url)
                } else {
                    WebResourceResponse(null, null, null)
                }
            } else {
                super.shouldInterceptRequest(view, url)
            }
        }
        return super.shouldInterceptRequest(view, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView, httpurl: String): Boolean {
        //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
        if (httpurl.startsWith("http:") || httpurl.startsWith("https:")) {
            view.loadUrl(httpurl)
            return true
        }
        if (httpurl.startsWith("tel:") || httpurl.startsWith("scheme:") || httpurl.startsWith("scheme:")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(httpurl))
            context?.startActivity(intent)
        }

        return true
    }


    private fun addImageClickListener(webview : WebView?) {
        webview?.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +

                "for(var i=0;i<objs.length;i++) " +
                "{"+
                "   var attribute = objs[i].getAttribute('onclick');\n" +
                "   if(attribute != null && attribute != 'undefined'){\n" +
                "       window.AndroidJsInterface.log('onclick attribute is not null  ');"+
                "   } else {\n"+
                "       objs[i].onclick=function() " +
                "       { "+
                "           window.AndroidJsInterface.openImage(this.src); " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                "       } \n" +
                "   }\n" +
                "}" +
                "})()")
    }

    /*private fun addImageClickListener(webview : WebView?) {
        val jsonContent = "javascript:(function(){\n" +
                "   var objs = document.getElementsByTagName(\"img\");\n" +
                "   window.AndroidJsInterface.log('log  '+objs);"  +
                "   for(var i=0;i<objs.length;i++){\n" +
                "       var attribute = objs[i].getAttribute(\"onclick\");\n" +
                "       if(attribute != null && attribute != \"undefined\")\n" +
                "       {\n" +
                "           //已经有点击事件了，所以什么都不做，执行原事件\n" +
                "       } else {\n" +
                "           objs[i].onclick=function(){\n" +
                "               window.AndroidJsInterface.openImage(this.src);\n" +
                "           }"+
                "       }\n" +
                "   }\n" +
                "})()"
        LogUtil.e("jsonContent:"+jsonContent)
        webview?.loadUrl(jsonContent)
    }*/
}