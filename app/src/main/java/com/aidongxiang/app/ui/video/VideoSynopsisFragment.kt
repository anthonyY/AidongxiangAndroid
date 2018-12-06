package com.aidongxiang.app.ui.video

import android.annotation.SuppressLint
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.*
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.app.utils.Utils
import com.aidongxiang.app.webview.BaseJavascriptInterface
import com.aidongxiang.app.webview.BaseJavascriptInterface.Companion.jsInterfaceObjectName
import com.aidongxiang.app.widgets.CommonDialog
import com.aidongxiang.business.model.Audio
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.Download
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.net.download.DownloadManager
import com.aiitec.openapi.utils.ToastUtil
import com.aiitec.widgets.ShareDialog
import kotlinx.android.synthetic.main.fragment_video_synopsis.*
import java.io.File

/**
 *
 * @author Anthony
 * createTime 2017/12/7.
 * @version 1.0
 */
@ContentView(R.layout.fragment_video_synopsis)
class VideoSynopsisFragment : BaseFragment(){

    var video: Audio  ?= null
    var id : Long  = -1
    lateinit var downloadCofirmDialog : CommonDialog
    lateinit var shareDialog : ShareDialog

    override fun initView(view: View?) {

        initDialog()
        setListener()
        initWebview()
    }

    private fun initDialog() {

        downloadCofirmDialog = CommonDialog(activity!!)
        downloadCofirmDialog.setTitle("下载视频")
        downloadCofirmDialog.setContent("确定下载？")
        downloadCofirmDialog.setOnConfirmClickListener {
            downloadCofirmDialog.dismiss()
            video?.let { download(it) }
        }
        shareDialog = ShareDialog(activity!!)
    }

    private fun setListener() {


        iv_video_share?.setOnClickListener {
            shareDialog.show()
        }
        iv_video_download?.setOnClickListener {
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return@setOnClickListener
            }
            video?.let { downloadCofirmDialog.show() }
        }
        video?.let { setVideoData(it) }
        iv_video_collection.setOnClickListener {
            if(video == null){
                return@setOnClickListener
            }
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return@setOnClickListener
            }
            requestCollection(video!!.isFavorite)
        }

        ll_video_praise.setOnClickListener {
            if(video == null){
                return@setOnClickListener
            }
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return@setOnClickListener
            }
            requestPraiseSwitch(video!!.isPraise)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebview() {

        webview_video_synopsis.settings?.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= 21) {//兼容https和http的图片
            webview_video_synopsis.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webview_video_synopsis.webChromeClient = WebChromeClient()
        webview_video_synopsis.webViewClient = BaseWebviewClient(activity, true)
        webview_video_synopsis.addJavascriptInterface(BaseJavascriptInterface(activity), jsInterfaceObjectName)

    }

    fun update(audio: Audio) {
        this.video = audio
        if(activity == null){
            return
        }
       setVideoData(audio)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setVideoData(audio: Audio) {
        this.video = audio
        id = audio.id
//        val webview = view?.findViewById<WebView>(R.id.webview_video_synopsis)

        val audiosSynopsis = Utils.getAbsSource(audio.description, Api.BASE_URL + "/")
        webview_video_synopsis.loadData(audiosSynopsis,"text/html; charset=UTF-8", null)
        if(audio.isPraise == 2){
            iv_video_praise.setImageResource(R.drawable.common_btn_like_pre)
            tv_video_praise_num.setTextColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryLight))
        } else {
            iv_video_praise.setImageResource(R.drawable.common_btn_like_nor)
            tv_video_praise_num.setTextColor(ContextCompat.getColor(activity!!, R.color.gray7))
        }
        if(audio.isFavorite == 2){
            iv_video_collection.setImageResource(R.drawable.common_btn_collect_pre)
        } else {
            iv_video_collection.setImageResource(R.drawable.common_btn_collect_nor)
        }
        tv_video_praise_num.text = audio.praiseNum.toString()
        tv_video_play_num.text = audio.playNum.toString()
        //收费视频
        if(audio.payType ==2 && audio.price > 0){
            tv_video_price.text = ""
//            tv_video_price.text = "¥"+ AiiUtil.formatString(audio.price)
        } else {
            tv_video_price.text = ""
        }

        tv_video_title.text = audio.name

        val imagePath = audio.imagePath
        val content = audio.name
        downloadCofirmDialog.setContent("确定下载$content？")
        shareDialog.setShareData("爱侗乡有精彩好看的视频哦，快来看看吧！", content, imagePath, "http://www.aidongxiang.com/download/download.html")
    }


    /**
     * 请求收藏
     */
    private fun requestCollection(open : Int) {

        val query = SubmitRequestQuery("FavoritesSwitch")
        query.action = AIIAction.ONE
        query.id = id
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(activity, progressDialog){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                video?.let {
                    if(it.isFavorite == 2){
                        it.isFavorite = 1
                        iv_video_collection.setImageResource(R.drawable.common_btn_collect_nor)
                    } else {
                        it.isFavorite = 2
                        iv_video_collection.setImageResource(R.drawable.common_btn_collect_pre)
                    }
                }
            }
        })
    }

    /**
     * 请求点赞
     */
    private fun requestPraiseSwitch(open : Int) {

        val query = SubmitRequestQuery("PraiseSwitch")
        query.action = AIIAction.ONE
        query.id = id
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(activity, progressDialog){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                video?.let {
                    if(it.isPraise == 2){
                        it.isPraise = 1
                        val praiseNum = it.praiseNum-1
                        it.praiseNum = praiseNum
                        tv_video_praise_num.text = praiseNum.toString()
                        iv_video_praise.setImageResource(R.drawable.common_btn_like_nor)
                        tv_video_praise_num.setTextColor(ContextCompat.getColor(activity!!, R.color.gray7))
                    } else {
                        it.isPraise = 2
                        val praiseNum = it.praiseNum+1
                        it.praiseNum = praiseNum
                        tv_video_praise_num.text = praiseNum.toString()
                        iv_video_praise.setImageResource(R.drawable.common_btn_like_pre)
                        tv_video_praise_num.setTextColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryLight))
                    }
                }
            }
        })
    }

    private fun download(vedio: Audio) {

        var download = App.aiidbManager.findObjectFromId(Download::class.java, vedio.id)
        var fileExists = false
        if (download != null && !TextUtils.isEmpty(download.path)) {
            val file = File(download.path)
            fileExists = file.exists()
        }

        if (download == null || !fileExists) {
            val downloadManager = DownloadManager.getInstance(activity)
            download = Download()
            download.timestamp = System.currentTimeMillis()
            download.id = vedio.id
            download.path = vedio.audioPath
            download.imagePath = vedio.imagePath
            download.title = vedio.name
            download.playLength = vedio.audioLength
            download.type = 2
            downloadManager.download(download)
            ToastUtil.show(activity, "开始下载...")
        } else {
            ToastUtil.show(activity,"视频已经存在")
        }
    }
}