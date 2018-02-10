package com.aidongxiang.app.ui.video

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
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
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.ui.mine.MyDownloadActivity
import com.aidongxiang.app.utils.Utils
import com.aidongxiang.app.widgets.CommonDialog
import com.aidongxiang.app.widgets.CustomVideoView
import kotlinx.android.synthetic.main.activity_video_details.*
import kotlinx.android.synthetic.main.layout_title_bar.*

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
    lateinit var adapter : SimpleFragmentPagerAdapter
    var id = -1
    var isPrepared = false
    var hasCache = false
    var isFirst = true
    var isPlayed = false
    var playPath : String ?= "http://lingmu111-10012243.cossh.myqcloud.com/yuntu_2.mp4"
//    var playPath : String ?= "http://192.168.31.7:8080/movie/yuntu_2.mp4"
    lateinit var commonDialog : CommonDialog
    var mVideoViewLayoutParams : ViewGroup.LayoutParams ?= null
    var currentPosition = 0
    var oldPosition = 0
    var duration = 0

    override fun init(savedInstanceState: Bundle?) {

        id = bundle.getInt(ARG_ID)
        title = "视频"
        adapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)
        adapter.addFragment(VideoSynopsisFragment(), "简介")
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

        setListener()

    }

    private fun setListener() {

        iv_video_status.setOnClickListener { iv_video_status.visibility = View.GONE }
        ivAudioDetailsPlay.setOnClickListener { switchPlayStatus(!isPlaying) }
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

    var toucTime : Long = 0
    private fun initVideo() {
//        videoView.setZOrderOnTop(true)
        videoview.setZOrderMediaOverlay(true)

        ivAudioDetailsPlay.setOnClickListener {

            if(videoview.isPlaying){
                videoview.pause()
                iv_video_status.visibility = View.VISIBLE
            } else {
                iv_video_status.visibility = View.GONE
                if(Utils.isWifi(this) || hasCache){
                    startVideo()
                } else {
                    if(isConfirmNotWifiPlay){
                        startVideo()
                    } else {
                        commonDialog.show()
                    }
                }
//                tv_video_5_minute.layoutParams.height = View.GONE
            }
        }

        videoview.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                //当没有显示立即购买按钮时才显示播放控件

                    ll_video_control.visibility = View.VISIBLE
                    toucTime = System.currentTimeMillis()
                    Handler().postDelayed({
                        if(supportFragmentManager.isDestroyed){
                            return@postDelayed
                        }
                        val delayed = System.currentTimeMillis()
                        if (delayed - toucTime >= 3000) {
                            if(videoview.isPlaying){
                                ll_video_control.visibility = View.GONE

                            }
                        }

                    }, 3000)
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

                if(isPrepared){
                    iv_video_wait.visibility = View.GONE
                }
            }

            override fun onPause() {
                ll_video_control.visibility = View.VISIBLE
                ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_play2)
//                if(videoView.currentPosition != 5*60*1000){
                //5分钟自动停止时不记录观看时间，否则下次进来还停留在5分钟，不能再次试看

//                }

            }
        })

        ll_video_control.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action){
                MotionEvent.ACTION_DOWN->{
                    ll_video_control.visibility = View.GONE
                    return@setOnTouchListener true
                }
            }
//            //手势添加按压的事件
//            mGesture?.onTouchEvent(motionEvent)
            return@setOnTouchListener super.onTouchEvent(motionEvent)
        }

        seekbar_video.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seekbar: SeekBar) {
                val value = seekbar.progress * videoview.duration/100
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

    private fun startVideo(){
        videoview.setBackgroundColor(0)

        if(!TextUtils.isEmpty(playPath)){
            if(isFirst){
                videoview.setVideoPath(playPath)
                isFirst = false
            }
            videoview.start()
        } else {
            if(videoview.isPlaying){
                videoview.pause()
            }
        }
    }


    /**
     * 切换选项卡内容
     */
    private fun switchTab(position: Int) {
        if(position == 0){
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

        if(!playing){
            ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_play2)
        } else {
            ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_stop2)
        }

        isPlaying = playing
    }


    override fun onCompletion(p0: MediaPlayer?) {
        if(isFullScreen){
            switchScreenRotation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_audio_details, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === R.id.action_download) {
            switchToActivity(MyDownloadActivity::class.java, MyDownloadActivity.ARG_POSITION to 0)
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    var rotation = Surface.ROTATION_90
    var isFullScreen = false
    private fun switchScreenRotation() {

//        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
//            mVideoViewLayoutParams = rl_video.layoutParams
//            val layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
////            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
////            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
////            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
////            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
//            rl_video.layoutParams = layoutParams
//            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//            rotation = Surface.ROTATION_0
//        } else if (rotation == Surface.ROTATION_0) {
//            //            RelativeLayout.LayoutParams lp = new  RelativeLayout.LayoutParams(320,240);
//            //            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//            rl_video.layoutParams = mVideoViewLayoutParams
//            rotation = Surface.ROTATION_90
//        }
        if (!isFullScreen && (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)) {
            mVideoViewLayoutParams = rl_video.layoutParams
            val layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            rl_video.layoutParams = layoutParams
            isFullScreen = true
            rotation = Surface.ROTATION_0
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            statusBarView?.visibility = View.GONE
            iv_full_screen.setImageResource(R.drawable.nonfullscreen)

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//            }
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
    }
    override fun onPrepared(mp: MediaPlayer?) {
        if(mp == null){
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


    private fun setCurrentValue() {
        if(isFinishing || supportFragmentManager.isDestroyed){
            return
        }
        currentPosition = videoview.currentPosition
        duration = videoview.duration
        if(duration == 0){
            seekbar_video.progress = 0
        } else {
            seekbar_video.progress = currentPosition*100/duration
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
            if(supportFragmentManager.isDestroyed){
                return@postDelayed
            }
            if(videoview.isPlaying){
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
        if(isFullScreen){
            switchScreenRotation()
        } else {
            if(videoview.isPlaying){
                videoview.pause()
            }
            super.onBackPressed()
        }
    }


}
