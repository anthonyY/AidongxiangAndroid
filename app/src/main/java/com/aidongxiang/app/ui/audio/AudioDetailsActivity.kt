package com.aidongxiang.app.ui.audio

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.widget.SeekBar
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.Api
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.observer.IMusicPlayObserver
import com.aidongxiang.app.observer.MusicPlaySubject
import com.aidongxiang.app.service.MusicService
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.app.utils.Utils
import com.aidongxiang.app.widgets.CommonDialog
import com.aidongxiang.app.widgets.PayDialog
import com.aidongxiang.business.model.Audio
import com.aidongxiang.business.response.AudioDetailsResponseQuery
import com.aiitec.openapi.enums.CacheMode
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.Download
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.net.download.DownloadManager
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.widgets.ShareDialog
import kotlinx.android.synthetic.main.activity_audio_details.*
import java.io.File

/**
 * 音频详情
 * @author Anthony
 * createTime 2017-11-26
 * @version 1.0
 */
@ContentView(R.layout.activity_audio_details)
class AudioDetailsActivity : BaseKtActivity(), IMusicPlayObserver {


    var TYPE_WECHART_PAY = 1
    var TYPE_ALIPAY = 2
    var id : Long = -1
    var audioPath : String ?= null
    private var mediaPlayer : MediaPlayer ?= null
    var isPlaying = false
    var isFirstPlay = true
    var audio: Audio ?= null
    lateinit var payDialog : PayDialog
    lateinit var downloadCofirmDialog : CommonDialog
    lateinit var shareDialog : ShareDialog
    val effectUrl = "http://test.aidongxiang.com/effect/jiaoben4731/index.html"

    override fun init(savedInstanceState: Bundle?) {
        id = bundle.getLong(ARG_ID)

        initDialog()
        setListener()
        initWebview()

//        val random = Random()
//        val ads = ArrayList<Ad>()
//        for(i in 0..5){
//            val ad = Ad()
//            ad.imagePath = HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)]
//            ad.name = "广告"
//            ads.add(ad)
//        }
//
//        adAudioDetails.startAD(ads.size, 4, true, ads)

        requestVideoDetails()
    }

    private fun initDialog() {

        downloadCofirmDialog = CommonDialog(this)
        downloadCofirmDialog.setTitle("下载音频")
        downloadCofirmDialog.setContent("确定下载？")
        downloadCofirmDialog.setOnConfirmClickListener {
            downloadCofirmDialog.dismiss()
            audio?.let { download(it) }
        }

        payDialog = PayDialog(this)
        payDialog.setOnPayListener{ payType->
            audio?.price?.let {
                when(payType){
                    TYPE_WECHART_PAY->{}
                    TYPE_ALIPAY->{ }
                }
            }
        }

        shareDialog = ShareDialog(this)
    }

