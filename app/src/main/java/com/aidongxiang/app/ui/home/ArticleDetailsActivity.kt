package com.aidongxiang.app.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.*
import com.aidongxiang.app.webview.BaseJavascriptInterface
import com.aidongxiang.business.response.ArticleDetailsResponseQuery
import com.aiitec.openapi.enums.CacheMode
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.widgets.ShareDialog
import kotlinx.android.synthetic.main.activity_common_web_view.*

@ContentView(R.layout.activity_common_web_view)
class ArticleDetailsActivity : BaseKtActivity() {

    var id : Long = -1
    var action = 1
    lateinit var shareDialog : ShareDialog
    @SuppressLint("SetJavaScriptEnabled")
    override fun init(savedInstanceState: Bundle?) {

        id = bundle.getLong(Constants.ARG_ID)
        action = bundle.getInt(Constants.ARG_ACTION)
        val title = bundle.getString(Constants.ARG_TITLE)
        val abstract = bundle.getString(Constants.ARG_ABSTRACT)
        var imagePath = bundle.getString(Constants.ARG_IMAGE_PATH)

        if (!TextUtils.isEmpty( title)) {
            setTitle(Html.fromHtml(title))
            toolbar?.visibility = View.VISIBLE
        } else {
            toolbar?.visibility = View.GONE
        }
//        LogUtil.i("url: " + url)
//        webview.loadUrl(url)
        webview.webViewClient = BaseWebviewClient(this, true)
        webview.addJavascriptInterface(BaseJavascriptInterface(this), "AndroidJsInterface")

        val settings = webview.settings
        settings.javaScriptEnabled = true

        if (Build.VERSION.SDK_INT >= 21) {//兼容https和http的图片
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        shareDialog = ShareDialog(this)
        if(!TextUtils.isEmpty(imagePath)){
            imagePath = Api.IMAGE_URL+imagePath
        }
        shareDialog.setShareData(title, abstract, imagePath, "http://www.aidongxiang.com/download/download.html?nid="+id)
        requestArticleDetails()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(action != 2 && action != 3){
            menuInflater.inflate(R.menu.menu_share, menu)
        }

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


    private fun requestArticleDetails(){

        val query = RequestQuery("ArticleDetails")
        query.cacheMode = CacheMode.PRIORITY_OFTEN
        query.id = id
        query.action = AIIAction.valueOf(action)
        App.aiiRequest.send(query, object : AIIResponse<ArticleDetailsResponseQuery>(this){

            override fun onSuccess(response: ArticleDetailsResponseQuery, index: Int) {
                super.onSuccess(response, index)
                response.article?.let {
                    if (!TextUtils.isEmpty( it.title)) {
                        setTitle(Html.fromHtml(it.title))
                        toolbar?.visibility = View.VISIBLE
                    } else {
                        toolbar?.visibility = View.GONE
                    }

                    webview.loadDataWithBaseURL(Api.BASE_URL, getAbsSource(it.content), "text/html", "utf-8", null)

//                    webview.loadData(it.content,"text/html; charset=UTF-8", null)
                }
            }
        })
    }

    override fun onBackPressed() {
        if(webview.canGoBack()){
            webview.goBack()
        } else {
            super.onBackPressed()
        }
    }

    //替换body里面的图片路径问题
    fun getAbsSource(source: String?): String? {
        return source?.replace("<img", "<img style='width:100%;height:auto'")
    }

}