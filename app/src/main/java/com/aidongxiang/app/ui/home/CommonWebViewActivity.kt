package com.aidongxiang.app.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants.ARG_TITLE
import com.aidongxiang.app.base.Constants.ARG_URL
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.widgets.ShareDialog
import kotlinx.android.synthetic.main.activity_common_web_view.*



@ContentView(R.layout.activity_common_web_view)
class CommonWebViewActivity : BaseKtActivity() {

    lateinit var shareDialog : ShareDialog
    @SuppressLint("SetJavaScriptEnabled")
    override fun init(savedInstanceState: Bundle?) {

        val url = bundle.getString(ARG_URL)
        val title = bundle.getString(ARG_TITLE)
        if (!TextUtils.isEmpty(title)) {
            setTitle(title)
            toolbar?.visibility = View.VISIBLE
        } else {
            toolbar?.visibility = View.GONE
        }

        LogUtil.i("url: " + url)
        webview.loadUrl(url)
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url)
                return true
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                view.loadUrl(url)
                return true
                //                return super.shouldOverrideUrlLoading(view, request);
            }
        }
        progressDialogShow()
        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                progressDialog?.setMessage("$progress%")
                if (progress >= 100){
                    progressDialogDismiss()
                }
            }
        }
        val settings = webview.settings
        settings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= 21) {//兼容https和http的图片
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        shareDialog = ShareDialog(this)
        shareDialog.setShareData("爱侗乡新闻", "了飒飒大是", "", "http://aidongxiang.com")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_share, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_share) {
            shareDialog.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        shareDialog.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if(webview.canGoBack()){
            webview.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
