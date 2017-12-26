package com.aidongxiang.app.ui.video

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aiitec.openapi.utils.LogUtil
import com.mabeijianxi.smallvideorecord2.StringUtils
import com.mabeijianxi.smallvideorecord2.SurfaceVideoView
import kotlinx.android.synthetic.main.activity_video_player.*

/**
 * 通用单独播放界面
 *
 * @author Anthony
 */
@ContentView(R.layout.activity_video_player)
class VideoPlayerActivity : BaseKtActivity(), SurfaceVideoView.OnPlayStateListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {


    /**
     * 播放路径
     */
    private var mPath: String? = null
    /**
     * 是否需要恢复播放
     */
    private var mNeedResume = false

    var toucTime : Long  = 0
    var isFirst = true
    private var mGesture : GestureDetector ?= null

    /**屏幕方向，默认竖屏**/
    var rotation =  Surface.ROTATION_0

    var mVideoViewLayoutParams : ViewGroup.LayoutParams?= null

    override fun doBeforeSetContent() {
//        super.doBeforeSetContent()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mPath = intent.getStringExtra("path")
        if (StringUtils.isEmpty(mPath)) {
            finish()
            return
        }
    }

    override fun init(savedInstanceState: Bundle?) {
//        val videoHight = (ScreenUtils.getScreenWidth(this) / (MediaRecorderBase.SMALL_VIDEO_WIDTH / (MediaRecorderBase.SMALL_VIDEO_HEIGHT * 1.0f)))
//        videoview.layoutParams.height = videoHight.toInt()
//        videoview.requestLayout()
        videoview.setOnPreparedListener(this)
        videoview.setOnPlayStateListener(this)
        videoview.setOnErrorListener(this)
        videoview.setOnInfoListener(this)
        videoview.setOnCompletionListener(this)

        mGesture = GestureDetector(this, MyGester())
        videoview.setZOrderOnTop(true)
        videoview.setZOrderMediaOverlay(true)

        setListener()

        mVideoViewLayoutParams = rl_video.layoutParams
        videoview.setVideoPath(mPath)

        setCurrentValue()

    }

    fun setListener(){
        play_status.setOnClickListener {

            if(videoview.isPlaying){
                videoview.pause()
                rl_video_control.visibility = View.VISIBLE
                play_status.setImageResource(R.drawable.common_btn_play)
            } else {
                if(isFirst && !TextUtils.isEmpty(mPath)){
                    isFirst = false
                    videoview.start()
                } else {
                    videoview.reOpen()
                }
                play_status.setImageResource(R.drawable.common_btn_pause)
                rl_video_control.visibility = View.GONE
            }
        }

        videoview.setOnClickListener {
            rl_video_control.visibility = View.VISIBLE
            toucTime = System.currentTimeMillis()
            Handler().postDelayed({
                val delayed = System.currentTimeMillis()
                if(delayed - toucTime >= 3000){
                    rl_video_control.visibility = View.GONE
                }

            }, 3000)
        }

        rl_video_control.setOnTouchListener { _, motionEvent ->
            //手势添加按压的事件
            mGesture?.onTouchEvent(motionEvent)
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
    }

    private fun setCurrentValue() {
        currentPosition = videoview.currentPosition
        duration = videoview.duration
        if(duration == 0){
            seekbar_video.progress = 0
        } else {
            seekbar_video.progress = currentPosition*100/duration
        }

        tv_video_current_time.text = formatTime(currentPosition)
        Handler().postDelayed({
            setCurrentValue()
        }, 500)
    }

    private fun formatTime(time : Int) : String{
        val second = time/1000
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
    var currentPosition = 0
    var duration = 0


    override fun onResume() {
        super.onResume()
        if (mNeedResume) {
            mNeedResume = false
            if (videoview.isRelease) {
                videoview.reOpen()
            } else {
                videoview.start()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (videoview.isPlaying) {
            mNeedResume = true
            videoview.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoview.release()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if(mp == null){
            return
        }
        videoview.setVolume(SurfaceVideoView.getSystemVolumn(this))
//
        loading.visibility = View.GONE
        val duration = mp.duration

        tv_video_duration.text = formatTime(duration)
        setCurrentValue()
        mp.setOnBufferingUpdateListener { p0, percent ->
            seekbar_video.secondaryProgress = percent
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        when (event?.keyCode) {// 跟随系统音量走
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                videoview.dispatchKeyEvent(this, event)
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                videoview.dispatchKeyEvent(this, event)
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onStateChanged(isPlaying: Boolean) {
        if (isPlaying) {
            play_status.setImageResource(R.drawable.common_btn_pause)
        } else {
            rl_video_control.visibility = View.VISIBLE
            play_status.setImageResource(R.drawable.common_btn_play)
        }
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        if (!isFinishing) {
            toast("播放失败")
        }
        finish()
        return false
    }


    override fun onCompletion(p0: MediaPlayer?) {
//        if (!isFinishing) {
//            videoview.reOpen()
//        }
        rl_video_control.visibility = View.VISIBLE
        play_status.setImageResource(R.drawable.common_btn_play)
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING -> {
                // 音频和视频数据不正确
            }
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                if (!isFinishing) {
                    videoview.pause()
                }
            }
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                if (!isFinishing){
                    videoview.start()
                }
            }
            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    videoview.background = null
                } else {
                    videoview.setBackgroundDrawable(null)
                }
            }
        }
        return false
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        rotation = windowManager.defaultDisplay.rotation
        LogUtil.d("onConfigurationChanged : $newConfig, rot : $rotation")
        switchScreenRotation()
    }

    private fun switchScreenRotation() {

        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            mVideoViewLayoutParams = rl_video.layoutParams
            val layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            rl_video.layoutParams = layoutParams
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        } else if (rotation == Surface.ROTATION_0) {
            //            RelativeLayout.LayoutParams lp = new  RelativeLayout.LayoutParams(320,240);
            //            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            rl_video.layoutParams = mVideoViewLayoutParams
        }
    }


    /**
     * 定义手势监听的内部类继承 GestureDetector.SimpleOnGestureListener
     * 重写onScroll滑动监听的方法，左右滑动值监听X轴
     */
    internal inner class MyGester : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {//横向移动距离大于纵向，只监听横向
//                iv.setVisibility(View.VISIBLE)//显示快进或者快退图标
//                if (distanceX > 0) {//说明是往左滑动
//                    iv.setImageResource(R.drawable.left)
//                } else if (distanceX < 0) {
//                    iv.setImageResource(R.drawable.right)
//                }
                //让videoView的播放位置移动到手势拖动后的位置(*15知识为了缩小滑动比例)
                val position = (videoview.currentPosition - distanceX * 15).toInt()
                seekbar_video.progress = position*100/videoview.duration

            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

    override fun onStop() {
        super.onStop()
    }


}