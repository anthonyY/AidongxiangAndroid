//package com.aidongxiang.app.ui.video
//
//import android.content.res.Configuration
//import android.media.MediaPlayer
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.text.TextUtils
//import android.view.*
//import android.widget.RelativeLayout
//import com.aidongxiang.app.R
//import com.aidongxiang.app.annotation.ContentView
//import com.aidongxiang.app.base.BaseKtActivity
//import com.aiitec.openapi.utils.LogUtil
//import com.aiitec.openapi.utils.ScreenUtils
//import mabeijianxi.camera.MediaRecorderBase
//import mabeijianxi.camera.util.StringUtils
//import mabeijianxi.camera.views.SurfaceVideoView
//
///**
// * 通用单独播放界面
// *
// * @author Anthony
// */
//@ContentView(R.layout.activity_video_player)
//class VideoPlayerActivity2 : BaseKtActivity(), SurfaceVideoView.OnPlayStateListener, MediaPlayer.OnErrorListener,
//        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {
//
//
//    /**
//     * 播放路径
//     */
//    private var mPath: String? = null
//    /**
//     * 是否需要恢复播放
//     */
//    private var mNeedResume = false
//
//    var toucTime : Long  = 0
//    var isFirst = true
//
//    override fun doBeforeSetContent() {
//        super.doBeforeSetContent()
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        mPath = intent.getStringExtra("path")
//        if (StringUtils.isEmpty(mPath)) {
//            finish()
//            return
//        }
//    }
//
//    override fun init(savedInstanceState: Bundle?) {
//        val videoHight = (ScreenUtils.getScreenWidth(this) / (MediaRecorderBase.SMALL_VIDEO_WIDTH / (MediaRecorderBase.SMALL_VIDEO_HEIGHT * 1.0f)))
//        videoview.layoutParams.height = videoHight.toInt()
//        videoview.requestLayout()
//        videoview.setOnPreparedListener(this)
//        videoview.setOnPlayStateListener(this)
//        videoview.setOnErrorListener(this)
//        videoview.setOnInfoListener(this)
//        videoview.setOnCompletionListener(this)
//        videoview.setVideoPath(mPath)
//
//        videoview.setZOrderOnTop(true)
//        videoview.setZOrderMediaOverlay(true)
//
//        play_status.setOnClickListener {
//
//            if(videoview!!.isPlaying){
//                videoview.pause()
//                rl_video_control.visibility = View.VISIBLE
//                play_status.setImageResource(R.drawable.common_btn_play)
//            } else {
//                if(isFirst && !TextUtils.isEmpty(mPath)){
//                    videoview.start()
//                } else {
//                    videoview.reOpen()
//                }
//                play_status.setImageResource(R.drawable.common_btn_pause)
////                rl_video_control.visibility = View.GONE
//            }
//        }
//
////        videoview.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
////            when (motionEvent.action) {
////                MotionEvent.ACTION_DOWN ->{
////                    LogUtil.e("--------------------------")
////                    rl_video_control.visibility = View.VISIBLE
////                    toucTime = System.currentTimeMillis()
////                    Handler().postDelayed({
////                        val delayed = System.currentTimeMillis()
////                        if(delayed - toucTime >= 1000){
////                            rl_video_control.visibility = View.GONE
////                        }
////
////                    }, 3000)
////                    return@OnTouchListener true
////                }
////            }
////            false
////        })
//        videoview.setOnClickListener {
//            LogUtil.e("--------------------------")
//            rl_video_control.visibility = View.VISIBLE
//            toucTime = System.currentTimeMillis()
//            Handler().postDelayed({
//                val delayed = System.currentTimeMillis()
//                if(delayed - toucTime >= 3000){
//                    rl_video_control.visibility = View.GONE
//                }
//
//            }, 3000)
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (mNeedResume) {
//            mNeedResume = false
//            if (videoview.isRelease) {
//                videoview.reOpen()
//            } else {
//                videoview.start()
//            }
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (videoview.isPlaying) {
//            mNeedResume = true
//            videoview.pause()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        videoview.release()
//    }
//
//    override fun onPrepared(p0: MediaPlayer?) {
//        videoview.setVolume(SurfaceVideoView.getSystemVolumn(this))
//        videoview.start()
//        loading.visibility = View.GONE
//    }
//
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
//            play_status.setImageResource(R.drawable.common_btn_pause)
//        } else {
//            rl_video_control.visibility = View.VISIBLE
//            play_status.setImageResource(R.drawable.common_btn_play)
//        }
//    }
//
//    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
//        if (!isFinishing) {
//            toast("播放失败")
//        }
//        finish()
//        return false
//    }
//
//
//    override fun onCompletion(p0: MediaPlayer?) {
//        if (!isFinishing) {
//            videoview.reOpen()
//        }
//    }
//
//    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
//        when (what) {
//            MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING -> {
//                // 音频和视频数据不正确
//            }
//            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
//                if (!isFinishing) {
//                    videoview.pause()
//                }
//            }
//            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
//                if (!isFinishing){
//                    videoview.start()
//                }
//            }
//            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    videoview.background = null
//                } else {
//                    videoview.setBackgroundDrawable(null)
//                }
//            }
//        }
//        return false
//    }
//
//    var mVideoViewLayoutParams : ViewGroup.LayoutParams?= null
//    override fun onConfigurationChanged(newConfig: Configuration?) {
//        super.onConfigurationChanged(newConfig)
//        val rot = windowManager.defaultDisplay.rotation
//        LogUtil.d("onConfigurationChanged : $newConfig, rot : $rot")
//        if (rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270) {
//            mVideoViewLayoutParams = rl_video.layoutParams
//            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
//            rl_video.layoutParams = layoutParams
//            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//
//        } else if (rot == Surface.ROTATION_0) {
//            //            RelativeLayout.LayoutParams lp = new  RelativeLayout.LayoutParams(320,240);
//            //            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//            rl_video.layoutParams = mVideoViewLayoutParams
//        }
//    }
//
//}