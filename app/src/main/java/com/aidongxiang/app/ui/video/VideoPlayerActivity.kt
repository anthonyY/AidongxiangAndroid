package com.aidongxiang.app.ui.video

import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.Api
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.utils.Utils
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import kotlinx.android.synthetic.main.activity_video_player.*

/**
 * 通用单独播放界面
 *
 * @author Anthony
 */
@ContentView(R.layout.activity_video_player)
class VideoPlayerActivity : BaseKtActivity(){


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

    private var isPlay: Boolean = false
    private var isPause: Boolean = false
    var orientationUtils: OrientationUtils? = null



    override fun doBeforeSetContent() {
//        super.doBeforeSetContent()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun init(savedInstanceState: Bundle?) {


        mPath = bundle.getString(ARG_PATH)
        if (TextUtils.isEmpty(mPath)) {
            finish()
            return
        }

        mPath?.let {
            if (it.startsWith("http") || it.startsWith("/storage") || it.startsWith("/sdcard")) {
            } else {
                mPath = Api.IMAGE_URL + mPath
            }
        }

        video_player.setUp(mPath, true, "测试视频")

        //增加封面
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Utils.setVideoThumbnailForImageView(mPath) { thumb ->
            if (!supportFragmentManager.isDestroyed) {
                GlideImgManager.loadFile(this@VideoPlayerActivity, thumb, imageView)
            }
        }
        video_player.thumbImageView = imageView
        //增加title
        video_player.titleTextView.visibility = View.VISIBLE
        //设置返回键
        video_player.backButton.visibility = View.VISIBLE
        //设置旋转
        orientationUtils = OrientationUtils(this, video_player)
        orientationUtils?.isEnable = false
        video_player.isAutoFullWithSize = true
        video_player.isShowFullAnimation = false
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        video_player.fullscreenButton.setOnClickListener({
            orientationUtils!!.resolveByClick()
            video_player.startWindowFullscreen(this@VideoPlayerActivity, false, true)
        })

        //是否可以滑动调整
        video_player.setIsTouchWiget(true)
        //设置返回按键功能
        video_player.backButton.setOnClickListener{

            onBackPressed()
        }

        video_player.setVideoAllCallBack(object : GSYSampleCallBack() {
            override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                if (orientationUtils != null) {
                    orientationUtils?.backToProtVideo()
                }
            }

            override fun onPrepared(url: String?, vararg objects: Any?) {
                orientationUtils?.isEnable = true
                isPlay = true
            }

            override fun onAutoComplete(url: String?, vararg objects: Any?) {
                super.onAutoComplete(url, *objects)
                finish()
            }
        })
        video_player.startPlayLogic()
    }


    override fun onPause() {
        super.onPause()
        video_player.onVideoPause()
        isPause = true
    }

    override fun onResume() {
        super.onResume()
        video_player.onVideoResume()
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        if (orientationUtils != null)
            orientationUtils!!.releaseListener()

        if (isPlay) {
            video_player.currentPlayer.release()
        }
    }

    override fun onBackPressed() {
        //先返回正常状态
//        if (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == orientationUtils!!.screenType) {
//            video_player.fullscreenButton.performClick()
//            return
//        }
        if (orientationUtils != null) {
            orientationUtils?.backToProtVideo()
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        //释放所有
        video_player.setVideoAllCallBack(null)
        super.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            video_player.onConfigurationChanged(this, newConfig, orientationUtils, true, true)
        }
    }


}