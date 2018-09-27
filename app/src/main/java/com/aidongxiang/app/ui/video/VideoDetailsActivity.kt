package com.aidongxiang.app.ui.video

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.service.MusicService
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.app.ui.mine.MyDownloadActivity
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.utils.Utils
import com.aidongxiang.app.widgets.CommonDialog
import com.aidongxiang.app.widgets.CustomVideoView
import com.aidongxiang.app.widgets.PayDialog
import com.aidongxiang.business.model.Audio
import com.aidongxiang.business.response.AudioDetailsResponseQuery
import com.aiitec.openapi.enums.CacheMode
import com.aiitec.openapi.model.Download
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.openapi.utils.ScreenUtils
import kotlinx.android.synthetic.main.activity_video_details.*
import kotlinx.android.synthetic.main.layout_title_bar.*
import java.io.File

/**
 * 视频详情
 * @author Anthony
 * createTime 2017-11-27
 * @version 1.0
 */
@ContentView(R.layout.activity_video_details)
class VideoDetailsActivity : BaseKtActivity(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {


    companion object {
        /**确认非wifi也可以播放*/
        var isConfirmNotWifiPlay = false
    }

    var isPlaying = false
    lateinit var adapter: SimpleFragmentPagerAdapter
    var id: Long = -1
    var isPrepared = false
    var hasCache = false
    var isFirst = true
    var isPlayed = false
    var playPath: String? = null
    // "http://lingmu111-10012243.cossh.myqcloud.com/yuntu_2.mp4"
    //    var playPath : String ?= "http://192.168.31.7:8080/movie/yuntu_2.mp4"
    lateinit var commonDialog: CommonDialog
    var mVideoViewLayoutParams: ViewGroup.LayoutParams? = null
    //    var mVideoViewLayoutParams2: ViewGroup.LayoutParams? = null
    var currentPosition = 0
    var oldPosition = 0
    var duration = 0
    var video: Audio? = null
    lateinit var videoSynopsisFragment: VideoSynopsisFragment
    var videoWidth = 0
    var videoHeight = 0
    var videoScale = 1f
    lateinit var payDialog: PayDialog
    var TYPE_WECHART_PAY = 1
    var TYPE_ALIPAY = 2

    override fun init(savedInstanceState: Bundle?) {

        id = bundle.getLong(ARG_ID)
        title = "视频"
        videoSynopsisFragment = VideoSynopsisFragment()
        adapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)
        adapter.addFragment(videoSynopsisFragment, "简介")
        adapter.addFragment(VideoCommentFragment.newInstance(id), "评论")
        viewPager.adapter = adapter

        commonDialog = CommonDialog(this)
        commonDialog.setTitle("确定播放？")
        commonDialog.setContent("您当前不是wifi网络，是否继续播放？")
        commonDialog.setOnConfirmClickListener {
            commonDialog.dismiss()
            isConfirmNotWifiPlay = true
            startVideo()
        }

        payDialog = PayDialog(this)
        payDialog.setOnPayListener { payType ->
            video?.price?.let {
                when (payType) {
                    TYPE_WECHART_PAY -> {
                    }
                    TYPE_ALIPAY -> {
                    }
                }
            }
        }

        mVideoViewLayoutParams = rl_video.layoutParams
        setListener()

        requestVideoDetails()
    }

    private fun setListener() {

        iv_video_status.setOnClickListener {
            iv_video_status.visibility = View.GONE
            startVideo()
        }
        rl_details_comment.setOnClickListener { switchTab(1) }
        rl_details_synopsis.setOnClickListener { switchTab(0) }
        rl_details_synopsis.isSelected = true

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                switchTab(position)
            }
        })

        initVideo()
    }

    var toucTime: Long = 0
    private fun initVideo() {
//        videoView.setZOrderOnTop(true)
        videoview.setZOrderMediaOverlay(true)

        ivAudioDetailsPlay.setOnClickListener {

            if (videoview.isPlaying) {
                videoview.pause()
                iv_video_status.visibility = View.VISIBLE
            } else {
                iv_video_status.visibility = View.GONE
                if (Utils.isWifi(this) || hasCache) {
                    startVideo()
                } else {
                    if (isConfirmNotWifiPlay) {
                        startVideo()
                    } else {
                        commonDialog.show()
                    }
                }
//                tv_video_5_minute.layoutParams.height = View.GONE
            }
        }

        rl_video.setOnClickListener {
            onTouchVideoView()
        }
        videoview.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                onTouchVideoView()
            }
            return@setOnTouchListener true
        }
        videoview.setOnPlayStateListener(object : CustomVideoView.OnPlayStateListener {
            override fun onPlay() {
                ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_stop2)
                ll_video_control.visibility = View.GONE
//                ll_video_seekbar.visibility = View.VISIBLE
                //播放过了
                isPlayed = true

                if (isPrepared) {
                    iv_video_wait.visibility = View.GONE
                }
            }

            override fun onPause() {
                ll_video_control.visibility = View.VISIBLE
                ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_play2)
            }
        })

        ll_video_control.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    ll_video_control.visibility = View.GONE
                    return@setOnTouchListener true
                }
            }
