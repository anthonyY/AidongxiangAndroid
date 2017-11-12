package com.aidongxiang.app.ui.square

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.utils.GlideImgManager
import kotlinx.android.synthetic.main.activity_publish_post.*
import mabeijianxi.camera.MediaRecorderActivity
import mabeijianxi.camera.model.MediaRecorderConfig
import android.R.attr.data
import android.app.Activity
import com.aidongxiang.app.utils.Utils
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.openapi.utils.ScreenUtils
import me.nereo.multi_image_selector.MultiImageSelectorActivity
import android.R.attr.data
import android.media.MediaPlayer
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.aidongxiang.app.adapter.PublishImageAdapter
import com.aidongxiang.app.model.Image
import com.aidongxiang.app.ui.video.VideoPlayerActivity
import kotlinx.android.synthetic.main.activity_video_player.*


/**
 * 发帖（微博）类
 * @author Anthony
 * createTime 2017-11-11
 */

@ContentView(R.layout.activity_publish_post)
class PublishPostActivity : BaseKtActivity() {

    var videoUri: String? = null
    var adapter: PublishImageAdapter? = null
    var datas = ArrayList<String>()

    companion object {
        val REQUEST_IMAGE = 1
        val REQUEST_RECORD_VIDEO = 2
        val REQUEST_SELECT_VIDEO = 3
    }

    override fun init(savedInstanceState: Bundle?) {


        adapter = PublishImageAdapter(this, datas)
        recycler_image.layoutManager = GridLayoutManager(this, 4)
        recycler_image.adapter = adapter

        rl_publish_video.setOnClickListener {
            if(videoUri != null){
                switchToActivity(VideoPlayerActivity::class.java, "path" to videoUri!!)
            }
        }
        iv_publish_image.setOnClickListener { switchToActivityForResult(ImageSelectActivity::class.java, REQUEST_IMAGE) }
        iv_publish_record_video.setOnClickListener {

            val config = MediaRecorderConfig.Buidler()
//                .doH264Compress(AutoVBRMode()
//                        //                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
//                )
//                .setMediaBitrateConfig(AutoVBRMode()
//                        //                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
//                )
                    .smallVideoWidth(1280)
                    .smallVideoHeight(720)
                    .recordTimeMax(10 * 1000)
                    .maxFrameRate(20)
                    .captureThumbnailsTime(3)
                    .recordTimeMin((1.5 * 1000).toInt())
                    .build()
            MediaRecorderActivity.goSmallVideoRecorder(this, PublishPostActivity::class.java.name, config)
        }
        iv_publish_select_video.setOnClickListener { chooseVideo() }


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        if (intent.hasExtra(MediaRecorderActivity.VIDEO_URI) && intent.hasExtra(MediaRecorderActivity.VIDEO_SCREENSHOT)) {
            videoUri = intent.getStringExtra(MediaRecorderActivity.VIDEO_URI)
            val videoScreenshot = intent.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT)
            if (!TextUtils.isEmpty(videoUri) && !TextUtils.isEmpty(videoScreenshot)) {
                GlideImgManager.loadFile(this, videoScreenshot, iv_video_thumb)
                rl_publish_video.visibility = View.VISIBLE
            }
        }
    }


    private fun chooseVideo() {
        switchToActivityForResult(VideoSelectActivity::class.java, REQUEST_SELECT_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 选取图片返回
        if (requestCode === REQUEST_SELECT_VIDEO) {
            if (resultCode === Activity.RESULT_OK) {
                if(data == null){
                    return
                }
                val path  = data.getStringExtra(VideoSelectActivity.ARG_PATH)
                val thumbPath = data.getStringExtra(VideoSelectActivity.ARG_THUMB_PATH)
                if(path != null){
                    videoUri = path
                    GlideImgManager.loadFile(this, thumbPath, iv_video_thumb)
                    rl_publish_video.visibility = View.VISIBLE
                }
            }
        } else if (requestCode === REQUEST_IMAGE) {
            if (data != null) {
                val paths = data.getStringArrayListExtra(ImageSelectActivity.EXTRA_RESULT)
                if (paths != null) {
                    if (paths.size == 0) {
                        recycler_image.layoutManager = GridLayoutManager(this, 1)
                    } else {
                        recycler_image.layoutManager = GridLayoutManager(this, 4)
                    }
                    datas.clear()
                    datas.addAll(paths)
                    adapter?.update()
                }
                rl_publish_video.visibility = View.GONE
            }
        }
    }
}