    private fun setListener(){
        ivAudioDetailsPlay.setOnClickListener {
            if(isFirstPlay){
                startPlay()
                isFirstPlay = false
            } else {
                switchPlayStatus(!isPlaying)
            }
        }

        seekbar_audio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2){
                    val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
                    intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_SEEK)
                    intent.putExtra(MusicService.ARG_SEEK_POSITION, p1)
                    startService(intent)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        MusicPlaySubject.getInstance().registerObserver(this)
        iv_audio_collection.setOnClickListener {
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return@setOnClickListener
            }
            audio?.let {
                requestCollection(it.isFavorite)
            }
        }

        ll_audio_praise.setOnClickListener {
            if(audio == null){
                return@setOnClickListener
            }
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return@setOnClickListener
            }
            requestPraiseSwitch(audio!!.isPraise)
        }

        iv_audio_download.setOnClickListener {
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return@setOnClickListener
            }
            audio?.let { downloadCofirmDialog.show()}
        }

        btn_audio_buy.setOnClickListener {
            audio?.price?.let {
                payDialog.show(it)
            }
        }
        iv_audio_share.setOnClickListener {
            shareDialog.show()
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebview() {

        webview_audio.settings?.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= 21) {//兼容https和http的图片
            webview_audio.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
//        webview_audio.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//                view.loadUrl(url)
//                return true
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
//                val url = request.url.toString()
//                view.loadUrl(url)
//                return true
//            }
//        }
        webview_audio.webChromeClient = WebChromeClient()
        if (Build.VERSION.SDK_INT >= 21) {//兼容https和http的图片
            webview_audio_effect.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        progressDialogShow()
        webview_audio_effect.webChromeClient = WebChromeClient()

        //支持屏幕缩放
        webview_audio_effect.settings.setSupportZoom(false)
        webview_audio_effect.settings.builtInZoomControls = false
        webview_audio_effect.settings?.javaScriptEnabled = true
        webview_audio_effect.loadUrl(effectUrl)
    }

    override fun onDestroy() {
        super.onDestroy()

        MusicPlaySubject.getInstance().removeObserver(this)
    }
    override fun onStart() {
        super.onStart()

    }

    /**
     * 请求收藏
     */
    private fun requestCollection(open : Int) {

        val query = SubmitRequestQuery("FavoritesSwitch")
        query.action = AIIAction.TWO
        query.id = id
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, false){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                audio?.let {
                    if(it.isPraise == 2){
                        it.isPraise = 1
                        iv_audio_collection.setImageResource(R.drawable.common_btn_collect_nor)
                    } else {
                        it.isPraise = 2
                        iv_audio_collection.setImageResource(R.drawable.common_btn_collect_pre)
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
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                audio?.let {
                    if(it.isPraise == 2){
                        it.isPraise = 1
                        val praiseNum = it.praiseNum-1
                        tv_audio_praise_num.text = praiseNum.toString()
                        iv_audio_praise.setImageResource(R.drawable.common_btn_like_nor)
                        tv_audio_praise_num.setTextColor(ContextCompat.getColor(this@AudioDetailsActivity, R.color.gray7))
                    } else {
                        it.isPraise = 2
                        val praiseNum = it.praiseNum-1
                        tv_audio_praise_num.text = praiseNum.toString()
                        iv_audio_praise.setImageResource(R.drawable.common_btn_like_pre)
                        tv_audio_praise_num.setTextColor(ContextCompat.getColor(this@AudioDetailsActivity, R.color.colorPrimaryLight))
                    }
                }
            }
        })
    }


    fun startPlay(){
        if(audio == null){
            return
        }
        if(TextUtils.isEmpty(audioPath)){
            return
        }
        LogUtil.i("开始播放 audioPath:"+audioPath)
        ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_stop2)
        val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
        intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_PLAY)
        intent.putExtra(MusicService.ARG_TITLE, audio!!.name)
        intent.putExtra(MusicService.ARG_URL, audioPath)
        startService(intent)
    }

    private fun switchPlayStatus(playing: Boolean) {

        if(!playing){
            ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_play2)
            val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
            intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_PAUSE)
            startService(intent)

        } else {
            ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_stop2)
            val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
            intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_START)
            startService(intent)
        }

        isPlaying = playing
    }


    var oldPosition = 0
    var currentPosition = 0
    var duration = 0
    private fun getCurrentValue(mediaPlayer: MediaPlayer, current: Int, duration: Int) {
        if(isFinishing || supportFragmentManager.isDestroyed){
            return
        }
        if(isPlaying != mediaPlayer.isPlaying){
            isPlaying = mediaPlayer.isPlaying
            if(isPlaying){
                ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_stop2)
            } else {
                ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_play2)
            }
        }
        this.currentPosition = current
        this.duration = duration
        if(duration == 0){
            seekbar_audio.progress = 0
        } else {
            seekbar_audio.progress = currentPosition*100/duration
        }
        tvCurrentTime.text = formatTime(currentPosition)
        tvDuration.text = formatTime(duration)

        //根据上次正在播放的时间和当前的时间对比，如果一致，则说明进度没有变化，进度没有变化就肯定是缓冲了
        if (oldPosition == currentPosition && mediaPlayer.isPlaying) {
//            loading.visibility = View.VISIBLE
        } else {
//            loading.visibility = View.GONE
        }
        oldPosition = currentPosition

    }

    private fun formatTime(time : Int) : String{
        val second = time/1000%60
        val minute = time/1000/60%60
        val hour = time/1000/60/60
        return if(hour > 0){
            "${formatNum(hour)}:${formatNum(minute)}:${formatNum(second)}"
        } else {
            "${formatNum(minute)}:${formatNum(second)}"
        }
    }
    fun formatNum(value : Int) : String{
        return if(value < 10){
            "0$value"
        } else {
            value.toString()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_audio_details, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_share) {
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return true
            }
//            switchToActivity(MyDownloadActivity::class.java, MyDownloadActivity.ARG_POSITION to 1)
            shareDialog.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
    }

    override fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int) {
        LogUtil.e("播放错误  what:$what  extra:$extra ")
//        toast("播放错误！")
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, bufferingProgress: Int) {
//        LogUtil.e("OnBuffering ${bufferingProgress}")
        seekbar_audio.secondaryProgress = bufferingProgress
        val currentProgress=seekbar_audio.max *mediaPlayer.currentPosition /mediaPlayer.duration
//        LogUtil.e("$currentProgress % play",  "$bufferingProgress% buffer")
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        this.mediaPlayer = mediaPlayer
        LogUtil.e("准备好了")
        if(audio == null){
            return
        }
        switchPlayStatus(true)
        tvDuration.text = formatTime(duration)
        val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
        intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_START)
        intent.putExtra(MusicService.ARG_TITLE, audio!!.name)
        startService(intent)

    }
    override fun updatePosition(mediaPlayer: MediaPlayer, current: Int, duration: Int) {
        getCurrentValue(mediaPlayer, current, duration)
    }


    private fun download(vedio : Audio){

        var download = App.aiidbManager.findObjectFromId(Download::class.java, vedio.id)
        var fileExists = false
        if (download != null && !TextUtils.isEmpty(download.path)) {
            val file = File(download.path)
            fileExists = file.exists()
        }

        if (download == null || !fileExists) {
            val downloadManager = DownloadManager.getInstance(this)
            download = Download()
            download.timestamp = System.currentTimeMillis()
            download.id = vedio.id
            download.path = vedio.audioPath
            download.imagePath = vedio.imagePath
            download.title = vedio.name
            download.playLength = vedio.audioLength
            download.type = 1
            downloadManager.download(download)
            toast("开始下载...")
        } else {
            toast("音频已经存在")
        }
    }

    private fun requestVideoDetails() {
        val query = RequestQuery("AudioDetails")
        query.cacheMode = CacheMode.PRIORITY_OFTEN
        query.id = id
        App.aiiRequest.send(query, object : AIIResponse<AudioDetailsResponseQuery>(this, progressDialog) {

            override fun onSuccess(response: AudioDetailsResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.audio?.let {
                    setAudioData(it)
                }
            }

            override fun onCache(content: AudioDetailsResponseQuery?, index: Int) {
                super.onCache(content, index)
                content?.audio?.let {
                    setAudioData(it)
                }
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setAudioData(audio: Audio) {
        this.audio = audio
        audioPath = audio.audioPath

        val download = App.aiidbManager.findObjectFromId(Download::class.java, audio.id)
        var fileExists = false
        if (download != null && !TextUtils.isEmpty(download.localPath)) {
            val file = File(download.localPath)
            fileExists = file.exists()
            //一下载并存在
            if(fileExists){
                audioPath = download.localPath
                LogUtil.e("已下载路径"+audioPath)
            }
        }


        if(MusicService.isPlaying ){
            if(!MusicService.playPath.equals(audioPath)){
                LogUtil.e("路径不一致"+MusicService.playPath+" --------- "+audioPath)
                ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_play2)
                seekbar_audio.progress = 0
                //正在播放别的音频，那么就停止掉
                val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
                intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_STOP)
                startService(intent)
            } else {
                isPlaying = true
                isFirstPlay = false
                ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_stop2)
            }
        }
        downloadCofirmDialog.setContent("确定下载${audio.name}？")
        webview_audio.settings.javaScriptEnabled = true
        val audiosSynopsis = Utils.getAbsSource(audio.description, Api.BASE_URL + "/")
        webview_audio.loadData(audiosSynopsis,"text/html; charset=UTF-8", null)
        if(audio.isPraise == 2){
            iv_audio_praise.setImageResource(R.drawable.common_btn_like_pre)
            tv_audio_praise_num.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight))
        } else {
            iv_audio_praise.setImageResource(R.drawable.common_btn_like_nor)
            tv_audio_praise_num.setTextColor(ContextCompat.getColor(this, R.color.gray7))
        }
        if(audio.isFavorite == 2){
            iv_audio_collection.setImageResource(R.drawable.common_btn_collect_pre)
        } else {
            iv_audio_collection.setImageResource(R.drawable.common_btn_collect_nor)
        }
        tv_audio_praise_num.text = audio.praiseNum.toString()
        tv_audio_play_num.text = audio.playNum.toString()
        if(audio.payType ==2 && audio.price > 0){
            tv_audio_price.text = ""
//            tv_audio_price.text = "¥"+AiiUtil.formatString(audio.price)
            btn_audio_buy.visibility = View.GONE
        } else {
            tv_audio_price.text = ""
            btn_audio_buy.visibility = View.GONE
        }

        tv_audio_title.text = audio.name
        val imagePath = audio.imagePath
        val content = audio.name
        shareDialog.setShareData("爱侗乡有好听的音乐哦，快来听听吧！", content, imagePath, "http://www.aidongxiang.com/download/download.html?aid="+id)
    }
}
