package com.aidongxiang.app.ui.video

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.widgets.CommonDialog
import com.aidongxiang.app.widgets.PayDialog
import com.aidongxiang.business.model.Audio
import com.aidongxiang.business.response.AudioDetailsResponseQuery
import com.aiitec.openapi.enums.CacheMode
import com.aiitec.openapi.model.Download
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.widgets.ShareDialog
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import kotlinx.android.synthetic.main.activity_video_details2.*
import java.io.File

/**
 * 视频详情
 * @author Anthony
 * createTime 2017-11-27
 * @version 1.0
 */
@ContentView(R.layout.activity_video_details2)
class VideoDetails2Activity : BaseKtActivity() {


    companion object {
        /**确认非wifi也可以播放*/
        var isConfirmNotWifiPlay = false
    }

    lateinit var adapter: SimpleFragmentPagerAdapter
    lateinit var thumbImageView: ImageView
    var id: Long = -1
    var playPath: String? = null
    // "http://lingmu111-10012243.cossh.myqcloud.com/yuntu_2.mp4"
    //    var playPath : String ?= "http://192.168.31.7:8080/movie/yuntu_2.mp4"
    lateinit var commonDialog: CommonDialog
    var duration = 0
    var video: Audio? = null
    lateinit var videoSynopsisFragment: VideoSynopsisFragment
    lateinit var payDialog: PayDialog
    var TYPE_WECHART_PAY = 1
    var TYPE_ALIPAY = 2
    private var isPlay: Boolean = false
    private var isPause: Boolean = false
    var orientationUtils: OrientationUtils? = null
    lateinit var shareDialog : ShareDialog

    override fun init(savedInstanceState: Bundle?) {

        id = bundle.getLong(Constants.ARG_ID)
        val title = bundle.getString(Constants.ARG_TITLE)
        if(TextUtils.isEmpty(title)){
            setTitle("视频")
        } else {
            setTitle(title)
        }
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
        shareDialog = ShareDialog(this)
        initVideo()
        setListener()

        requestVideoDetails()
    }

    private fun setListener() {

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


    }

    private fun initVideo() {


//        val source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
//        val source1 = "http://192.168.31.7:8080/movie/巴命斗牛第二场.avi"

        //增加封面
        thumbImageView = ImageView(this)

        thumbImageView.scaleType = ImageView.ScaleType.CENTER_CROP

        video_player.thumbImageView = thumbImageView
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
            video_player.startWindowFullscreen(this@VideoDetails2Activity, true, true)
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
                if (orientationUtils != null) {
                    orientationUtils?.backToProtVideo()
                }
            }
        })
//        video_player.startPlayLogic()
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_audio_details, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_share) {
            if (Constants.user == null) {
                switchToActivity(LoginActivity::class.java)
                return true
            }

            shareDialog.show()
//            switchToActivity(MyDownloadActivity::class.java, MyDownloadActivity.ARG_POSITION to 0)
            return true
        }
        return super.onOptionsItemSelected(item)
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
        LogUtil.e("playPath:"+playPath)
        val download = App.aiidbManager.findObjectFromId(Download::class.java, audio.id)
        var fileExists = false
        if (download != null && !TextUtils.isEmpty(download.localPath)) {
            val file = File(download.localPath)
            fileExists = file.exists()
            //一下载并存在
            if (fileExists) {
                LogUtil.e("有缓存:"+download.localPath)
                playPath = download.localPath
            }
        }
//        Glide.with(this).load(audio.imagePath).into(thumbImageView)
        playPath = playPath?.replace("\\/", "/")
        video_player.setUp(playPath, true, audio.name)
        val imagePath = audio.imagePath
        val content = audio.name
        shareDialog.setShareData("爱侗乡有精彩好看的视频哦，快来看看吧！", content, imagePath, "http://www.aidongxiang.com/download/download.html?vid="+id)
        GlideImgManager.load(this, audio.imagePath, thumbImageView)
        setCommentNum(audio.commentNum)
        videoSynopsisFragment.update(audio)
    }

    /**
     * 这个会经常更新， 并且是由子Fragment 更新
     */
    fun setCommentNum(num: Int) {
        tv_video_comment_num.text = num.toString()
    }

//
//    private fun resetVideoWidth() {
//        var mVideoWidth = videoWidth
//// 获取视频资源的高度
//        var mVideoHeight = videoHeight
//// 获取屏幕的宽度
//// 在资源尺寸可以播放观看时处理
//        if (mVideoHeight > 0 && mVideoWidth > 0) {
//// 拉伸比例
//            videoScale = mVideoWidth.toFloat() / mVideoHeight
//// 视频资源拉伸至屏幕宽度，横屏竖屏需结合传感器等特殊处理
//// 拉伸VideoView高度
//            if (videoScale != 0f) {
//                if (mVideoWidth > mVideoHeight) {
//                    if (isFullScreen) {
//                        mVideoHeight = ScreenUtils.getScreenWidth(this)
//                        mVideoWidth = (mVideoHeight * videoScale).toInt()
//                        val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
//                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
//                        rl_video2.layoutParams = layoutParams
//                    } else {
//                        mVideoHeight = rl_video2.measuredHeight
//                        mVideoWidth = (mVideoHeight * videoScale).toInt()
//                        val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
//                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
//                        rl_video2.layoutParams = layoutParams
//                    }
//                    mVideoWidth = videoview.measuredWidth//ScreenUtils.getScreenWidth(this)
//                    mVideoHeight = ((mVideoWidth / videoScale).toInt())
//                } else {
//                    if (isFullScreen) {
//                        mVideoHeight = ScreenUtils.getScreenHeight(this)
//                        mVideoWidth = (mVideoHeight * videoScale).toInt()
//                        val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
//                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
//                        rl_video2.layoutParams = layoutParams
//                    } else {
//                        mVideoHeight = rl_video2.measuredHeight
//                        mVideoWidth = (mVideoHeight * videoScale).toInt()
//                        val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
//                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
//                        rl_video2.layoutParams = layoutParams
//                    }
//                    mVideoHeight = videoview.measuredHeight//ScreenUtils.getScreenHeight(this)
//                    mVideoWidth = ((mVideoHeight * videoScale).toInt())
//                }
//            }
//            // 设置surfaceview画布大小
//            videoview.holder.setFixedSize(mVideoWidth, mVideoHeight)
//// 重绘VideoView大小，这个方法是在重写VideoView时对外抛出方法
//            videoview.setMeasure(mVideoWidth, mVideoHeight)
//// 请求调整
//            videoview.requestLayout()
//        }
//    }

    fun addCommentNum(){
        video?.let {
            val commentNum = it.commentNum++
            tv_video_comment_num.text = commentNum.toString()
        }
    }

}