//            //手势添加按压的事件
//            mGesture?.onTouchEvent(motionEvent)
            return@setOnTouchListener super.onTouchEvent(motionEvent)
        }

        seekbar_video.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seekbar: SeekBar) {
                val value = seekbar.progress * videoview.duration / 100
                videoview.seekTo(value)
            }
        })

        iv_full_screen.setOnClickListener {
            switchScreenRotation()
        }
        videoview.setOnPreparedListener(this)
        videoview.setOnCompletionListener(this)
//        enlargeSeekBar()

    }

    private fun onTouchVideoView(){
        //当没有显示立即购买按钮时才显示播放控件
        ll_video_control.visibility = View.VISIBLE
        toucTime = System.currentTimeMillis()
        Handler().postDelayed({
            if (supportFragmentManager.isDestroyed) {
                return@postDelayed
            }
            val delayed = System.currentTimeMillis()
            if (delayed - toucTime >= 3000) {
                if (videoview.isPlaying) {
                    ll_video_control.visibility = View.GONE

                }
            }
        }, 3000)
    }

    private fun startVideo() {
        if (MusicService.isPlaying) {
            val intent = Intent(this, MusicService::class.java)
            intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_STOP)
            startService(intent)
        }
        videoview.setBackgroundColor(0)
        setCurrentValue()
        LogUtil.e("startVideo:" + playPath)
        if (!TextUtils.isEmpty(playPath)) {
            if (isFirst) {
                videoview.setVideoURI(Uri.parse(playPath))
                isFirst = false
            }
            videoview.start()

            val retr = MediaMetadataRetriever()

            try {
                retr.setDataSource(playPath, HashMap<String, String>())
                val videoHeightStr = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                val videoWidthStr = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                videoHeight = Integer.parseInt(videoHeightStr) // 视频高度
                videoWidth = Integer.parseInt(videoWidthStr) // 视频宽度
                resetVideoWidth()
            } catch (e: Exception) {
                e.printStackTrace()
            }


        } else {
            if (videoview.isPlaying) {
                videoview.pause()
            }
        }
    }


    /**
     * 切换选项卡内容
     */
    private fun switchTab(position: Int) {
        if (position == 0) {
            rl_details_comment.isSelected = false
            rl_details_synopsis.isSelected = true
            viewPager.currentItem = position
        } else {
            rl_details_comment.isSelected = true
            rl_details_synopsis.isSelected = false
            viewPager.currentItem = position
        }

    }

    private fun switchPlayStatus(playing: Boolean) {

        if (!playing) {
            ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_play2)
        } else {
            ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_stop2)
        }

        isPlaying = playing
    }


    override fun onCompletion(p0: MediaPlayer?) {
        if (isFullScreen) {
            switchScreenRotation()
        }
        ll_video_control.visibility = View.VISIBLE
        ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_play2)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_audio_details, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_download) {
            if (Constants.user == null) {
                switchToActivity(LoginActivity::class.java)
                return true
            }
            switchToActivity(MyDownloadActivity::class.java, MyDownloadActivity.ARG_POSITION to 0)
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    var rotation = Surface.ROTATION_90
    var isFullScreen = false
    private fun switchScreenRotation() {

        if (videoWidth < videoHeight) {
            //如果宽<高，全屏只需改变大小就行了，屏幕方向不改变
            var mVideoHeight = 0
            if (!isFullScreen) {
                mVideoViewLayoutParams = rl_video.layoutParams
                val layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                rl_video.layoutParams = layoutParams
                isFullScreen = true
                iv_full_screen.setImageResource(R.drawable.nonfullscreen)
                titlebar.visibility = View.GONE
                mVideoHeight = ScreenUtils.getScreenHeight(this)
            } else {
                iv_full_screen.setImageResource(R.drawable.fullscreen)
                rl_video.layoutParams = mVideoViewLayoutParams
                isFullScreen = false
                titlebar.visibility = View.VISIBLE
                mVideoHeight = rl_video.layoutParams.height
            }

            var mVideoWidth = (mVideoHeight * videoScale).toInt()
            val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            rl_video2.layoutParams = layoutParams

            mVideoHeight = videoview.measuredHeight//ScreenUtils.getScreenHeight(this)
            mVideoWidth = ((mVideoHeight * videoScale).toInt())

            // 设置surfaceview画布大小
            videoview.holder.setFixedSize(mVideoWidth, mVideoHeight)
            // 重绘VideoView大小，这个方法是在重写VideoView时对外抛出方法
            videoview.setMeasure(mVideoWidth, mVideoHeight)
            // 请求调整
            videoview.requestLayout()

            return
        }
        if (!isFullScreen && (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)) {
            mVideoViewLayoutParams = rl_video.layoutParams
            val layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            rl_video.layoutParams = layoutParams
            isFullScreen = true
            rotation = Surface.ROTATION_0
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            iv_full_screen.setImageResource(R.drawable.nonfullscreen)

            Utils.switchFullScreen(window, true)
            titlebar.visibility = View.GONE
        } else {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            Utils.switchFullScreen(window, false)

            iv_full_screen.setImageResource(R.drawable.fullscreen)

            rl_video.layoutParams = mVideoViewLayoutParams
            isFullScreen = false
            rotation = Surface.ROTATION_90
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            statusBarView?.visibility = View.VISIBLE
            titlebar.visibility = View.VISIBLE
        }
        resetVideoWidth()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (mp == null) {
            return
        }
        isPrepared = true
        loading.visibility = View.GONE


        val duration = mp.duration

        tv_video_duration.text = formatTime(duration)
        setCurrentValue()

//        response?.current_audio?.watchTime?.let {
//            //倒数五秒的话就当作完成了，就不seekTo了
//            if(it > 0 && it*1000 < videoView.duration-5000){
//                videoview.seekTo(it*1000)
//            }
//        }

        mp.setOnBufferingUpdateListener { _, percent ->
            seekbar_video.secondaryProgress = percent
        }
        mp.setOnVideoSizeChangedListener { mediaPlayer, _, _ ->
            // 获取视频资源的高度

            this.videoWidth = mediaPlayer.videoWidth
            this.videoHeight = mediaPlayer.videoHeight
            resetVideoWidth()
        }

    }


    private fun formatTime(time: Int): String {
        val second = time / 1000 % 60
        val minute = time / 1000 / 60 % 60
        val hour = time / 1000 / 60 / 60
        return if (hour > 0) {
            "${formatNum(hour)}:${formatNum(minute)}:${formatNum(second)}"
        } else {
            "${formatNum(minute)}:${formatNum(second)}"
        }
    }

    fun formatNum(value: Int): String {
        return if (value < 10) {
            "0$value"
        } else {
            value.toString()
        }
    }


    private fun setCurrentValue() {
        if (isFinishing || supportFragmentManager.isDestroyed) {
            return
        }
        currentPosition = videoview.currentPosition
        duration = videoview.duration
        if (duration == 0) {
            seekbar_video.progress = 0
        } else {
            seekbar_video.progress = currentPosition * 100 / duration
        }
        tv_video_current_time.text = formatTime(currentPosition)

        //根据上次正在播放的时间和当前的时间对比，如果一致，则说明进度没有变化，进度没有变化就肯定是缓冲了
        if (oldPosition == currentPosition && videoview.isPlaying) {
            loading.visibility = View.VISIBLE
        } else {
            loading.visibility = View.GONE
        }
        oldPosition = currentPosition
        Handler().postDelayed({
            if (supportFragmentManager.isDestroyed) {
                return@postDelayed
            }
            if (videoview.isPlaying) {
                setCurrentValue()
            }

        }, 500)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        rotation = windowManager.defaultDisplay.rotation
//        switchScreenRotation()
    }

    override fun onBackPressed() {
        if (isFullScreen) {
            switchScreenRotation()
        } else {
            if (videoview.isPlaying) {
                videoview.pause()
            }
            super.onBackPressed()
        }
    }


    private fun requestVideoDetails() {
        val query = RequestQuery("AudioDetails")
        query.id = id
        query.cacheMode = CacheMode.PRIORITY_OFTEN
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

    private fun setAudioData(audio: Audio) {
        this.video = audio
        playPath = audio.audioPath
        val download = App.aiidbManager.findObjectFromId(Download::class.java, audio.id)
        var fileExists = false
        if (download != null && !TextUtils.isEmpty(download.localPath)) {
            val file = File(download.localPath)
            fileExists = file.exists()
            //一下载并存在
            if (fileExists) {
                playPath = download.localPath
                LogUtil.e("已下载路径" + playPath)
            }
        }
        GlideImgManager.load(this, audio.imagePath, iv_video_wait)
        setCommentNum(audio.commentNum)
        videoSynopsisFragment.update(audio)
    }

    /**
     * 这个会经常更新， 并且是由子Fragment 更新
     */
    fun setCommentNum(num: Int) {
        tv_video_comment_num.text = num.toString()
    }


    private fun resetVideoWidth() {
        var mVideoWidth = videoWidth
// 获取视频资源的高度
        var mVideoHeight = videoHeight
// 获取屏幕的宽度
// 在资源尺寸可以播放观看时处理
        if (mVideoHeight > 0 && mVideoWidth > 0) {
// 拉伸比例
            videoScale = mVideoWidth.toFloat() / mVideoHeight
// 视频资源拉伸至屏幕宽度，横屏竖屏需结合传感器等特殊处理
// 拉伸VideoView高度
            if (videoScale != 0f) {
                if (mVideoWidth > mVideoHeight) {
                    if (isFullScreen) {
                        mVideoHeight = ScreenUtils.getScreenWidth(this)
                        mVideoWidth = (mVideoHeight * videoScale).toInt()
                        val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                        rl_video2.layoutParams = layoutParams
                    } else {
                        mVideoHeight = rl_video2.measuredHeight
                        mVideoWidth = (mVideoHeight * videoScale).toInt()
                        val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                        rl_video2.layoutParams = layoutParams
                    }
                    mVideoWidth = videoview.measuredWidth//ScreenUtils.getScreenWidth(this)
                    mVideoHeight = ((mVideoWidth / videoScale).toInt())
                } else {
                    if (isFullScreen) {
                        mVideoHeight = ScreenUtils.getScreenHeight(this)
                        mVideoWidth = (mVideoHeight * videoScale).toInt()
                        val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                        rl_video2.layoutParams = layoutParams
                    } else {
                        mVideoHeight = rl_video2.measuredHeight
                        mVideoWidth = (mVideoHeight * videoScale).toInt()
                        val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                        rl_video2.layoutParams = layoutParams
                    }
                    mVideoHeight = videoview.measuredHeight//ScreenUtils.getScreenHeight(this)
                    mVideoWidth = ((mVideoHeight * videoScale).toInt())
                }
            }
            // 设置surfaceview画布大小
            videoview.holder.setFixedSize(mVideoWidth, mVideoHeight)
// 重绘VideoView大小，这个方法是在重写VideoView时对外抛出方法
            videoview.setMeasure(mVideoWidth, mVideoHeight)
// 请求调整
            videoview.requestLayout()
        }
    }

    fun addCommentNum(){
        video?.let {
            val commentNum = it.commentNum++
            tv_video_comment_num.text = commentNum.toString()
        }
    }

}
