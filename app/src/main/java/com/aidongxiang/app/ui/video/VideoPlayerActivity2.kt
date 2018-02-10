package com.aidongxiang.app.ui.video

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
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
import com.aidongxiang.app.utils.Utils
import com.aidongxiang.app.widgets.CustomVideoView
import com.aiitec.openapi.utils.LogUtil
import kotlinx.android.synthetic.main.activity_video_player2.*

/**
 *
 * @author Anthony
 * createTime 2018/1/28.
 * @version 1.0
 */

/**
 * 通用单独播放界面
 *
 * @author Anthony
 */
@ContentView(R.layout.activity_video_player2)
class VideoPlayerActivity2 : BaseKtActivity(), MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {


    companion object {
        val ARG_PATH = "path"
    }
    /**
     * 播放路径
     */
    private var mPath: String? = null
    /**
     * 是否需要恢复播放
     */
    private var mNeedResume = false

    var toucTime : Long  = 0
    private var mGesture : GestureDetector?= null

    /**屏幕方向，默认竖屏**/
    var rotation = Surface.ROTATION_90

    var mVideoViewLayoutParams : ViewGroup.LayoutParams?= null
    var oldPosition = 0

    override fun doBeforeSetContent() {
//        super.doBeforeSetContent()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun init(savedInstanceState: Bundle?) {
//        val videoHight = (ScreenUtils.getScreenWidth(this) / (MediaRecorderBase.SMALL_VIDEO_WIDTH / (MediaRecorderBase.SMALL_VIDEO_HEIGHT * 1.0f)))
//        videoview.layoutParams.height = videoHight.toInt()
//        videoview.requestLayout()

        mPath = bundle.getString(ARG_PATH)
        if (TextUtils.isEmpty(mPath)) {
            finish()
            return
        }
//        mPath = "http://lingmu111-10012243.cossh.myqcloud.com/%E6%B5%8B%E8%AF%95%E8%A7%86%E9%A2%91.mp4"

        LogUtil.e("mPath:"+mPath)
        mGesture = GestureDetector(this, MyGester())
        videoview.setZOrderOnTop(true)
        videoview.setZOrderMediaOverlay(true)

        setListener()

        mVideoViewLayoutParams = rl_video.layoutParams
        videoview.setVideoPath(mPath)

        videoview.start()
        play_status.setImageResource(R.drawable.common_btn_pause)
        rl_video_control.visibility = View.GONE

        setCurrentValue()

    }

    fun setListener(){
        videoview.setOnPreparedListener(this)
        videoview.setOnPlayStateListener(object : CustomVideoView.OnPlayStateListener{
            override fun onPause() {
                swithcPlaystatus(false)
            }

            override fun onPlay() {
                swithcPlaystatus(true)
            }

        })
        videoview.setOnErrorListener(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoview.setOnInfoListener(this)
        }
        videoview.setOnCompletionListener(this)
        play_status.setOnClickListener {
            val isPlaying = videoview.isPlaying
            swithcPlaystatus(!isPlaying)
            if(isPlaying){
                videoview.pause()
            } else {
                videoview.start()
            }

        }
        videoview.setZOrderOnTop(true)
        videoview.setZOrderMediaOverlay(true)
        videoview.setOnTouchListener { _, motionEvent ->
            mGesture?.onTouchEvent(motionEvent)
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                rl_video_control.visibility = View.VISIBLE
                toucTime = System.currentTimeMillis()
                Handler().postDelayed({
                    val delayed = System.currentTimeMillis()
                    if(delayed - toucTime >= 3000){
                        rl_video_control.visibility = View.GONE
                    }

                }, 3000)
            }
            return@setOnTouchListener true
        }



        seekbar_video.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seekbar: SeekBar) {
                val value = seekbar.progress * videoview.duration/100
                videoview.seekTo(value)
            }
        })

        iv_full_screen.setOnClickListener {
            LogUtil.e("================")
            switchScreenRotation()
        }


    }

    fun swithcPlaystatus(isPlaying : Boolean){
        if (isPlaying) {
            play_status.setImageResource(R.drawable.common_btn_pause)
            rl_video_control.visibility = View.GONE
        } else {
            rl_video_control.visibility = View.VISIBLE
            play_status.setImageResource(R.drawable.common_btn_play)
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


        //根据上次正在播放的时间和当前的时间对比，如果一致，则说明进度没有变化，进度没有变化就肯定是缓冲了
        if (oldPosition == currentPosition && videoview.isPlaying) {
            loading.visibility = View.VISIBLE
        } else {
            loading.visibility = View.GONE
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
    var currentPosition = 0
    var duration = 0


    override fun onResume() {
        super.onResume()
        if (mNeedResume) {
            mNeedResume = false
            videoview.start()

        }
    }

    override fun onPause() {
        super.onPause()
        if (videoview.isPlaying) {
            mNeedResume = true
            videoview.pause()
        }
    }


    override fun onPrepared(mp: MediaPlayer?) {
        if(mp == null){
            return
        }
//        videoview.setVolume(SurfaceVideoView.getSystemVolumn(this))
//
        loading.visibility = View.GONE
        val duration = mp.duration

        tv_video_duration.text = formatTime(duration)
        setCurrentValue()
        mp.setOnBufferingUpdateListener { p0, percent ->

            seekbar_video.secondaryProgress = percent
        }

    }

//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        return super.dispatchTouchEvent(ev)
//    }
//
//    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
//        when (event?.keyCode) {// 跟随系统音量走
//            KeyEvent.KEYCODE_VOLUME_DOWN -> {
//                videoview.dispatchKeyEvent(this, event)
//            }
//            KeyEvent.KEYCODE_VOLUME_UP -> {
//                videoview.dispatchKeyEvent(this, event)
//            }
//        }
//        return super.dispatchKeyEvent(event)
//    }
//
//    override fun onStateChanged(isPlaying: Boolean) {
//        if (isPlaying) {
//            play_status.setImageResource(R.drawable.btn_stop)
//        } else {
//            rl_video_control.visibility = View.VISIBLE
//            play_status.setImageResource(R.drawable.btn_play)
//        }
//    }

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
                if (!supportFragmentManager.isDestroyed) {
                    videoview.pause()
                    videoview.setBackgroundColor(Color.TRANSPARENT)
                }

            }
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                if (!isFinishing){
                    videoview.start()
                }
            }

        }
        return false
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
//        rotation = windowManager.defaultDisplay.rotation
        LogUtil.d("onConfigurationChanged : $newConfig, rot : $rotation")
//        switchScreenRotation()
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
//                seekbar_video.progress = position*100/videoview.duration
                videoview.seekTo(position)

            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

    override fun onStop() {
        super.onStop()
        videoview.stopPlayback()
    }

    var isFullScreen = false
    private fun switchScreenRotation() {

        LogUtil.e("---isFullScreen:$isFullScreen--------rotation:$rotation")
        if (!isFullScreen && (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)) {
            mVideoViewLayoutParams = rl_video.layoutParams
            val layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            rl_video.layoutParams = layoutParams
            isFullScreen = true
            rotation = Surface.ROTATION_0
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            iv_full_screen.setImageResource(R.drawable.nonfullscreen)
            Utils.switchFullScreen(window, true)
            LogUtil.e("全屏")
        } else {
            Utils.switchFullScreen(window, false)
            iv_full_screen.setImageResource(R.drawable.fullscreen)
            rl_video.layoutParams = mVideoViewLayoutParams
            isFullScreen = false
            rotation = Surface.ROTATION_90
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            LogUtil.e("取消全屏")
        }
    }


}
